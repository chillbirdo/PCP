/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pcp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import pcp.instancereader.InstanceReader;

public class PCP {
    private static final Logger logger = Logger.getLogger( PCP.class.getName());
    private static enum ColorState {
        
        SHARED,
        AVAILABLE,
        UNAVAILABLE,
        NOT_USED
    }    
    
    public static final int UNCOLORED = -1;
    
    public static void main(String[] args) {
        long time = System.currentTimeMillis();     
        
        Graph g;
        try{
           g = InstanceReader.readInstance( "pcp_instances\\test\\test3.pcp");
           //g = InstanceReader.readInstance( "pcp_instances\\pcp\\n120p5t2s5.pcp");
           // g = InstanceReader.readInstance( "pcp_instances\\in\\dsjc500.5-2.in");
//           g.setMaxColors( g.getHighestDegree()+1);
//           g.getNode( 0).setColor(0);
//           g.getNode( 2).setColor(1);

           logger.log( Level.FINE, g.toString());
           logger.log( Level.FINE, "----------");
           logger.log( Level.FINE, g.toColorString());
        }catch( Exception ex){
            ex.printStackTrace();
        }

        logger.log( Level.FINE,  "Reading instance complete. It took " + (System.currentTimeMillis() - time) + " ms.");
    }
}
