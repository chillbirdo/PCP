package pcp.alg;

import java.util.Collection;
import pcp.coloring.Coloring;
import pcp.model.Graph;
import pcp.model.Node;

public class DangerAlgorithm {
    
    private Graph g;
    private Coloring cC;
    private int maxColors;
    
    //Node Danger
    private final double C = 1.0;
    private final double k = 1.0;
    private final double ku = 0.025;
    private final double ka = 0.33;
    //Color Danger
    private final double k1 = 1.0;
    private final double k2 = 1.0;
    private final double k3 = 0.5;
    private final double k4 = 0.025;
    
    public DangerAlgorithm( Graph g){
        this.g = g;
        this.maxColors = getHighestDegree();
        g.initMaxColorsAvailable( maxColors);
        this.cC = new Coloring(g);
    }
    
    public void colorGraph(){
        for( int i = 0; i < cC.getUncoloredNodes().size(); i++){
            Node n = selectNextNodeOfSet( cC.getUncoloredNodes());
            int c = selectColorForNode(n);
            cC.colorNode(n, c);
        }
    }
    
    private Node selectNextNodeOfSet( Collection<Node> nodeSet){
        double minND = Double.MAX_VALUE;
        Node chosenNode = null;
        for( Node n : nodeSet){
            double F = C / Math.pow(maxColors - n.getDiffcolored(maxColors), k);
            double nD = F + ku*n.getUncolored() + ka*(n.getColorsShared()/n.getColorsAvailable());
            if( nD < minND){
                chosenNode = n;
            }
        }
        return chosenNode;
    }
    
    private int selectColorForNode( Node n){
        
        return 0;
    }
    
    private int getHighestDegree() {
        int maxdegree = 0;
        for (Node n : g.getNodes()) {
            if (n.getDegree() > maxdegree) {
                maxdegree = n.getDegree();
            }
        }
        return maxdegree;
    }
            
}
