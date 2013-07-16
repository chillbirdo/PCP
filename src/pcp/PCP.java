package pcp;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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
        testParameters();
    }

    private static void optimized(){
         //logger.getHandlers()[0].setFormatter( new BriefLogFormatter());

        Graph g = null;
        Coloring c = null;
        try {
            //g = InstanceReader.readInstance( "pcp_instances/test/test4.pcp");
            //g = InstanceReader.readInstance("pcp_instances/pcp/n20p5t2s1.pcp");
            g = InstanceReader.readInstance("pcp_instances/pcp/n40p5t2s5.pcp");
            //g = InstanceReader.readInstance("pcp_instances/pcp/n120p5t2s5.pcp");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        logger.fine(g.toString());

        c = calculateInitialColoring(g, null, null);
        ArrayList<Integer> colorList = EasyToEliminateColorFinder.randomFind(c);

//        for( int color : colorList){
//        int color = colorList.get(0);
//        logger.info("EasyToEliminateColor: " + color);
//        for (Iterator<NodeColorInfo> it = c.getSelectedColoredNCIs().iterator(); it.hasNext();) {
//            NodeColorInfo nci = it.next();
//            if (nci.getColor() == color) {
//                c.uncolorNci(nci);
//                it.remove();
//                c.unselectNci(nci);
//                //SPEEDUP: write method to unselect a colored node
//            }
//        }
//        }


        //tests
        c.logColorStats();
        ColoringTest test = new ColoringTest(c, g);
        test.performAll();       
    }
    
    private static void testParameters() {
        Graph g = null;
        Coloring c = null;

        int bestresult = Integer.MAX_VALUE;
        String bestresultStr = "";
        for (double ks = 0.1; ks <= 2.0; ks += 0.1) {
            for (double ku = 0.1; ku <= 2.0; ku += 0.1) {
                int sumChromatic = 0;
                try {
                    File folder = new File("pcp_instances/pcp/");
                    for (final File fileEntry : folder.listFiles()) {
                        if (fileEntry.isFile()) {
                            g = InstanceReader.readInstance( fileEntry.getAbsolutePath());
                            c = calculateInitialColoring(g, ks, ku);
                            sumChromatic += c.getChromatic();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String out = "--- result: " + sumChromatic + " ks=" + ks + "; ku=" + ku + ";";
                if( sumChromatic < bestresult){
                    bestresult = sumChromatic;
                    bestresultStr = out;
                }
                logger.severe( out);
            }
        }
        logger.severe("\n\n best result: " + bestresultStr);
    }

    private static Coloring calculateInitialColoring(Graph g, Double ks, Double ku) {
        logger.info("Calculating initial solution..");
        Coloring c = null;
        boolean succeeded = false;
        int chromatic = Math.round(g.getHighestDegree() / 2f);
        Coloring stablecoloring = null;
        do {
            c = new Coloring(g);
            logger.finest(c.toString());

            logger.fine("Selecting nodes:");
            NodeSelector.greedyMinDegree(c, g.getPartitionSize().length, chromatic, ks, ku);
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
