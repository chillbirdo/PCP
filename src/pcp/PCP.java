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
import test.pcp.coloring.ColoringTest;

public class PCP {

    private static final Logger logger = Logger.getLogger(PCP.class.getName());
    public static final int NODE_UNCOLORED = -1;
    public static final int NODE_UNSELECTED = -2;

    public static void main(String[] args) {
        optimized();
//        testDangerVsOneStepCD();
    }

    private static void optimized() {
        //logger.getHandlers()[0].setFormatter( new BriefLogFormatter());

        Graph g = null;
        Coloring c = null;
        try {
            //g = InstanceReader.readInstance( "pcp_instances/test/test4.pcp");
//            g = InstanceReader.readInstance("pcp_instances/pcp/n20p5t2s1.pcp");
//            g = InstanceReader.readInstance("pcp_instances/pcp/n40p5t2s5.pcp");
            g = InstanceReader.readInstance("pcp_instances/in/dsjc500.5-1.in");
            //g = InstanceReader.readInstance("pcp_instances/pcp/n120p5t2s5.pcp");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        c = calcInitialColoringOneStepCD(g);
        //c = calcInitialColoringDanger(g, null, null);
//        ColoringTest.testCorrectSetContents(c);
        while (true) {
            Coloring cc = Recolorer.recolorAllColorsOneStepCD(c);
            LocalSearch.start(cc);
            if( !ColoringTest.performAll(cc)){
                logger.severe( "TERMINATING: NOT ALL TESTS SUCCEDED!");
            }
            c = cc;
        }

//        cc.logColorStats();
//        ColoringTest.performAll(cc);
    }

    private static Coloring calcInitialColoringDanger(Graph g, Double ks, Double ku) {
        logger.info("Calculating initial solution..");
        Coloring c = null;
        boolean succeeded = false;
        c = new Coloring(g);
        NodeSelector.greedyMinDegree(c, ks, ku);

        int upperbound = c.getHighestDegreeSelected() + 1;
        int lowerbound = 0;
        Coloring stablecoloring = null;
        while (upperbound - lowerbound > 1) {
            int actual = lowerbound + Math.round((upperbound - lowerbound) / 2);
            logger.info("upper: " + upperbound + " lower: " + lowerbound + " actual: " + actual);

            c = new Coloring(g);
            NodeSelector.greedyMinDegree(c, ks, ku);
            c.initColorArrayOfEachNci(actual);
            succeeded = DangerAlgorithm.applyColoring(c, actual);
            if (succeeded) {
                upperbound = actual;
                stablecoloring = c;
                logger.info("\tFound solution with " + upperbound + " colors.");
            } else {
                lowerbound = actual;
            }
        }
        return stablecoloring;
    }

    private static Coloring calcInitialColoringOneStepCD(Graph g) {
        logger.info("Calculating initial solution..");
        Coloring c = null;
        int succeeded = 0;
        c = new Coloring(g);
        NodeSelector.greedyMinDegree(c, null, null);

        int upperbound = c.getHighestDegreeSelected() + 1;
        int lowerbound = 0;
        Coloring stablecoloring = null;
        while (upperbound - lowerbound > 1) {
            int actual = lowerbound + Math.round((upperbound - lowerbound) / 2);
            logger.fine("upper: " + upperbound + " lower: " + lowerbound + " actual: " + actual);

            c = new Coloring(g);
            c.initColorArrayOfEachNci(actual);
            succeeded = OneStepCD.performOnUnselected(c);
            if (succeeded == 0) {
                upperbound = actual;
                stablecoloring = c;
                logger.fine("\tFound solution with " + upperbound + " colors.");
            } else {
                lowerbound = actual;
            }
        }
        logger.info("Initial Solution OneStepCD: " + stablecoloring.getChromatic() + " colors.");
        return stablecoloring;
    }

    private static void testParameters() {
        Graph g = null;
        Coloring c = null;

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
                            c = calcInitialColoringDanger(g, ks, ku);
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
                    Coloring cDanger = calcInitialColoringDanger(g, null, null);
                    Coloring cOneStepCD = calcInitialColoringOneStepCD(g);
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
