package pcp;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import pcp.model.Graph;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcp.alg.DangerAlgorithm;
import pcp.alg.EasyToEliminateColorFinder;
import pcp.alg.LocalSearch;
import pcp.alg.NodeSelector;
import pcp.alg.OneStepCD;
import pcp.alg.Recolorer;
import pcp.model.Coloring;
import pcp.model.NodeColorInfo;
import pcp.instancereader.InstanceReader;
import pcp.model.ColoringDanger;
import test.pcp.coloring.ColoringTest;

public class PCP {

    private static final Logger logger = Logger.getLogger(PCP.class.getName());
    public static final int NODE_UNCOLORED = -1;
    public static final int NODE_UNSELECTED = -2;

    public static void main(String[] args) {
        allFiles();
        //optimized(new File("pcp_instances/pcp/n20p5t2s3.pcp"));
    }

    public static void allFiles() {
        try {
            File folder = new File("pcp_instances/pcp/");
            for (final File fileEntry : folder.listFiles()) {
                logger.severe(fileEntry.getName());
                if (fileEntry.isFile()) {
                    logger.severe(fileEntry.getName());
                    optimized(fileEntry);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void optimized(File instanceFile) {
        //logger.getHandlers()[0].setFormatter( new BriefLogFormatter());

        Graph g = null;
        Coloring c = null;
        try {
            g = InstanceReader.readInstance(instanceFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        c = OneStepCD.calcInitialColoring(g);
        //c = calcInitialColoringDanger(g, null, null);
//        ColoringTest.testCorrectSetContents(c);
        boolean couldReduceColors;
        do {
            couldReduceColors = false;
            ArrayList<Coloring> cL = Recolorer.recolorAllColorsOneStepCD(c);
            for (Coloring cc : cL) {
                if (LocalSearch.start(cc)) {
                    if (!ColoringTest.performAll(cc)) {
                        logger.severe("TERMINATING: NOT ALL TESTS SUCCEDED!");
                        return;
                    }
                    c = cc;
                    couldReduceColors = true;
                    break;
                }
            }
        } while (couldReduceColors);

        logger.severe("ALORITHM TERMINATED for file " + instanceFile.getName() + ": best solution: " + c.getChromatic());

//        cc.logColorStats();
//        ColoringTest.performAll(cc);
    }

    private static void testParameters() {
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
                            c = DangerAlgorithm.calcInitialColoring(g);
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
                    ColoringDanger cDanger = DangerAlgorithm.calcInitialColoring(g);
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
