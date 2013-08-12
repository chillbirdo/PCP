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
        int edges = new Integer(lineSplit[1]);
        int partitionAmount = new Integer(lineSplit[2]);
        int partitionSize[] = new int[partitionAmount];
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
            } else if (lineSplit.length == 2) {
                int node1 = new Integer(lineSplit[0]);
                int node2 = new Integer(lineSplit[1]);
                neighbourAmount[node1]++;
                neighbourAmount[node2]++;
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
        Node[] node = new Node[nodeAmount];
        Node[][] nodeInPartition = new Node[partitionAmount][maxPartitionSize];

        int nodeCount = 0;
        int[] nodesInPartitionCount = new int[partitionAmount];
        for (int i : nodesInPartitionCount) {
            i = 0;
        }
        int maxDegree = 0;
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            lineSplit = line.trim().split(" ");
            if (lineSplit.length == 1) {
                int partition = new Integer(lineSplit[0]);
                node[nodeCount] = new Node(nodeCount, partition, neighbourAmount[nodeCount]);
                nodeInPartition[partition][nodesInPartitionCount[partition]] = node[nodeCount];
                nodesInPartitionCount[partition]++;
                nodeCount++;
            } else if (lineSplit.length == 2) {
                Node node1 = node[new Integer(lineSplit[0])];
                Node node2 = node[new Integer(lineSplit[1])];
                node1.addNeighbour(node2);
                node2.addNeighbour(node1);
            }
        }
        removeEdgesInPartitions(node);
        return new Graph(node, nodeInPartition, partitionSize, edges);
    }

    /*
     * It is assumed that this method is called before any coloring is done
     */
    private static void removeEdgesInPartitions(Node[] node) {
        for (int i = 0; i < node.length; i++) {
            Node n = node[i];
            int neighboursToReduce = 0;
            for (int j = 0; j < n.getNeighbours().length; j++) {
                Node neigh = n.getNeighbour(j);
                if (n.getPartition() == neigh.getPartition()) {
                    neighboursToReduce++;
                    n.setNeighbour(j, null);
                }
            }
            if (neighboursToReduce > 0) {
                n.setDegree(n.getDegree() - neighboursToReduce);
                Node[] reducedNeighbours = new Node[n.getNeighbours().length - neighboursToReduce];
                int idx = 0;
                for (Node neigh : n.getNeighbours()) {
                    if (neigh != null) {
                        reducedNeighbours[idx] = neigh;
                        idx++;
                    }
                }
                n.setNeighbours(reducedNeighbours);
            }
        }
    }
}
