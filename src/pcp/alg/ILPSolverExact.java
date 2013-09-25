package pcp.alg;

import ilog.concert.*;
import ilog.cplex.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pcp.model.Coloring;
import pcp.model.Graph;
import pcp.model.Node;
import pcp.model.NodeColorInfo;
import pcp.model.NodeColorInfoIF;

public class ILPSolverExact {

    private static final Logger logger = Logger.getLogger(ILPSolver2.class.getName());

    public static Coloring solve(Graph g, int maxColors) {

        Coloring coloring = new Coloring(g);
        coloring.initColorArrayOfEachNci(maxColors);

        //build and solve ILP with m
        try {
            IloCplex cplex = new IloCplex();
            cplex.setOut(null);

            //init z variables and objective
            IloIntVar[] za = new IloIntVar[maxColors];
            for (int i = 0; i < za.length; i++) {
                za[i] = cplex.boolVar();
            }
            cplex.addMinimize(cplex.sum(za));

            //initialize x variable datastructure
            List[] xL = new List[g.getPartitionAmount()];
            for (int p = 0; p < xL.length; p++) {
                int partitionSize = g.getPartitionSize(p);
                xL[p] = new ArrayList<IloIntVar[]>(partitionSize);
                for (int v = 0; v < partitionSize; v++) {
                    IloIntVar[] iiva = new IloIntVar[maxColors];
                    for (int c = 0; c < iiva.length; c++) {
                        iiva[c] = cplex.boolVar();
                    }
                    xL[p].add(iiva);
                }
            }


            for (int c = 0; c < maxColors; c++) {
                IloIntVar[] xVars = new IloIntVar[g.getNodes().length];
                for (int nId = 0; nId < g.getNodes().length; nId++) {
                    int nPartition = g.getNode(nId).getPartition();
                    int nIdxInPartition = g.getNode(nId).getIdxInPartition();
                    IloIntVar[] iiva = (IloIntVar[]) xL[nPartition].get(nIdxInPartition);
                    xVars[nId] = iiva[c];
                }
                cplex.add(cplex.ifThen(cplex.ge(cplex.sum(xVars), 1), cplex.eq(za[c], 1)));
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

            //add constraints 2: no two adjacent nodes may have the same color
            //select only edges that are between nodes represented by x:
            //set constraint:
            for (Integer[] edge : g.getEdges()) {
                Node n1 = g.getNode(edge[0]);
                Node n2 = g.getNode(edge[1]);

                int p1 = n1.getPartition();
                int p2 = n2.getPartition();
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


            //solve, output and integrate solution into coloring
            if (cplex.solve()) {
//                logger.finest("Solution status = " + cplex.getStatus());
                logger.severe("ILP Solution value  = " + cplex.getObjValue() + " conflicts.");
//                int result = (int) Math.round(cplex.getObjValue());

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
                                Node n = g.getNodeOfPartition(p, v);
                                NodeColorInfo nci = coloring.getNciById(n.getId());
                                logger.finest("ILP: coloring node " + nci.getNode().getId() + " with color " + j);
                                coloring.selectNci(nci);
                                coloring.colorNci(nci, j);
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

        return coloring;
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