package pcp.alg;

import ilog.concert.*;
import ilog.cplex.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;
import pcp.model.Coloring;
import pcp.model.Node;
import pcp.model.NodeColorInfo;
import pcp.model.NodeColorInfoIF;

public class ILPSolver2 {

    private static final Logger logger = Logger.getLogger(ILPSolver2.class.getName());

    public static int performOnUnselected(Coloring cc, boolean coloringRestrition) {

        int result = 0;

        //find all unselected partitions and create a mapping partition_index -> index_in_m
        //find the size of m
        int[] unSelectedPartitionMapping = new int[cc.getGraph().getPartitionAmount()];
        int unSelectedPartitionCount = 0;
        for (int i = 0; i < unSelectedPartitionMapping.length; i++) {
            unSelectedPartitionMapping[i] = -1;
        }
        for (int i = 0; i < cc.getGraph().getPartitionAmount(); i++) {
            if (!cc.isPartitionSelected(i)) {
                unSelectedPartitionMapping[i] = unSelectedPartitionCount;
                unSelectedPartitionCount++;
            }
        }

        //build datastructure that maps colored ncis to lists of unselected ncis (in unselected partitions)
        Map<NodeColorInfoIF, List<Node>> connectionMap = new TreeMap<NodeColorInfoIF, List<Node>>();
        for (NodeColorInfoIF insideNci : cc.getUnselectedNCIs()) {
            for (Node neigh : insideNci.getNode().getNeighbours()) {
                NodeColorInfoIF outsideNci = cc.getNciById(neigh.getId());
                if (cc.getSelectedColoredNCIs().contains(outsideNci)) {
                    if (!connectionMap.containsKey(outsideNci)) {
                        List<Node> l = new ArrayList<Node>(cc.getGraph().getHighestDegree());
                        connectionMap.put(outsideNci, l);
                    }
                    connectionMap.get(outsideNci).add(insideNci.getNode());
                }
            }
        }

        //build and solve ILP with m
        try {
            IloCplex cplex = new IloCplex();
            cplex.setOut(null);

            //init z variables and objective
            IloIntVar[] za = new IloIntVar[connectionMap.size()];
            for (int i = 0; i < za.length; i++) {
                za[i] = cplex.boolVar();
            }
            cplex.addMinimize(cplex.sum(za));

            //initialize x variable datastructure
            List[] xL = new List[unSelectedPartitionCount];
            for (int p = 0; p < xL.length; p++) {
                int realPartition = xPartitionToRealPartition(p, unSelectedPartitionMapping);
                int partitionSize = cc.getGraph().getPartitionSize(realPartition);
                xL[p] = new ArrayList<IloIntVar[]>(partitionSize);
                for (int v = 0; v < partitionSize; v++) {
                    IloIntVar[] iiva = new IloIntVar[cc.getChromatic()];
                    for (int c = 0; c < iiva.length; c++) {
                        iiva[c] = cplex.boolVar();
                    }
                    xL[p].add(iiva);
                }
            }

            //add x->z dependencies
            int zIdx = 0;
            for (Entry e : connectionMap.entrySet()) {
                NodeColorInfoIF outsideNci = (NodeColorInfoIF) e.getKey();
                List<Node> insideNodes = (List<Node>) e.getValue();
                IloIntVar[] xVars = new IloIntVar[insideNodes.size()];
                for (int i = 0; i < xVars.length; i++) {
                    Node insideNode = insideNodes.get(i);
                    xVars[i] = ((IloIntVar[]) xL[unSelectedPartitionMapping[insideNode.getPartition()]].get(insideNode.getIdxInPartition()))[outsideNci.getColor()];
                }
                cplex.add(cplex.ifThen(cplex.ge(cplex.sum(xVars), 1), cplex.eq(za[zIdx], 1)));
                zIdx++;
            }

            //add contraints 1: only one node-color-pair selected
            for (int p = 0; p < xL.length; p++) {
                IloLinearIntExpr expr = cplex.linearIntExpr();
                for (int v = 0; v < xL[p].size(); v++) {
                    IloIntVar[] xpv = (IloIntVar[]) xL[p].get(v);
                    for (int c = 0; c < xpv.length; c++) {
                        expr.addTerm(1, xpv[c]);
                    }
                }
//                logger.finest("CONSTRAINT 1, PARTITION " + p + ":\n" + expr);
                cplex.addEq(expr, 1);
            }


            if (coloringRestrition) {
                //add constraints 2: no two adjacent nodes may have the same color
                //select only edges that are between nodes represented by x:
                ArrayList<Integer[]> xEdges = new ArrayList<Integer[]>();
                for (Integer[] edge : cc.getGraph().getEdges()) {
                    Node n1 = cc.getGraph().getNode(edge[0]);
                    Node n2 = cc.getGraph().getNode(edge[1]);
                    if (unSelectedPartitionMapping[n1.getPartition()] != -1 && unSelectedPartitionMapping[n2.getPartition()] != -1) {
//                    logger.finest("CONSTRAINT 2: added edge: " + edge[0] + " to " + edge[1]);
                        xEdges.add(edge);
                    }
                }
                //set constraint:
                for (Integer[] edge : xEdges) {
                    Node n1 = cc.getGraph().getNode(edge[0]);
                    Node n2 = cc.getGraph().getNode(edge[1]);

                    int p1 = unSelectedPartitionMapping[n1.getPartition()];
                    int p2 = unSelectedPartitionMapping[n2.getPartition()];
                    int v1idx = n1.getIdxInPartition();
                    int v2idx = n2.getIdxInPartition();
                    IloIntVar[] v1 = (IloIntVar[]) xL[p1].get(v1idx);
                    IloIntVar[] v2 = (IloIntVar[]) xL[p2].get(v2idx);
                    for (int color = 0; color < v1.length; color++) {
                        IloLinearIntExpr expr = cplex.linearIntExpr();
                        expr.addTerm(1, v1[color]);
                        expr.addTerm(1, v2[color]);
                        cplex.addLe(expr, 1);
                    }
//                logger.finer("\nCONTSTRAINT 2 for edge (" + edge[0] + ", " + edge[1] + "): " + objectiveExpr);
                }
            }
            //solve, output and integrate solution into coloring
            if (cplex.solve()) {
//                logger.finest("Solution status = " + cplex.getStatus());
//                logger.fine("ILP Solution value  = " + cplex.getObjValue() + " conflicts.");
                result = (int) Math.round(cplex.getObjValue());
                for (int p = 0; p < xL.length; p++) {
                    List<IloIntVar[]> vList = xL[p];
//                    logger.fine("Partition " + p + ":");
                    for (int v = 0; v < vList.size(); v++) {
                        IloIntVar[] iiva = vList.get(v);
                        double[] val = cplex.getValues(iiva);
                        String valStr = "\tNode " + v + ": ";
                        for (int j = 0; j < val.length; ++j) {
                            valStr += val[j] + " ";
                            //integrate
                            if (Math.round(val[j]) == 1) {
                                int partition = xPartitionToRealPartition(p, unSelectedPartitionMapping);
                                Node n = cc.getGraph().getNodeOfPartition(partition, v);
                                NodeColorInfo nci = cc.getNciById(n.getId());
                                logger.finest("ILP: coloring node " + nci.getNode().getId() + " with color " + j);
                                cc.selectNci(nci);
                                cc.colorNci(nci, j);
                            }
                        }
//                        logger.finer(valStr);
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