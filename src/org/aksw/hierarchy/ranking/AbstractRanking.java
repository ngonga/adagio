/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.ranking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 * Implements the default sorting method needed to convert scores to ranks
 * @author ngonga
 */
public abstract class AbstractRanking implements Ranking{
    
    /** 
     * Uses getScores to compute ranks assigned to individuals
     * @param m Input data
     * @return Map of individual name to rank 
     */
    public Map<String, Integer> getRanks(LabeledMatrix m)
    {
        //now sort by score and assign rank
        Map<String, Double> scores = getScores(m);
        Map<String, Integer> ranks = new HashMap<>();
        int counter = 1;
        //System.out.println(eloScores);
        while (!scores.isEmpty()) {
            double max = Double.NEGATIVE_INFINITY;
            String name = "";
            if (scores.size() == 1) {
                name = scores.keySet().iterator().next();
                scores.remove(name);
            } else {
                for (String s : scores.keySet()) {
                    if (scores.get(s) > max) {
                        name = s;
                        max = scores.get(s);
                    }
                }
            }
            ranks.put(name, counter);
            counter++;
            scores.remove(name);
        }
        
        return ranks;
    }
    
    /**
     * Write output of ranking to a file
     * @param file Name of output file
     * @param ranks Ranks
     */
    public void writeRanks(String file, Map<String, Integer> ranks) {
        try {
            File f = new File(file);
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (String s : ranks.keySet()) {
                bw.write(s + "\t" + ranks.get(s)+"\n");
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
