/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.ranking;

import java.util.Map;
import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 *
 * @author ngonga
 */
public interface Ranking {
    Map<String, Integer> getRanks(LabeledMatrix m);
    Map<String, Double> getScores(LabeledMatrix m);
    void writeRanks(String file, Map<String, Integer> ranks);
    String getName();
}
