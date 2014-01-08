/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pcp.tools;

import pcp.model.Coloring;

/**
 *
 * @author gilbert
 */
public class ColoringDoubleDouble {
    
    public Coloring coloring;
    public double conflictingNodes;
    public double recolorings;

    public ColoringDoubleDouble( Coloring coloring, double conflictingNodes, double recolorings){
        this.coloring = coloring;
        this.conflictingNodes = conflictingNodes;
        this.recolorings = recolorings;
    }
}
