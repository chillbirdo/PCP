package pcp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Graph {
    private static final Logger logger = Logger.getLogger( Graph.class.getName());
    
    private Node[] node;
    private Node[][] nodeInPartition;
    private int partitionSize[];
    private Stack<Node> shrinkStack;
    private int maxColors;

    public Graph(Node[] node, Node[][] nodeInPartition, int partitionSize[]) {
        this.node = node;
        this.nodeInPartition = nodeInPartition;
        this.partitionSize = partitionSize;
    }

    public Node getNode(int idx) {
        return node[idx];
    }

    public void shrink(int maxColors) {
    }

    public void setMaxColorsAvailable(int maxColors) {
        logger.log( Level.INFO, "Maxcolors: " + maxColors);
        this.maxColors = maxColors;
        for (Node n : node) {
            n.initColorArray(maxColors);
        }
    }

    public int getHighestDegree() {
        int maxdegree = 0;
        for (Node n : node) {
            if (n.getDegree() > maxdegree) {
                maxdegree = n.getDegree();
            }
        }
        return maxdegree;
    }

    public String toString() {
        String ret = "size " + node.length + ";\n";
        for (Node n : node) {
            ret += "n" + n.getId() + ": p=" + n.getPartition() + "; ";
            int neig = 0;
            for (Node neighbour : n.getNeighbours()) {
                ret += "n" + (neig++) + "=" + neighbour.getId() + ", ";
            }
            ret += "\n";
        }
        return ret;
    }

    public String toColorString() {
        String ret = "";
        for (Node n : node) {
            ret += "n" + n.getId() + ": "
                    + "neighbours=" + n.getDegree() + "; "
                    + "color=" + n.getColor() + "; "
                    + "uncolored=" + n.getUncolored() + "/" + n.getNeighbours().length + "; "
                    + "available=" + n.getColorsAvailable() + "; "
                    + "shared=" + n.getColorsShared() + "; "
                    + "\n";
        }
        return ret;
    }

}
