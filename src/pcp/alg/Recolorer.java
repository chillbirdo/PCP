package pcp.alg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import pcp.PCP;
import pcp.model.Coloring;
import pcp.model.ColoringIF;
import test.pcp.coloring.ColoringTest;

public class Recolorer {

    private static final Logger logger = Logger.getLogger(Recolorer.class.getName());

    /*
     * for every color c: deselect each nci with color c, recolor
     * with OneStepCD and take the recoloring with fewest conflicts
     * returns a new coloring, potentially with conflicts
     */
    public static ArrayList<Coloring> recolorAllColorsOneStepCD(final Coloring c, int recolorAlg) {
        ArrayList<Coloring> cL = new ArrayList<Coloring>(c.getChromatic());
        for (int color = 0; color < c.getChromatic(); color++) {
            ColoringIF cc = new Coloring(c);
            NodeSelector.unselectAllNcisOfColor(cc, color);
            cc.reduceColor(color);

            int res = 0;
            if (recolorAlg == PCP.RECOLOR_WITH_ILP) {
                res = ILPSolver.performOnUnselected((Coloring) cc);
            } else if (recolorAlg == PCP.RECOLOR_WITH_ONESTEPCD) {
                res = OneStepCD.performOnUnselected((Coloring) cc);
            }
            cL.add((Coloring) cc);
            logger.finer("RECOLORER_TEST: color " + color + ", conflicts: " + res + " conflictingNcis: " + cc.getConflictingNCIs().size());
        }
        Collections.sort(cL);
        return cL;
    }
}
