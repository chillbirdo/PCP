package pcp.instancereader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import pcp.Graph;
import pcp.Node;
import sun.misc.IOUtils;

/*
 * Reads instances of different file formats
 */
public class InstanceReader {

    public static Graph readInstance(String filePath) throws FileNotFoundException, IOException, Exception {
        File file = new File(filePath);
        if (!file.isFile()) {
            //TODO LOG ERROR
            //AND THROW EXC
        }
        Scanner scanner = new Scanner(new FileReader(file));
        //identify filetype
        if (file.getName().endsWith("pcp") || file.getName().endsWith("in")) {
            return readPCPInstance( scanner);
        } 
//        else if (file.getName().endsWith("in")) {
//            return readINInstance( scanner);
//      }
        else{
            throw new Exception("Unknown file type.");
        }
    }
    
    private static Graph readPCPInstance( Scanner scanner) throws Exception{
        String line = scanner.nextLine();
        //System.out.println( line);
        
        String[] lineSplit = line.trim().split(" ");
        int lineCount = 1;
        if( lineSplit.length < 3){
            throw new Exception( "Invalid Instance Header.");
        }
        IRGraph iRG = new IRGraph( new Integer( lineSplit[0]), new Integer( lineSplit[2]));
        while( scanner.hasNext()){
            line = scanner.nextLine();
            //System.out.println( line);
            lineSplit = line.trim().split(" ");
            if( lineSplit.length == 1){
                iRG.addNode( new Integer(lineSplit[0]));
            }else if( lineSplit.length == 2){
                IRNode n1 = iRG.getNode(new Integer(lineSplit[0]));
                IRNode n2 = iRG.getNode(new Integer(lineSplit[1]));
                n1.addNeighbour(iRG.getNodes().indexOf(n2));
                n2.addNeighbour(iRG.getNodes().indexOf(n1));
            }else{
                throw new Exception("Invalid InstanceData at line " + lineCount);
            }
            lineCount++;
        }
        System.out.println( "Converting Graph..");
        return iRG.toGraph();
    }
}