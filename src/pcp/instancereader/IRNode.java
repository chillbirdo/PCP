package pcp.instancereader;

import pcp.*;
import java.util.LinkedList;

public class IRNode {

    private int id;
    private LinkedList<Integer> neighbour;
    private int partition;
    
    public IRNode( int id, int partition){
        this.id = id;
        this.partition = partition;
        this.neighbour = new LinkedList<Integer>();
    }

    public int getId() {
        return id;
    }
    
    public Node toNode(){
        return new Node( id, partition);
    }

    public void addNeighbour( Integer idx){
        neighbour.add( idx);
    }
    
    public LinkedList<Integer> getNeighbours(){
        return neighbour;
    }

    public int getPartition() {
        return partition;
    }
}
