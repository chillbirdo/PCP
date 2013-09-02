package pcp.alg;

import ilog.concert.*;
import ilog.cplex.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import pcp.model.Coloring;
import pcp.model.Node;
import pcp.model.NodeColorInfo;

public class ILPSolver {

    private static final Logger logger = Logger.getLogger(ILPSolver.class.getName());

    public static int performOnUnselected(Coloring cc) {

        int result = 0;
        //find all unselected partitions and create a mapping partition_index -> index_in_m
        //find the size of m
        int[] selectedPartitionMapping = new int[cc.getGraph().getPartitionAmount()];
        int selectedPartitionCount = 0;
        for (int i = 0; i < selectedPartitionMapping.length; i++) {
            selectedPartitionMapping[i] = -1;
        }
        for (int i = 0; i < cc.getGraph().getPartitionAmount(); i++) {
            if (!cc.isPartitionSelected(i)) {
                selectedPartitionMapping[i] = selectedPartitionCount;
                selectedPartitionCount++;
            }
        }

        //create and fill m
        List[] mL = new List[selectedPartitionCount];
        for (int p = 0; p < mL.length; p++) {
            int partition = xPartitionToRealPartition(p, selectedPartitionMapping);
            Node[] nodesInPartition = cc.getGraph().getNodesOfPartition(partition);
            mL[p] = new ArrayList<ArrayList<Integer>>(nodesInPartition.length);
            for (int v = 0; v < nodesInPartition.length; v++) {
                Node n = nodesInPartition[v];
                NodeColorInfo nci = cc.getNciById(n.getId());
                mL[p].add(nci.getConflictArray());
            }
        }

        //solve ILP with m
        try {
            IloCplex cplex = new IloCplex();
            cplex.setOut(null);
            
            //initialize variables and objective expression
            List[] xL = new List[mL.length];
            IloLinearIntExpr objectiveExpr = cplex.linearIntExpr();
            for (int p = 0; p < xL.length; p++) {
                xL[p] = new ArrayList<IloIntVar[]>(mL[p].size());
                for (int v = 0; v < mL[p].size(); v++) {
                    List conflictList = (List) mL[p].get(v);
                    IloIntVar[] iiva = new IloIntVar[conflictList.size()];
                    for (int c = 0; c < iiva.length; c++) {
                        iiva[c] = cplex.boolVar();
                        objectiveExpr.addTerm((Integer) conflictList.get(c), iiva[c]);
                    }
                    xL[p].add(iiva);
                }
            }
            cplex.addMinimize(objectiveExpr);
            logger.finer("\nOBJECTIVE: " + objectiveExpr);

            //build contraints 1: only one node-color-pair selected
            for (int p = 0; p < xL.length; p++) {
                IloLinearIntExpr expr = cplex.linearIntExpr();
                for (int v = 0; v < xL[p].size(); v++) {
                    IloIntVar[] xpv = (IloIntVar[]) xL[p].get(v);
                    for (int c = 0; c < xpv.length; c++) {
                        IloIntVar[] iiva = (IloIntVar[]) xL[p].get(v);
                        expr.addTerm(1, iiva[c]);
                    }
                }
                cplex.addEq(expr, 1);
                logger.finer("\nCONTSTRAINT 1 for " + p + ": " + objectiveExpr);
            }

            //build constraints 2: no two adjacent nodes may have the same color
            //select only edges that are between nodes represented by x:
            ArrayList<Integer[]> xEdges = new ArrayList<Integer[]>();
            for (Integer[] edge : cc.getGraph().getEdges()) {
                Node n1 = cc.getGraph().getNode(edge[0]);
                Node n2 = cc.getGraph().getNode(edge[1]);
                if (selectedPartitionMapping[n1.getPartition()] != -1 && selectedPartitionMapping[n2.getPartition()] != -1) {
                    xEdges.add(edge);
                }
            }
            //set constraint:
            for (Integer[] edge : xEdges) {
                IloLinearIntExpr expr = cplex.linearIntExpr();
                Node n1 = cc.getGraph().getNode(edge[0]);
                Node n2 = cc.getGraph().getNode(edge[1]);

                int p1 = selectedPartitionMapping[n1.getPartition()];
                int p2 = selectedPartitionMapping[n2.getPartition()];
                int v1 = n1.getIdxInPartition();
                int v2 = n2.getIdxInPartition();
                IloIntVar[] c1 = (IloIntVar[]) xL[p1].get(v1);
                IloIntVar[] c2 = (IloIntVar[]) xL[p2].get(v2);
                for (int color = 0; color < c1.length; color++) {
                    expr.addTerm(1, c1[color]);
                    expr.addTerm(1, c2[color]);
                }
                cplex.addLe(expr, 1);
                logger.finer("\nCONTSTRAINT 2 for edge (" + edge[0] + ", " + edge[1] + "): " + objectiveExpr);
            }

            //solve, output and integrate solution into coloring
            if (cplex.solve()) {
                logger.finest("Solution status = " + cplex.getStatus());
                logger.fine("ILP Solution value  = " + cplex.getObjValue() + " conflicts.");
                result = (int) Math.round(cplex.getObjValue());
                for (int p = 0; p < xL.length; p++) {
                    List<IloIntVar[]> vList = xL[p];
                    logger.fine("Partition " + p + ":");
                    for (int v = 0; v < vList.size(); v++) {
                        IloIntVar[] iiva = vList.get(v);
                        double[] val = cplex.getValues(iiva);
                        String valStr = "\tNode " + v + ": ";
                        for (int j = 0; j < val.length; ++j) {
                            valStr += val[j] + " ";
                            //integrate
                            if (Math.round(val[j]) == 1) {
                                int partition = xPartitionToRealPartition(p, selectedPartitionMapping);
                                Node n = cc.getGraph().getNodeOfPartition(partition, v);
                                NodeColorInfo nci = cc.getNciById(n.getId());
                                logger.finest("coloring node " + nci.getNode().getId());
                                cc.selectNci(nci);
                                cc.colorNci(nci, j);
                            }
                        }
                        logger.finer(valStr);
                    }
                }
            }
            cplex.end();
//            logger.fine("ILP solved: " + cc.getConflictingNCIs().size() + " CONFLICTS!");
        } catch (IloException e) {
            System.err.println("Concert exception '" + e + "' caught");
        }
        return result;
    }

    private static int xPartitionToRealPartition(int xPartition, int[] selectedPartitionMapping) {
        int partition = -1;
        for (int i = 0; i < selectedPartitionMapping.length; i++) {
            if (selectedPartitionMapping[i] == xPartition) {
                partition = i;
                break;
            }
        }
        if (partition == -1) {
            logger.severe("UNEXPECED: could not map partition");
        }
        return partition;
    }
}