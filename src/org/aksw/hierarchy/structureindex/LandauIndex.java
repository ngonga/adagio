/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.structureindex;

import java.util.HashMap;
import java.util.Map;
import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 * Implements Landau's index
 * @author ngonga
 */
public class LandauIndex extends AbstractIndex {

    public Map<String, Double> getValues(LabeledMatrix m) {
        double score = 0d, va;
        for (int i = 0; i < m.data.getRowDimension(); i++) {
            va = 0d;
            for (int j = 0; j < m.data.getColumnDimension(); j++) {
                if (i != j) {
                    if (m.data.get(i, j) > m.data.get(j, i)) {
                        va = va + 1;
                    } else if (m.data.get(i, j) == m.data.get(j, i)) {
                        va = va + 0.5;
                    }
                }
            }
//            System.out.print(va + "\t");
//            System.out.println(Math.pow(va - 0.5 * ((double) data.getRowDimension() - 1), 2));
            score = score + Math.pow(va - 0.5 * ((double) m.data.getRowDimension() - 1), 2);
        }
        double value = (double) score * 12 / (Math.pow((double) m.data.getRowDimension(), 3) - (double) m.data.getRowDimension());
        
        return generateMap(value);
    }
    
    
}
