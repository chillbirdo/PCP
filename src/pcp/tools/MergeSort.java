package pcp.tools;

import java.util.logging.Logger;
import pcp.Node;

public class MergeSort {

    private static final Logger logger = Logger.getLogger( MergeSort.class.getName());
    
    private static Node[] sort(int l, int r, Node[] nodeArr) {
        if (l < r) {
            int q = (l + r) / 2;
            sort(l, q, nodeArr);
            sort(q + 1, r, nodeArr);
            merge(l, q, r, nodeArr);
        }
        return nodeArr;
    }

    private static void merge(int l, int q, int r, Node[] nodeArr) {
        Node[] arr = new Node[nodeArr.length];
        int i, j;
        for (i = l; i <= q; i++) {
            arr[i] = nodeArr[i];
        }
        for (j = q + 1; j <= r; j++) {
            arr[r + q + 1 - j] = nodeArr[j];
        }
        i = l;
        j = r;
        for (int k = l; k <= r; k++) {
            if (arr[i].compareTo(arr[j]) <= 0) {
                nodeArr[k] = arr[i];
                i++;
            } else {
                nodeArr[k] = arr[j];
                j--;
            }
        }
    }
    
    public static Node[] sortNodes( Node[] nodeArr){
        return sort( 0, nodeArr.length - 1, nodeArr);
    }
} 