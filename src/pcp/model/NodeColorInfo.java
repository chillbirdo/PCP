package pcp.model;

import java.util.ArrayList;
import java.util.logging.Logger;
import pcp.PCP;
import pcp.model.Node;

/*
 * simple datastructure to hold colorinformation related to a node
 */
public class NodeColorInfo implements NodeColorInfoIF, Comparable<NodeColorInfo>{

    private static final Logger logger = Logger.getLogger(NodeColorInfo.class.getName());
    private Node node;                              //the node to which this nci is refering
    private int color;                              //the color of node n
    private ArrayList<Integer> conflicts;           //number of conflicts a color would produce
    private int colorsAvailable;                    //number of colors available

    public NodeColorInfo(Node n) {
        this.node = n;
        this.color = PCP.NODE_UNSELECTED;
        this.conflicts = null;
    }
    
    /*
     * copy constructor
     */
    public NodeColorInfo( NodeColorInfoIF nci){
        this.node = nci.getNode();
        this.color = nci.getColor();
        this.colorsAvailable = nci.getColorsAvailable();
        this.conflicts = new ArrayList<Integer>(nci.getConflictArray().size());
        for( Integer i : nci.getConflictArray()){
            this.conflicts.add( i);
        }
    }

    /*
     * adapt the length of the conflicts-ArrayList to the number of maximal colors
     */
    public void initConflictsArray(int maxColors) {
        if (conflicts == null ){//&& neighboursShared == null) {
            conflicts = new ArrayList<Integer>(maxColors);
            this.colorsAvailable = maxColors;
            for (int i = 0; i < maxColors; i++) {
                conflicts.add(i, 0);
            }
        } else {
            logger.warning("UNEXPECTED: tried to init colorarray wich has already been initialized.");
        }
    }

    public String toString(){
        return getNode().getId() + ":" + getColor();
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

    public int getColorsAvailable() {
        return colorsAvailable;
    }

    public void setColorsAvailable(int colorsAvailable) {
        this.colorsAvailable = colorsAvailable;
    }

    public int getDiffColoredNeighbours() {
        return conflicts.size() - colorsAvailable;
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

    public boolean isSelected() {
        return color != PCP.NODE_UNSELECTED;
    }

    public void setColorUncolored() {
        this.color = PCP.NODE_UNCOLORED;
    }

    public void setColorUnselected() {
        this.color = PCP.NODE_UNSELECTED;
    }

    public ArrayList<Integer> getConflictArray() {
        return conflicts;
    }

    @Override
    public int compareTo(NodeColorInfo nci) {
        return this.getNode().getId() - nci.getNode().getId();
    }

}
