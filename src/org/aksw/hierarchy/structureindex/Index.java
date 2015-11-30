/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.structureindex;

import java.util.Map;
import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 * Interface for indexes that aim to characterize a given dataset. Includes
 * indexes such as Landau's index or the directional consistency index (DCI). 
 * We use maps because the indexes can return p-values. All maps must contain 
 * at least "value" as key.
 * 
 * @author ngonga
 */
public interface Index {
    Map<String, Double> getValues(LabeledMatrix m);
    double getValue(LabeledMatrix m);
}
