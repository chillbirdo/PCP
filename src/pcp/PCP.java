package pcp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    public static void main(String[] a) {
//        testSelectorParameters();
        testDangerVsOneStepCD("pcp_instances/pcp");
//        testDangerVsOneStepCD("pcp_instances/pcp20");
//        testDangerVsOneStepCD("pcp_instances/pcp40");
//        testDangerVsOneStepCD("pcp_instances/pcp60");
//        testDangerVsOneStepCD("pcp_instances/pcp70");
//        testDangerVsOneStepCD("pcp_instances/pcp80");
//        testDangerVsOneStepCD("pcp_instances/pcp90");
//        testDangerVsOneStepCD("pcp_instances/pcp100");
//        testDangerVsOneStepCD("pcp_instances/pcp120");
//        
//        testDangerVsOneStepCD("pcp_instances/pcpn90p1");
//        testDangerVsOneStepCD("pcp_instances/pcpn90p2");
//        testDangerVsOneStepCD("pcp_instances/pcpn90p3");
//        testDangerVsOneStepCD("pcp_instances/pcpn90p4");
//        testDangerVsOneStepCD("pcp_instances/pcpn90p5");
//        testDangerVsOneStepCD("pcp_instances/pcpn90p6");
//        testDangerVsOneStepCD("pcp_instances/pcpn90p7");
//        testDangerVsOneStepCD("pcp_instances/pcpn90p8");
//        testDangerVsOneStepCD("pcp_instances/pcpn90p9");
    }

//    public static void main(String[] args) {
////        int[] algs = {RECOLOR_WITH_ILP, RECOLOR_WITH_ILP_NOCOLORINGCONSTRAINT, RECOLOR_WITH_ILP2, RECOLOR_WITH_ILP2_NOCOLORINGCONTRAINT};
//        int[] algs = {RECOLOR_WITH_RANDOM, RECOLOR_WITH_ONESTEPCD, RECOLOR_WITH_ILP, RECOLOR_WITH_ILP2};
////        int[] algs = {RECOLOR_WITH_RANDOM};
////        int[] algs = {RECOLOR_WITH_ILP, RECOLOR_WITH_ILP2};
//        double recoloredTabuSize = 0;
//        double[][] tabuIntervalsPCP = {{0, 0.5},
//            {0.5, 1},
//            {1, 4},
//            {0, 5},
//            {5, 10},
//            {10, 20}};
//        int[] iterationFactorsPCP = {1, 10, 20, 50};
//        int repPCP = 5;
//
//        algComparisonTable1("pcp_instances/pcpn90p1/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcpn90p2/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcpn90p3/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcpn90p4/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcpn90p5/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcpn90p6/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcpn90p7/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcpn90p8/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcpn90p9/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//
//        algComparisonTable1("pcp_instances/pcp20/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcp40/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcp60/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcp70/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcp80/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcp90i5/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcp100/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//        algComparisonTable1("pcp_instances/pcp120/", algs, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
//
//
////        logger.severe("EXACT ONES:");
////        
////         //EXACT TESTS
////        int[] algsEXACT = {RECOLOR_WITH_ILP, RECOLOR_WITH_ILP_NOCOLORINGCONSTRAINT, RECOLOR_WITH_ILP2, RECOLOR_WITH_ILP2_NOCOLORINGCONTRAINT};
////        algComparisonTable1("pcp_instances/pcpn90p7/", algsEXACT, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
////        algComparisonTable1("pcp_instances/pcpn90p8/", algsEXACT, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
////        algComparisonTable1("pcp_instances/pcpn90p9/", algsEXACT, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
////        algComparisonTable1("pcp_instances/pcp90i5/", algsEXACT, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
////        algComparisonTable1("pcp_instances/pcp100/", algsEXACT, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
////        algComparisonTable1("pcp_instances/pcp120/", algsEXACT, repPCP, tabuIntervalsPCP, iterationFactorsPCP);
////
////        logger.severe("RECOLOR ON TABULIST:");
////        
////        
//////    RECOLORED TABULIST TESTS
////        int[] algsNoRandom = {RECOLOR_WITH_ONESTEPCD, RECOLOR_WITH_ILP, RECOLOR_WITH_ILP2};
////        double[] recoloredTabuSizeFactors = {0.0, 0.3, 0.5, 1.0, 2.0, 5.0, 10.0};
////        algComparisonTable2("pcp_instances/pcpn90p5/", algsNoRandom, repPCP, tabuIntervalsPCP, recoloredTabuSizeFactors);
////        algComparisonTable2("pcp_instances/pcpn90p7/", algsNoRandom, repPCP, tabuIntervalsPCP, recoloredTabuSizeFactors);
////        algComparisonTable2("pcp_instances/pcpn90p9/", algsNoRandom, repPCP, tabuIntervalsPCP, recoloredTabuSizeFactors);
////        algComparisonTable2("pcp_instances/pcp90i5/", algsNoRandom, repPCP, tabuIntervalsPCP, recoloredTabuSizeFactors);
////        algComparisonTable2("pcp_instances/pcp100/", algsNoRandom, repPCP, tabuIntervalsPCP, recoloredTabuSizeFactors);
////        algComparisonTable2("pcp_instances/pcp120/", algsNoRandom, repPCP, tabuIntervalsPCP, recoloredTabuSizeFactors);
////    IN !!!
//    logger.severe (
//    "--- !!! IN !!! ---");
//        double[][] tabuIntervalsIN = {
//        {0.25, 0.75},
//        {0.0, 1},
//        {0.0, 0.5},
//        {0.5, 1.0},
//        {0.25, 1},
//        {0.0, 0.75}
//    };
//    int[] iterationFactorsIN = {10};
//    int repIN = 1;
//    int[] algsIn = {RECOLOR_WITH_ILP};
//
////        algComparisonTable1("pcp_instances/in1/", algsIn, repIN, tabuIntervalsIN, iterationFactorsIN);
////    algComparisonTable1("pcp_instances/in2/", algsIn, repIN, tabuIntervalsIN, iterationFactorsIN);
////        algComparisonTable1("pcp_instances/in3/", algsIn, repIN, tabuIntervalsIN, iterationFactorsIN);
////        algComparisonTable1("pcp_instances/in4/", algsIn, repIN, tabuIntervalsIN, iterationFactorsIN);
//    }
    public static void algComparisonTable1(String instSet, int[] algs, int rep, double[][] tabuIntervals, int[] iterationFactors) {

//        int[] algs = {RECOLOR_WITH_RANDOM};
        List<String> algLabelList = new ArrayList<String>(10);
        List<String> entryLabelList = new ArrayList<String>(100);
        List<List<List<Double>>> methodList = new ArrayList<List<List<Double>>>(10);
        for (int method : algs) {
            //fill methodLabels
            algLabelList.add(LatexPrinter.getAlgName(method));

            //fill entries
            List<List<Double>> entryList = new ArrayList<List<Double>>();
            for (int iterationFactor : iterationFactors) {
                logger.severe("iterationFactor: " + iterationFactor);
                for (double[] tabuInterval : tabuIntervals) {
//                    logger.severe("\ttabuInterval: " + tabuInterval[0] + " - " + tabuInterval[1]);
                    entryList.add(allFiles(instSet, method, rep, tabuInterval[0], tabuInterval[1], iterationFactor, 0.0));
                }
            }
            methodList.add(entryList);
        }

        String[] pathSplit = instSet.split("/");
        String tableName = pathSplit[pathSplit.length - 1];
        String tableStr = LatexPrinter.toLatexTableStr(tableName, algLabelList, tabuIntervals, iterationFactors, methodList);
        logger.severe("\n\n" + tableStr);
    }

    public static void algComparisonTable2(String instSet, int[] algs, int rep, double[][] tabuIntervals, double[] recoloredTabuSizeFactors) {

        int iterationFactor = 5;

//        int[] algs = {RECOLOR_WITH_RANDOM};
        List<String> algLabelList = new ArrayList<String>(10);
        List<String> entryLabelList = new ArrayList<String>(100);
        List<List<List<Double>>> methodList = new ArrayList<List<List<Double>>>(10);
        for (int method : algs) {
            //fill methodLabels
            algLabelList.add(LatexPrinter.getAlgName(method));

            //fill entries
            List<List<Double>> entryList = new ArrayList<List<Double>>();
            for (double recoloredTabuSizeFactor : recoloredTabuSizeFactors) {
                logger.severe("iterationFactor: " + iterationFactor);
                for (double[] tabuInterval : tabuIntervals) {
                    logger.severe("\ttabuInterval: " + tabuInterval[0] + " - " + tabuInterval[1]);
                    entryList.add(allFiles(instSet, method, rep, tabuInterval[0], tabuInterval[1], iterationFactor, recoloredTabuSizeFactor));
                }
            }
            methodList.add(entryList);
        }

        String[] pathSplit = instSet.split("/");
        String tableName = pathSplit[pathSplit.length - 1];
        String tableStr = LatexPrinter.toLatexTable2Str(tableName, algLabelList, tabuIntervals, methodList, recoloredTabuSizeFactors);
        logger.severe("\n\n" + tableStr);
    }

    public static List<Double> allFiles(String path, int recolorAlg, int repetitions, double tabuSizeMinFactor, double tabuSizeMaxFactor, double iterationsFactor, double recoloredTabuSizeFactor) {
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
                        ColoringDoubleDouble cdp = optimized(fileEntry, recolorAlg, tabuSizeMinFactor, tabuSizeMaxFactor, iterationsFactor, recoloredTabuSizeFactor);
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

//            logger.severe("\n FINISHED!\n" + "\\textbf{" + mean + "} & " + variance + " & " + avgTime);
//            logger.severe(conflictingNodesAmountAvg + " & " + recoloringAvg + "\n\n");

            ret.add(mean);
            ret.add(variance);
            ret.add(avgTime);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    private static ColoringDoubleDouble optimized(File instanceFile, int recolorAlg, double tabuSizeMinFactor, double tabuSizeMaxFactor, double iterationsFactor, double recoloredTabuSizeFactor) {
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
                if (TabuSearch.start2(cc, tabuSizeMinFactor, tabuSizeMaxFactor, maxIterations, recoloredTabuSizeFactor)) {
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
//     

    /*
     * best params pcp/in: ks 2.5, ku 1.0
     */
    private static void testSelectorParameters() {
        Graph g = null;
        ColoringDanger c = null;

        int bestresult = Integer.MAX_VALUE;
        String bestresultStr = "";
        for (double ks = 0.2; ks <= 10; ks += 0.2) {
//            for (double ku = 0.1; ku <= 2.0; ku += 0.1) {
            int sumChromatic = 0;
            try {
                File folder = new File("pcp_instances/in2/");
                for (final File fileEntry : folder.listFiles()) {
                    if (fileEntry.isFile()) {
                        g = InstanceReader.readInstance(fileEntry.getAbsolutePath());
                        c = Danger.calcInitialColoring(g, ks, 1);
                        sumChromatic += c.getChromatic();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String out = "--- result: " + sumChromatic + " ks=" + ks + "; ku=" + 1.0 + ";";
            if (sumChromatic < bestresult) {
                bestresult = sumChromatic;
                bestresultStr = out;
            }
            logger.severe(out);
//            }
        }
        logger.severe("\n\n best result: " + bestresultStr);
    }

    private static void testDangerVsOneStepCD(String filepath) {

        int bestresult = Integer.MAX_VALUE;
        String bestresultStr = "";
        int sumDanger = 0;
        int sumOneStepCD = 0;
        int sumHybrid = 0;
        try {
            File folder = new File(filepath);
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isFile()) {
                    Graph g = InstanceReader.readInstance(fileEntry.getAbsolutePath());

                    long starttime = System.currentTimeMillis();
                    ColoringDanger cDanger = Danger.calcInitialColoring(g, 2.5, 1.0);
                    long dangertime = System.currentTimeMillis() - starttime;

                    starttime = System.currentTimeMillis();
                    Coloring cOneStepCD = OneStepCD.calcInitialColoring(g);
                    long oscdtime = System.currentTimeMillis() - starttime;

                    starttime = System.currentTimeMillis();
                    Coloring cHybrid = new Coloring(Danger.calcInitialColoringHybrid(g, 2.5, 1.0));
                    long hybridtime = System.currentTimeMillis() - starttime;

                    sumDanger += cDanger.getChromatic();
                    sumOneStepCD += cOneStepCD.getChromatic();

                    logger.severe(fileEntry.getName() + " -- DANGER: " + cDanger.getChromatic() + "; " + dangertime + "; "
                            + "-- OneStepCD: " + cOneStepCD.getChromatic() + "; " + oscdtime + "; "
                            + "-- Hybrid: " + cHybrid.getChromatic() + "; " + hybridtime + ";");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        logger.severe("\n\n RESULT: DANGER: " + sumDanger + "; OneStepCD: " + sumOneStepCD + ";");
    }
}
