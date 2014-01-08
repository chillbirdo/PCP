package pcp.alg;

import java.util.Collection;
import java.util.logging.Logger;
import pcp.model.Coloring;
import pcp.model.ColoringDanger;
import pcp.model.NodeColorInfoDanger;
import pcp.model.Graph;
import pcp.model.NodeColorInfoIF;
import test.pcp.coloring.ColoringTest;

public class Danger {

    private static final Logger logger = Logger.getLogger(Danger.class.getName());
    //Node Danger
    private static final double C = 1.0;
    private static final double k = 1.0;
    private static final double ku = 0.025;
    private static final double ka = 0.33;
    //Color Danger
    private static final double k1 = 1.0;
    private static final double k2 = 1.0;
    private static final double k3 = 0.5;
    private static final double k4 = 0.025;

    public static boolean applyColoringDanger(ColoringDanger coloring, int maxColors) {
        logger.finer("Applying DANGER with maxColors:" + maxColors);
        while (coloring.getSelectedUncoloredNCIs().size() > 0) {
            NodeColorInfoDanger nci = selectMostDangerousNci(coloring.getSelectedUncoloredNCIs(), maxColors);
            if (nci == null) {
                return false;
            }
            logger.finer("\tDANGER Node selection: Node " + nci.getNode().getId());
            int c = selectColorForNci(nci, maxColors, coloring);
            logger.finer("\tDANGER Color selection: Color " + c);
            coloring.colorNci(nci, c);
            logger.finest(coloring.toString());
        }
        return true;
    }

    private static NodeColorInfoDanger selectMostDangerousNci(Collection uncoloredNciSet, int maxColors) {
        double maxND = 0;
        NodeColorInfoDanger chosenNci = null;
        for (NodeColorInfoDanger uncoloredNci : (Collection<NodeColorInfoDanger>)uncoloredNciSet) {
            if (uncoloredNci.getColorsAvailable() == 0 || maxColors - uncoloredNci.getDiffColoredNeighbours() == 0) {
                logger.fine("Unable to color the selection within " + maxColors + " colors.");
                return null;
            }
            //double F = C / Math.pow(maxColors - uncoloredNci.getDiffColoredNeighbours(maxColors), k);
            double F = C / (maxColors - uncoloredNci.getDiffColoredNeighbours());
            double nD = F + ku * uncoloredNci.getUncoloredNeighbours() + ka * (uncoloredNci.getColorsShared() / uncoloredNci.getColorsAvailable());
            if (nD > maxND) {
                maxND = nD;
                chosenNci = uncoloredNci;
            }
        }
        logger.finer("\tDANGER Node selection: maxNodeDanger = " + maxND);
        return chosenNci;
    }

    /*
     *  over all colors
     *   search each uncolored node that has color c available
     *   choose the node that has most different_colored
     *   search how often c is used
     */
    private static int selectColorForNci(NodeColorInfoDanger nci, int maxColors, ColoringDanger coloring) {
        int chosenColor = -1;
        int highestColor = 0;
        double minNC = Double.MAX_VALUE;
        for (int c = 0; c < maxColors; c++) {
            if (nci.isColorUnavailable(c)) {
                continue;
            }
            int maxDiffColored = -1;
            NodeColorInfoIF maxDiffColoredNci = null;
            for (NodeColorInfoIF uncoloredNci : coloring.getSelectedUncoloredNCIs()) {
                if (!uncoloredNci.isColorUnavailable(c)) {
                    if (uncoloredNci.getDiffColoredNeighbours() > maxDiffColored) {
                        maxDiffColored = uncoloredNci.getDiffColoredNeighbours();
                        maxDiffColoredNci = uncoloredNci;
                    }
                }
            }
            int timesUsed = 0;
            for (NodeColorInfoIF coloredNci : coloring.getSelectedColoredNCIs()) {
                if (coloredNci.getColor() == c) {
                    timesUsed++;
                }
            }
            ///*orig*/double nC = k1 / Math.pow(maxColors - maxDiffColored, k2) + k3 * maxDiffColoredNci.getUncoloredNeighbours() + k4 * timesUsed;
            double nC = k1 / (maxColors - maxDiffColored) + k3 * ((NodeColorInfoDanger)maxDiffColoredNci).getUncoloredNeighbours() + k4 * timesUsed;
            if (nC < minNC) {
                minNC = nC;
                chosenColor = c;
            }
            logger.finest("\tcolor " + c + ": " + nC);
        }
        return chosenColor;
    }
    
    
    public static ColoringDanger calcInitialColoringHybrid(Graph g, double ks, double ku) {
        logger.info("Calculating initial solution..");

        ColoringDanger c = new ColoringDanger(OneStepCD.calcInitialColoring(g));
        int chromatic = OneStepCD.calcInitialColoring(g).getChromatic();
        NodeSelector.greedyMinDegree(c, ks, ku);

        int count = 1;
        while( Danger.applyColoringDanger(c, chromatic)){
            chromatic--;
            count++;
        }

        return c;
    }
    
    
    /*
     * calculates initial solution
     */
    public static ColoringDanger calcInitialColoring(Graph g, double ks, double ku) {
        logger.info("Calculating initial solution..");
        ColoringDanger c = null;
        boolean succeeded = false;
        c = new ColoringDanger(g);
        NodeSelector.greedyMinDegree(c, ks, ku);

        int upperbound = c.getHighestDegreeSelected() + 1;
        int lowerbound = 0;
        ColoringDanger stablecoloring = null;
        while (upperbound - lowerbound > 1) {
            int actual = lowerbound + Math.round((upperbound - lowerbound) / 2);
            logger.fine("upper: " + upperbound + " lower: " + lowerbound + " actual: " + actual);

            c = new ColoringDanger(g);
            NodeSelector.greedyMinDegree(c, ks, ku);
            c.initColorArrayOfEachNci(actual);
            succeeded = Danger.applyColoringDanger(c, actual);
            if (succeeded) {
//                ColoringTest.performAllDanger(c);
                upperbound = actual;
                stablecoloring = c;
                logger.info("\tFound solution with " + actual + " colors.");
            } else {
                lowerbound = actual;
            }
        }
        return stablecoloring;
    }    
}
