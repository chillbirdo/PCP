package pcp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import pcp.model.Graph;
import java.util.logging.Logger;
import pcp.alg.Danger;
import pcp.alg.LocalSearch;
import pcp.alg.OneStepCD;
import pcp.alg.Recolorer;
import pcp.model.Coloring;
import pcp.instancereader.InstanceReader;
import pcp.model.ColoringDanger;
import test.pcp.coloring.ColoringTest;

public class PCP {

    private static final Logger logger = Logger.getLogger(PCP.class.getName());
    public static final int NODE_UNCOLORED = -1;
    public static final int NODE_UNSELECTED = -2;
    public static final int RECOLOR_WITH_ONESTEPCD = 0;
    public static final int RECOLOR_WITH_ILP = 1;

    public static void main(String[] args) {
//        allFiles( RECOLOR_WITH_ILP);
        File file = new File("pcp_instances/pcp/n100p5t2s1.pcp");
        //best factors:
        //for .pcp:
        double iterationsFactor = 10;
        double tabuSizeFactor = 0.04;
        //for .in:
        //double iterationsFactor = 0.3;
        //double tabuSizeFactor = 0.002;
        
//        int chromatic = optimized(file, tabuSizeFactor, iterationsFactor, RECOLOR_WITH_ILP);
        Coloring c = optimized(file, tabuSizeFactor, iterationsFactor, RECOLOR_WITH_ONESTEPCD);
        c.logSolution();
    }

    public static void allFiles(int recolorAlg) {
        try {
            int minChromaticSum = Integer.MAX_VALUE;
            //pcp
            double iterationsFactor = 10;
            double tabuSizeFactor = 0.04;
            //in
//            double iterationsFactor = 0.3;
//            double tabuSizeFactor = 0.002;

            int chromaticSum = 0;
            File folder = new File("pcp_instances/pcp/");
            File[] allfiles = folder.listFiles();
            List<File> al = Arrays.asList(allfiles);
            Collections.sort(al);
            for (final File fileEntry : al) {
                long timeMillisPerFile = System.currentTimeMillis();
                if (fileEntry.isFile()) {
                    Coloring c = optimized(fileEntry, tabuSizeFactor, iterationsFactor, RECOLOR_WITH_ILP);
                    int chromatic = c.getChromatic();
                    double timePassedPerFile = (double) (System.currentTimeMillis() - timeMillisPerFile) / 1000d;
                    logger.severe(fileEntry.getName() + "\t\t" + timePassedPerFile + "\t\t" + tabuSizeFactor + "\t\t" + iterationsFactor + "\t\t" + chromatic);
                    chromaticSum += chromatic;
                }
            }
            logger.severe("\n FINISHED! SUM: " + chromaticSum);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Coloring optimized(File instanceFile, double tabuSizeFactor, double iterationsFactor, int recolorAlg) {
        Graph g = null;
        Coloring c = null;
        try {
            g = InstanceReader.readInstance(instanceFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        c = OneStepCD.calcInitialColoring(g);
        boolean couldReduceColors;
        int maxIterations = (int) Math.round((double) c.getGraph().getNodes().length * (double) c.getChromatic() * iterationsFactor);
        int tabuSize = (int) Math.round((double) c.getGraph().getNodes().length * (double) c.getChromatic() * tabuSizeFactor);
//        int maxIterations = (int) Math.round((double) c.getGraph().getEdges() * (double) c.getChromatic() * iterationsFactor);
//        int tabuSize = (int) Math.round((double) c.getGraph().getEdges() * (double) c.getChromatic() * tabuSizeFactor);
//        int tabuSize = c.getChromatic() * 50;
//        logger.severe("tabuSize: " + tabuSize + "; maxIterations: " + maxIterations);
        do {
            couldReduceColors = false;
            ArrayList<Coloring> cL = Recolorer.recolorAllColors(c, recolorAlg);
            for (Coloring cc : cL) {
                if( cc.getConflictingNCIs().isEmpty()){
                    c = cc;
                    couldReduceColors = true;
                    break;
                }
                if (LocalSearch.start(cc, tabuSize, maxIterations)) {
//                    if (!ColoringTest.performAll(cc)) {
//                        logger.severe("TERMINATING: NOT ALL TESTS SUCCEDED!");
//                        return -1;
//                    }
                    if (!ColoringTest.testSolutionValidityNoConflicts(cc)) {
                        logger.severe("TERMINATING: SOLUTION IS NOT VALID!");
                        return null;
                    }
                    c = cc;
                    couldReduceColors = true;
                    break;
                }
            }
        } while (couldReduceColors);

        logger.info("ALORITHM TERMINATED for file " + instanceFile.getName() + ": best solution: " + c.getChromatic());

//        c.logColorStats();
//        ColoringTest.performAll(c);
        return c;
    }

    /*
     * best parameters (nodes * colors * factor)
     * pcp: ts 0.04, it 5
     * in: ts 0.002, it 0.3
     * 
     * best parameters (edges * colors * factor)
     * pcp: ts 0.0009, it 0.1
     * in: 
     */
    public static void tabuSizeTest() {
        try {
            int minChromaticSum = Integer.MAX_VALUE;
            String minChromaticStr = "";
            double iterationsFactor = 0.1;
            File folder = new File("pcp_instances/in/");
            int sumOverall = 0;
            for (double tabuSizeFactor = 0.0001; tabuSizeFactor <= 0.006; tabuSizeFactor += 0.0002) {
//                for (double iterationsFactor = 1; iterationsFactor <= 10.1; iterationsFactor += 0.5) {
                int chromaticSum = 0;
                long timeMillisPerFolder = System.currentTimeMillis();
                for (final File fileEntry : folder.listFiles()) {
                    long timeMillisPerFile = System.currentTimeMillis();
                    if (fileEntry.isFile()) {
                        Coloring c = optimized(fileEntry, tabuSizeFactor, iterationsFactor, RECOLOR_WITH_ONESTEPCD);
                        int chromatic = c.getChromatic();
                        double timePassedPerFile = (double) (System.currentTimeMillis() - timeMillisPerFile) / 1000d;
//                        logger.severe(fileEntry.getName() + "\t\t" + timePassedPerFile + "\t\t" + tabuSizeFactor + "\t\t" + iterationsFactor + "\t\t" + chromatic);
                        chromaticSum += chromatic;
                    }
                }
                sumOverall += chromaticSum;
                double timePassedPerFolder = (double) (System.currentTimeMillis() - timeMillisPerFolder) / 1000d;
                String outputStr = "--> SUM:\t\t" + timePassedPerFolder + "\t\t" + tabuSizeFactor + "\t\t" + iterationsFactor + "\t\t" + chromaticSum;
                logger.severe(outputStr);
                if (chromaticSum < minChromaticSum) {
                    minChromaticSum = chromaticSum;
                    minChromaticStr = "\n BEST: " + outputStr;
                }
//                }
            }
            logger.severe("\n FINISHED!");
            logger.severe(minChromaticStr);
            logger.severe("Overallsum: " + sumOverall);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void initialColoringTest() {
        try {
//            File folder = new File("pcp_instances/test/test1.pcp");
            File file = new File("pcp_instances/pcp/n100p5t2s1.pcp");
            Graph g = InstanceReader.readInstance(file.getAbsolutePath());
            //OneStepCD.calcInitialColoring(g);
            ColoringDanger d = Danger.calcInitialColoring(g);
            ColoringTest.performAllDanger(d);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void testSelectorParameters() {
        Graph g = null;
        ColoringDanger c = null;

        int bestresult = Integer.MAX_VALUE;
        String bestresultStr = "";
        for (double ks = 0.1; ks <= 2.0; ks += 0.1) {
            for (double ku = 0.1; ku <= 2.0; ku += 0.1) {
                int sumChromatic = 0;
                try {
                    File folder = new File("pcp_instances/pcp/");
                    for (final File fileEntry : folder.listFiles()) {
                        if (fileEntry.isFile()) {
                            g = InstanceReader.readInstance(fileEntry.getAbsolutePath());
                            c = Danger.calcInitialColoring(g);
                            sumChromatic += c.getChromatic();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String out = "--- result: " + sumChromatic + " ks=" + ks + "; ku=" + ku + ";";
                if (sumChromatic < bestresult) {
                    bestresult = sumChromatic;
                    bestresultStr = out;
                }
                logger.severe(out);
            }
        }
        logger.severe("\n\n best result: " + bestresultStr);
    }

    private static void testDangerVsOneStepCD() {

        int bestresult = Integer.MAX_VALUE;
        String bestresultStr = "";
        int sumDanger = 0;
        int sumOneStepCD = 0;
        try {
            File folder = new File("pcp_instances/in/");
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isFile()) {
                    Graph g = InstanceReader.readInstance(fileEntry.getAbsolutePath());
                    ColoringDanger cDanger = Danger.calcInitialColoring(g);
                    Coloring cOneStepCD = OneStepCD.calcInitialColoring(g);
                    sumDanger += cDanger.getChromatic();
                    sumOneStepCD += cOneStepCD.getChromatic();

                    logger.severe("--- DANGER: " + cDanger.getChromatic() + "; OneStepCD: " + cOneStepCD.getChromatic() + ";");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        logger.severe("\n\n RESULT: DANGER: " + sumDanger + "; OneStepCD: " + sumOneStepCD + ";");
    }
}
