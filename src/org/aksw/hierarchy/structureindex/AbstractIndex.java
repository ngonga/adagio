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
public abstract class AbstractIndex implements Index {
    
    public static final String VALUE = "value";
    public double getValue(LabeledMatrix m)
    {
        return getValues(m).get(VALUE);
    }
    
    public Map<String, Double> generateMap(double value)
    {
        Map<String, Double> map = new HashMap<>();
        map.put(VALUE, value);
        return map;
    }
}
