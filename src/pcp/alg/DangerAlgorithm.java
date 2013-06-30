package pcp.alg;

import java.util.Collection;
import pcp.coloring.Coloring;
import pcp.coloring.NodeColorInfo;
import pcp.model.Graph;
import pcp.model.Node;

public class DangerAlgorithm {

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

    public static Coloring applyColoring(Graph g, int maxColors) {
        Coloring coloring = new Coloring(g, maxColors);
        for (int i = 0; i < coloring.getUncoloredNodeColorInfos().size(); i++) {
            NodeColorInfo nci = selectMostDangerousNci(coloring.getUncoloredNodeColorInfos(), maxColors);
            int c = selectColorForNci(nci, maxColors, coloring);
            coloring.colorNodeColorInfo(nci, c);
        }
        return coloring;
    }

    private static NodeColorInfo selectMostDangerousNci(Collection<NodeColorInfo> uncoloredNciSet, int maxColors) {
        double maxND = 0;
        NodeColorInfo chosenNci = null;
        for (NodeColorInfo uncoloredNci : uncoloredNciSet) {
            double F = C / Math.pow(maxColors - uncoloredNci.getDiffColored(maxColors), k);
            double nD = F + ku * uncoloredNci.getUncolored() + ka * (uncoloredNci.getColorsShared() / uncoloredNci.getColorsAvailable());
            if (nD > maxND) {
                chosenNci = uncoloredNci;
            }
        }
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
        double minNC = Double.MAX_VALUE;
        for (int c = 0; c < maxColors; c++) {
            int maxDiffColored = 0;
            NodeColorInfo maxDiffColoredNci = null;
            for (NodeColorInfo uncoloredNci : coloring.getUncoloredNodeColorInfos()) {
                if (uncoloredNci.isColorAvailable(c)) {
                    if (uncoloredNci.getDiffColored(maxColors) > maxDiffColored) {
                        maxDiffColored = uncoloredNci.getDiffColored(maxColors);
                        maxDiffColoredNci = uncoloredNci;
                    }
                }
            }
            int timesUsed = 0;
            for (NodeColorInfo coloredNci : coloring.getColoredNodeColorInfos()) {
                if (coloredNci.getColor() == c) {
                    timesUsed++;
                }
            }
            double nC = k1/Math.pow(maxColors-maxDiffColored, k2) + k3*maxDiffColoredNci.getUncolored() + k4*timesUsed;
            if( nC < minNC){
                chosenColor = c;
            }
        }
        return chosenColor;
    }
}
