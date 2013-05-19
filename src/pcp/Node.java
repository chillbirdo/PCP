package pcp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.omg.PortableServer.POAManagerPackage.State;
import pcp.tools.MergeSort;

public class Node implements Comparable<Node> {
    private static final Logger logger = Logger.getLogger( Node.class.getName());
    
    private static enum ColorState {

        SHARED,
        AVAILABLE,
        UNAVAILABLE
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

            if (neigh.isColorAvailable(color) || neigh.isColorShared(color)) {
                neigh.setColorUnavailable(color);
                for (Node neighOfNeigh : neigh.getNeighbours()) {
                    if (neighOfNeigh != this) {
                        if (neighOfNeigh.isColorShared(color)) {
                            neighOfNeigh.setColorAvailable(color);
                        }
                    }
                }
            }
        }
    }

    /*
     * performs uncoloring
     */
    public void unColor() {
        int oldColor = this.color;
        this.color = PCP.UNCOLORED;
        this.colors[oldColor] = ColorState.AVAILABLE;
        boolean oldColorAvailableOnAllNeighs = true;
        for (Node neigh : neighbour) {
            neigh.increaseUncolored();
            //check if neigh has oldcolor AVAILABLE now
            boolean noNeighOfNeighHasOldColor = true;
            for (Node neighOfNeigh : neigh.getNeighbours()) {
                if (neighOfNeigh == this) {
                    continue;
                }
                if( neighOfNeigh.getColor() == oldColor){
                    noNeighOfNeighHasOldColor = false;
                }
            }
            if( noNeighOfNeighHasOldColor){
                neigh.setColorAvailable(oldColor);
                //if the neighbour switched oldcolor to available
                //check if its neighbours can share oldcolor now
                for (Node neighOfNeigh : neigh.getNeighbours()) {
                    if (neighOfNeigh == this || neighOfNeigh.isColorUnavailable(oldColor)) {
                        continue;
                    }
                    boolean allNeighOfNeighOfNeighHaveOldColorAvailable = true;
                    for( Node neighOfNeighOfNeigh : neighOfNeigh.getNeighbours()){
                        if( !neighOfNeighOfNeigh.isColorAvailable(oldColor)){
                            allNeighOfNeighOfNeighHaveOldColorAvailable = false;
                        }
                    }
                    if( allNeighOfNeighOfNeighHaveOldColorAvailable){
                        neighOfNeigh.setColorShared( oldColor);
                    }
                }
            }else{
                oldColorAvailableOnAllNeighs = false;
            }
            //check if neigh has oldcolor SHARED now    
            boolean allNeighOfNeighHaveOldColorAvailable = true;
            for (Node neighOfNeigh : neigh.getNeighbours()) {
                if (neighOfNeigh == this) {
                    continue;
                }
                if( neighOfNeigh.isColorUnavailable(oldColor)){
                    allNeighOfNeighHaveOldColorAvailable = false;
                }
            }
            if( allNeighOfNeighHaveOldColorAvailable){
                neigh.setColorShared(oldColor);
            }
        }
        if (oldColorAvailableOnAllNeighs) {
            this.colors[oldColor] = ColorState.SHARED;
        }
    }

    /*
     * adapt the lenght of the colorarray to the number of maximal colors
     */
    public void initColorArray(int maxColors) {
        if (colors == null) {
            colors = new ColorState[maxColors];
            this.colorsAvailable = maxColors;
            this.colorsShared = maxColors;
            for (int i = 0; i < colors.length; i++) {
                colors[i] = ColorState.SHARED;
            }
        }
    }

    private void updCountingValues(ColorState fromState, ColorState toState) {
        switch (fromState) {
            case SHARED: {
                switch (toState) {
                    case AVAILABLE: {
                        this.colorsShared--;
                    }
                    break;
                    case UNAVAILABLE: {
                        this.colorsShared--;
                        this.colorsAvailable--;
                    }
                    break;
                }
            }
            break;
            case AVAILABLE: {
                switch (toState) {
                    case SHARED: {
                        this.colorsShared++;
                    }
                    break;
                    case UNAVAILABLE: {
                        this.colorsAvailable--;
                    }
                    break;
                }
            }
            break;
            case UNAVAILABLE: {
                switch (toState) {
                    case SHARED: {
                        this.colorsShared++;
                        this.colorsAvailable++;
                    }
                    break;
                    case AVAILABLE: {
                        this.colorsAvailable++;
                    }
                    break;
                }
            }
            break;
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

    public boolean isColorAvailable(int color) {
        return this.colors[color] == ColorState.AVAILABLE;
    }

    public boolean isColorShared(int color) {
        return this.colors[color] == ColorState.SHARED;
    }

    public boolean isColorUnavailable(int color) {
        return this.colors[color] == ColorState.UNAVAILABLE;
    }

    public void setColorUnavailable(int color) {
        updCountingValues(this.colors[color], ColorState.UNAVAILABLE);
        this.colors[color] = ColorState.UNAVAILABLE;
    }

    public void setColorAvailable(int color) {
        updCountingValues(this.colors[color], ColorState.AVAILABLE);
        this.colors[color] = ColorState.AVAILABLE;
    }

    public void setColorShared(int color) {
        updCountingValues(this.colors[color], ColorState.SHARED);
        this.colors[color] = ColorState.SHARED;
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

    public ColorState[] getColorArray() {
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

    public void decreaseDegree() {
        degree--;
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