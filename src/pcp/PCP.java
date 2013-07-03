package pcp;

import pcp.model.Graph;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcp.alg.DangerAlgorithm;
import pcp.alg.InitialNodeSelection;
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
           
           logger.info( "Selecting nodes:");
           int maxColor = g.getHighestDegree();
           Coloring c = new Coloring( g, maxColor);
           InitialNodeSelection.randomSelect(c);
           
           logger.info( "Applying coloring:");
           DangerAlgorithm.applyColoring( c, maxColor);
           
           logger.info( c.toStringColored());
           logger.info( c.toStringUncolored());
           logger.info( c.toStringUnselected());
           
        }catch( Exception ex){
            ex.printStackTrace();
        }

    }
}
