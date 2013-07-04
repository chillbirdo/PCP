package pcp.coloring;

import java.util.ArrayList;
import java.util.logging.Logger;
import pcp.PCP;
import static pcp.PCP.ColorState.AVAILABLE;
import static pcp.PCP.ColorState.SHARED;
import static pcp.PCP.ColorState.UNAVAILABLE;
import pcp.model.Node;

public class NodeColorInfo {

    private static final Logger logger = Logger.getLogger(NodeColorInfo.class.getName());
    private Node node;                        //the node to which this nci is refering
    private int color;                        //the color of node n
    private int uncoloredNeighbours;          //number of uncolored neighbours
    private ArrayList<PCP.ColorState> colors; //state of each colors
    private int colorsAvailable;              //number of colors available
    private int colorsShared;                 //number of color shared
    private int degreeToSelected;             //number of adjacent edges to selected nodes

    public NodeColorInfo(Node n) {
        this.node = n;
        this.color = PCP.UNSELECTED;
        this.uncoloredNeighbours = 0;
        this.degreeToSelected = 0;
        this.colors = null;
    }

    /*
     * adapt the length of the colorarray to the number of maximal colors
     */
    public void initColorArray(int maxColors) {
        if (colors == null) {
            colors = new ArrayList<PCP.ColorState>(maxColors);
            this.colorsAvailable = maxColors;
            this.colorsShared = maxColors;
            for (int i = 0; i < maxColors; i++) {
                colors.add(i, SHARED);
            }
            logger.warning("----- INIT nci " + node.getId() + " with maxColors " + colors.size() + " " + maxColors);
        } else {
            logger.warning("UNEXPECTED: tried to init colorarray wich has already been initialized.");
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
        updCountingValues(this.colors.get(color), UNAVAILABLE);
        this.colors.set(color, UNAVAILABLE);
    }

    public void setColorAvailable(int color) {
        updCountingValues(this.colors.get(color), AVAILABLE);
        this.colors.set(color, AVAILABLE);
    }

    public void setColorShared(int color) {
        updCountingValues(this.colors.get(color), SHARED);
        this.colors.set(color, SHARED);
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getUncoloredNeighbours() {
        return uncoloredNeighbours;
    }

    public void setUncoloredNeighbours(int uncolored) {
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
        return colors.get(color);
    }

    public boolean isColorAvailable(int color) {
        return colors.get(color) == AVAILABLE;
    }

    public boolean isColorShared(int color) {
        return colors.get(color) == SHARED;
    }

    public boolean isColorUnavailable(int color) {
        return colors.get(color) == UNAVAILABLE;
    }

    public boolean isSelected() {
        return color != PCP.UNSELECTED;
    }

    void select() {
        this.color = PCP.UNCOLORED;
    }

    void unselect() {
        this.color = PCP.UNSELECTED;
    }

    public int getDegreeToSelected() {
        return degreeToSelected;
    }

    public void increaseDegreeToSelected() {
        this.degreeToSelected++;
    }

    public void decreaseDegreeToSelected() {
        this.degreeToSelected--;
    }
}
