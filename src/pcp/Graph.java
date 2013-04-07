package pcp;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private Node[] node;
    
    private Node[][] nodeInPartition;
    private int partitionSize[];
    
    public Graph( Node[] node, Node[][] nodeInPartition, int partitionSize[]) {
        this.node = node;
        this.nodeInPartition = nodeInPartition;
        this.partitionSize = partitionSize;
    }

    public Node getNode( int idx){
        return node[idx];
    }
    
    public String toString() {
        String ret = "size " + node.length + ";\n";
        int i = 0;
        for (Node n : node) {
            ret += "n " + (i++) + ": p=" + n.getPartition() + "; ";
            int neig = 0;
            for (Node neighbour : n.getNeighbours()) {
                ret += "n" + (neig++) + "=" + neighbour.getId() + ", ";
            }
            ret += "\n";
        }
        return ret;
    }
}
