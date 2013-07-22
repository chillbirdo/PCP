package pcp.alg;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import pcp.model.Coloring;
import pcp.model.Node;
import pcp.model.NodeColorInfo;

public class LocalSearch {

    private static final Logger logger = Logger.getLogger(LocalSearch.class.getName());
    private static final int MAX_ITERATIONS = 100000;

    public static boolean start(Coloring c) {
        LinkedList<NodeColorInfo> tabuNciList = new LinkedList<NodeColorInfo>();
        LinkedList<Integer> tabuColorList = new LinkedList<Integer>();
        int tabuSize = c.getChromatic() * 20;
        int iterations = 0;
        while (c.getConflictingNCIs().size() > 0 && iterations <= MAX_ITERATIONS) {
            //find node-color-pair with least resulting conflicts
            NodeColorInfo chosenNci = null;
            NodeColorInfo chosenConflictingNci = null;
            int chosenColor = 0;
            int minConflicts = Integer.MAX_VALUE;
            for (NodeColorInfo conflictingNci : c.getConflictingNCIs()) {
                for (Node nodeOfCluster : c.getGraph().getNodeInPartition()[conflictingNci.getNode().getPartition()]) {
                    NodeColorInfo nciOfCluster = c.getNciById(nodeOfCluster.getId());
                    for (int color = 0; color < c.getChromatic() && minConflicts > 0; color++) {
                        if (nciOfCluster == conflictingNci && color == conflictingNci.getColor()) {
                            continue;
                        }
                        //lookup tabulist
                        int idx = 0;
                        boolean tabu = false;
                        for (Iterator<NodeColorInfo> it = tabuNciList.iterator(); it.hasNext();) {
                            if (it.next() == nciOfCluster) {
                                if (tabuColorList.get(idx) == color) {
                                    tabu = true;
                                    break;
                                }
                            }
                            idx++;
                        }
                        if (tabu) {
                            continue;
                        }
                        //save pair with least resulting conflicts
                        if (nciOfCluster.getConflicts(color) < minConflicts) {
                            minConflicts = nciOfCluster.getConflicts(color);
                            chosenNci = nciOfCluster;
                            chosenConflictingNci = conflictingNci;
                            chosenColor = color;
                        }
                    }
                    if (minConflicts == 0) {
                        break;
                    }
                }
                if (minConflicts == 0) {
                    break;
                }
            }
            //add chosen node and color to tabulist
            tabuNciList.addFirst(chosenNci);
            tabuColorList.addFirst(chosenColor);
            if (tabuNciList.size() > tabuSize) {
                tabuNciList.removeLast();
                tabuColorList.removeLast();
            }
            //set chosen color to chosen node
            c.uncolorNci(chosenConflictingNci);
            c.getSelectedColoredNCIs().remove(chosenConflictingNci);
            if (chosenConflictingNci != chosenNci) {
                c.unselectNci(chosenConflictingNci);
                c.selectNci(chosenNci);
            }
            c.colorNci(chosenNci, chosenColor);
            //update set of conflicting nodes
            c.getConflictingNCIs().remove(chosenConflictingNci);
            if (minConflicts > 0) {
                Set resultingConflictNcis = c.getConflictingNeighboursOfNci(chosenNci, chosenNci.getConflicts(chosenColor));
                c.getConflictingNCIs().addAll(resultingConflictNcis);
            }
            iterations++;
            //logger.info("LOCALSEARCH: size of tabulist: " + tabuNciList.size() + " / " + tabuSize);
//            logger.info("LOCALSEARCH: New size of conflicting Nodes: " + c.getConflictingNCIs().size() + " " + c.getConflictingNCIs());
//            logger.info("LOCALSEARCH: " + c.getChromatic());
        }

        if (iterations < MAX_ITERATIONS) {
            logger.info("LOCALSEARCH: Found solution with chromatic: " + c.getChromatic());
            return true;
        }
        logger.info("LOCALSEARCH: Aborted because of too many iterations!");
        return false;
    }
}
