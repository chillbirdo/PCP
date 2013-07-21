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

public class Coloring {
    
    private static final Logger logger = Logger.getLogger(Coloring.class.getName());
    private Graph g;
    private NodeColorInfo[] nodeColorInfo;
    private int chromatic;
    private boolean[] isPartitonSelected;
    private Set<NodeColorInfo> unselectedNCIs;//set of unselected ncis **of unselected clusters**
    private Set<NodeColorInfo> selectedColoredNCIs;
    private Set<NodeColorInfo> selectedUncoloredNCIs;
    private Set<NodeColorInfo> conflictingNCIs;
    
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
        this.selectedColoredNCIs = new HashSet<NodeColorInfo>();
        this.selectedUncoloredNCIs = new HashSet<NodeColorInfo>();
        this.unselectedNCIs = new HashSet<NodeColorInfo>(Arrays.asList(nodeColorInfo));
        this.conflictingNCIs = new HashSet<NodeColorInfo>();//ncis that should be recolored
    }

    /*
     * copy constructor
     */
    public Coloring(Coloring c) {
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
        this.selectedColoredNCIs = new HashSet<NodeColorInfo>(c.getSelectedColoredNCIs().size());
        for (NodeColorInfo nciOrig : c.getSelectedColoredNCIs()) {
            NodeColorInfo nciCopy = nodeColorInfo[nciOrig.getNode().getId()];
            selectedColoredNCIs.add(nciCopy);
        }
        this.selectedUncoloredNCIs = new HashSet<NodeColorInfo>(c.getSelectedUncoloredNCIs().size());
        for (NodeColorInfo nciOrig : c.getSelectedUncoloredNCIs()) {
            NodeColorInfo nciCopy = nodeColorInfo[nciOrig.getNode().getId()];
            selectedUncoloredNCIs.add(nciCopy);
        }
        this.unselectedNCIs = new HashSet<NodeColorInfo>(c.getUnselectedNCIs().size());
        for (NodeColorInfo nciOrig : c.getUnselectedNCIs()) {
            NodeColorInfo nciCopy = nodeColorInfo[nciOrig.getNode().getId()];
            unselectedNCIs.add(nciCopy);
        }
        this.conflictingNCIs = new HashSet<NodeColorInfo>(c.getConflictingNCIs().size());
        for (NodeColorInfo nciOrig : c.getConflictingNCIs()) {
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
    public void selectNci(NodeColorInfo nci) {
        if (!nci.isSelected() && unselectedNCIs.contains(nci)) {
            nci.setColorUncolored();
            //update neighbours
            for (Node neigh : nci.getNode().getNeighbours()) {
                NodeColorInfo neighNci = getNciById(neigh.getId());
                neighNci.increaseUncolored();
                neighNci.increaseDegreeToSelected();
            }
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
    public void unselectNci(NodeColorInfo nci) {
        if (nci.getColor() == PCP.NODE_UNCOLORED && selectedUncoloredNCIs.contains(nci)) {
            nci.setColorUnselected();
            //update neighbours
            for (Node neigh : nci.getNode().getNeighbours()) {
                NodeColorInfo neighNci = getNciById(neigh.getId());
                neighNci.decreaseUncolored();
                neighNci.decreaseDegreeToSelected();
            }
            //add all ncis of particular partition to unselectedNCIs
            int nciPartition = nci.getNode().getPartition();
            for (Node nodeOfPartition : g.getNodeInPartition()[nciPartition]) {
                unselectedNCIs.add(getNciById(nodeOfPartition.getId()));
            }
            //remove nci from selected
            selectedUncoloredNCIs.remove(nci);
            //state that is partition has no selected node now
            this.isPartitonSelected[nci.getNode().getPartition()] = false;
        } else {
            logger.severe("UNEXPECTED: tried to unselect either a colored or an already unselected node!  (node=" + nci.getNode().getId() + ", color=" + nci.getColor() + ")");
        }
    }

    /*
     * it is assumed that when a color is set, the node has been uncolored before
     */
    public void colorNci(NodeColorInfo nci, int color) {
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
            neighNci.decreaseUncolored();
            neighNci.increaseConflicts(color);
            if (neighNci.getConflicts(color) == 1) {
                neighNci.decreaseColorsAvailable();
//                if (neighNci.isColorShared(color)) {
//                    neighNci.setColorShared(color, false);
//                    neighNci.decreaseColorsShared();
//                }
//                //update shared state of second-level neighbours
//                for (Node neighOfNeigh : neigh.getNeighbours()) {
//                    NodeColorInfo neighOfNeighNci = getNciById(neighOfNeigh.getId());
//                    if (neighOfNeigh != nci.getNode()) {
//                        if (neighOfNeighNci.isColorShared(color)) {
//                            neighOfNeighNci.setColorShared(color, false);
//                            neighOfNeighNci.decreaseColorsShared();
//                        }
//                    }
//                }
            }
        }
    }

    /*
     * performs uncoloring
     * note: removing from the colored-nci-list has to be done manually.
     */
    public void uncolorNci(NodeColorInfo nci) {
        if (!selectedColoredNCIs.contains(nci)) {
            logger.severe("UNEXPECTED: tried to uncolor a node that was not in set of colored!");
            return;
        }
//        this.selectedColoredNCIs.remove(nci);
        this.selectedUncoloredNCIs.add(nci);
        
        int oldColor = nci.getColor();
        nci.setColor(PCP.NODE_UNCOLORED);
        
//        ArrayList<Node> neightsGoneAvailable = new ArrayList<Node>(nci.getNode().getNeighbours().length);
        for (Node neigh : nci.getNode().getNeighbours()) {
            NodeColorInfo neighNci = getNciById(neigh.getId());
            neighNci.increaseUncolored();
            neighNci.decreaseConflicts(oldColor);
            if (neighNci.isColorAvailable(oldColor)) {
                neighNci.increaseColorsAvailable();
//                neightsGoneAvailable.add(neigh);
            }
        }
//        //shared
//        for (Node neigh : neightsGoneAvailable) {
//            NodeColorInfo neighNci = getNciById(neigh.getId());
//            if (neighNci.isColorAvailable(oldColor)) {
//                boolean allSelectedNeighOfNeighHaveColorAvailable = true;
//                for (Node neighOfNeigh : neigh.getNeighbours()) {
//                    if (neighOfNeigh == nci.getNode()) {
//                        continue;
//                    }
//                    NodeColorInfo neighOfNeighNci = getNciById(neighOfNeigh.getId());
//                    if (neighOfNeighNci.isSelected() && neighOfNeighNci.isColorUnavailable(oldColor)) {
//                        allSelectedNeighOfNeighHaveColorAvailable = false;
//                        break;
//                    }
//                }
//                if (allSelectedNeighOfNeighHaveColorAvailable) {
//                    neighNci.setColorShared(oldColor, true);
//                    neighNci.increaseColorsShared();
//                }
//            }
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
    public Set<NodeColorInfo> getConflictingNeighboursOfNci(NodeColorInfo nci, int conflictAmount) {
        Set<NodeColorInfo> al = new HashSet<NodeColorInfo>(conflictAmount);
        for (Node neigh : nci.getNode().getNeighbours()) {
            NodeColorInfo neighNci = getNciById(neigh.getId());
            if (neighNci.getColor() == nci.getColor()) {
                al.add(neighNci);
            }
        }
        return al;
    }

    /*
     * get the highest degree from each selected nodes to other selected nodes
     */
    public int getHighestDegreeSelected() {
        int maxDegreeToSelected = 0;
        for (NodeColorInfo nci : nodeColorInfo) {
            if (nci.isSelected() && nci.getDegreeToSelected() > maxDegreeToSelected) {
                maxDegreeToSelected = nci.getDegreeToSelected();
            }
        }
        logger.finest("getting highest-degree-selected: " + maxDegreeToSelected);
        return maxDegreeToSelected;
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
        return toStringColored() + "\n" + toStringUncolored() + "\n" + toStringUnselected();
    }
    
    private String toStringNciList(Collection<NodeColorInfo> nciCollection, String name) {
        String ret = "\n" + name + ": {\n";
        for (NodeColorInfo nci : nciCollection) {
            ret += "n" + nci.getNode().getId() + ": "
                    + "color=" + nci.getColor() + "; "
                    + "uncolored_neighs=" + nci.getUncoloredNeighbours() + "/" + nci.getNode().getDegree() + "; "
                    + "colors_available=" + nci.getColorsAvailable() + "; "
                    //                    + "colors_shared=" + nci.getColorsShared() + "; "
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
    
    public Set<NodeColorInfo> getSelectedColoredNCIs() {
        return selectedColoredNCIs;
    }
    
    public Set<NodeColorInfo> getSelectedUncoloredNCIs() {
        return selectedUncoloredNCIs;
    }
    
    public Set<NodeColorInfo> getUnselectedNCIs() {
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
    
    public Set<NodeColorInfo> getConflictingNCIs() {
        return conflictingNCIs;
    }
    
    public boolean isPartitionSelected(int partition) {
        return this.isPartitonSelected[partition];
    }
}