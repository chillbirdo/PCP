package pcp;

import java.io.File;
import java.util.ArrayList;
import pcp.model.Graph;
import java.util.logging.Logger;
import pcp.alg.Danger;
import pcp.alg.ILPSolverExact;
//import pcp.alg.Danger;
import pcp.alg.TabuSearch;
import pcp.alg.OneStepCD;
import pcp.alg.Recolorer;
import pcp.model.Coloring;
import pcp.instancereader.InstanceReader;
import pcp.model.ColoringDanger;
//import pcp.model.ColoringDanger;

public class PCP {

    private static final Logger logger = Logger.getLogger(PCP.class.getName());
    public static final int NODE_UNCOLORED = -1;
    public static final int NODE_UNSELECTED = -2;
    public static final int RECOLOR_WITH_ONESTEPCD = 0;
    public static final int RECOLOR_WITH_ILP = 1;
    public static final int RECOLOR_WITH_ILP_NOCOLORINGCONSTRAINT = 2;
    public static final int RECOLOR_WITH_ILP2 = 3;
    public static final int RECOLOR_WITH_ILP2_NOCOLORINGCONTRAINT = 4;
    public static final int RECOLOR_WITH_RANDOM = 5;

    public static void main(String[] a) {
        File inst = new File("pcp_instances/pcp/n20p5t2s1.pcp");
        Coloring solution = solve( inst, RECOLOR_WITH_ONESTEPCD, 0, 5, 10, 0);
    }

    private static Coloring solve(File instanceFile, int recolorAlg, double tabuSizeMinFactor, double tabuSizeMaxFactor, double iterationsFactor, double recoloredTabuSizeFactor) {
        Graph g = null;
        Coloring c = null;
        try {
            g = InstanceReader.readInstance(instanceFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        c = new Coloring(Danger.calcInitialColoringHybrid(g, 2.5, 1.0));

        boolean couldReduceColors;
        int maxIterations = (int) Math.round((double) c.getGraph().getPartitionAmount() * ((double) c.getChromatic() - 1) * iterationsFactor);
//        logger.severe("tabuSize: " + tabuSize + "; maxIterations: " + maxIterations);
        int recolorings = 1;
        double sumAvgConflNodes = 0.0;
        do {
//            if (!ColoringTest.performAll(c)) {
//                logger.severe("TERMINATING: NOT ALL TESTS SUCCEDED!");
//                return null;
//            }

            couldReduceColors = false;
            ArrayList<Coloring> cL = Recolorer.recolorAllColors(c, recolorAlg);
            recolorings += cL.size();
            //count avgConflictingNodes
            double avgConflictingNodes = 0;
            for (Coloring cc : cL) {
                avgConflictingNodes += cc.getConflictingNCIs().size();
            }
            sumAvgConflNodes += avgConflictingNodes;

            for (Coloring cc : cL) {
                if (cc.getConflictingNCIs().isEmpty()) {
                    c = cc;
                    couldReduceColors = true;
                    break;
                }
                if (TabuSearch.start(cc, tabuSizeMinFactor, tabuSizeMaxFactor, maxIterations, recoloredTabuSizeFactor)) {
                    c = cc;
                    couldReduceColors = true;
                    break;
                }
            }
        } while (couldReduceColors);

        double avgConflNodes = sumAvgConflNodes / recolorings;
//        logger.severe("Avg ConflictingNodes: " + avgConflNodes);
        logger.info("ALGORITHM TERMINATED for file " + instanceFile.getName() + ": best solution: " + c.getChromatic());

//        c.logColorStats();
//        ColoringTest.performAll(c);
        return c;
    }

    /*
     * solve the instance exact with a primitive ilp solver
     */
    private static Coloring solveExact(File instanceFile) {
        Graph g = null;
        Coloring c = null;
        try {
            g = InstanceReader.readInstance(instanceFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        c = OneStepCD.calcInitialColoring(g);
        c = ILPSolverExact.solve(g, c.getChromatic());

        return c;
    }

    private static void testDangerVsOneStepCD(String filepath) {

        String bestresultStr = "";
        double sumDanger = 0;
        double sumOneStepCD = 0;
        double sumHybrid = 0;
        double timeSumOneStepCD = 0;
        double timeSumHybrid = 0;
        try {
            File folder = new File(filepath);
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isFile()) {
                    Graph g = InstanceReader.readInstance(fileEntry.getAbsolutePath());

                    long starttime = System.currentTimeMillis();
                    ColoringDanger cDanger = Danger.calcInitialColoring(g, 2.5, 1.0);
                    long dangertime = System.currentTimeMillis() - starttime;

//                    starttime = System.currentTimeMillis();
//                    Coloring cOneStepCD = OneStepCD.calcInitialColoring(g);
//                    long oscdtime = System.currentTimeMillis() - starttime;
//
//                    starttime = System.currentTimeMillis();
//                    Coloring cHybrid = new Coloring(Danger.calcInitialColoringHybrid(g, 2.5, 1.0));
//                    long hybridtime = System.currentTimeMillis() - starttime;

                    sumDanger += cDanger.getChromatic();
//                    sumOneStepCD += cOneStepCD.getChromatic();
//                    sumHybrid += cHybrid.getChromatic();
//                    timeSumOneStepCD += oscdtime;
//                    timeSumHybrid += hybridtime;

                    logger.severe(fileEntry.getName() + " "
                            + "-- DANGER: " + cDanger.getChromatic() + "; " + dangertime + "; ");
//                            + "-- OneStepCD: " + cOneStepCD.getChromatic() + "; " + oscdtime + "; "
//                            + "-- Hybrid: " + cHybrid.getChromatic() + "; " + hybridtime + "; ");
                }
            }
            double foldersize = folder.listFiles().length;
            double sumdiff = sumOneStepCD - sumHybrid + 1;

            double avgDanger = sumDanger / foldersize;
            double avgOscd = sumOneStepCD / foldersize;
            double avgHybrid = sumHybrid / foldersize;
            double avgTimeOscd = timeSumOneStepCD / foldersize;
            double avgTimeHybrid = timeSumHybrid / foldersize;
            double avgTimeDanger = (avgTimeHybrid - avgTimeOscd) / sumdiff;
            logger.severe("\n\n RESULT: "
                    + "DANGER: " + avgDanger);
//                    + "OneStepCD: " + avgOscd + "; " + avgTimeOscd + " "
//                    + "Hybrid: " + avgHybrid + "; " + avgTimeHybrid + "; " + avgTimeDanger);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
