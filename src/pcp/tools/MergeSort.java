package pcp.tools;

import java.util.logging.Logger;

public class MergeSort {

    private static final Logger logger = Logger.getLogger( MergeSort.class.getName());
    
    private static double[] sort(int l, int r, double[] doubleArr) {
        if (l < r) {
            int q = (l + r) / 2;
            sort(l, q, doubleArr);
            sort(q + 1, r, doubleArr);
            merge(l, q, r, doubleArr);
        }
        return doubleArr;
    }

    private static void merge(int l, int q, int r, double[] doubleArr) {
        double[] arr = new double[doubleArr.length];
        int i, j;
        for (i = l; i <= q; i++) {
            arr[i] = doubleArr[i];
        }
        for (j = q + 1; j <= r; j++) {
            arr[r + q + 1 - j] = doubleArr[j];
        }
        i = l;
        j = r;
        for (int k = l; k <= r; k++) {
            if (arr[i] <= arr[j]) {
                doubleArr[k] = arr[i];
                i++;
            } else {
                doubleArr[k] = arr[j];
                j--;
            }
        }
    }
    
    public static double[] sortNodes( double[] doubleArr){
        return sort( 0, doubleArr.length - 1, doubleArr);
    }
} 