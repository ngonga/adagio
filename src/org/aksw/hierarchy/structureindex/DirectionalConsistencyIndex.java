/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.structureindex;

import java.util.Map;
import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 * Compute the directional consistency index of a data set, which is defined as
 * (H-L)/(H+L), where H is the sum of interactions carried out by winners of
 * dyads while L is the same for losers.
 *
 * @author ngonga
 */
public class DirectionalConsistencyIndex extends AbstractIndex {

    public Map<String, Double> getValues(LabeledMatrix m) {
        double H = 0d;
        double L = 0d;
        for (int i = 0; i < m.data.getRowDimension(); i++) {
            for (int j = 0; j < i; j++) {
                H = H + Math.max(m.data.get(i, j), m.data.get(j, i));
                L = L + Math.min(m.data.get(i, j), m.data.get(j, i));
            }
        }
        return generateMap((H - L) / (H + L));
    }
    
    public static void main(String args[])
    {
        LabeledMatrix m = new LabeledMatrix("E:/Work/Projects/2014/HARBOR/landauTest.txt","csv");        
        DirectionalConsistencyIndex dci = new DirectionalConsistencyIndex();
        System.out.println(dci.getValue(m));
    }
}
