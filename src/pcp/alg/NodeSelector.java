package pcp.alg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;
import pcp.coloring.Coloring;
import pcp.coloring.NodeColorInfo;
import pcp.model.Graph;
import pcp.model.Node;

public class NodeSelector {

    private static final Logger logger = Logger.getLogger(NodeSelector.class.getName());
    private static double ks = 1.0;
    private static double ku = 0.05;

    public static void greedyMinDegree(Coloring c, Graph g, int maxColors) {
        ArrayList<Node> selectedNodes = new ArrayList<Node>(g.getPartitionSize().length);
        ArrayList<Node> nodesToConsider = new ArrayList<Node>(g.getNodes().length);
        nodesToConsider.addAll(Arrays.asList(g.getNodes()));

        logger.finest("Selecting nodes by greedyMinDegree");
        while (!nodesToConsider.isEmpty()) {
            logger.finest("\n");
            Node selectNode = null;
            double minEval = Double.MAX_VALUE;
            for (int i = 0; i < nodesToConsider.size(); i++) {
                Node n = nodesToConsider.get(i);
                //calc sum adjacent selected and considerable nodes
                double sumOfAdjacentSelected = 0.0;
                double sumOfAdjacentConsiderable = 0.0;
                for (Node neigh : n.getNeighbours()) {
                    if (selectedNodes.contains(neigh)) {
                        sumOfAdjacentSelected += 1.0;
                    } else if (nodesToConsider.contains(neigh)) {
                        sumOfAdjacentConsiderable += 1.0;
                    }
                }
                double eval = ks * sumOfAdjacentSelected / (double)maxColors + ku * sumOfAdjacentConsiderable / (double)maxColors;
                logger.finest("Node " + n.getId() + " : " + eval);
                if (eval < minEval) {
                    selectNode = n;
                    minEval = eval;
                }
            }
            logger.finest("chose node " + selectNode.getId());
            selectedNodes.add(selectNode);
            c.selectNci(c.getNciById(selectNode.getId()));
            //remove all nodes of the same partition as selectednode
            for (Iterator<Node> it = nodesToConsider.iterator(); it.hasNext();) {
                Node nodeToConsider = it.next();
                if (nodeToConsider.getPartition() == selectNode.getPartition()) {
                    it.remove();
                }
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
