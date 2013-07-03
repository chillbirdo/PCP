package pcp.coloring;

import java.util.logging.Logger;
import pcp.PCP;
import static pcp.PCP.ColorState.AVAILABLE;
import static pcp.PCP.ColorState.SHARED;
import static pcp.PCP.ColorState.UNAVAILABLE;
import pcp.model.Node;

public class NodeColorInfo {

    private static final Logger logger = Logger.getLogger(NodeColorInfo.class.getName());

    private int nodeId;             //the id of the nci is the same as of the node
    private int color;              //the color of node n
    private int uncoloredNeighbours;//number of uncolored neighbours
    private PCP.ColorState[] colors;//state of all colors
    private int colorsAvailable;    //number of colors available
    private int colorsShared;       //number of color shared

    public NodeColorInfo(Node n, int maxColors) {
        this.nodeId = n.getId();
        this.color = PCP.UNCOLORED;
        this.uncoloredNeighbours = n.getDegree();
        initColorArray(maxColors);
    }

    /*
     * adapt the length of the colorarray to the number of maximal colors
     */
    public void initColorArray(int maxColors) {
        if (colors == null) {
            colors = new PCP.ColorState[maxColors];
            this.colorsAvailable = maxColors;
            this.colorsShared = maxColors;
            for (int i = 0; i < colors.length; i++) {
                colors[i] = PCP.ColorState.SHARED;
            }
        }
    }

    /*
     * when the colorstate of a color is changed, shared and available have to stay consistent
     */
    private void updCountingValues(PCP.ColorState fromState, PCP.ColorState toState) {
        switch (fromState) {
            case SHARED: {
                switch (toState) {
                    case AVAILABLE: {
                        decreaseColorsShared();
                    }
                    break;
                    case UNAVAILABLE: {
                        decreaseColorsShared();
                        decreaseColorsAvailable();
                    }
                    break;
                }
            }
            break;
            case AVAILABLE: {
                switch (toState) {
                    case SHARED: {
                        increaseColorsShared();
                    }
                    break;
                    case UNAVAILABLE: {
                        decreaseColorsAvailable();
                    }
                    break;
                }
            }
            break;
            case UNAVAILABLE: {
                switch (toState) {
                    case SHARED: {
                        increaseColorsShared();
                        increaseColorsAvailable();
                    }
                    break;
                    case AVAILABLE: {
                        increaseColorsAvailable();
                    }
                    break;
                }
            }
            break;
        }
    }

    
    public void setColorUnavailable(int color) {
        updCountingValues(this.colors[color], PCP.ColorState.UNAVAILABLE);
        this.colors[color] = PCP.ColorState.UNAVAILABLE;
    }

    public void setColorAvailable(int color) {
        updCountingValues(this.colors[color], PCP.ColorState.AVAILABLE);
        this.colors[color] = PCP.ColorState.AVAILABLE;
    }

    public void setColorShared(int color) {
        updCountingValues(this.colors[color], PCP.ColorState.SHARED);
        this.colors[color] = PCP.ColorState.SHARED;
    }    
    
    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getUncolored() {
        return uncoloredNeighbours;
    }

    public void setUncolored(int uncolored) {
        this.uncoloredNeighbours = uncolored;
    }

    public int getColorsAvailable() {
        return colorsAvailable;
    }

    public void setColorsAvailable(int colorsAvailable) {
        this.colorsAvailable = colorsAvailable;
    }

    public int getColorsShared() {
        return colorsShared;
    }

    public void setColorsShared(int colorsShared) {
        this.colorsShared = colorsShared;
    }

    public int getDiffColored(int maxColors) {
        return maxColors - colorsAvailable;
    }

    public void increaseUncolored() {
        uncoloredNeighbours++;
    }

    public void decreaseUncolored() {
        uncoloredNeighbours--;
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

    public PCP.ColorState getColorState(int color) {
        return colors[color];
    }

    public boolean isColorAvailable(int color) {
        return this.colors[color] == PCP.ColorState.AVAILABLE;
    }

    public boolean isColorShared(int color) {
        return this.colors[color] == PCP.ColorState.SHARED;
    }

    public boolean isColorUnavailable(int color) {
        return this.colors[color] == PCP.ColorState.UNAVAILABLE;
    }
    public boolean isSelected() {
        return color != PCP.UNSELECTED;
    }
    public void select(){
        this.color = PCP.UNCOLORED;
    }
    public void unselect(){
        this.color = PCP.UNSELECTED;
    }
}
