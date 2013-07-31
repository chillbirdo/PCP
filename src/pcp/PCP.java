package pcp;

import java.io.File;
import java.util.ArrayList;
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

    public static void main(String[] args) {
        allFiles();
//        testDangerVsOneStepCD();
//        initTest();
//        optimized(new File("pcp_instances/pcp/n20p5t2s3.pcp"));
    }

    public static void allFiles() {
        try {
            int minChromaticSum = Integer.MAX_VALUE;
            String minChromaticStr = "";
            File folder = new File("pcp_instances/pcp/");
            for (double tabuSizeFactor = 0.01; tabuSizeFactor <= 0.2; tabuSizeFactor += 0.01) {
                for (double iterationsFactor = 0.1; iterationsFactor <= 2.1; iterationsFactor *= 0.5) {
                    int chromaticSum = 0;
                    long timeMillisPerFolder = System.currentTimeMillis();
                    for (final File fileEntry : folder.listFiles()) {
                        long timeMillisPerFile = System.currentTimeMillis();
                        if (fileEntry.isFile()) {
                            int chromatic = optimized(fileEntry, tabuSizeFactor, iterationsFactor);
                            double timePassedPerFile = (double)(System.currentTimeMillis() - timeMillisPerFile)/1000d;
                            logger.severe(fileEntry.getName() + "\t\t" + timePassedPerFile + "\t\t" + tabuSizeFactor + "\t\t" + iterationsFactor + "\t\t" + chromatic);
                            chromaticSum += chromatic;
                        }
                    }
                    double timePassedPerFolder = (double)(System.currentTimeMillis() - timeMillisPerFolder)/1000d;
                    String outputStr = "--> SUM:\t\t" + timePassedPerFolder + "\t\t" + tabuSizeFactor + "\t\t" + iterationsFactor + "\t\t" + chromaticSum;
                    logger.severe(outputStr);
                    if (chromaticSum < minChromaticSum) {
                        minChromaticStr = "\n BEST: " + outputStr;
                    }
                }
            }
            logger.severe("\n FINISHED!");
            logger.severe(minChromaticStr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void initTest() {
        try {
//            File folder = new File("pcp_instances/test/test1.pcp");
            File folder = new File("pcp_instances/pcp/n100p5t2s1.pcp");
            Graph g = InstanceReader.readInstance(folder.getAbsolutePath());
            //OneStepCD.calcInitialColoring(g);
            ColoringDanger d = Danger.calcInitialColoring(g);
            ColoringTest.performAllDanger(d);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static int optimized(File instanceFile, double tabuSizeFactor, double iterationsFactor) {
        Graph g = null;
        Coloring c = null;
        try {
            g = InstanceReader.readInstance(instanceFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        c = OneStepCD.calcInitialColoring(g);
        boolean couldReduceColors;
        do {
            couldReduceColors = false;
            ArrayList<Coloring> cL = Recolorer.recolorAllColorsOneStepCD(c);
            for (Coloring cc : cL) {
                if (LocalSearch.start(cc, tabuSizeFactor, iterationsFactor)) {
                    if (!ColoringTest.performAll(cc)) {
                        logger.severe("TERMINATING: NOT ALL TESTS SUCCEDED!");
                        return -1;
                    }
                    c = cc;
                    couldReduceColors = true;
                    break;
                }
            }
        } while (couldReduceColors);

        logger.info("ALORITHM TERMINATED for file " + instanceFile.getName() + ": best solution: " + c.getChromatic());

        return c.getChromatic();
//        cc.logColorStats();
//        ColoringTest.performAll(cc);
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
