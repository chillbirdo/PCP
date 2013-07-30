package pcp.alg;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import pcp.model.Coloring;
import pcp.model.Node;
import pcp.model.NodeColorInfo;
import pcp.model.NodeColorInfoIF;

public class LocalSearch {

    private static final Logger logger = Logger.getLogger(LocalSearch.class.getName());
    private static final int MAX_ITERATIONS = 300000;

    public static boolean start(Coloring c, int tabuSize, int iterations) {
        logger.info("LOCALSEARCH: trying to eliminate " + c.getConflictingNCIs().size() + " conflicting nodes.");

        int[][] tabuData = new int[c.getGraph().getNodes().length][c.getChromatic()];
        for (int i = 0; i < tabuData.length; i++) {
            for (int j = 0; j < tabuData[0].length; j++) {
                tabuData[i][j] = 0;
            }
        }

        while (c.getConflictingNCIs().size() > 0 && iterations <= MAX_ITERATIONS) {
            //find node-color-pair with least resulting conflicts
            NodeColorInfoIF chosenNci = null;
            NodeColorInfoIF chosenConflictingNci = null;
            int chosenColor = 0;
            int minConflicts = Integer.MAX_VALUE;
            for (NodeColorInfoIF conflictingNci : c.getConflictingNCIs()) {
                for (Node nodeOfCluster : c.getGraph().getNodesOfPartition(conflictingNci.getNode().getPartition())) {
                    NodeColorInfo nciOfCluster = c.getNciById(nodeOfCluster.getId());
                    for (int color = 0; color < c.getChromatic() && minConflicts > 0; color++) {
                        if (nciOfCluster == conflictingNci && color == conflictingNci.getColor()) {
                            continue;
                        }
                        //lookup tabulist
                        boolean tabu = tabuData[nodeOfCluster.getId()][color] > iterations;
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
            if (chosenConflictingNci == null) {
                for (int i = 0; i < tabuData.length; i++) {
                    for (int j = 0; j < tabuData[0].length; j++) {
                        tabuData[i][j] = 0;
                    }
                }
                iterations = 0;
                tabuSize = tabuSize / 2;
                logger.info("LOCALSEARCH: all possibilities are on the tabu list. New tabusize: " + tabuSize);
                if (tabuSize < c.getChromatic()) {
                    return false;
                }
                continue;
            }

            //add chosen node and color to tabulist
            tabuData[chosenNci.getNode().getId()][chosenColor] = iterations + tabuSize;

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
        }

        if (iterations < MAX_ITERATIONS) {
            logger.info("LOCALSEARCH: Found solution with chromatic: " + c.getChromatic());
            return true;
        }
        logger.info("LOCALSEARCH: Aborted because of too many iterations! " + iterations);
        return false;
    }
}
