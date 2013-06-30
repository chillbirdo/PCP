package pcp.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.omg.PortableServer.POAManagerPackage.State;
import pcp.PCP;
import static pcp.PCP.UNCOLORED;
import pcp.coloring.Coloring;
import pcp.PCP.ColorState;
import pcp.tools.MergeSort;

public class Node{
    private static final Logger logger = Logger.getLogger( Node.class.getName());
    
    private int id;
    private Node[] neighbour;
    private int partition;
    private int degree = 0;//counted up in addNeighbour()

    public Node(int id, int partition, int neighbourAmount) {
        this.id = id;
        this.partition = partition;
        this.neighbour = new Node[neighbourAmount];
        for (Node n : neighbour) {
            n = null;
        }
    }

    public Node getNeighbour(int idx) {
        return neighbour[idx];
    }

    public void setNeighbour(int idx, Node n) {
        neighbour[idx] = n;
    }

    public void setNeighbours(Node[] neighbour) {
        this.neighbour = neighbour;
    }

    public int getId() {
        return id;
    }

    public Node[] getNeighbours() {
        return neighbour;
    }

    public void addNeighbour(Node neighbour) {
        this.neighbour[degree] = neighbour;
        degree++;
    }

    public int getPartition() {
        return partition;
    }

    public void sortNeighboursByColor() {
        MergeSort.sortNodes(neighbour);
    }

    public int getDegree() {
        return degree;
    }

    public void decreaseDegree() {
        degree--;
    }
}