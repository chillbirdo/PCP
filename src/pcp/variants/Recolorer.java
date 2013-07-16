package pcp.variants;

import java.util.ArrayList;
import java.util.Iterator;
import pcp.alg.*;
import pcp.model.Coloring;
import pcp.model.NodeColorInfo;

/*
 * class providing different ways to recieve a recoloring of a specific color
 */
public class Recolorer {

    /*
     * perform a recoloring for a specific color
     */
    public static Coloring recolorWithGreedySelectorAndDanger(final Coloring c, int color) {
        Coloring cc = unselectAllNcisOfColor(c, color);
        NodeSelector.greedyMinDegree(cc, cc.getChromatic() - 1, null, null);
        DangerAlgorithm.applyColoring(cc, cc.getChromatic() - 1);
        return cc;
    }

    /*
     * perform a recoloring for every color and choose the one with least conflicts
     */
    public static Coloring recolorEveryColorWithGreedySelectorAndDanger(final Coloring c) {
        Coloring cWithFewestConflicts = null;
        int minConflicts = Integer.MAX_VALUE;
        for (int i = 0; i < c.getChromatic(); i++) {
            Coloring cc = recolorWithGreedySelectorAndDanger(c, i);
            if (cc.getConflictingNCIs().size() < minConflicts) {
                minConflicts = cc.getConflictingNCIs().size();
                cWithFewestConflicts = cc;
            }
        }
        return cWithFewestConflicts;
    }

    private static Coloring unselectAllNcisOfColor(final Coloring c, int color) {
        Coloring cc = new Coloring(c);
        for (Iterator<NodeColorInfo> it = cc.getSelectedColoredNCIs().iterator(); it.hasNext();) {
            NodeColorInfo nci = it.next();
            if (nci.getColor() == color) {
                cc.uncolorNci(nci);
                it.remove();
                cc.unselectNci(nci);
                //SPEEDUP: write method to unselect a colored node
            }
        }
        return cc;
    }
}
