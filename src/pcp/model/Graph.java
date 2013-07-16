package pcp.model;

import java.util.logging.Logger;

public class Graph {
    private static final Logger logger = Logger.getLogger( Graph.class.getName());
    
    private final Node[] node;
    private final Node[][] nodeInPartition;
    private final int partitionSize[];

    public Graph(Node[] node, Node[][] nodeInPartition, int partitionSize[]) {
        this.node = node;
        this.nodeInPartition = nodeInPartition;
        this.partitionSize = partitionSize;
    }

    public String toString() {
        String ret = "size " + node.length + ";\n";
        for (Node n : node) {
            ret += "n" + n.getId() + ": p=" + n.getPartition() + "; ";
            ret += "degree=" + n.getDegree() + "; ";
            int neig = 0;
            for (Node neighbour : n.getNeighbours()) {
                ret += "n" + (neig++) + "=" + neighbour.getId() + ", ";
            }
            ret += "\n";
        }
        return ret;
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
    
    public Node getNode(int idx) {
        return node[idx];
    }

    public Node[] getNodes() {
        return node;
    }

    public int[] getPartitionSize() {
        return partitionSize;
    }

    public Node[][] getNodeInPartition() {
        return nodeInPartition;
    }
}
