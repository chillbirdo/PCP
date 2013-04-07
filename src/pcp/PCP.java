/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pcp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcp.instancereader.IRGraph;
import pcp.instancereader.InstanceReader;

public class PCP {

    public static final int UNCOLORED = -1;
    
    public static void main(String[] args) {
        Graph g;
        try{
//            g = InstanceReader.readInstance( "pcp_instances\\pcp\\n20p5t2s1.pcp");
            g = InstanceReader.readInstance( "pcp_instances\\in\\dsjc500.5-4.in");
            //System.out.println( g);
        }catch( Exception ex){
            ex.printStackTrace();
        }

        //wait to exit
        System.out.println( "Reading instance complete. Press return.");
        byte[] b = new byte[256];
        try {
            System.in.read(b);
        } catch (IOException ex) {
            Logger.getLogger(PCP.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
