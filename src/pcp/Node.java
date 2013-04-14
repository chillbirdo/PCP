package pcp;

import java.util.LinkedList;


public class Node {

    private int id;
    private Node[] neighbour;
    private int color;
    private int partition;
    private int neighbourAmount = 0;
    
    public Node( int id, int partition, int neighboutAmount){
        this.id = id;
        this.color = PCP.UNCOLORED;
        this.partition = partition;
        this.neighbour = new Node[neighboutAmount];
        for( Node n : neighbour){
            n = null;
        }
    }

    public int getId() {
        return id;
    }
    
    public Node[] getNeighbours(){
        return neighbour;
    }

    public void addNeighbour( Node neighbour){
        this.neighbour[neighbourAmount] = neighbour;
        neighbourAmount++;
    }
    
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getPartition() {
        return partition;
    }
}
