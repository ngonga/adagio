/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.evaluation;

import java.util.Map;
import org.simba.hierarchy.adagio.LabeledMatrix;
import org.simba.hierarchy.ranking.Ranking;

/**
 * Measures the robustness of a ranking approach
 * @author ngonga
 */
public class RobustnessEvaluation {
    
    public double getRobustness (Ranking r, LabeledMatrix m, double portion)
    {
        //1. Clone m
        Map<String, Integer> ranks = r.getRanks(m);
        //2. Run ranking on m
        LabeledMatrix copy = new LabeledMatrix(m.data.getColumnDimension());
        copy.labels = m.labels;
        copy.data = m.data.copy();
        //3. Measure difference between results and old ranking
       return 1d;
    }
}
