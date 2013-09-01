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
import java.util.logging.Level;
import java.util.logging.Logger;
import pcp.model.Graph;
import pcp.model.Node;

/**
 *
 * @author fritzgi
 */
public class InstanceReader {

    private static final Logger logger = Logger.getLogger(InstanceReader.class.getName());

    public static Graph readInstance(String filePath) throws FileNotFoundException, IOException, Exception {
        long time = System.currentTimeMillis();

        File file = new File(filePath);
        if (!file.isFile()) {
            logger.log(Level.SEVERE, "could not find file: " + file.getAbsolutePath());
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        //identify filetype
        if (file.getName().endsWith("pcp") || file.getName().endsWith("in")) {
            Graph g = readPCPInstance(file);
            logger.log(Level.INFO, "Reading instance complete. It took " + (System.currentTimeMillis() - time) + " ms.");
            return g;
        } else {
            throw new Exception("Unknown file type.");
        }
    }

    private static Graph readPCPInstance(File file) throws Exception {
        Scanner scanner = new Scanner(new FileReader(file));
        String line = scanner.nextLine();

        String[] lineSplit = line.trim().split(" ");
        int lineCount = 1;
        if (lineSplit.length < 3) {
            throw new Exception("Invalid Instance Header.");
        }

        //phase1: count, to allow exact memory allocation
        int nodeAmount = new Integer(lineSplit[0]);
        int[] nodeToPartition = new int[nodeAmount];
        int nodeToPartitionIdx = 0;
        int edgeAmount = new Integer(lineSplit[1]);
        int partitionAmount = new Integer(lineSplit[2]);
        int partitionSize[] = new int[partitionAmount];
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>(edgeAmount);
        int[] neighbourAmount = new int[nodeAmount];
        for (int i : neighbourAmount) {
            i = 0;
        }
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            lineSplit = line.trim().split(" ");
            if (lineSplit.length == 1) {
                int partition = new Integer(lineSplit[0]);
                partitionSize[partition]++;
                nodeToPartition[nodeToPartitionIdx] = partition;
                nodeToPartitionIdx++;
            } else if (lineSplit.length == 2) {
                int node1idx = new Integer(lineSplit[0]);
                int node2idx = new Integer(lineSplit[1]);
                if (nodeToPartition[node1idx] != nodeToPartition[node2idx]) {
                    neighbourAmount[node1idx]++;
                    neighbourAmount[node2idx]++;
                }
            } else {
                throw new Exception("Invalid InstanceData at line " + lineCount);
            }
            lineCount++;
        }

        int maxPartitionSize = 0;
        for (int i : partitionSize) {
            if (i > maxPartitionSize) {
                maxPartitionSize = i;
            }
        }

        //phase2 create node data
        scanner = new Scanner(new FileReader(file));
        scanner.nextLine();
        Node[] nodes = new Node[nodeAmount];
        Node[][] nodesInPartition = new Node[partitionAmount][maxPartitionSize];

        int nodeCount = 0;
        int[] nodesInPartitionCount = new int[partitionAmount];
        for (int i : nodesInPartitionCount) {
            i = 0;
        }
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            lineSplit = line.trim().split(" ");
            if (lineSplit.length == 1) {
                int partition = new Integer(lineSplit[0]);
                nodes[nodeCount] = new Node(nodeCount, nodesInPartitionCount[partition], partition, neighbourAmount[nodeCount]);
                nodesInPartition[partition][nodesInPartitionCount[partition]] = nodes[nodeCount];
                nodesInPartitionCount[partition]++;
                nodeCount++;
            } else if (lineSplit.length == 2) {
                Integer[] edge = {new Integer(lineSplit[0]), new Integer(lineSplit[1])};
                Node node1 = nodes[edge[0]];
                Node node2 = nodes[edge[1]];
                if (node1.getPartition() != node2.getPartition()) {
                    node1.addNeighbour(node2);
                    node2.addNeighbour(node1);
                    edges.add(edge);
                }
            }
        }
        return new Graph(nodes, nodesInPartition, partitionSize, edges);
    }
}
