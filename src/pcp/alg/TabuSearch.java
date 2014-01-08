package pcp.alg;

import java.util.Set;
import java.util.logging.Logger;
import pcp.model.Coloring;
import pcp.model.Node;
import pcp.model.NodeColorInfo;
import pcp.model.NodeColorInfoIF;

public class TabuSearch {

    private static final Logger logger = Logger.getLogger(TabuSearch.class.getName());

    public static boolean start(Coloring c, double tabuSizeMinFactor, double tabuSizeMaxFactor, double maxIterations, double recoloredTabuSizeFactor) {
//        logger.severe("Tabusize: " + tabuSize);
        logger.info("LOCALSEARCH: trying to eliminate " + c.getConflictingNCIs().size() + " conflicting nodes.");

        int tabuSizeMin = (int) Math.round(((double) c.getChromatic() - 1) * tabuSizeMinFactor);
        int tabuSizeMax = (int) Math.round(((double) c.getChromatic() - 1) * tabuSizeMaxFactor);

        int[][] tabuData = new int[c.getGraph().getNodes().length][c.getChromatic()];
        for (int i = 0; i < tabuData.length; i++) {
            for (int j = 0; j < tabuData[0].length; j++) {
                tabuData[i][j] = 0;
            }
        }

        //put the most recently recolored cluster-set on the tabulist
        if (recoloredTabuSizeFactor > 0) {
            int recoloredTabuSize = (int) Math.round(((double) c.getChromatic() - 1) * recoloredTabuSizeFactor);
            for (NodeColorInfoIF nci : c.getRecentlyRecoloredNCIs()) {
                int id = nci.getNode().getId();
                for (int color = 0; color < c.getChromatic(); color++) {
                    tabuData[id][color] = recoloredTabuSize;
                }
            }
        }
        int iterations = 0;
        int tabuSizeTooLong = 0;
        while (c.getConflictingNCIs().size() > 0 && iterations <= maxIterations) {
            //find node-color-pair with fewest resulting conflicts
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
                        //save pair with least resulting conflicts
                        if (nciOfCluster.getConflicts(color) == 0 || (!tabu && nciOfCluster.getConflicts(color) < minConflicts)) {
                            minConflicts = nciOfCluster.getConflicts(color);
                            chosenConflictingNci = conflictingNci;
                            chosenNci = nciOfCluster;
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
            //if all possibilities are on the tabu list
            if (chosenConflictingNci == null) {
                logger.info("TABUSEARCH: all possibilities are on the tabu list. IT IS TOO LONG! (" + tabuSizeTooLong + ")");
                iterations++;
                tabuSizeTooLong++;
                continue;
            }

            //add chosen node and color to tabulist
            int randomTabuSize = (int) Math.round(Math.random() * (tabuSizeMax - tabuSizeMin)) + tabuSizeMin;
            tabuData[chosenNci.getNode().getId()][chosenColor] = iterations + randomTabuSize;


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

        if (iterations < maxIterations) {
            logger.info("LOCALSEARCH: Found solution with chromatic: " + c.getChromatic());
            return true;
        }
        if (tabuSizeTooLong > 0) {
            logger.info("LOCALSEARCH: Aborted because of too many iterations! tabuSizeTooLong: " + tabuSizeTooLong);
        }
        return false;
    }

    public static boolean start2(Coloring c, double tabuSizeMinFactor, double tabuSizeMaxFactor, double maxIterations, double recoloredTabuSizeFactor) {
//        logger.severe("Tabusize: " + tabuSize);
        logger.info("LOCALSEARCH: trying to eliminate " + c.getConflictingNCIs().size() + " conflicting nodes.");

        int tabuSizeMin = (int) Math.round(((double) c.getChromatic() - 1) * tabuSizeMinFactor);
        int tabuSizeMax = (int) Math.round(((double) c.getChromatic() - 1) * tabuSizeMaxFactor);

        int[][] tabuData = new int[c.getGraph().getNodes().length][c.getChromatic()];
        for (int i = 0; i < tabuData.length; i++) {
            for (int j = 0; j < tabuData[0].length; j++) {
                tabuData[i][j] = 0;
            }
        }

        //put the most recently recolored cluster-set on the tabulist
        if (recoloredTabuSizeFactor > 0) {
            int recoloredTabuSize = (int) Math.round(((double) c.getChromatic() - 1) * recoloredTabuSizeFactor);
            for (NodeColorInfoIF nci : c.getRecentlyRecoloredNCIs()) {
                int id = nci.getNode().getId();
                for (int color = 0; color < c.getChromatic(); color++) {
                    tabuData[id][color] = recoloredTabuSize;
                }
            }
        }
        int iterations = 0;
        int tabuSizeTooLong = 0;
        while (c.getConflictingNCIs().size() > 0 && iterations <= maxIterations) {
            //find node-color-pair with fewest resulting conflicts
            NodeColorInfoIF chosenNci = null;
            NodeColorInfoIF chosenConflictingNci = null;
            int chosenColor = 0;
            int bestFitness = Integer.MAX_VALUE;
            boolean aspiration = false;
            for (NodeColorInfoIF conflictingNci : c.getConflictingNCIs()) {
                for (Node nodeOfCluster : c.getGraph().getNodesOfPartition(conflictingNci.getNode().getPartition())) {
                    NodeColorInfo nciOfCluster = c.getNciById(nodeOfCluster.getId());
                    for (int color = 0; color < c.getChromatic() && bestFitness > 0; color++) {
                        if (nciOfCluster == conflictingNci && color == conflictingNci.getColor()) {
                            continue;
                        }
                        //lookup tabulist
                        boolean tabu = tabuData[nodeOfCluster.getId()][color] > iterations;
                        //save pair with least resulting conflicts
                        int fitness = nciOfCluster.getConflicts(color) - conflictingNci.getConflicts(conflictingNci.getColor());
                        aspiration = (c.getConflictingNCIs().size() == 1 && nciOfCluster.getConflicts(color) == 0);
                        if (aspiration || (!tabu && fitness < bestFitness)) {
                            bestFitness = fitness;
                            chosenConflictingNci = conflictingNci;
                            chosenNci = nciOfCluster;
                            chosenColor = color;
                        }
                        if (aspiration) {
                            break;
                        }
                    }
                    if (aspiration) {
                        break;
                    }
                }
                if (aspiration) {
                    break;
                }
            }
            //if all possibilities are on the tabu list
            if (chosenConflictingNci == null) {
                logger.info("TABUSEARCH: all possibilities are on the tabu list. IT IS TOO LONG! (" + tabuSizeTooLong + ")");
                iterations++;
                tabuSizeTooLong++;
                continue;
            }

            //add chosen node and color to tabulist
            int randomTabuSize = (int) Math.round(Math.random() * (tabuSizeMax - tabuSizeMin)) + tabuSizeMin;
            tabuData[chosenNci.getNode().getId()][chosenColor] = iterations + randomTabuSize;

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
            if (bestFitness > 0) {
                Set resultingConflictNcis = c.getConflictingNeighboursOfNci(chosenNci, chosenNci.getConflicts(chosenColor));
                c.getConflictingNCIs().addAll(resultingConflictNcis);
            }
            iterations++;
        }

        if (iterations < maxIterations) {
            logger.info("LOCALSEARCH: Found solution with chromatic: " + c.getChromatic());
            return true;
        }
        if (tabuSizeTooLong > 0) {
            logger.info("LOCALSEARCH: Aborted because of too many iterations! tabuSizeTooLong: " + tabuSizeTooLong);
        }
        return false;
    }
}
