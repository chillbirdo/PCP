package pcp.alg;

import java.util.logging.Logger;
import pcp.model.Coloring;

public class Recolorer {

    private static final Logger logger = Logger.getLogger(Coloring.class.getName());

    /*
     * for every color c: deselect each nci with color c, recolor
     * with OneStepCD and take the recoloring with fewest conflicts
     * returns a new coloring, potentially with conflicts
     */
    public static Coloring recolorAllColorsOneStepCD(final Coloring c) {
        int minConflicts = Integer.MAX_VALUE;
        Coloring minConflictsColoring = null;
        for (int color = 0; color < c.getChromatic(); color++) {
            Coloring cc = new Coloring(c);
            NodeSelector.unselectAllNcisOfColor(cc, color);
            cc.reduceColor(color);
            int res = OneStepCD.performOnUnselected(cc);
            logger.info("RECOLORING: color " + color + ", conflicts: " + res);
            if (res < minConflicts) {
                minConflicts = res;
                minConflictsColoring = cc;
                if(minConflicts == 0)
                    break;
            }
        }
        return minConflictsColoring;
    }
}
