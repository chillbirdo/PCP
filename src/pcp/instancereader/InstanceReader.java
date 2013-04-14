/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pcp.instancereader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import pcp.Graph;
import pcp.Node;

/**
 *
 * @author fritzgi
 */
public class InstanceReader {

    public static Graph readInstance(String filePath) throws FileNotFoundException, IOException, Exception {
        File file = new File(filePath);
        System.out.println( file.getAbsolutePath());
        if (!file.isFile()) {
            //TODO LOG ERROR
            //AND THROW EXC
        }
        //identify filetype
        if (file.getName().endsWith("pcp") || file.getName().endsWith("in")) {
            return readPCPInstance( file);
        } 
        else{
            throw new Exception("Unknown file type.");
        }
    }
    
    private static Graph readPCPInstance(File file) throws Exception{
        Scanner scanner = new Scanner(new FileReader(file));
        String line = scanner.nextLine();
        //System.out.println( line);
        
        String[] lineSplit = line.trim().split(" ");
        int lineCount = 1;
        if( lineSplit.length < 3){
            throw new Exception( "Invalid Instance Header.");
        }
        
        //phase1: count, to allow exact memory allocation
        int nodeAmount = new Integer(lineSplit[0]);
        int partitionAmount = new Integer(lineSplit[2]);
        int partitionSize[] = new int[partitionAmount];
        int[] neighbourAmount = new int[nodeAmount];
        for( int i : neighbourAmount){
            i=0;
        }
        while( scanner.hasNext()){
            line = scanner.nextLine();
            lineSplit = line.trim().split(" ");
            if( lineSplit.length == 1){
                int partition = new Integer(lineSplit[0]);
                partitionSize[partition]++;
            }else if( lineSplit.length == 2){
                int node1 = new Integer(lineSplit[0]);
                int node2 = new Integer(lineSplit[1]);
                neighbourAmount[node1]++;
                neighbourAmount[node2]++;
            }else{
                throw new Exception("Invalid InstanceData at line " + lineCount);
            }
            lineCount++;
        }
        int maxPartitionSize = 0;
        for( int i : partitionSize){
            if( i > maxPartitionSize){
                maxPartitionSize = i;
            }
        }
        
        //phase2 create node data
        scanner = new Scanner(new FileReader(file));
        scanner.nextLine();
        Node[] node = new Node[nodeAmount];
        Node[][] nodeInPartition = new Node[partitionAmount][maxPartitionSize];
        
        int nodeCount = 0;
        int[] nodesInPartitionCount = new int[partitionAmount];
        for( int i : nodesInPartitionCount){
            i=0;
        }
        while( scanner.hasNext()){
            line = scanner.nextLine();
            lineSplit = line.trim().split(" ");
            if( lineSplit.length == 1){
                int partition = new Integer(lineSplit[0]);
                node[nodeCount] = new Node( nodeCount, partition, neighbourAmount[nodeCount]);
                nodeInPartition[partition][nodesInPartitionCount[partition]] = node[nodeCount];
                nodesInPartitionCount[partition]++;
                nodeCount++;
            }else if( lineSplit.length == 2){
                Node node1 = node[new Integer(lineSplit[0])];
                Node node2 = node[new Integer(lineSplit[1])];
                node1.addNeighbour(node2);
                node2.addNeighbour(node1);
            }
        }
        return new Graph( node, nodeInPartition, partitionSize);
    }    
}
