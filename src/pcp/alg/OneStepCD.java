package pcp.alg;

import java.util.Set;
import java.util.logging.Logger;
import pcp.model.Coloring;
import pcp.model.Graph;
import pcp.model.Node;
import pcp.model.NodeColorInfo;
import pcp.model.NodeColorInfoIF;

public class OneStepCD {

    private static final Logger logger = Logger.getLogger(OneStepCD.class.getName());

    /*
     * this method selects and colors all unselected NCIs of Coloring c
     * returns number of conflicts
     */
    public static int performOnUnselected(Coloring c) {
        int conflicts = 0;
        while (c.getUnselectedNCIs().size() > 0) {
            Integer maxMinDegree = Integer.MIN_VALUE;
            NodeColorInfo maxMinDegreeNci = null;
            for (int p = 0; p < c.getGraph().getPartitionAmount(); p++) {
                if (c.isPartitionSelected(p)) {
                    continue;
                }
                Integer minDegree = Integer.MAX_VALUE;
                NodeColorInfo minDegreeNci = null;
                for( int i = 0; i < c.getGraph().getPartitionSize(p); i++){
                    Node n = c.getGraph().getNodeOfPartition(p, i);
                    NodeColorInfo nci = c.getNciById(n.getId());
                    nci.getDiffColoredNeighbours();
                    if (nci.getDiffColoredNeighbours() < minDegree) {
                        minDegree = nci.getDiffColoredNeighbours();
                        minDegreeNci = nci;
                    }
                }
                if (minDegreeNci.getDiffColoredNeighbours() > maxMinDegree) {
                    maxMinDegree = minDegreeNci.getDiffColoredNeighbours();
                    maxMinDegreeNci = minDegreeNci;
                }
            }
            c.selectNci(maxMinDegreeNci);
            //select color with minimal conflicts
            int minConflicts = Integer.MAX_VALUE;
            int chosenColor = 0;
            for (int color = 0; color < c.getChromatic(); color++) {
                if (maxMinDegreeNci.isColorAvailable(color)) {
                    chosenColor = color;
                    minConflicts = 0;
                    break;
                } else if (maxMinDegreeNci.getConflicts(color) < minConflicts) {
                    minConflicts = maxMinDegreeNci.getConflicts(color);
                    chosenColor = color;
                }
            }
            c.colorNci(maxMinDegreeNci, chosenColor);
            if (maxMinDegreeNci.getConflicts(chosenColor) > 0) {
                conflicts += maxMinDegreeNci.getConflicts(chosenColor);
                Set<NodeColorInfoIF> conflictingNcis = c.getConflictingNeighboursOfNci(maxMinDegreeNci, maxMinDegreeNci.getConflicts(chosenColor));
                c.getConflictingNCIs().addAll(conflictingNcis);
            }
        }
        return conflicts;
    }

    public static Coloring calcInitialColoring(Graph g) {
        logger.info("Calculating initial solution with OneStepCD..");
        Coloring c = new Coloring(g);
        c.initColorArrayOfEachNci(g.getHighestDegree() + 1);
        OneStepCD.performOnUnselected(c);

        Coloring c2 = new Coloring(g);
        c2.initColorArrayOfEachNci(getColorsUsed(c));
        OneStepCD.performOnUnselected(c2);

        logger.info("Initial Solution OneStepCD: " + c2.getChromatic() + " colors, " + c2.getConflictingNCIs().size() + " conflicting.");
        return c2;
    }

    /*
     * returns how many different colors are used by coloring c
     */
    private static int getColorsUsed(Coloring c) {
        boolean[] colorUsed = new boolean[c.getChromatic()];

        for (int i = 0; i < colorUsed.length; i++) {
            colorUsed[i] = false;
        }
        for (NodeColorInfoIF nci : c.getSelectedColoredNCIs()) {
            colorUsed[nci.getColor()] = true;
        }
        int count = 0;
        for (int i = 0; i < colorUsed.length; i++) {
            if (colorUsed[i]) {
                count++;
            }
        }
        return count;
    }
}