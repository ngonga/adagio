/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.ranking;

import java.util.HashMap;
import java.util.Map;
import org.simba.hierarchy.adagio.Adagio;
import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 * Implements ranking based on Adagio
 * @author ngonga
 */
public class AdagioRanking extends AbstractRanking {

    public Map<String, Integer> getRanks(LabeledMatrix m) {
        Adagio d = new Adagio(m.data.getArrayCopy());
        d.run();
        d.clean();
        return d.getRankMap(m.labels);
    }

    /**
     * Computes scores based on the dominance class d. score = height of DAG - d +1.
     * Roots get a score of height of DAG.
     * @param m
     * @return Scores assigned by Dag2
     */     
    public Map<String, Double> getScores(LabeledMatrix m) {
        Map<String, Integer> ranks = getRanks(m);
        Map<String, Double> scores = new HashMap<>();
        double max = 0;
        for(String key: ranks.keySet())
        {
            if(ranks.get(key) > max)
                max = ranks.get(key);
        }
        
        for(String key: ranks.keySet())
        {
            scores.put(key, max + 1 -ranks.get(key));
        }
        return scores;
    }
    
    public String getName()
    {
        return "Adagio";
    }
}
