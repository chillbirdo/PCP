package pcp.alg;

import java.util.Collection;
import java.util.logging.Logger;
import pcp.coloring.Coloring;
import pcp.coloring.NodeColorInfo;
import pcp.model.Graph;

public class DangerAlgorithm {

    private static final Logger logger = Logger.getLogger(DangerAlgorithm.class.getName());
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

    public static boolean applyColoring( Coloring coloring, int maxColors) {
        logger.finer("Applying DANGER with maxColors:" + maxColors);
        while( coloring.getSelectedUncoloredNCIs().size() > 0){
            NodeColorInfo nci = selectMostDangerousNci(coloring.getSelectedUncoloredNCIs(), maxColors);
            if( nci == null){
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

    private static NodeColorInfo selectMostDangerousNci(Collection<NodeColorInfo> uncoloredNciSet, int maxColors) {
        double maxND = 0;
        NodeColorInfo chosenNci = null;
        for (NodeColorInfo uncoloredNci : uncoloredNciSet) {
            if( uncoloredNci.getColorsAvailable() == 0){
                logger.severe( "Unable to color the selection within " + maxColors + " colors.");
                return null;
            }
            //double F = C / Math.pow(maxColors - uncoloredNci.getDiffColoredNeighbours(maxColors), k);
            double F = C / (maxColors - uncoloredNci.getDiffColoredNeighbours());
            double nD = F + ku * uncoloredNci.getUncoloredNeighbours();// + ka * (uncoloredNci.getColorsShared() / uncoloredNci.getColorsAvailable());
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
    private static int selectColorForNci(NodeColorInfo nci, int maxColors, Coloring coloring) {
        int chosenColor = -1;
        int highestColor = 0;
        double minNC = Double.MAX_VALUE;
        for (int c = 0; c < maxColors; c++) {
            if (nci.isColorUnavailable(c)) {
                continue;
            }
            int maxDiffColored = -1;
            NodeColorInfo maxDiffColoredNci = null;
            for (NodeColorInfo uncoloredNci : coloring.getSelectedUncoloredNCIs()) {
                if (!uncoloredNci.isColorUnavailable(c)) {
                    if (uncoloredNci.getDiffColoredNeighbours() > maxDiffColored) {
                        maxDiffColored = uncoloredNci.getDiffColoredNeighbours();
                        maxDiffColoredNci = uncoloredNci;
                    }
                }
            }
            int timesUsed = 0;
            for (NodeColorInfo coloredNci : coloring.getSelectedColoredNCIs()) {
                if (coloredNci.getColor() == c) {
                    timesUsed++;
                }
            }
            double nC = k1 / Math.pow(maxColors - maxDiffColored, k2) + k3 * maxDiffColoredNci.getUncoloredNeighbours() + k4 * timesUsed;
            if (nC < minNC) {
                minNC = nC;
                chosenColor = c;
            }
            logger.finest("\tcolor " + c + ": " + nC);
        }
        return chosenColor;
    }
}
