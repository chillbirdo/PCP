package pcp;

import pcp.model.Graph;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcp.alg.DangerAlgorithm;
import pcp.alg.NodeSelector;
import pcp.coloring.Coloring;
import pcp.instancereader.InstanceReader;

public class PCP {
    private static final Logger logger = Logger.getLogger( PCP.class.getName());
    
    public static final int UNCOLORED = -1;
    public static final int UNSELECTED = -2;
    public static enum ColorState {
        SHARED,
        AVAILABLE,
        UNAVAILABLE,
    }    
    
    public static void main(String[] args) {
        //logger.getHandlers()[0].setFormatter( new BriefLogFormatter());
        long time = System.currentTimeMillis();     
        
        Graph g;
        try{
           g = InstanceReader.readInstance( "pcp_instances/test/test4.pcp");
           logger.log( Level.FINE,  "Reading instance complete. It took " + (System.currentTimeMillis() - time) + " ms.");
           logger.info( g.toString());
           
           int maxColor = g.getHighestDegree();
           Coloring c = new Coloring( g, maxColor);
           logger.finest( c.toString());

           logger.info( "Selecting nodes:");
           //NodeSelector.randomSelect(c);
           NodeSelector.testSelect(c);
           logger.info( c.toString());
           
           logger.info( "Applying coloring:");
           //TODO: getHighest degree of selected graph = new maxColor
           DangerAlgorithm.applyColoring( c, maxColor);
           
           logger.info( c.toString());
           
        }catch( Exception ex){
            ex.printStackTrace();
        }

    }
}
