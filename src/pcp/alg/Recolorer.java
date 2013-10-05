package pcp.alg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import pcp.PCP;
import pcp.model.Coloring;
import pcp.model.ColoringIF;
import pcp.model.Node;
import pcp.model.NodeColorInfo;
import pcp.model.NodeColorInfoIF;
import test.pcp.coloring.ColoringTest;

public class Recolorer {

    private static final Logger logger = Logger.getLogger(Recolorer.class.getName());

    public static int randomRecoloringPerformOnUnselected( Coloring c){
        int conflicts = 0;
        while (c.getUnselectedNCIs().size() > 0) {
//            randomNum = minimum + (int)(Math.random()*maximum); 
//            List l = c.getUnselectedNCIs().toArray();
            int unselectedNodeIdx = (int)(Math.random()*c.getUnselectedNCIs().size());
            NodeColorInfo nci = (NodeColorInfo)c.getUnselectedNCIs().toArray()[unselectedNodeIdx];
            int color = (int)(Math.random()*c.getChromatic());
            c.selectNci(nci);
            c.colorNci(nci, color);
        }
        return conflicts;
    }
    
    /*
     * for every color c: deselect each nci with color c, recolor
     * with recolorAlg and add the resulting coloring to the list. sort the list ascendingly by
     * amount of conflicts
     */
    public static ArrayList<Coloring> recolorAllColors(final Coloring c, int recolorAlg) {
        ArrayList<Coloring> cL = new ArrayList<Coloring>(c.getChromatic());
        for (int color = 0; color < c.getChromatic(); color++) {
            ColoringIF cc = new Coloring(c);
//            ColoringIF test = new Coloring(cc);

            NodeSelector.unselectAllNcisOfColor(cc, color);
//            NodeSelector.unselectAllNcisOfColor(test, color);

            cc.reduceColor(color);
//            test.reduceColor(color);

//            ColoringTest.performAll((Coloring)cc);

            int res = 0;
            if (recolorAlg == PCP.RECOLOR_WITH_ILP) {
                res = ILPSolver.performOnUnselected((Coloring) cc);
            }else if ( recolorAlg == PCP.RECOLOR_WITH_ILP2){
                res = ILPSolver2.performOnUnselected((Coloring) cc);
            } else if (recolorAlg == PCP.RECOLOR_WITH_ONESTEPCD) {
                res = OneStepCD.performOnUnselected((Coloring) cc);
            } else if (recolorAlg == PCP.RECOLOR_WITH_RANDOM) {
                res = Recolorer.randomRecoloringPerformOnUnselected((Coloring) cc);
            }

//            OneStepCD.performOnUnselected((Coloring) test);


            cL.add((Coloring) cc);
            int conflictingNcis = cc.getConflictingNCIs().size();
            logger.info("RECOLORER: recolored color " + color + "  with " + ((recolorAlg == 0) ? "OnStepCD" : "ILP") + ", conflictingNcis: " + conflictingNcis);

//            int conflictingNcisONESTEPCD = test.getConflictingNCIs().size();
//            logger.info("RECOLORER: recolored color " + color + "  with " + "OnStepCD" + ", conflictingNcis: " + conflictingNcisONESTEPCD);

//            if( conflictingNcis > conflictingNcisONESTEPCD){
//                logger.severe("ACHTUNG GEFAHR: " + conflictingNcis + " zu " + conflictingNcisONESTEPCD);
//            }
            
            if (conflictingNcis == 0) {
                logger.info("RECOLORER: found new soloution with chromatic " + cc.getChromatic());
                break;
            }
        }
        Collections.sort(cL);//sort list: fewest conflicts first
        return cL;
    }
}
