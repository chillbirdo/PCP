package pcp.alg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;
import pcp.model.Coloring;
import pcp.model.NodeColorInfo;
import pcp.model.Graph;
import pcp.model.Node;

public class NodeSelector {

    private static final Logger logger = Logger.getLogger(NodeSelector.class.getName());
    private static final double ks = 1.4;
    private static final double ku = 0.8;

    public static void greedyMinDegree(Coloring c, Double pks, Double pku) {
        double ks = NodeSelector.ks;
        double ku = NodeSelector.ku;
        if (pks != null) {
            ks = pks;
        }
        if (pku != null) {
            ku = pku;
        }
        logger.finest("Selecting nodes by greedyMinDegree");
        while (!c.getUnselectedNCIs().isEmpty()) {
            logger.finest("\n");
            NodeColorInfo selectNci = null;
            double minEval = Double.MAX_VALUE;
            for (NodeColorInfo nci : c.getUnselectedNCIs()) {
                //calc sum adjacent selected and considerable nodes
                //(i.e.: unselected nodes from unselected Cluster) 
                double sumOfAdjacentSelected = 0.0;
                double sumOfAdjacentConsiderable = 0.0;
                for (Node neigh : nci.getNode().getNeighbours()) {
                    NodeColorInfo neighNci = c.getNciById(neigh.getId());
                    if (neighNci.isSelected()) {
                        sumOfAdjacentSelected += 1.0;
                    } else if (c.getUnselectedNCIs().contains(neighNci)) {
                        sumOfAdjacentConsiderable += 1.0;
                    }
                }
                double eval = ks * sumOfAdjacentSelected + ku * sumOfAdjacentConsiderable;
                logger.finest("Node " + nci.getNode().getId() + " : " + eval);
                if (eval < minEval) {
                    selectNci = nci;
                    minEval = eval;
                }
            }
            logger.finest("chose node " + selectNci.getNode().getId());
            c.selectNci(selectNci);
        }
    }

    private static void unselectAllNcisOfColor( Coloring c, int color) {
        for (Iterator<NodeColorInfo> it = c.getSelectedColoredNCIs().iterator(); it.hasNext();) {
            NodeColorInfo nci = it.next();
            if (nci.getColor() == color) {
                c.uncolorNci(nci);
                it.remove();
                c.unselectNci(nci);
                //SPEEDUP: write method to unselect a colored node
            }
        }
    }

    public static void randomSelect(Coloring coloring) {
        Graph g = coloring.getGraph();
        for (int i = 0; i < g.getPartitionSize().length; i++) {
            int size = g.getPartitionSize()[i];
            Random randomGenerator = new Random();
            int r = randomGenerator.nextInt(size);
            int nodeId = g.getNodeInPartition()[i][r].getId();
            NodeColorInfo nci = coloring.getNciById(nodeId);
            coloring.selectNci(nci);
            logger.finest("selected node " + nodeId);
        }
    }

    public static void testSelect(Coloring coloring) {
        coloring.selectNci(coloring.getNciById(1));
        logger.finest("selected node " + 1);
        coloring.selectNci(coloring.getNciById(2));
        logger.finest("selected node " + 2);
        coloring.selectNci(coloring.getNciById(4));
        logger.finest("selected node " + 4);
    }
}
