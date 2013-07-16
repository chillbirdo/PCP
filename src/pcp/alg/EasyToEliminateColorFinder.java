package pcp.alg;

import java.util.ArrayList;
import java.util.Collections;
import pcp.model.Coloring;
import pcp.model.NodeColorInfo;

public class EasyToEliminateColorFinder {

    public static ArrayList<Integer> randomFind(Coloring c) {
        ArrayList<Integer> easiestToEleminate = new ArrayList<Integer>(c.getChromatic());
        for (int i = 0; i < c.getChromatic(); i++) {
            easiestToEleminate.add( i);
        }
        Collections.shuffle(easiestToEleminate);
        return easiestToEleminate;
    }

    public static ArrayList<Integer> find(Coloring c) {
        ArrayList<Integer> easiestToEleminate = new ArrayList<Integer>(c.getChromatic());
        for (int i = 0; i < c.getChromatic(); i++) {
            easiestToEleminate.add(0);
            for (NodeColorInfo nci : c.getSelectedColoredNCIs()) {
                if (nci.getColor() == i) {
                    //TODO
                }
            }
        }

        return easiestToEleminate;
    }
}
