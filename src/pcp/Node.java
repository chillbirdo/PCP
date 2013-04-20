package pcp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import pcp.tools.MergeSort;

public class Node implements Comparable<Node> {

    private static enum ColorState {

        AVAILABLE,
        UNAVAILABLE,
        LOCKED,
        NOT_USED
    }
    private int id;
    private Node[] neighbour;
    private int color;
    private int partition;
    private int degree = 0;//counted up in addNeighbour()
    private boolean ccDependent = false;
    private int diffcolored = 0;
    private int uncolored;
    private ColorState[] colorsAvailableArray;
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
        if (this.color == color || this.color == PCP.UNCOLORED) {
            return;
        }
        this.color = color;
        for (Node neigh : neighbour) {
            neigh.decreaseUncolored();
            neigh.updColsAvailAfterColoring(color);
        }
        for( Node neigh : neighbour){
         //   neigh.updateColsShared()
        }
    }

    public void updColsAvailAfterColoring(int color) {
        if (colorsAvailableArray[color] == ColorState.AVAILABLE) {
            colorsAvailableArray[color] = ColorState.UNAVAILABLE;
            diffcolored++;
            colorsAvailable--;
        }
    }

    /*
     * it is assumed that when a node is uncolored, the node has been colored before
     */
    public void uncolor() {
        int oldColor = this.color;
        this.color = PCP.UNCOLORED;
        for (Node neigh : neighbour) {
            neigh.increaseUncolored();
            neigh.updColsAvailAfterUncoloring(oldColor);
        }
    }

    public void updColsAvailAfterUncoloring(int oldColor) {
        if (colorsAvailableArray[oldColor] != ColorState.UNAVAILABLE) {
            System.err.println("ERROR: farbe " + oldColor + " müsste an dieser stelle unavailable sein");
        }
        //lookup if one more of the neighbours has color oldColor,
        //if not, oldColor can be set free
        boolean oneNeighbourHasOldColor = false;
        for (Node neigh : neighbour) {
            if (neigh.getColor() == oldColor) {
                oneNeighbourHasOldColor = true;
                break;
            }
        }
        if (!oneNeighbourHasOldColor) {
            colorsAvailableArray[oldColor] = ColorState.AVAILABLE;
            diffcolored--;
            colorsAvailable++;
        }
    }

    private void updateDiffcolored() {
        int dc = 0;
        int i = 0;
        ColorState cs = colorsAvailableArray[i];
        while (cs != ColorState.NOT_USED) {
            if (cs == ColorState.UNAVAILABLE) {
                dc++;
            }
        }
        this.diffcolored = dc;
    }

    public void setMaxColorsAvailable(int maxColors) {
        if (colorsAvailableArray == null) {
            colorsAvailableArray = new ColorState[maxColors];
            this.colorsAvailable = maxColors;
            this.colorsShared = maxColors;
            for (int i = 0; i < colorsAvailableArray.length; i++) {
                colorsAvailableArray[i] = ColorState.AVAILABLE;
            }
        } else {
            if (maxColors >= colorsAvailableArray.length) {
                //TODO: good logging
                System.out.println("ERROR: node " + getId() + " setMaxColor: " + maxColors + " : color >=colorAvailable.length");
                return;
            }
            for (int i = maxColors - 1; i < colorsAvailableArray.length && colorsAvailableArray[i] != ColorState.NOT_USED; i++) {
                if (colorsAvailableArray[i] == ColorState.AVAILABLE) {
                    colorsAvailable--;
                }
                colorsAvailableArray[i] = ColorState.NOT_USED;
            }
        }
    }

    public ColorState getColorState(int color) {
        return colorsAvailableArray[color];
    }

    public ColorState[] getColorsAvailableList() {
        return colorsAvailableArray;
    }

    public int getPotentialDifference() {
        return diffcolored + uncolored;
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

    public int getDiffcolored() {
        return diffcolored;
    }

    public int getUncolored() {
        return uncolored;
    }
}