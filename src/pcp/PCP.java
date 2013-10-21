package pcp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import pcp.model.Graph;
import java.util.logging.Logger;
import pcp.alg.ILPSolverExact;
//import pcp.alg.Danger;
import pcp.alg.LocalSearch;
import pcp.alg.OneStepCD;
import pcp.alg.Recolorer;
import pcp.model.Coloring;
import pcp.instancereader.InstanceReader;
import pcp.tools.ColoringDoubleDouble;
import pcp.tools.LatexPrinter;
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

    public static void main(String[] args) {
        int[] algs = {RECOLOR_WITH_ILP, RECOLOR_WITH_ILP_NOCOLORINGCONSTRAINT, RECOLOR_WITH_ILP2, RECOLOR_WITH_ILP2_NOCOLORINGCONTRAINT};
//        int[] algs = {RECOLOR_WITH_RANDOM, RECOLOR_WITH_ONESTEPCD, RECOLOR_WITH_ILP, RECOLOR_WITH_ILP2};
        algComparisonTable1("pcp_instances/pcpn90p1/", algs, 10);
        algComparisonTable1("pcp_instances/pcpn90p2/", algs, 10);
        algComparisonTable1("pcp_instances/pcpn90p3/", algs, 10);
        algComparisonTable1("pcp_instances/pcpn90p4/", algs, 10);
        algComparisonTable1("pcp_instances/pcpn90p5/", algs, 10);
        algComparisonTable1("pcp_instances/pcpn90p6/", algs, 10);
        algComparisonTable1("pcp_instances/pcpn90p7/", algs, 10);
        algComparisonTable1("pcp_instances/pcpn90p8/", algs, 10);
        algComparisonTable1("pcp_instances/pcpn90p9/", algs, 10);
        algComparisonTable1("pcp_instances/pcp20/", algs, 10);
        algComparisonTable1("pcp_instances/pcp40/", algs, 10);
        algComparisonTable1("pcp_instances/pcp60/", algs, 10);
        algComparisonTable1("pcp_instances/pcp70/", algs, 10);
        algComparisonTable1("pcp_instances/pcp80/", algs, 10);
        algComparisonTable1("pcp_instances/pcp90/", algs, 10);
        algComparisonTable1("pcp_instances/pcp100/", algs, 10);
        algComparisonTable1("pcp_instances/pcp120/", algs, 10);


//        double[][] tabuIntervalsIn = {{0.25, 0.75},
//            {0.0, 1.0},
//            {0.0, 0.5},
//            {0.5, 1.0},
//            {0.25, 1.0},
//            {0.0, 0.75},
//            {1.0, 2.0},
//            {1.5, 2.5},
//            {2.0, 3.0},
//            {3.0, 5.0}};
//        tabuSizeTable("pcp_instances/in1/", tabuIntervalsIn, 5.0);
//        tabuSizeTable("pcp_instances/in2/", tabuIntervalsIn, 5.0);
//        tabuSizeTable("pcp_instances/in3/", tabuIntervalsIn, 5.0);
//        tabuSizeTable("pcp_instances/in4/", tabuIntervalsIn, 5.0);
    
    }

    public static void algComparisonTable1(String instSet, int[] algs, int rep) {
        int iterationsFactor = 10;

//        int[] algs = {RECOLOR_WITH_RANDOM};
        List<String> algLabelList = new ArrayList<String>(10);
        double[][] tabuIntervalsPCP = {//{{0.25, 0.75},
            {1.0, 2.5},
            {2.5, 5.0},
            {2.0, 4.0},
            {1.0, 5.0}};

        List<String> entryLabelList = new ArrayList<String>(100);
        List<List<List<Double>>> methodList = new ArrayList<List<List<Double>>>(10);
        for (int method : algs) {
            //fill methodLabels
            algLabelList.add(LatexPrinter.getAlgName(method));

            //fill entries
            List<List<Double>> entryList = new ArrayList<List<Double>>();
            for (double[] tabuInterval : tabuIntervalsPCP) {
                entryList.add(allFiles(instSet, method, rep, tabuInterval[0], tabuInterval[1], iterationsFactor));
            }
            methodList.add(entryList);
        }

        String[] pathSplit = instSet.split("/");
        String tableName = pathSplit[pathSplit.length-1];
        String tableStr = LatexPrinter.toLatexTableStr(tableName, algLabelList, tabuIntervalsPCP, methodList);
        logger.severe("\n\n" + tableStr);
    }

    public static void tabuSizeTable(String instSet, double[][] tabuIntervals, double iterationsFactor) {
        int rep = 10;

        int[] algs = {RECOLOR_WITH_RANDOM};
        List<String> algLabelList = new ArrayList<String>(10);

        List<String> entryLabelList = new ArrayList<String>(100);
        List<List<List<Double>>> methodList = new ArrayList<List<List<Double>>>(10);
        for (int method : algs) {
            //fill methodLabels
            algLabelList.add(LatexPrinter.getAlgName(method));

            //fill entries
            List<List<Double>> entryList = new ArrayList<List<Double>>();
            for (double[] tabuInterval : tabuIntervals) {
                entryList.add(allFiles(instSet, method, rep, tabuInterval[0], tabuInterval[1], iterationsFactor));
            }
            methodList.add(entryList);
        }

        String tableStr = LatexPrinter.toLatexTableStr(instSet, algLabelList, tabuIntervals, methodList);
        logger.severe("\n\n" + tableStr);
    }

//            entryList.add( allFiles("pcp_instances/pcpn90p1/", method, tabuSizeMin, tabuSizeMax, rep));
//            entryList.add( allFiles("pcp_instances/pcpn90p2/", method, tabuSizeMin, tabuSizeMax, rep));
//            entryList.add( allFiles("pcp_instances/pcpn90p4/", method, tabuSizeMin, tabuSizeMax, rep));
//            entryList.add( allFiles("pcp_instances/pcpn90p5/", method, tabuSizeMin, tabuSizeMax, rep));
//            entryList.add( allFiles("pcp_instances/pcpn90p6/", method, tabuSizeMin, tabuSizeMax, rep));
//            entryList.add( allFiles("pcp_instances/pcpn90p7/", method, tabuSizeMin, tabuSizeMax, rep));
//            entryList.add( allFiles("pcp_instances/pcpn90p8/", method, tabuSizeMin, tabuSizeMax, rep));
//            entryList.add( allFiles("pcp_instances/pcpn90p9/", method, tabuSizeMin, tabuSizeMax, rep));
    public static List<Double> allFiles(String path, int recolorAlg, int repetitions, double tabuSizeMinFactor, double tabuSizeMaxFactor, double iterationsFactor) {
//        logger.severe(path);

        List<Double> ret = new ArrayList<Double>(3);
        if (repetitions < 1) {
            repetitions = 1;
        }
        try {
            int minChromaticSum = Integer.MAX_VALUE;

            File folder = new File(path);
            File[] allfiles = folder.listFiles();
            List<File> fileList = Arrays.asList(allfiles);
            double meanInstSum = 0.0;
            double avgTimeInstSum = 0.0;
            double varianceInstSum = 0.0;

//            double conflictingNodesAmountSum = 0.0;
//            double recoloringSum = 0.0;

            Collections.sort(fileList);
            for (int f = 0; f < fileList.size(); f++) {
                File fileEntry = fileList.get(f);
                long timeMillisPerFile = System.currentTimeMillis();
                if (fileEntry.isFile()) {
                    int best = Integer.MAX_VALUE;
                    int worst = 0;
                    double repSum = 0.0;
                    double[] resultsOfRepetition = new double[repetitions];

//                    double conflictingNodesAmount = 0.0;
//                    double recoloringAmount = 0.0;
                    for (int i = 0; i < repetitions; i++) {
                        ColoringDoubleDouble cdp = optimized(fileEntry, recolorAlg, tabuSizeMinFactor, tabuSizeMaxFactor, iterationsFactor);
                        Coloring c = cdp.coloring;
                        int chromatic = c.getChromatic();
                        repSum += chromatic;
                        resultsOfRepetition[i] = chromatic;
//                        conflictingNodesAmount += cdp.conflictingNodes;
//                        recoloringAmount += cdp.recolorings;
                        if (chromatic < best) {
                            best = chromatic;
                        }
                        if (chromatic > worst) {
                            worst = chromatic;
                        }
                    }
                    double meanInst = repSum / repetitions;

                    double varianceInst = 0.0;
                    for (int i = 0; i < repetitions; i++) {
                        varianceInst += Math.pow((resultsOfRepetition[i] - meanInst), 2);
                    }
                    varianceInst = varianceInst / repetitions;

//                    conflictingNodesAmount = conflictingNodesAmount / repetitions;
//                    recoloringAmount = recoloringAmount / repetitions;
                    double timeAllReps = (double) (System.currentTimeMillis() - timeMillisPerFile) / 1000d;
                    double avgTimeInst = timeAllReps / repetitions;

//                    logger.severe(fileEntry.getName() + "\t\t" + avgTimePassedPerFile + "\t\t" + tabusizeMinFactor + "\t\t" + iterationsFactor + "\t\t" + best + "\t\t" + avg + "\t\t" + worst);

                    meanInstSum += meanInst;
                    varianceInstSum += varianceInst;
                    avgTimeInstSum += avgTimeInst;
//                    conflictingNodesAmountSum += conflictingNodesAmount;
//                    recoloringSum += recoloringAmount;
                }
            }

//            double conflictingNodesAmountAvg = conflictingNodesAmountSum / fileList.size();
//            double recoloringAvg = recoloringSum / fileList.size();
            double mean = meanInstSum / fileList.size();
            double variance = varianceInstSum / fileList.size();
            double avgTime = avgTimeInstSum / fileList.size();

//             logger.severe("\n FINISHED!\n" + "\\textbf{" + mean + "} & " + variance + " & " + avgTime);
//            logger.severe(conflictingNodesAmountAvg + " & " + recoloringAvg + "\n\n");

            ret.add(mean);
            ret.add(variance);
            ret.add(avgTime);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    private static ColoringDoubleDouble optimized(File instanceFile, int recolorAlg, double tabuSizeMinFactor, double tabuSizeMaxFactor, double iterationsFactor) {
        Graph g = null;
        Coloring c = null;
        try {
            g = InstanceReader.readInstance(instanceFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        c = OneStepCD.calcInitialColoring(g);

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
            int tabusizeMin = (int) Math.round(((double) c.getChromatic() - 1) * tabuSizeMinFactor);
            int tabusizeMax = (int) Math.round(((double) c.getChromatic() - 1) * tabuSizeMaxFactor);

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
                if (LocalSearch.start(cc, tabusizeMin, tabusizeMax, maxIterations)) {
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
        ColoringDoubleDouble cdp = new ColoringDoubleDouble(c, avgConflNodes, recolorings);
        return cdp;
    }

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
    /*
     * best parameters (nodes * colors * factor)
     * pcp: ts 0.04, it 5
     * in: ts 0.002, it 0.3
     * 
     * best parameters (edges * colors * factor)
     * pcp: ts 0.036, it 5
     * in: 
     */
//    public static void tabuSizeTest(String path) {
//        try {
//            int minChromaticSum = Integer.MAX_VALUE;
//            String minChromaticStr = "";
//            double iterationsFactor = 5;
//            File folder = new File(path);
//            int sumOverall = 0;
//            for (double tabuSizeFactor = 0.0001; tabuSizeFactor <= 0.06; tabuSizeFactor += 0.0001) {
////                for (double iterationsFactor = 1; iterationsFactor <= 10.1; iterationsFactor += 0.5) {
//                int chromaticSum = 0;
//                long timeMillisPerFolder = System.currentTimeMillis();
//                for (final File fileEntry : folder.listFiles()) {
//                    long timeMillisPerFile = System.currentTimeMillis();
//                    if (fileEntry.isFile()) {
//                        ColoringDoubleDouble cdp = optimized(fileEntry, tabuSizeFactor, iterationsFactor, RECOLOR_WITH_ONESTEPCD);
//                        Coloring c = cdp.coloring;
//                        int chromatic = c.getChromatic();
//                        double timePassedPerFile = (double) (System.currentTimeMillis() - timeMillisPerFile) / 1000d;
////                        logger.severe(fileEntry.getName() + "\t\t" + timePassedPerFile + "\t\t" + tabuSizeFactor + "\t\t" + iterationsFactor + "\t\t" + chromatic);
//                        chromaticSum += chromatic;
//                    }
//                }
//                sumOverall += chromaticSum;
//                double timePassedPerFolder = (double) (System.currentTimeMillis() - timeMillisPerFolder) / 1000d;
//                String outputStr = "--> SUM:\t\t" + timePassedPerFolder + "\t\t" + tabuSizeFactor + "\t\t" + iterationsFactor + "\t\t" + chromaticSum;
//                logger.info(outputStr);
//                if (chromaticSum < minChromaticSum) {
//                    minChromaticSum = chromaticSum;
//                    minChromaticStr = "\n BEST: " + outputStr;
//                }
////                }
//            }
//            logger.severe("\n FINISHED: " + path);
//            logger.severe(minChromaticStr);
//            logger.severe("Overallsum: " + sumOverall);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//    public static void initialColoringTest() {
//        try {
////            File folder = new File("pcp_instances/test/test1.pcp");
//            File file = new File("pcp_instances/pcp/n100p5t2s1.pcp");
//            Graph g = InstanceReader.readInstance(file.getAbsolutePath());
//            //OneStepCD.calcInitialColoring(g);
//            ColoringDanger d = Danger.calcInitialColoring(g);
//            ColoringTest.performAllDanger(d);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//    private static void testSelectorParameters() {
//        Graph g = null;
//        ColoringDanger c = null;
//
//        int bestresult = Integer.MAX_VALUE;
//        String bestresultStr = "";
//        for (double ks = 0.1; ks <= 2.0; ks += 0.1) {
//            for (double ku = 0.1; ku <= 2.0; ku += 0.1) {
//                int sumChromatic = 0;
//                try {
//                    File folder = new File("pcp_instances/pcp/");
//                    for (final File fileEntry : folder.listFiles()) {
//                        if (fileEntry.isFile()) {
//                            g = InstanceReader.readInstance(fileEntry.getAbsolutePath());
//                            c = Danger.calcInitialColoring(g);
//                            sumChromatic += c.getChromatic();
//                        }
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                String out = "--- result: " + sumChromatic + " ks=" + ks + "; ku=" + ku + ";";
//                if (sumChromatic < bestresult) {
//                    bestresult = sumChromatic;
//                    bestresultStr = out;
//                }
//                logger.severe(out);
//            }
//        }
//        logger.severe("\n\n best result: " + bestresultStr);
//    }
//    private static void testDangerVsOneStepCD() {
//
//        int bestresult = Integer.MAX_VALUE;
//        String bestresultStr = "";
//        int sumDanger = 0;
//        int sumOneStepCD = 0;
//        try {
//            File folder = new File("pcp_instances/in/");
//            for (final File fileEntry : folder.listFiles()) {
//                if (fileEntry.isFile()) {
//                    Graph g = InstanceReader.readInstance(fileEntry.getAbsolutePath());
//                    ColoringDanger cDanger = Danger.calcInitialColoring(g);
//                    Coloring cOneStepCD = OneStepCD.calcInitialColoring(g);
//                    sumDanger += cDanger.getChromatic();
//                    sumOneStepCD += cOneStepCD.getChromatic();
//
//                    logger.severe("--- DANGER: " + cDanger.getChromatic() + "; OneStepCD: " + cOneStepCD.getChromatic() + ";");
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        logger.severe("\n\n RESULT: DANGER: " + sumDanger + "; OneStepCD: " + sumOneStepCD + ";");
//    }
}
