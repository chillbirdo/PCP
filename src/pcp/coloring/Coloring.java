package pcp.coloring;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import pcp.PCP;
import pcp.model.Graph;
import pcp.model.Node;

public class Coloring {

    private Graph g;
    private NodeColorInfo[] nodeColorInfo;
    private Set<NodeColorInfo> coloredNodes;
    private Set<NodeColorInfo> uncoloredNodes;

    public Coloring(Graph g, int maxColors) {
        this.g = g;
        this.nodeColorInfo = new NodeColorInfo[g.getNodes().length];
        for( int i = 0; i < nodeColorInfo.length; i++){
            Node n = g.getNode(i);
            nodeColorInfo[i] = new NodeColorInfo(n, maxColors);
        }
        this.coloredNodes = new HashSet<NodeColorInfo>();
        this.uncoloredNodes = new HashSet<NodeColorInfo>((Arrays.asList(nodeColorInfo)));
    }

    /*
     * it is assumed that when a color is set, the node has been uncolored before
     */
    public void colorNode( NodeColorInfo nci, int color) {
        Node n = g.getNode(nci.getNodeId());
        this.uncoloredNodes.remove(nci);
        this.coloredNodes.add(nci);
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
    public void unColorNode(NodeColorInfo nci) {
        this.coloredNodes.remove(nci);
        this.uncoloredNodes.add(nci);

        int oldColor = nci.getColor();
        nci.setColor(PCP.UNCOLORED);
        nci.setColorAvailable(oldColor);
        boolean oldColorAvailableOnAllNeighs = true;
        for (Node neigh : n.getNeighbours()) {
            neigh.increaseUncolored();
            //check if neigh has oldcolor AVAILABLE now
            boolean noNeighOfNeighHasOldColor = true;
            for (Node neighOfNeigh : neigh.getNeighbours()) {
                if (neighOfNeigh == n) {
                    continue;
                }
                if (neighOfNeigh.getColor() == oldColor) {
                    noNeighOfNeighHasOldColor = false;
                }
            }
            if (noNeighOfNeighHasOldColor) {
                neigh.setColorAvailable(oldColor);
                //if the neighbour switched oldcolor to available
                //check if its neighbours can share oldcolor now
                for (Node neighOfNeigh : neigh.getNeighbours()) {
                    if (neighOfNeigh == n || neighOfNeigh.isColorUnavailable(oldColor)) {
                        continue;
                    }
                    boolean allNeighOfNeighOfNeighHaveOldColorAvailable = true;
                    for (Node neighOfNeighOfNeigh : neighOfNeigh.getNeighbours()) {
                        if (!neighOfNeighOfNeigh.isColorAvailable(oldColor)) {
                            allNeighOfNeighOfNeighHaveOldColorAvailable = false;
                        }
                    }
                    if (allNeighOfNeighOfNeighHaveOldColorAvailable) {
                        neighOfNeigh.setColorShared(oldColor);
                    }
                }
            } else {
                oldColorAvailableOnAllNeighs = false;
            }
            //check if neigh has oldcolor SHARED now    
            boolean allNeighOfNeighHaveOldColorAvailable = true;
            for (Node neighOfNeigh : neigh.getNeighbours()) {
                if (neighOfNeigh == n) {
                    continue;
                }
                if (neighOfNeigh.isColorUnavailable(oldColor)) {
                    allNeighOfNeighHaveOldColorAvailable = false;
                }
            }
            if (allNeighOfNeighHaveOldColorAvailable) {
                neigh.setColorShared(oldColor);
            }
        }
        if (oldColorAvailableOnAllNeighs) {
            n.setColorShared(oldColor);
        }
    }

//    public String toString() {
//        String ret = "";
//        for (Node n : node) {
//            ret += "n" + n.getId() + ": "
//                    + "neighbours=" + n.getDegree() + "; "
//                    + "color=" + n.getColor() + "; "
//                    + "uncolored=" + n.getUncolored() + "/" + n.getNeighbours().length + "; "
//                    + "available=" + n.getColorsAvailable() + "; "
//                    + "shared=" + n.getColorsShared() + "; "
//                    + "\n";
//        }
//        return ret;
//    }
    
    
    public Set<Node> getColoredNodes() {
        return coloredNodes;
    }

    public Set<Node> getUncoloredNodes() {
        return uncoloredNodes;
    }
}
