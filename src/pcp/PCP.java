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

    public static void allFiles() {
        try {

            File folder = new File("pcp_instances/pcp/");
            for (int tabuSizeFactor = 1; tabuSizeFactor <= 11; tabuSizeFactor += 1) {
                for (int iterations = 10000; iterations <= 100000; iterations *= 10) {
                    int chromaticSum = 0;
                    for (final File fileEntry : folder.listFiles()) {
                        if (fileEntry.isFile()) {
                            int chromatic = optimized(fileEntry, tabuSizeFactor, iterations);
                            chromaticSum += chromatic;
                        }
                    }
                    logger.severe("tabuSiteFactor: " + tabuSizeFactor + " iterations: " + iterations + " sum: " + chromaticSum);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static int optimized(File instanceFile, int tabuSizeFactor, int iterations) {
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
                int tabuSize = cc.getChromatic() * tabuSizeFactor;
                if (LocalSearch.start(cc, tabuSize, iterations)) {
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
