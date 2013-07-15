package pcp;

import java.util.ArrayList;
import pcp.model.Graph;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcp.alg.DangerAlgorithm;
import pcp.alg.EasyToEliminateColorFinder;
import pcp.alg.NodeSelector;
import pcp.coloring.Coloring;
import pcp.coloring.NodeColorInfo;
import pcp.instancereader.InstanceReader;
import test.pcp.coloring.ColoringTest;

public class PCP {

    private static final Logger logger = Logger.getLogger(PCP.class.getName());
    public static final int NODE_UNCOLORED = -1;
    public static final int NODE_UNSELECTED = -2;

    public static void main(String[] args) {
        //logger.getHandlers()[0].setFormatter( new BriefLogFormatter());

        Graph g = null;
        Coloring c = null;
        try {
            //g = InstanceReader.readInstance( "pcp_instances/test/test4.pcp");
            g = InstanceReader.readInstance("pcp_instances/pcp/n20p5t2s1.pcp");
            //g = InstanceReader.readInstance("pcp_instances/pcp/n40p5t2s5.pcp");
            //g = InstanceReader.readInstance("pcp_instances/pcp/n120p5t2s5.pcp");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        logger.fine(g.toString());

        c = calculateInitialColoring(g);
        ArrayList<Integer> colorList = EasyToEliminateColorFinder.randomFind(c);
        
//        for( int color : colorList){
            int color = colorList.get(0);
            logger.info( "EasyToEliminateColor: " + color);
            for( NodeColorInfo nci : c.getSelectedColoredNCIs()){
                if( nci.getColor() == color){
                    c.uncolorNci(nci);
                }
            }
//        }
        

        //tests
        c.logColorStats();
        ColoringTest test = new ColoringTest(c, g);
        test.performAll();
    }

    private static Coloring calculateInitialColoring(Graph g) {
        logger.info("Calculating initial solution..");
        Coloring c = null;
        boolean succeeded = false;
        int chromatic = Math.round(g.getHighestDegree() / 2f);
        Coloring stablecoloring = null;
        do {
            c = new Coloring(g);
            logger.finest(c.toString());

            logger.fine("Selecting nodes:");
            NodeSelector.greedyMinDegree(c, g, chromatic);
            if (!succeeded) {
                chromatic = c.getHighestDegreeSelected() + 1;
            }
            c.initColorArrayOfEachNci(chromatic);

            logger.fine(c.toString());
            logger.fine("Applying coloring:");

            succeeded = DangerAlgorithm.applyColoring(c, chromatic);
            if (succeeded) {
                stablecoloring = c;
                logger.info("\tFound solution with " + chromatic + " colors.");
            }

            logger.fine(c.toString());
            chromatic--;
        } while (succeeded);
        return stablecoloring;
    }
}
