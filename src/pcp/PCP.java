/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pcp;

import pcp.model.Graph;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
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
            g = InstanceReader.readInstance( "pcp_instances\\test\\test3.pcp");
            Coloring c = DangerAlgorithm.applyColoring(g, g.getHighestDegree());

           logger.log( Level.INFO, g.toString());
           logger.log( Level.FINE, "----------");
           //logger.log( Level.FINE, g.toColorString());
        }catch( Exception ex){
            ex.printStackTrace();
        }

        logger.log( Level.FINE,  "Reading instance complete. It took " + (System.currentTimeMillis() - time) + " ms.");
    }
}
