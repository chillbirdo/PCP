package pcp.alg;

import pcp.model.Coloring;
import pcp.model.Graph;
import pcp.model.Node;
import pcp.model.NodeColorInfo;

public class OneStepCD {

    /*
     * this method selects and colors all unselected NCIs of Coloring c
     * returns number of conflicts
     */
    public static int performOnUnselected(Coloring c) {
        int conflicts = 0;
        while (c.getUnselectedNCIs().size() > 0) {
            Integer maxMinDegree = Integer.MIN_VALUE;
            NodeColorInfo maxMinDegreeNci = null;
            for (int p = 0; p < c.getGraph().getNodeInPartition().length; p++) {
                if (c.isPartitionSelected(p)) {
                    continue;
                }
                Integer minDegree = Integer.MAX_VALUE;
                NodeColorInfo minDegreeNci = null;
                for (Node n : c.getGraph().getNodeInPartition()[p]) {
                    NodeColorInfo nci = c.getNciById(n.getId());
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
            conflicts += maxMinDegreeNci.getConflicts(chosenColor);
            if(maxMinDegreeNci.getConflicts(chosenColor) != 0){
                c.getConflictingNCIs().add(maxMinDegreeNci);
            }
        }
        return conflicts;
    }
}