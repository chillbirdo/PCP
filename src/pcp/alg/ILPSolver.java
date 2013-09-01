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

    public static void performOnUnselected(Coloring cc) {

        //find all unselected partitions and create a mapping partition_index -> index_in_m
        //find the size of m
        ArrayList<Integer> partitionMapping = new ArrayList<Integer>();
        int mSize = 0;
        for (int i = 0; i < cc.getGraph().getPartitionAmount(); i++) {
            if (!cc.isPartitionSelected(i)) {
                partitionMapping.add(i);
                int partitionSize = cc.getGraph().getPartitionSize(i);
//                mSize += partitionSize * cc.getChromatic();
            }
        }

        //create and fill m
        List[] mL = new List[partitionMapping.size()];
        for (int p = 0; p < mL.length; p++) {
            mL[p] = new ArrayList<ArrayList<Integer>>();
            int partition = partitionMapping.get(p);
            Node[] nodesInPartition = cc.getGraph().getNodesOfPartition(partition);
            for (int v = 0; v < nodesInPartition.length; v++) {
                Node n = nodesInPartition[v];
                NodeColorInfo nci = cc.getNciById(n.getId());
                mL[p].add(nci.getConflictArray());
            }
        }

        //solve ILP with m
        try {
            IloCplex cplex = new IloCplex();

            //initialize variables and objective expression
            List[] xL = new List[mL.length];
            IloLinearIntExpr objectiveExpr = cplex.linearIntExpr();
            for (int p = 0; p < xL.length; p++) {
                xL[p] = new ArrayList<IloIntVar[]>(mL[p].size());
                for (int v = 0; v < mL[p].size(); v++) {
                    
                    logger.severe("SCHAU : " + mL[p].get(v).getClass().getName());
                    
                    List conflictList = (List) mL[p].get(v);
                    
                    IloIntVar[] iiva = new IloIntVar[conflictList.size()];
                    for (int c = 0; c < iiva.length; c++) {
                        iiva[c] = cplex.boolVar();
                        objectiveExpr.addTerm((Integer) conflictList.get(c), iiva[c]);
                    }
                    mL[p].add(iiva);
                }
            }
            cplex.addMinimize(objectiveExpr);

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
            }

            //build constraints 2: no two adjacent nodes may have the same color
            for (Integer[] edge : cc.getGraph().getEdges()) {
                IloLinearIntExpr expr = cplex.linearIntExpr();
                Node n1 = cc.getGraph().getNode(edge[0]);
                Node n2 = cc.getGraph().getNode(edge[1]);
                int p1 = n1.getPartition();
                int v1 = n1.getIdxInPartition();
                int p2 = n2.getPartition();
                int v2 = n2.getIdxInPartition();
                IloIntVar[] c1 = (IloIntVar[]) xL[p1].get(v1);
                IloIntVar[] c2 = (IloIntVar[]) xL[p2].get(v2);
                for (int color = 0; color < c1.length; color++) {
                    expr.addTerm(1, c1[color]);
                    expr.addTerm(1, c2[color]);
                }
                cplex.addLe(expr, 1);
            }

            //solve and output
            if (cplex.solve()) {
                cplex.output().println("Solution status = " + cplex.getStatus());
                cplex.output().println("Solution value  = " + cplex.getObjValue());
                for (int p = 0; p < xL.length; p++) {
                    List<IloIntVar[]> vList = xL[p];
                    cplex.output().println("Partition " + p + ":");
                    for (int v = 0; v < vList.size(); v++) {
                        IloIntVar[] iiva = vList.get(v);
                        double[] val = cplex.getValues(iiva);
                        int ncols = cplex.getNcols();
                        String valStr = "\tNode " + v + ": ";
                        for (int j = 0; j < ncols; ++j) {
                            valStr += val[j] + " ";
                        }
                        cplex.output().println(valStr);
                    }
                }
            }
            cplex.end();
        } catch (IloException e) {
            System.err.println("Concert exception '" + e + "' caught");
        }
    }
}