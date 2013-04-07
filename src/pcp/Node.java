package pcp;

import java.util.LinkedList;


public class Node {

    private int id;
    private Node[] neighbour;
    private int color;
    private int partition;

    
    public Node( int id, int partition){
        this.id = id;
        this.color = PCP.UNCOLORED;
        this.partition = partition;
    }

    public int getId() {
        return id;
    }
    
    public Node[] getNeighbours(){
        return neighbour;
    }

    public void setNeighbours( Node[] neighbour){
        this.neighbour = neighbour;
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
