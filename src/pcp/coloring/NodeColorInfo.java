package pcp.coloring;

import java.util.ArrayList;
import java.util.logging.Logger;
import pcp.PCP;
import pcp.model.Node;

public class NodeColorInfo {

    private static final Logger logger = Logger.getLogger(NodeColorInfo.class.getName());
    private Node node;                              //the node to which this nci is refering
    private int color;                              //the color of node n
    private int uncoloredNeighbours;                //number of uncolored neighbours
    private ArrayList<Integer> conflicts;           //number of conflicts a coloring would produce
//    private ArrayList<Integer> neiSghboursShared;    //number of selected neighbours that share the available color
    private int colorsAvailable;                    //number of colors available
//    private int colorsShared;                     //number of color shared
    private int degreeToSelected;                   //number of adjacent edges to selected nodes

    public NodeColorInfo(Node n) {
        this.node = n;
        this.color = PCP.NODE_UNSELECTED;
        this.uncoloredNeighbours = 0;
        this.degreeToSelected = 0;
        this.conflicts = null;
//        this.neighboursShared = null;
    }

    /*
     * adapt the length of the conflicts-ArrayList to the number of maximal colors
     */
    public void initConflictsArray(int maxColors) {
        if (conflicts == null ){//&& neighboursShared == null) {
            conflicts = new ArrayList<Integer>(maxColors);
//            neighboursShared = new ArrayList<Integer>(maxColors);
            this.colorsAvailable = maxColors;
//            this.colorsShared = maxColors;
            for (int i = 0; i < maxColors; i++) {
                conflicts.add(i, 0);
//                neighboursShared.add(i, 0);//number changes when nodes are selected
            }
            logger.warning("----- INIT nci " + node.getId() + " with maxColors " + conflicts.size() + " " + maxColors);
        } else {
            logger.warning("UNEXPECTED: tried to init colorarray wich has already been initialized.");
        }
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

//    public int getColorsShared() {
//        return colorsShared;
//    }

//    public void setColorsShared(int colorsShared) {
//        this.colorsShared = colorsShared;
//    }

    public int getDiffColoredNeighbours() {
        return getColorsUsed() - colorsAvailable;
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

    public Integer getConflicts(int color) {
        return conflicts.get(color);
    }

    public boolean isColorAvailable(int color) {
        return conflicts.get(color) == 0;
    }

    public boolean isColorUnavailable(int color) {
        return conflicts.get(color) > 0;
    }

    public void increaseConflicts(int color) {
        conflicts.set(color, conflicts.get(color) + 1);
    }

    public void decreaseConflicts(int color) {
        conflicts.set(color, conflicts.get(color) - 1);
    }

//    public void decreaseColorsShared() {
//        this.colorsShared--;
//    }

//    public void increaseColorsShared() {
//        this.colorsShared++;
//    }

    public boolean isSelected() {
        return color != PCP.NODE_UNSELECTED;
    }

    void select() {
        this.color = PCP.NODE_UNCOLORED;
    }

    void unselect() {
        this.color = PCP.NODE_UNSELECTED;
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
    
    public int getColorsUsed(){
        return this.conflicts.size();
    }
}
