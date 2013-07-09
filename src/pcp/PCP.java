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
    
    public static final int NODE_UNCOLORED = -1;
    public static final int NODE_UNSELECTED = -2;
    
    public static void main(String[] args) {
        //logger.getHandlers()[0].setFormatter( new BriefLogFormatter());
        long time = System.currentTimeMillis();     
        
        Graph g;
        try{
           //g = InstanceReader.readInstance( "pcp_instances/test/test4.pcp");
           g = InstanceReader.readInstance( "pcp_instances/pcp/n20p5t2s1.pcp");
           logger.log( Level.FINE,  "Reading instance complete. It took " + (System.currentTimeMillis() - time) + " ms.");
           logger.info( g.toString());
           
           Coloring c = new Coloring( g);
           logger.finest( c.toString());

           logger.info( "Selecting nodes:");
           NodeSelector.randomSelect(c);
           //NodeSelector.testSelect(c);
           int maxColors = c.getHighestDegreeSelected() + 1;
           c.initColorArrayOfEachNci(maxColors);
           
           logger.info( c.toString());
           
           logger.info( "Applying coloring:");
           //TODO: getHighest degree of selected graph = new maxColor
           DangerAlgorithm.applyColoring( c, maxColors);
           
           logger.info( c.toString());
           
        }catch( Exception ex){
            ex.printStackTrace();
        }

    }
}
