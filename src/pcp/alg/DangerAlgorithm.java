package pcp.alg;

import java.util.Collection;
import pcp.coloring.Coloring;
import pcp.coloring.NodeColorInfo;
import pcp.model.Graph;
import pcp.model.Node;

public class DangerAlgorithm {
    
    //Node Danger
    private static final double C = 1.0;
    private static final double k = 1.0;
    private static final double ku = 0.025;
    private static final double ka = 0.33;
    //Color Danger
    private static final double k1 = 1.0;
    private static final double k2 = 1.0;
    private static final double k3 = 0.5;
    private static final double k4 = 0.025;
    
    public static Coloring applyColoring( Graph g, int maxColors){
        Coloring coloring = new Coloring(g, maxColors);
        for( int i = 0; i < coloring.getUncoloredNodeColorInfos().size(); i++){
            NodeColorInfo nci = selectMostDangerousNci( coloring.getUncoloredNodeColorInfos(), maxColors);
            int c = selectColorForNci(nci, maxColors);
            coloring.colorNodeColorInfo(nci, c);
        }
        return coloring;
    }
    
    private static NodeColorInfo selectMostDangerousNci( Collection<NodeColorInfo> nodeSet, int maxColors){
        double maxND = Double.MAX_VALUE;
        NodeColorInfo chosenNci = null;
        for( NodeColorInfo nci : nodeSet){
            double F = C / Math.pow(maxColors - nci.getDiffcolored(maxColors), k);
            double nD = F + ku*nci.getUncolored() + ka*(nci.getColorsShared()/nci.getColorsAvailable());
            if( nD > maxND){
                chosenNci = nci;
            }
        }
        return chosenNci;
    }
    
    private static int selectColorForNci( NodeColorInfo nci, int maxColors){
        //TODO
        return 0;
    }
}
