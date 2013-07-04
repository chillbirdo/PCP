package pcp.coloring;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import pcp.PCP;
import pcp.alg.DangerAlgorithm;
import pcp.model.Graph;
import pcp.model.Node;

public class Coloring {

    private static final Logger logger = Logger.getLogger(Coloring.class.getName());
    
    private Graph g;
    private NodeColorInfo[] nodeColorInfo;
    private Set<NodeColorInfo> unselectedNCIs;
    private Set<NodeColorInfo> selectedColoredNCIs;
    private Set<NodeColorInfo> selectedUncoloredNCIs;
    
    public Coloring(Graph g) {
        this.g = g;
        this.nodeColorInfo = new NodeColorInfo[g.getNodes().length];
        for( int i = 0; i < nodeColorInfo.length; i++){
            Node n = g.getNode(i);
            nodeColorInfo[i] = new NodeColorInfo(n);
        }
        this.selectedColoredNCIs = new HashSet<NodeColorInfo>();
        this.selectedUncoloredNCIs = new HashSet<NodeColorInfo>();
        this.unselectedNCIs = new HashSet<NodeColorInfo>((Arrays.asList(nodeColorInfo)));
    }
    
    public void initColorArrayOfEachNci( int maxColors){
        for( NodeColorInfo nci : nodeColorInfo){
            nci.initColorArray(maxColors);
        }
    }

    /*
     * select a node from a partition
     */
    public void selectNci( NodeColorInfo nci){
        if( !nci.isSelected() && unselectedNCIs.contains(nci)){
            nci.select();
            for( Node neigh : nci.getNode().getNeighbours()){
                NodeColorInfo neighNci = getNciById(neigh.getId());
                neighNci.increaseUncolored();
                neighNci.increaseDegreeToSelected();
            }
            unselectedNCIs.remove( nci);
            selectedUncoloredNCIs.add(nci);
        }else{
            logger.warning("UNEXPECTED: tried to select an already selected node. (node=" + nci.getNode().getId() + ", color=" + nci.getColor() + ")");
        }
    }
    
    /*
     * select a node from a partition
     */
    public void unselectNci( NodeColorInfo nci){
        if( nci.getColor() == PCP.UNCOLORED && selectedUncoloredNCIs.contains( nci)){
            nci.unselect();
            for( Node neigh : nci.getNode().getNeighbours()){
                NodeColorInfo neighNci = getNciById(neigh.getId());
                neighNci.decreaseUncolored();
                neighNci.decreaseDegreeToSelected();
            }
            selectedUncoloredNCIs.remove(nci);
            unselectedNCIs.add(nci);
        }else{
            logger.severe("UNEXPECTED: tried to unselect either a colored or an already unselected node!  (node=" + nci.getNode().getId() + ", color=" + nci.getColor() + ")");
        }
    }
    
    /*
     * it is assumed that when a color is set, the node has been uncolored before
     */
    public void colorNci( NodeColorInfo nci, int color) {
        if( !selectedUncoloredNCIs.contains(nci)){
            logger.severe("UNEXPECTED: tried to color a node that was not in set of uncolored!");
            return;
        }
        this.selectedUncoloredNCIs.remove(nci);
        this.selectedColoredNCIs.add(nci);

        if (nci.getColor() == color || color == PCP.UNCOLORED) {
            return;
        }
        nci.setColor(color);
        nci.setColorUnavailable(color);
        for (Node neigh : nci.getNode().getNeighbours()) {
            NodeColorInfo neighNci = nodeColorInfo[neigh.getId()];
            neighNci.decreaseUncolored();

            if (neighNci.isColorAvailable(color) || neighNci.isColorShared(color)) {
                neighNci.setColorUnavailable(color);
                for (Node neighOfNeigh : neigh.getNeighbours()) {
                    NodeColorInfo neighOfNeighNci = nodeColorInfo[neighOfNeigh.getId()];
                    if (neighOfNeigh != nci.getNode()) {
                        if (neighOfNeighNci.isColorShared(color)) {
                            neighOfNeighNci.setColorAvailable(color);
                        }
                    }
                }
            }
        }
    }

    /*
     * performs uncoloring
     */
    public void uncolorNci(NodeColorInfo nci) {
        if( !selectedUncoloredNCIs.contains(nci)){
            logger.severe("UNEXPECTED: tried to uncolor a node that was not in set of colored!");
            return;
        }
        this.selectedColoredNCIs.remove(nci);
        this.selectedUncoloredNCIs.add(nci);

        int oldColor = nci.getColor();
        nci.setColor(PCP.UNCOLORED);
        nci.setColorAvailable(oldColor);
        boolean oldColorAvailableOnAllNeighs = true;
        for (Node neigh : nci.getNode().getNeighbours()) {
            NodeColorInfo neighNci = this.nodeColorInfo[neigh.getId()];
            neighNci.increaseUncolored();
            //check if neigh has oldcolor AVAILABLE now
            boolean noNeighOfNeighHasOldColor = true;
            for (Node neighOfNeigh : neigh.getNeighbours()) {
                NodeColorInfo neighOfNeighNci = this.nodeColorInfo[neighOfNeigh.getId()];
                if (neighOfNeigh == nci.getNode()) {
                    continue;
                }
                if (neighOfNeighNci.getColor() == oldColor) {
                    noNeighOfNeighHasOldColor = false;
                }
            }
            if (noNeighOfNeighHasOldColor) {
                neighNci.setColorAvailable(oldColor);
                //if the neighbour switched oldcolor to available
                //check if its neighbours can share oldcolor now
                for (Node neighOfNeigh : neigh.getNeighbours()) {
                    NodeColorInfo neighOfNeighNci = this.nodeColorInfo[neighOfNeigh.getId()];
                    if (neighOfNeigh == nci.getNode() || neighOfNeighNci.isColorUnavailable(oldColor)) {
                        continue;
                    }
                    boolean allNeighOfNeighOfNeighHaveOldColorAvailable = true;
                    for (Node neighOfNeighOfNeigh : neighOfNeigh.getNeighbours()) {
                        NodeColorInfo neighOfNeighOfNeighNci = this.nodeColorInfo[neighOfNeighOfNeigh.getId()];
                        if (!neighOfNeighOfNeighNci.isColorAvailable(oldColor)) {
                            allNeighOfNeighOfNeighHaveOldColorAvailable = false;
                        }
                    }
                    if (allNeighOfNeighOfNeighHaveOldColorAvailable) {
                        neighOfNeighNci.setColorShared(oldColor);
                    }
                }
            } else {
                oldColorAvailableOnAllNeighs = false;
            }
            //check if neigh has oldcolor SHARED now    
            boolean allNeighOfNeighHaveOldColorAvailable = true;
            for (Node neighOfNeigh : neigh.getNeighbours()) {
                NodeColorInfo neighOfNeighNci = this.nodeColorInfo[neighOfNeigh.getId()];
                if (neighOfNeigh == nci.getNode()) {
                    continue;
                }
                if (neighOfNeighNci.isColorUnavailable(oldColor)) {
                    allNeighOfNeighHaveOldColorAvailable = false;
                }
            }
            if (allNeighOfNeighHaveOldColorAvailable) {
                neighNci.setColorShared(oldColor);
            }
        }
        if (oldColorAvailableOnAllNeighs) {
            nci.setColorShared(oldColor);
        }
    }
    
    /*
     * get the highest degree from each selected nodes to other selected nodes
     */
    public int getHighestDegreeSelected(){
        int maxDegreeToSelected = 0;
        for( NodeColorInfo nci : nodeColorInfo){
            if( nci.isSelected() && nci.getDegreeToSelected() > maxDegreeToSelected){
                maxDegreeToSelected = nci.getDegreeToSelected();
            }
        }
        logger.finest( "getting highest-degree-selected: " + maxDegreeToSelected);
        return maxDegreeToSelected;
    }
    

    public String toStringUncolored() {
        return toStringNciList( this.selectedUncoloredNCIs, "Uncolored Nodes");
    }

    public String toStringUnselected() {
        return toStringNciList( this.unselectedNCIs, "Unselected Nodes");
    }
    
    public String toStringColored() {
        return toStringNciList( this.selectedColoredNCIs, "Colored Nodes");
    }
    
    public String toString(){
        return toStringColored() + "\n" + toStringUncolored() + "\n" + toStringUnselected();
    }
    
    private String toStringNciList( Collection<NodeColorInfo> nciCollection, String name){
        String ret = "\n" + name + ": {\n";
        for (NodeColorInfo nci : nciCollection) {
            ret += "n" + nci.getNode().getId() + ": "
                    + "color=" + nci.getColor() + "; "
                    + "uncolored_neighs=" + nci.getUncoloredNeighbours() + "/" + nci.getNode().getDegree() + "; "
                    + "colors_available=" + nci.getColorsAvailable() + "; "
                    + "colors_shared=" + nci.getColorsShared() + "; "
                    + "\n";
        }
        return ret + "}";        
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
    
    public NodeColorInfo getNciById( int id){
        return this.nodeColorInfo[id];
    }
}
