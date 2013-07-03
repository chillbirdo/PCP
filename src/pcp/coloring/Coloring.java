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
    
    public Coloring(Graph g, int maxColors) {
        this.g = g;
        this.nodeColorInfo = new NodeColorInfo[g.getNodes().length];
        for( int i = 0; i < nodeColorInfo.length; i++){
            Node n = g.getNode(i);
            nodeColorInfo[i] = new NodeColorInfo(n, maxColors);
        }
        this.selectedColoredNCIs = new HashSet<NodeColorInfo>();
        this.selectedUncoloredNCIs = new HashSet<NodeColorInfo>();
        this.unselectedNCIs = new HashSet<NodeColorInfo>((Arrays.asList(nodeColorInfo)));
    }

    /*
     * select a node from a partition
     */
    public void selectNodeColorInfo( NodeColorInfo nci){
        if( !nci.isSelected() && unselectedNCIs.contains(nci)){
            nci.select();
            unselectedNCIs.remove( nci);
            selectedUncoloredNCIs.add(nci);
        }else{
            logger.warning("UNEXPECTED: tried to select an already selected node. (node=" + nci.getNodeId() + ", color=" + nci.getColor() + ")");
        }
    }
    
    /*
     * select a node from a partition
     */
    public void unselectNodeColorInfo( NodeColorInfo nci){
        if( nci.getColor() == PCP.UNCOLORED && selectedUncoloredNCIs.contains( nci)){
            nci.unselect();
            selectedUncoloredNCIs.remove(nci);
            unselectedNCIs.add(nci);
        }else{
            logger.severe("UNEXPECTED: tried to unselect either a colored or an already unselected node!  (node=" + nci.getNodeId() + ", color=" + nci.getColor() + ")");
        }
    }
    
    /*
     * it is assumed that when a color is set, the node has been uncolored before
     */
    public void colorNodeColorInfo( NodeColorInfo nci, int color) {
        if( !selectedUncoloredNCIs.contains(nci)){
            logger.severe("UNEXPECTED: tried to color a node that was not in set of uncolored!");
            return;
        }
        this.selectedUncoloredNCIs.remove(nci);
        this.selectedColoredNCIs.add(nci);

        Node n = g.getNode(nci.getNodeId());
        if (nci.getColor() == color || color == PCP.UNCOLORED) {
            return;
        }
        nci.setColor(color);
        nci.setColorUnavailable(color);
        for (Node neigh : n.getNeighbours()) {
            NodeColorInfo neighNci = nodeColorInfo[neigh.getId()];
            neighNci.decreaseUncolored();

            if (neighNci.isColorAvailable(color) || neighNci.isColorShared(color)) {
                neighNci.setColorUnavailable(color);
                for (Node neighOfNeigh : neigh.getNeighbours()) {
                    NodeColorInfo neighOfNeighNci = nodeColorInfo[neighOfNeigh.getId()];
                    if (neighOfNeigh != n) {
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
    public void unColorNodeColorInfo(NodeColorInfo nci) {
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
        Node n = g.getNode(nci.getNodeId());
        for (Node neigh : n.getNeighbours()) {
            NodeColorInfo neighNci = this.nodeColorInfo[neigh.getId()];
            neighNci.increaseUncolored();
            //check if neigh has oldcolor AVAILABLE now
            boolean noNeighOfNeighHasOldColor = true;
            for (Node neighOfNeigh : neigh.getNeighbours()) {
                NodeColorInfo neighOfNeighNci = this.nodeColorInfo[neighOfNeigh.getId()];
                if (neighOfNeigh == n) {
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
                    if (neighOfNeigh == n || neighOfNeighNci.isColorUnavailable(oldColor)) {
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
                if (neighOfNeigh == n) {
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

    public String toStringUncolored() {
        return toStringNciList( this.selectedUncoloredNCIs, "Uncolored Nodes");
    }

    public String toStringUnselected() {
        return toStringNciList( this.unselectedNCIs, "Uncolored Nodes");
    }
    
    public String toStringColored() {
        return toStringNciList( this.selectedColoredNCIs, "Colored Nodes");
    }
    
    private String toStringNciList( Collection<NodeColorInfo> nciCollection, String name){
        String ret = "\n" + name + ": {\n";
        for (NodeColorInfo nci : nciCollection) {
            Node n = g.getNode(nci.getNodeId());
            ret += "n" + nci.getNodeId() + ": "
                    + "color=" + nci.getColor() + "; "
                    + "uncolored_neighs=" + nci.getUncolored() + "/" + n.getDegree() + "; "
                    + "colors_available=" + nci.getColorsAvailable() + "; "
                    + "colors_shared=" + nci.getColorsShared() + "; "
                    + "\n";
        }
        return "}" + ret;        
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
