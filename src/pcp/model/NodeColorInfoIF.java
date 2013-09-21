package pcp.model;

import java.util.List;

public interface NodeColorInfoIF{

    public void initConflictsArray(int maxColors);

    public Node getNode();

    public void setNode( Node n);

    public int getColor();

    public void setColor(int color);

    public int getColorsAvailable();

    public void setColorsAvailable(int colorsAvailable);

    public int getDiffColoredNeighbours();

    public void decreaseColorsAvailable();

    public void increaseColorsAvailable();

    public Integer getConflicts(int color);

    public boolean isColorAvailable(int color);

    public boolean isColorUnavailable(int color);

    public void increaseConflicts(int color);

    public void decreaseConflicts(int color);

    public boolean isSelected();

    public void setColorUncolored();

    public void setColorUnselected();

    public List<Integer> getConflictArray();
}
