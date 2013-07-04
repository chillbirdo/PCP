package pcp.alg;

import java.util.Random;
import java.util.logging.Logger;
import pcp.coloring.Coloring;
import pcp.coloring.NodeColorInfo;
import pcp.model.Graph;

public class NodeSelector {

    private static final Logger logger = Logger.getLogger(NodeSelector.class.getName());
    
    public static void randomSelect( Coloring coloring){
        Graph g = coloring.getGraph();
        for( int i = 0; i < g.getPartitionSize().length; i++){
            int size = g.getPartitionSize()[i];
            Random randomGenerator = new Random();
            int r = randomGenerator.nextInt(size);
            int nodeId = g.getNodeInPartition()[i][r].getId();
            NodeColorInfo nci = coloring.getNciById(nodeId);
            coloring.selectNodeColorInfo(nci);
            logger.finest( "selected node " + nodeId);
        }
    }

    public static void testSelect( Coloring coloring){
        coloring.selectNodeColorInfo(coloring.getNciById(1));
        logger.finest( "selected node " + 1);
        coloring.selectNodeColorInfo(coloring.getNciById(2));
        logger.finest( "selected node " + 2);
        coloring.selectNodeColorInfo(coloring.getNciById(4));
        logger.finest( "selected node " + 4);
    }
    
    
    public static void greedyMinDegree( Coloring coloring){
        
    }
}
