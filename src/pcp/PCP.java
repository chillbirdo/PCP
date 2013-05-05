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
           g.setMaxColors( g.getHighestDegree()+1);
           g.getNode( 0).setColor(0);
           g.getNode( 2).setColor(1);
           //g = InstanceReader.readInstance( "pcp_instances\\pcp\\n20p5t2s1.pcp");
           //g = InstanceReader.readInstance( "pcp_instances\\in\\dsjc500.5-4.in");
           
//           g.getNode(0).setColor(1);
//           g.getNode(1).setColor(2);
           System.out.println( g.toString());
           System.out.println( "----------");
           System.out.println( g.toColorString());
        }catch( Exception ex){
            ex.printStackTrace();
        }


        
//        Node nei1 = new Node( 1,0,1);
//        nei1.setColor( 0);
//        Node nei2 = new Node( 2,0,1);
//        nei2.setColor( 3);
//        Node nei3 = new Node( 3,0,1);
//        nei3.setColor( 2);
//        Node nei4 = new Node( 4,0,1);
//        nei4.setColor( 1);
//
//        Node n = new Node( 0, 0, 4);
//        n.addNeighbour(nei1);
//        n.addNeighbour(nei2);
//        n.addNeighbour(nei3);
//        n.addNeighbour(nei4);
//        for( Node neighbour : n.getNeighbours()){
//            System.out.println( neighbour.getColor());
//        }
//        n.sortNeighboursByColor();
//        System.out.println( "------------");
//        for( Node neighbour : n.getNeighbours()){
//            System.out.println( neighbour.getColor());
//        }
        
        System.out.println( "Reading instance complete. It took " + (System.currentTimeMillis() - time) + " ms.");
    }
}
