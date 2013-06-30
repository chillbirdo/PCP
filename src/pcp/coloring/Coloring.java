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
    private Set<NodeColorInfo> coloredNodeColorInfos;
    private Set<NodeColorInfo> uncoloredNodeColorInfos;

    public Coloring(Graph g, int maxColors) {
        this.g = g;
        this.nodeColorInfo = new NodeColorInfo[g.getNodes().length];
        for( int i = 0; i < nodeColorInfo.length; i++){
            Node n = g.getNode(i);
            nodeColorInfo[i] = new NodeColorInfo(n, maxColors);
        }
        this.coloredNodeColorInfos = new HashSet<NodeColorInfo>();
        this.uncoloredNodeColorInfos = new HashSet<NodeColorInfo>((Arrays.asList(nodeColorInfo)));
    }

    /*
     * it is assumed that when a color is set, the node has been uncolored before
     */
    public void colorNodeColorInfo( NodeColorInfo nci, int color) {
        Node n = g.getNode(nci.getNodeId());
        this.uncoloredNodeColorInfos.remove(nci);
        this.coloredNodeColorInfos.add(nci);
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
        this.coloredNodeColorInfos.remove(nci);
        this.uncoloredNodeColorInfos.add(nci);

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
    
    public Set<NodeColorInfo> getColoredNodeColorInfos() {
        return coloredNodeColorInfos;
    }

    public Set<NodeColorInfo> getUncoloredNodeColorInfos() {
        return uncoloredNodeColorInfos;
    }
}
