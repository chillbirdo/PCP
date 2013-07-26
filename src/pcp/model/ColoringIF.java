package pcp.model;

import java.util.Collection;
import java.util.Set;

public interface ColoringIF {

    public void initColorArrayOfEachNci(int maxColors);

    public void selectNci(NodeColorInfoIF nci);

    public void unselectNci(NodeColorInfoIF nci);

    public void colorNci(NodeColorInfoIF nci, int color);

    public void uncolorNci(NodeColorInfoIF nci);

    public void reduceColor(int col);

    public Set<NodeColorInfoIF> getConflictingNeighboursOfNci(NodeColorInfoIF nci, int conflictAmount);

    public String toStringUncolored();

    public String toStringUnselected();

    public String toStringColored();

    public void logColorStats();

    public Set<NodeColorInfoIF> getSelectedColoredNCIs();

    public Set<NodeColorInfoIF> getSelectedUncoloredNCIs();

    public Set<NodeColorInfoIF> getUnselectedNCIs();

    public Graph getGraph();

    public NodeColorInfoIF getNciById(int id);

    public int getChromatic();

    public Set<NodeColorInfoIF> getConflictingNCIs();

    public boolean isPartitionSelected(int partition);
}
