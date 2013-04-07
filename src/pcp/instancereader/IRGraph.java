package pcp.instancereader;

import pcp.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IRGraph {

    private List<IRNode> iRNode;
    private List<List<IRNode>> iRNodesInPartition;

    public IRGraph(int nodeAmount, int partitionAmount) {
        iRNode = new LinkedList<IRNode>();
        iRNodesInPartition = new ArrayList<List<IRNode>>(partitionAmount);
        for (int i = 0; i < partitionAmount; i++) {
            iRNodesInPartition.add(new LinkedList());
        }
    }

    public void addNode(int partition) {
        IRNode n = new IRNode( iRNode.size(), partition);
        iRNode.add(n);
        iRNodesInPartition.get(partition).add(n);
    }

    /**
     * Transform IRGraph to the more performant DataStructure Graph
     */
    public Graph toGraph() {
        //transform nodes
        System.out.println("Converting Nodes..");
        Node[] node = new Node[this.iRNode.size()];
        for (int i = 0; i < node.length; i++) {
            node[i] = this.iRNode.get(i).toNode();
        }
        System.out.println("Converting Edges..");
        for (int i = 0; i < node.length; i++) {
            Node n = node[i];
            IRNode irn = this.iRNode.get(i);
            Node[] neighbour = new Node[irn.getNeighbours().size()];
            for (int neigh = 0; neigh < neighbour.length; neigh++) {
                Integer irnNeighbourIdx = irn.getNeighbours().get(neigh);
                neighbour[neigh] = node[irnNeighbourIdx];
            }
            n.setNeighbours(neighbour);
        }
        //get max partition size to alloc enough memory
        System.out.println("Converting NodesInPartition..");
        int maxPartitionSize = 0;
        for (List l : iRNodesInPartition) {
            if (l.size() > maxPartitionSize) {
                maxPartitionSize = l.size();
            }
        }
        Node[][] nodesInPartition = new Node[this.iRNodesInPartition.size()][maxPartitionSize];
        int partitionSize[] = new int[this.iRNodesInPartition.size()];
        for (int i = 0; i < this.iRNodesInPartition.size(); i++) {
            List<IRNode> partition = this.iRNodesInPartition.get(i);
            partitionSize[i] = partition.size();
            for (int j = 0; j < partition.size(); j++) {
                int iRIdx = this.iRNode.indexOf(partition.get(j));
                nodesInPartition[i][j] = node[iRIdx];
            }
        }
        //clear IRData completely
        System.out.println("Clearing IRPartition..");
        this.iRNode.clear();
        this.iRNode = null;
        this.iRNodesInPartition.clear();
        this.iRNodesInPartition = null;
        
        return new Graph( node, nodesInPartition, partitionSize);
    }

    public IRNode getNode(int idx) {
        return iRNode.get(idx);
    }

    public List<IRNode> getNodes() {
        return iRNode;
    }

    public List<IRNode> getNodesInPartition(int partition) {
        return iRNodesInPartition.get(partition);
    }

    public String toString() {
        String ret = "size " + iRNode.size() + ";\n";
        int i = 0;
        for (IRNode n : iRNode) {
            ret += "n " + (i++) + ": p=" + n.getPartition() + " ";
            int neig = 0;
            for (Integer neighbour : n.getNeighbours()) {
                ret += "n" + (neig++) + "=" + neighbour + ", ";
            }
            ret += "\n";
        }
        return ret;
    }
}
