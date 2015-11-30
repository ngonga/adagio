/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.structureindex;

import java.util.HashMap;
import java.util.Map;
import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 *
 * @author ngonga
 */
public class AdjustedLandauIndex extends AbstractIndex {

    public static double COUNT = 10000d;

    public Map<String, Double> getValues(LabeledMatrix m) {
        LandauIndex li = new LandauIndex();
        double rCount = 0d;
        double lCount = 0d;
        double l0, lr, l = 0d;
        for (int i = 0; i < COUNT; i++) {
            LabeledMatrix h0 = new LabeledMatrix(m.data.getRowDimension());
            LabeledMatrix hr = new LabeledMatrix(m.data.getRowDimension());
            //h0.data = new Matrix(data.getArrayCopy());
            for (int j = 0; j < m.data.getRowDimension(); j++) {
                for (int k = 0; k < m.data.getColumnDimension(); k++) {
                    if (j == k) {
                        h0.data.set(j, k, 0d);
                    } else if (m.data.get(j, k) == 0 && m.data.get(k, j) == 0) {
                        if (Math.random() >= 0.5) {
                            h0.data.set(j, k, 1d);
                            h0.data.set(k, j, 0d);
                        } else {
                            h0.data.set(j, k, 0d);
                            h0.data.set(k, j, 1d);
                        }
                    } else {
                        if (m.data.get(j, k) > m.data.get(k, j)) {
                            h0.data.set(j, k, 1d);
                            h0.data.set(k, j, 0d);
                        } else if (m.data.get(j, k) == m.data.get(k, j)) {
                            h0.data.set(j, k, 0.5);
                            h0.data.set(k, j, 0.5);
                        } else {
                            h0.data.set(j, k, 0d);
                            h0.data.set(k, j, 1d);
                        }
                    }

                }
            }

            for (int j = 0; j < m.data.getRowDimension(); j++) {
                for (int k = 0; k < m.data.getColumnDimension(); k++) {
                    if (j == k) {
                        hr.data.set(j, k, 0);
                    } else {
                        if (Math.random() > 0.5) {
                            hr.data.set(j, k, 1d);
                            hr.data.set(k, j, 1d);
                        } else {
                            hr.data.set(j, k, 0d);
                            hr.data.set(k, j, 1d);
                        }
                    }
                }
            }
            l0 = li.getValue(h0);
            lr = li.getValue(hr);
            if (lr >= l0) {
                rCount++;
            }
            if (lr <= l0) {
                lCount++;
            }
            l = l + l0;
        }
        Map<String, Double> result = new HashMap<>();
        result.put(AbstractIndex.VALUE, l / COUNT);
        result.put("pr", rCount / COUNT);
        result.put("pl", lCount / COUNT);
        return result;
    }
}
