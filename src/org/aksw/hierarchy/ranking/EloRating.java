/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.ranking;

import java.util.HashMap;
import java.util.Map;
import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 * Computes Elo rating based on interaction ratings. Basic score is assumed to
 * be 1000.
 *
 * @author ngonga
 */
public class EloRating extends AbstractRanking {

    public static int BASICSCORE = 1000;        
    public Map<String, Double> getScores(LabeledMatrix m) {
        Map<String, Double> eloScores = new HashMap<>();
        Map<String, Integer> eloRanks = new HashMap<>();
        for (int i = 0; i < m.data.getRowDimension(); i++) {
            //individual name
            String name = m.labels.get(i);
            //get eloScore
            double score = 0d;
            int games = 0;
            for (int j = 0; j < m.data.getColumnDimension(); j++) {
                score = score + (int) (m.data.get(i, j) - m.data.get(j, i));
                games = games + (int) (m.data.get(i, j) + m.data.get(j, i));
            }
            if (games != 0) {
                score = (BASICSCORE * games + 400 * score) / games;
            } else {
                score = BASICSCORE;
            }
            eloScores.put(name, score);
        }
        return eloScores;
    }
    
    public String getName()
    {
        return "ELO";
    }
}
