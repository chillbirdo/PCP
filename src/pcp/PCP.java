package pcp;

import pcp.model.Graph;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcp.alg.DangerAlgorithm;
import pcp.alg.NodeSelector;
import pcp.coloring.Coloring;
import pcp.instancereader.InstanceReader;
import test.pcp.coloring.ColoringTest;

public class PCP {

    private static final Logger logger = Logger.getLogger(PCP.class.getName());
    public static final int NODE_UNCOLORED = -1;
    public static final int NODE_UNSELECTED = -2;

    public static void main(String[] args) {
        //logger.getHandlers()[0].setFormatter( new BriefLogFormatter());
        long time = System.currentTimeMillis();

        Graph g = null;
        Coloring c = null;
        try {
            //g = InstanceReader.readInstance( "pcp_instances/test/test4.pcp");
            g = InstanceReader.readInstance("pcp_instances/pcp/n20p5t2s1.pcp");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        logger.log(Level.FINE, "Reading instance complete. It took " + (System.currentTimeMillis() - time) + " ms.");
        logger.info(g.toString());

        int succeeded = 1;
        do {
            c = new Coloring(g);
            logger.finest(c.toString());

            logger.info("Selecting nodes:");
            NodeSelector.greedyMinDegree(c, g, Math.round(g.getHighestDegree() / 2f));
            int maxColors = c.getHighestDegreeSelected() + 1;
            c.initColorArrayOfEachNci(maxColors);

            logger.info(c.toString());

            logger.info("Applying coloring:");
            //TODO: getHighest degree of selected graph = new maxColor
            succeeded = DangerAlgorithm.applyColoring(c, maxColors);

            logger.info(c.toString());
            c.logColorStats();


            //tests
            ColoringTest test = new ColoringTest(c, g);
            test.performAll();

        }while( succeeded == 0);
    
    }
}
