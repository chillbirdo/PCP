package pcp.alg;

import java.util.logging.Logger;
import pcp.model.Coloring;
import test.pcp.coloring.ColoringTest;

public class Recolorer {

    private static final Logger logger = Logger.getLogger(Recolorer.class.getName());

    /*
     * for every color c: deselect each nci with color c, recolor
     * with OneStepCD and take the recoloring with fewest conflicts
     * returns a new coloring, potentially with conflicts
     */
    public static Coloring recolorAllColorsOneStepCD(final Coloring c) {
        int minConflicts = Integer.MAX_VALUE;
        int minConflictsColor = 0;
        Coloring minConflictsColoring = null;
        for (int color = 0; color < c.getChromatic(); color++) {
            Coloring cc = new Coloring(c);
            NodeSelector.unselectAllNcisOfColor(cc, color);
//            ColoringTest.testCorrectConflictsValues(cc);
            cc.reduceColor(color);
//            ColoringTest.testCorrectConflictsValues(cc);
            int res = OneStepCD.performOnUnselected(cc);
//            ColoringTest.testCorrectConflictsValues(cc);
            logger.finer("RECOLORER_TEST: color " + color + ", conflicts: " + res + " conflictingNcis: " + cc.getConflictingNCIs().size());
            if (res < minConflicts) {
                minConflictsColor = color;
                minConflicts = res;
                minConflictsColoring = cc;
                if (minConflicts == 0) {
                    break;
                }
            }
        }
        logger.info("RECOLORER_RESULT: color " + minConflictsColor + ", conflicts: " + minConflicts + " conflictnodes: " + minConflictsColoring.getConflictingNCIs().size());
        return minConflictsColoring;
    }
}
