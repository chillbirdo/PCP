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
     * with recolorAlg and add the resulting coloring to the list. sort the list ascendingly by
     * amount of conflicts
     */
    public static ArrayList<Coloring> recolorAllColors(final Coloring c, int recolorAlg) {
        ArrayList<Coloring> cL = new ArrayList<Coloring>(c.getChromatic());
        for (int color = 0; color < c.getChromatic(); color++) {
//            logger.info("RECOLORER: recoloring color " + color + " with " + ((recolorAlg == 0) ? "OnStepCD" : "ILP"));
            ColoringIF cc = new Coloring(c);
            NodeSelector.unselectAllNcisOfColor(cc, color);
            cc.reduceColor(color);
            
//            ColoringTest.performAll((Coloring)cc);
            
            int res = 0;
            if (recolorAlg == PCP.RECOLOR_WITH_ILP) {
                res = ILPSolver.performOnUnselected((Coloring) cc);
            } else if (recolorAlg == PCP.RECOLOR_WITH_ONESTEPCD) {
                res = OneStepCD.performOnUnselected((Coloring) cc);
            }
            cL.add((Coloring) cc);
            int conflictingNcis = cc.getConflictingNCIs().size();
            logger.info("RECOLORER: recolored color " + color + "  with " + ((recolorAlg == 0) ? "OnStepCD" : "ILP") + ", conflictingNcis: " + conflictingNcis);
            if( conflictingNcis == 0){
                logger.info("RECOLORER: found new soloution with chromatic " + cc.getChromatic());
                break;
            }
        }
        Collections.sort(cL);//sort list: fewest conflicts first
        return cL;
    }
}
