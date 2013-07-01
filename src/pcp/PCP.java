package pcp;

import pcp.model.Graph;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcp.alg.DangerAlgorithm;
import pcp.coloring.Coloring;
import pcp.instancereader.InstanceReader;

public class PCP {
    private static final Logger logger = Logger.getLogger( PCP.class.getName());
    public static enum ColorState {
        SHARED,
        AVAILABLE,
        UNAVAILABLE,
    }    
    public static final int UNCOLORED = -1;
    
    public static void main(String[] args) {
        long time = System.currentTimeMillis();     
        
        Graph g;
        try{
           g = InstanceReader.readInstance( "pcp_instances/test/test4.pcp");
           logger.info( g.toString());
           
           logger.info( "Applying coloring:");
           Coloring c = DangerAlgorithm.applyColoring(g, g.getHighestDegree());
           logger.info( c.toStringColored());
           logger.info( c.toStringUncolored());
           
        }catch( Exception ex){
            ex.printStackTrace();
        }

        logger.log( Level.FINE,  "Reading instance complete. It took " + (System.currentTimeMillis() - time) + " ms.");
    }
}
