package pcp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import pcp.tools.MergeSort;

public class Node implements Comparable<Node> {

    private static enum ColorState {

        SHARED,
        AVAILABLE,
        UNAVAILABLE,
        NOT_USED
    }
    private int id;
    private Node[] neighbour;
    private int color;
    private int partition;
    private int degree = 0;//counted up in addNeighbour()
    private boolean ccDependent = false;
    private int uncolored;
    private ColorState[] colors;
    private int colorsAvailable;
    private int colorsShared;

    public Node(int id, int partition, int neighboutAmount) {
        this.id = id;
        this.color = PCP.UNCOLORED;
        this.partition = partition;
        this.uncolored = neighboutAmount;
        this.neighbour = new Node[neighboutAmount];
        for (Node n : neighbour) {
            n = null;
        }
    }

    /*
     * it is assumed that when a color is set, the node has been uncolored before
     */
    public void setColor(int color) {
        if (this.color == color || color == PCP.UNCOLORED) {
            return;
        }
        this.color = color;
        this.setColorUnavailable(color);
        for (Node neigh : neighbour) {
            neigh.decreaseUncolored();
            
            if ( neigh.isColorAvailable(color) || neigh.isColorShared(color)) {
                neigh.setColorUnavailable(color);
                for (Node neighOfNeigh : neigh.getNeighbours()) {
                    if( neighOfNeigh != this){
                        neighOfNeigh.decreaseColorsShared();
                    }
                }
            }
        }
    }

    public void setMaxColorsAvailable(int maxColors) {
        if (colors == null) {
            colors = new ColorState[maxColors];
            this.colorsAvailable = maxColors;
            this.colorsShared = maxColors;
            for (int i = 0; i < colors.length; i++) {
                colors[i] = ColorState.SHARED;
            }
        } else {
            if (maxColors >= colors.length) {
                //TODO: good logging
                System.out.println("ERROR: node " + getId() + " setMaxColor: " + maxColors + " : color >=colorAvailable.length");
                return;
            }
            for (int i = maxColors - 1; i < colors.length && colors[i] != ColorState.NOT_USED; i++) {
                setColorNotUsed(i);
            }
        }
    }

//    /*
//     * it is assumed that when a node is uncolored, the node has been colored before
//     */
//    public void uncolor() {
//        int oldColor = this.color;
//        this.color = PCP.UNCOLORED;
//        for (Node neigh : neighbour) {
//            neigh.increaseUncolored();
//            neigh.updColsAvailAfterUncoloring(oldColor);
//        }
//    }
//
//    public void updColsAvailAfterUncoloring(int oldColor) {
//        if (colorsAvailableArray[oldColor] != ColorState.UNAVAILABLE) {
//            System.err.println("ERROR: farbe " + oldColor + " müsste an dieser stelle unavailable sein");
//        }
//        //lookup if one more of the neighbours has color oldColor,
//        //if not, oldColor can be set free
//        boolean oneNeighbourHasOldColor = false;
//        for (Node neigh : neighbour) {
//            if (neigh.getColor() == oldColor) {
//                oneNeighbourHasOldColor = true;
//                break;
//            }
//        }
//        if (!oneNeighbourHasOldColor) {
//            colorsAvailableArray[oldColor] = ColorState.AVAILABLE;
//            diffcolored--;
//            colorsAvailable++;
//        }
//    }
    
    public boolean isColorAvailable( int color){
        return this.colors[color] == ColorState.AVAILABLE;
    }
    
    public boolean isColorShared( int color){
        return this.colors[color] == ColorState.SHARED;
    }
    
    public boolean isColorUnavailable( int color){
        return this.colors[color] == ColorState.UNAVAILABLE;
    }
    
    public boolean isColorNotUsed( int color){
        return this.colors[color] == ColorState.NOT_USED;
    }
    
    public void setColorUnavailable( int color){
        updCountingValues(color);
        this.colors[color] = ColorState.UNAVAILABLE;
    }
    
    public void setColorAvailable( int color){
        updCountingValues(color);
        this.colors[color] = ColorState.AVAILABLE;
    }
    
    public void setColorShared( int color){
        this.colors[color] = ColorState.SHARED;
    }
    
    public void setColorNotUsed( int color){
        updCountingValues(color);
        this.colors[color] = ColorState.NOT_USED;
    }
    
    private void updCountingValues( int color){
        switch( this.colors[color]){
            case SHARED:{
                this.colorsShared--;
            }//nobeak!
            case AVAILABLE:{
                this.colorsAvailable--;
            }break;
            default:break;
        }
    }
    
    public void decreaseColorsAvailable() {
        this.colorsAvailable--;
    }

    public void increaseColorsAvailable() {
        this.colorsAvailable++;
    }

    public void decreaseColorsShared() {
        this.colorsShared--;
    }

    public void increaseColorsShared() {
        this.colorsShared++;
    }

    public ColorState getColorState(int color) {
        return colors[color];
    }

    public ColorState[] getColorsAvailableList() {
        return colors;
    }

    public boolean isCcDependent() {
        return ccDependent;
    }

    public void setCcDependent(boolean ccDependent) {
        this.ccDependent = ccDependent;
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

    public int getColor() {
        return color;
    }

    public void increaseUncolored() {
        uncolored++;
    }

    public void decreaseUncolored() {
        uncolored--;
    }

    public int getPartition() {
        return partition;
    }

    public void sortNeighboursByColor() {
        MergeSort.sortNodes(neighbour);
    }

    public int compareTo(Node n) {
        return this.getColor() - n.getColor();
    }

    public int getDegree() {
        return degree;
    }

    public int getDiffcolored(int maxColors) {
        return maxColors - colorsAvailable;
    }

    public int getUncolored() {
        return uncolored;
    }

    public int getColorsAvailable() {
        return colorsAvailable;
    }

    public int getColorsShared() {
        return colorsShared;
    }

    public ColorState[] getColors() {
        return colors;
    }
}