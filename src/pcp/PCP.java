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
            // g = InstanceReader.readInstance("pcp_instances/pcp/n20p5t2s1.pcp");
            //g = InstanceReader.readInstance("pcp_instances/pcp/n40p5t2s5.pcp");
            g = InstanceReader.readInstance("pcp_instances/pcp/n120p5t2s5.pcp");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        logger.log(Level.FINE, "Reading instance complete. It took " + (System.currentTimeMillis() - time) + " ms.");
        logger.info(g.toString());

        boolean succeeded = false;
        int chromatic = Math.round(g.getHighestDegree() / 2f);
        Coloring stablecoloring = null;
        do {
            logger.fine("-----------------------------");
            logger.fine("--- CHROMATIC: " + chromatic);
            logger.fine("----------------------------");

            c = new Coloring(g);
            logger.finest(c.toString());

            logger.info("Selecting nodes:");
            NodeSelector.greedyMinDegree(c, g, chromatic);
            if (!succeeded) {
                chromatic = c.getHighestDegreeSelected() + 1;
            }
            c.initColorArrayOfEachNci(chromatic);

            logger.info(c.toString());
            logger.info("Applying coloring:");

            succeeded = DangerAlgorithm.applyColoring(c, chromatic);
            if (succeeded) {
                stablecoloring = c;
            }

            logger.info(c.toString());
            chromatic--;
        } while (succeeded);

        //tests
        stablecoloring.logColorStats();
        ColoringTest test = new ColoringTest(stablecoloring, g);
        test.performAll();
    }
}
