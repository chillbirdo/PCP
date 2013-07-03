package pcp.alg;

import java.util.Random;
import pcp.coloring.Coloring;
import pcp.model.Graph;

public class InitialNodeSelection {
    public static void randomSelect( Coloring coloring){
        Graph g = coloring.getGraph();
        for( int i = 0; i < g.getPartitionSize().length; i++){
            int size = g.getPartitionSize()[i];
            Random randomGenerator = new Random();
            int r = randomGenerator.nextInt(size);
            int nodeId = g.getNodeInPartition()[i][r].getId();
            coloring.getNciById(nodeId).select();
            
        }
    }
    
    public static void greedyMinDegree( Coloring coloring){
        
    }
}
