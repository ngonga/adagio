/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.ranking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 *
 * @author ngonga
 */
public class DavidScore extends AbstractRanking{
    
    public static boolean CORRECTED = false;
    public Map<String, Double> getScores(LabeledMatrix m)
    {
        Map<String, Double> davidScores = new HashMap<>();
        double[][] P = new double[m.data.getRowDimension()][m.data.getColumnDimension()];
        double p;
        //compute P
        for (int i = 0; i < m.data.getRowDimension(); i++) {
            for (int j = 0; j < m.data.getColumnDimension(); j++) {
                if (i == j) {
                    P[i][j] = 0;
                } else {
                    if (m.data.get(i, j) + m.data.get(j, i) == 0) {
                        P[i][j] = 0d;
                    } else {
                        p = (m.data.get(i, j)) / (m.data.get(i, j) + m.data.get(j, i));
                        P[i][j] = (m.data.get(i, j)) / (m.data.get(i, j) + m.data.get(j, i));
                        if (CORRECTED) {
                            P[i][j] = P[i][j] - ((P[i][j] - 0.5) * (1 + (m.data.get(i, j) + m.data.get(j, i) + 1)));
                        }
                    }
                }
            }
        }

        //compute W & L
        List<Double> W = new ArrayList<>();
        List<Double> L = new ArrayList<>();

        for (int i = 0; i < m.data.getRowDimension(); i++) {
            double w = 0d;
            double l = 0d;
            for (int j = 0; j < m.data.getColumnDimension(); j++) {
                w = w + P[i][j];
                l = l + P[j][i];
            }
            W.add(w);
            L.add(l);
        }

        for (int i = 0; i < m.data.getRowDimension(); i++) {
            double score = W.get(i) - L.get(i);
            for (int j = 0; j < m.data.getColumnDimension(); j++) {
                score = score + P[i][j] * W.get(j) - P[j][i] * L.get(j);
            }
            davidScores.put(m.labels.get(i), score);
        }
        return davidScores;
    }
    
    public String getName()
    {
        return "DavidScore";
    }
    
    public static void main(String args[])
    {
         String path = "E:/Work/Projects/2014/HARBOR/ShizukaMcDonald/Sterck1997-3b-6.csv";
        LabeledMatrix m = new LabeledMatrix(path, null);
        System.out.println(new DavidScore().getScores(m));
    }
}
