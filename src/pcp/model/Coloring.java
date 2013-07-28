package pcp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import pcp.PCP;
import pcp.model.Graph;
import pcp.model.Node;

public class Coloring implements ColoringIF, Comparable<ColoringIF> {

    private static final Logger logger = Logger.getLogger(Coloring.class.getName());
    private Graph g;
    private NodeColorInfo[] nodeColorInfo;
    private int chromatic;
    private boolean[] isPartitonSelected;
    private Set<NodeColorInfoIF> unselectedNCIs;//set of unselected ncis **of unselected clusters**
    private Set<NodeColorInfoIF> selectedColoredNCIs;
    private Set<NodeColorInfoIF> selectedUncoloredNCIs;
    private Set<NodeColorInfoIF> conflictingNCIs;

    public Coloring(Graph g) {
        this.g = g;
        this.nodeColorInfo = new NodeColorInfo[g.getNodes().length];
        for (int i = 0; i < nodeColorInfo.length; i++) {
            Node n = g.getNode(i);
            nodeColorInfo[i] = new NodeColorInfo(n);
        }
        this.isPartitonSelected = new boolean[g.getNodeInPartition().length];
        for (int i = 0; i < isPartitonSelected.length; i++) {
            isPartitonSelected[i] = false;
        }
        this.selectedColoredNCIs = new HashSet<NodeColorInfoIF>();
        this.selectedUncoloredNCIs = new HashSet<NodeColorInfoIF>();
        this.unselectedNCIs = new HashSet<NodeColorInfoIF>(Arrays.asList(nodeColorInfo));
        this.conflictingNCIs = new HashSet<NodeColorInfoIF>();//ncis that should be recolored
    }

    /*
     * copy constructor
     */
    public Coloring(ColoringIF c) {
        this.g = c.getGraph();
        this.chromatic = c.getChromatic();
        this.nodeColorInfo = new NodeColorInfo[g.getNodes().length];
        for (int i = 0; i < g.getNodes().length; i++) {
            this.nodeColorInfo[i] = new NodeColorInfo(c.getNciById(i));
        }
        this.isPartitonSelected = new boolean[g.getNodeInPartition().length];
        for (int i = 0; i < isPartitonSelected.length; i++) {
            isPartitonSelected[i] = c.isPartitionSelected(i);
        }
        this.selectedColoredNCIs = new HashSet<NodeColorInfoIF>(c.getSelectedColoredNCIs().size());
        for (NodeColorInfoIF nciOrig : c.getSelectedColoredNCIs()) {
            NodeColorInfo nciCopy = nodeColorInfo[nciOrig.getNode().getId()];
            selectedColoredNCIs.add(nciCopy);
        }
        this.selectedUncoloredNCIs = new HashSet<NodeColorInfoIF>(c.getSelectedUncoloredNCIs().size());
        for (NodeColorInfoIF nciOrig : c.getSelectedUncoloredNCIs()) {
            NodeColorInfo nciCopy = nodeColorInfo[nciOrig.getNode().getId()];
            selectedUncoloredNCIs.add(nciCopy);
        }
        this.unselectedNCIs = new HashSet<NodeColorInfoIF>(c.getUnselectedNCIs().size());
        for (NodeColorInfoIF nciOrig : c.getUnselectedNCIs()) {
            NodeColorInfo nciCopy = nodeColorInfo[nciOrig.getNode().getId()];
            unselectedNCIs.add(nciCopy);
        }
        this.conflictingNCIs = new HashSet<NodeColorInfoIF>(c.getConflictingNCIs().size());
        for (NodeColorInfoIF nciOrig : c.getConflictingNCIs()) {
            NodeColorInfo nciCopy = nodeColorInfo[nciOrig.getNode().getId()];
            conflictingNCIs.add(nciCopy);
        }
    }

    
    public void initColorArrayOfEachNci(int maxColors) {
        chromatic = maxColors;
        for (NodeColorInfo nci : nodeColorInfo) {
            nci.initConflictsArray(maxColors);
        }
    }

    /*
     * select a node
     */
    public void selectNci(NodeColorInfoIF nci) {
        if (!nci.isSelected() && unselectedNCIs.contains(nci)) {
            nci.setColorUncolored();
            //remove all ncis of particular partition from unselectedNCIs
            int nciPartition = nci.getNode().getPartition();
            for (Node nodeOfPartition : g.getNodeInPartition()[nciPartition]) {
                unselectedNCIs.remove(getNciById(nodeOfPartition.getId()));
            }
            //add nci to selected
            selectedUncoloredNCIs.add(nci);
            //state that its partition has a seleted nci now
            this.isPartitonSelected[nci.getNode().getPartition()] = true;
        } else {
            logger.warning("UNEXPECTED: tried to select an already selected node. (node=" + nci.getNode().getId() + ", color=" + nci.getColor() + ")");
        }
    }

    /*
     * unselect a node
     */
    public void unselectNci(NodeColorInfoIF nci) {
        if (nci.getColor() == PCP.NODE_UNCOLORED && selectedUncoloredNCIs.contains(nci)) {
            nci.setColorUnselected();
            //add all ncis of particular partition to unselectedNCIs
            int nciPartition = nci.getNode().getPartition();
            for (Node nodeOfPartition : g.getNodeInPartition()[nciPartition]) {
                unselectedNCIs.add(getNciById(nodeOfPartition.getId()));
            }
            //remove nci from selected
            selectedUncoloredNCIs.remove(nci);
            //state that partition has no selected node now
            this.isPartitonSelected[nci.getNode().getPartition()] = false;
        } else {
            logger.severe("UNEXPECTED: tried to unselect either a colored or an already unselected node!  (node=" + nci.getNode().getId() + ", color=" + nci.getColor() + ")");
        }
    }

    /*
     * it is assumed that when a color is set, the node has been uncolored before
     */
    public void colorNci(NodeColorInfoIF nci, int color) {
        if (!selectedUncoloredNCIs.contains(nci)) {
            logger.severe("UNEXPECTED: tried to color a node that was not in set of uncolored!");
            return;
        }
        if (color == nci.getColor() || color == PCP.NODE_UNCOLORED) {
            logger.severe("UNEXPECTED: tried to color a node of color " + nci.getColor() + " to color " + color + "!");
            return;
        }

        this.selectedUncoloredNCIs.remove(nci);
        this.selectedColoredNCIs.add(nci);
        nci.setColor(color);
        for (Node neigh : nci.getNode().getNeighbours()) {
            NodeColorInfo neighNci = getNciById(neigh.getId());
            neighNci.increaseConflicts(color);
            if (neighNci.getConflicts(color) == 1) {
                neighNci.decreaseColorsAvailable();
            }
        }
    }

    /*
     * performs uncoloring
     * note: removing from the colored-nci-list has to be done manually.
     */
    public void uncolorNci(NodeColorInfoIF nci) {
        if (!selectedColoredNCIs.contains(nci)) {
            logger.severe("UNEXPECTED: tried to uncolor a node that was not in set of colored!");
            return;
        }
        this.selectedUncoloredNCIs.add(nci);

        int oldColor = nci.getColor();
        nci.setColor(PCP.NODE_UNCOLORED);

        for (Node neigh : nci.getNode().getNeighbours()) {
            NodeColorInfo neighNci = getNciById(neigh.getId());
            neighNci.decreaseConflicts(oldColor);
            if (neighNci.isColorAvailable(oldColor)) {
                neighNci.increaseColorsAvailable();
            }
        }
    }

    /*
     * prerequisite: all ncis with color col must be UNCOLORED!
     * all nci with color > col decrease their color by 1
     */
    public void reduceColor(int col) {
        for (NodeColorInfo nci : nodeColorInfo) {
            if (nci.getColor() == col) {
                logger.severe("Unexpected: tried to reduce color " + col + ", but nci " + nci.getNode().getId() + " is still colored with that color!");
                return;
            } else if (nci.getColor() > col) {
                nci.setColor(nci.getColor() - 1);
            }
            nci.decreaseColorsAvailable();
            nci.getConflictArray().remove(col);
        }
        chromatic--;
    }


    /*
     * returns a set of ncis that are in conflict with nci
     */
    public Set<NodeColorInfoIF> getConflictingNeighboursOfNci(NodeColorInfoIF nci, int conflictAmount) {
        Set<NodeColorInfoIF> al = new HashSet<NodeColorInfoIF>(conflictAmount);
        for (Node neigh : nci.getNode().getNeighbours()) {
            NodeColorInfo neighNci = getNciById(neigh.getId());
            if (neighNci.getColor() == nci.getColor()) {
                al.add(neighNci);
            }
        }
        return al;
    }

    public String toStringUncolored() {
        return toStringNciList(this.selectedUncoloredNCIs, "Uncolored Nodes");
    }

    public String toStringUnselected() {
        return toStringNciList(this.unselectedNCIs, "Unselected Nodes");
    }

    public String toStringColored() {
        return toStringNciList(this.selectedColoredNCIs, "Colored Nodes");
    }

    public String toString() {
        return "conflicting nodes: " + getConflictingNCIs().size();
    }

    private String toStringNciList(Collection<NodeColorInfoIF> nciCollection, String name) {
        String ret = "\n" + name + ": {\n";
        for (NodeColorInfoIF nci : nciCollection) {
            ret += "n" + nci.getNode().getId() + ": "
                    + "color=" + nci.getColor() + "; "
                    + "colors_available=" + nci.getColorsAvailable() + "; "
                    + "\n";
        }
        return ret + "}";
    }

    public void logColorStats() {
        int[] colorStats = new int[getChromatic()];
        for (int i = 0; i < colorStats.length; i++) {
            colorStats[i] = 0;
        }
        for (NodeColorInfo nci : nodeColorInfo) {
            if (nci.getColor() >= 0) {
                colorStats[nci.getColor()]++;

            }
        }
        logger.info("COLORSTATS:");
        logger.info("chromatic = " + getChromatic());
        for (int i = 0; i < colorStats.length; i++) {
            logger.info("color " + i + " = " + colorStats[i]);
        }
    }
    
    public Set<NodeColorInfoIF> getSelectedColoredNCIs() {
        return selectedColoredNCIs;
    }

    public Set<NodeColorInfoIF> getSelectedUncoloredNCIs() {
        return selectedUncoloredNCIs;
    }

    public Set<NodeColorInfoIF> getUnselectedNCIs() {
        return unselectedNCIs;
    }

    public Graph getGraph() {
        return g;
    }

    public NodeColorInfo getNciById(int id) {
        return this.nodeColorInfo[id];
    }

    public int getChromatic() {
        return chromatic;
    }

    public Set<NodeColorInfoIF> getConflictingNCIs() {
        return conflictingNCIs;
    }

    public boolean isPartitionSelected(int partition) {
        return this.isPartitonSelected[partition];
    }

    public int compareTo(ColoringIF c) {
        return this.getConflictingNCIs().size() - c.getConflictingNCIs().size();
    }

    public Set<NodeColorInfoIF> getConflictingNeighboursOfNci(NodeColorInfo nci, int conflictAmount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}