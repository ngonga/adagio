/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.adagio;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.simba.hierarchy.evaluation.DeprecatedEvaluation;

/**
 * Extends Adagio by reserving the order in which the ranks are computed. 
 * @author ngonga
 */
public class BottomUpAdagio extends Adagio {
    
    public BottomUpAdagio(double[][] input) {
        super(input);
    }
    
    @Override
    public Map<Integer, Integer> computeRanks() {
        Map<Integer, Integer> ranks = new HashMap<>();
        Map<Integer, Integer> result = new HashMap<>();
        for (int i = 0; i < data.getColumnDimension(); i++) {
            double value = 0d;
            for (int j = 0; j < data.getRowDimension(); j++) {
                value = value + data.get(i, j);
            }
            if (value == 0d) {
                ranks.put(i, 1);
            }
        }

        //propage ranks          
        for (int i = 0; i < data.getRowDimension(); i++) {
            ranks = propagateRanks(ranks);
        }
        
        //finish up with rewritting ranks
        int max = 0;
        for(int individual: ranks.keySet())
            if(max < ranks.get(individual)) max = ranks.get(individual);
        for(int individual: ranks.keySet())
        {
            result.put(individual, max - ranks.get(individual)+1);
        }
        return result;
    }
    
    
    @Override
    Map<Integer, Integer> propagateRanks(Map<Integer, Integer> ranks) {
        Set<Integer> keys = new HashSet<>();
        for (int i : ranks.keySet()) {
            keys.add(i);
        }
        for (int i : keys) {
            //get parents
            Set<Integer> parents = new HashSet<>();
            for (int j = 0; j < data.getColumnDimension(); j++) {
                if (data.get(j, i) > 0) {
                    parents.add(j);
                }
            }
            for (int c : parents) {
                if (ranks.containsKey(c)) {
                    ranks.put(c, Math.max(ranks.get(c), ranks.get(i) + 1));
                } else {
                    ranks.put(c, ranks.get(i) + 1);
                }
            }
        }        
        return ranks;
    }
    
    public static void main(String args[]) {
//        String path = "E:/Work/Projects/2014/HARBOR/MS/male_dominance_data (MS).txt";
        String path = "E:/Work/Projects/2014/HARBOR/ShizukaMcDonald_Data/Kohda1991-2.csv";
        LabeledMatrix m = new LabeledMatrix(path,null);
//        System.out.println(m);
        Map<String, Integer> map = new HashMap<>();
        
        Adagio d = new BottomUpAdagio(m.getTieResolvedMatrix().data.getArrayCopy());
//        d.data = m.data;
        d.run();
        d.clean();
        m.data = d.data;
        System.out.println(m);
        visualizeGraph(d.data, m.labels, path + ".result");
        d.computeRanks(path + ".bu.ranks", m.labels);
        DeprecatedEvaluation.getDavidMeanSquaredError(d.getRankMap(m.labels), m);
    }
}
