package test.pcp.coloring;

import java.util.ArrayList;
import java.util.logging.Logger;
import pcp.PCP;
import pcp.coloring.Coloring;
import pcp.coloring.NodeColorInfo;
import pcp.model.Graph;
import pcp.model.Node;

/**
 *
 * @author gilbert
 */
public class ColoringTest {

    private static final Logger logger = Logger.getLogger(PCP.class.getName());
    private Coloring c;
    private Graph g;

    public ColoringTest(Coloring c, Graph g) {
        this.c = c;
        this.g = g;
    }

    /*
     * performs all tests
     */
    public void performAll() {
        testSolutionValidityNoConflicts();
        testSolutionValiditySelection();
        testCorrectConflictsValues();
        testCorrectCountingColorValues();
    }

    /*
     * Test if there are no two adjacent nodes colored with the same color
     */
    public void testSolutionValidityNoConflicts() {
        for (NodeColorInfo nci : c.getSelectedColoredNCIs()) {
            Node n = nci.getNode();
            int color = nci.getColor();
            for (Node neigh : n.getNeighbours()) {
                NodeColorInfo neighNci = c.getNciById(neigh.getId());
                int neighColor = neighNci.getColor();
                if (color == neighColor) {
                    logger.severe("TEST FAILED: Solution has unresolved conflicts!");
                }
            }
        }
        logger.severe("TEST SUCCEEDED: Solution is valid due to unresolved conflicts.");
    }

    /*
     * Tests if there is exactly one node per partition selected
     */
    public void testSolutionValiditySelection() {
        if (g.getPartitionSize().length != c.getSelectedColoredNCIs().size()) {
            logger.severe("TEST FAILED: Solution has an invalid selection or not all nodes are colored.");
            return;
        }
        boolean checkSelected[] = new boolean[c.getSelectedColoredNCIs().size()];
        for (int i = 0; i < checkSelected.length; i++) {
            checkSelected[i] = false;
        }
        for (NodeColorInfo nci : c.getSelectedColoredNCIs()) {
            Node n = nci.getNode();
            if (checkSelected[n.getPartition()]) {
                logger.severe("TEST FAILED: There are more than one Nodes selected in the same partition.");
            }
            checkSelected[n.getPartition()] = true;
        }
        logger.severe("TEST SUCCEEDED: There is exactly one node selected per partition.");
    }

    /*
     * Test if all colorvalues are correct
     */
    public void testCorrectConflictsValues() {
        for (Node n : g.getNodes()) {
            //init conflictarray and collect
            NodeColorInfo nci = c.getNciById(n.getId());

            int[] conflicts = new int[c.getChromatic()];
            for (int i = 0; i < conflicts.length; i++) {
                conflicts[i] = 0;
            }
            for (Node neigh : n.getNeighbours()) {
                NodeColorInfo neighNci = c.getNciById(neigh.getId());
                if (neighNci.getColor() >= 0) {
                    conflicts[neighNci.getColor()]++;
                }
            }
            //compare to colorarray held by the nci
            for (int i = 0; i < conflicts.length; i++) {
                if (conflicts[i] != nci.getConflicts(i)) {
                    logger.severe("TEST FAILED: Node " + n.getId() + " has wrong colorinfo for color " + i);
                    return;
                }
            }
        }
        logger.severe("TEST SUCCEEDED: Colorinfos are correct");
    }

    /*
     * Test if all counting colorvalues are correct
     */
    public void testCorrectCountingColorValues() {
//      logger.fine("\nTesting correct counting values");
        for (Node n : g.getNodes()) {
//            logger.fine("Node " + n.getId());
            NodeColorInfo nci = c.getNciById(n.getId());
            int colorsAvailable = 0;
            int colorsUnavailable = 0;
            for (int i = 0; i < c.getChromatic(); i++) {
                if (nci.isColorAvailable(i)) {
                    colorsAvailable++;
                } else if (nci.getConflicts(i) > 0) {
                    colorsUnavailable++;
                }
            }
            int uncoloredNeighbours = 0;
            int degreeToSelected = 0;
            for (Node neigh : n.getNeighbours()) {
                NodeColorInfo neighNci = c.getNciById(neigh.getId());
                if (neighNci.getColor() == PCP.NODE_UNCOLORED) {
                    uncoloredNeighbours++;
                }
                if (neighNci.getColor() != PCP.NODE_UNSELECTED) {
                    degreeToSelected++;
                }
            }
            //compare to values holded by nci
//            logger.fine("colorsAvailable:\t" + colorsAvailable + " vs " + nci.getColorsAvailable());
//            logger.fine("diffcolors:\t" + colorsUnavailable + " vs " + nci.getDiffColoredNeighbours());
//            logger.fine("uncoloredNeighs:\t" + uncoloredNeighbours + " vs " + nci.getUncoloredNeighbours());
//            logger.fine("degreeToSelected\t" + degreeToSelected + " vs " + nci.getDegreeToSelected());

            if (colorsAvailable != nci.getColorsAvailable() || colorsUnavailable != nci.getDiffColoredNeighbours()
                    || uncoloredNeighbours != nci.getUncoloredNeighbours() || degreeToSelected != nci.getDegreeToSelected()) {
                logger.severe("TEST FAILED: some colorvalues are not correct!");
                return;
            }
        }
        logger.severe("TEST SUCCEEDED: all counting color values are correct.");
    }
}