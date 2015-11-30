/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.ranking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.simba.hierarchy.adagio.Adagio;
import org.simba.hierarchy.adagio.LabeledMatrix;
import org.simba.hierarchy.evaluation.Evaluation;

/**
 * After "Finding a dominance order most consistent with a linear hierarchy: a
 * new procedure and review", deVries 1996
 *
 * @author ngonga
 */
public class ISIRanking extends AbstractRanking {

    public static int TMAX = 10;
    
    public double[][] getDominance(LabeledMatrix m) {
        double[][] dominance = new double[m.data.getRowDimension()][m.data.getColumnDimension()];
        for (int i = 0; i < m.data.getRowDimension(); i++) {
            for (int j = i; j < m.data.getColumnDimension(); j++) {
                if (i == j) {
                    dominance[i][i] = 0;
                } else if (m.data.get(i, j) > m.data.get(j, i)) {
                    dominance[i][j] = 1;
                    dominance[j][i] = 0;
                } else if (m.data.get(i, j) < m.data.get(j, i)) {
                    dominance[j][i] = 1;
                    dominance[i][j] = 0;
                } else {
                    if (m.data.get(i, j) > m.data.get(j, i)) {
                        dominance[i][j] = 0.5;
                        dominance[j][i] = 0.5;
                    }
                }
            }
        }
        return dominance;
    }

    public int getI(List<Integer> ranks, double[][] dominance) {
        int d = 0;
        for (int i = 0; i < ranks.size(); i++) {
            for (int j = i + 1; j < ranks.size(); j++) {
                if (dominance[ranks.get(j)][ranks.get(i)] > 0) {
                    d++;
                }
            }
        }
        return d;
    }

    public double getSI(List<Integer> ranks, double[][] dominance, LabeledMatrix m) {
        double d = 0;
        for (int i = 0; i < ranks.size(); i++) {
            for (int j = i + 1; j < ranks.size(); j++) {
                if (dominance[j][i] > 0) {
                    d = d + m.data.get(j, i) - m.data.get(i, j);
                }
            }
        }
        return d;
    }

    public List<Integer> clone(List<Integer> l) {
        List<Integer> r = new ArrayList<>();
        for (Integer i : l) {
            r.add(i);
        }
        return r;
    }

    public Map<String, Integer> getRanks(LabeledMatrix m) {
        List<Integer> ranks = new ArrayList<>();
        List<Integer> bestRanking;
        int tries = 0;
        for (int i = 0; i < m.data.getRowDimension(); i++) {
            ranks.add(i);
        }

        bestRanking = clone(ranks);

        double[][] d = getDominance(m);
        int Imin = getI(ranks, d);
        double SImin = getSI(ranks, d, m);

        boolean stop2 = false, stop1 = false;
        boolean breakLoop;

        double iterations = 0;
        while (!stop1) {
            //stop1 = true;
            while (!stop2 && iterations<=TMAX*100) {
                iterations++;
                stop2 = true;
                breakLoop = false;
                for (int i = 0; i < m.data.getRowDimension(); i++) {
                    for (int j = i + 1; j < m.data.getRowDimension(); j++) {
                        if (d[ranks.get(j)][ranks.get(i)] > 0) //inconsistency
                        {
                            int net = 0;
                            for (int k = i; k < j; k++) {
                                if (m.data.get(ranks.get(j), ranks.get(k)) != m.data.get(ranks.get(k), ranks.get(j)))
                                         {
                                    net = net + (int) Math.signum(m.data.get(ranks.get(j), ranks.get(k)) - m.data.get(ranks.get(k), ranks.get(j)));
                                }
                            }
                            //if swapping fixes inconsistencies
                            if (net > 0) {
                                int temp = ranks.get(i);
                                ranks.set(i, ranks.get(j));
                                ranks.set(j, temp);

                                stop2 = false;
                                breakLoop = true;
                                break;
                            }
                        }
                    }
                    if (breakLoop) {
                        break;
                    }
                }
            }

            int I = getI(ranks, d);
            double SI = getSI(ranks, d, m);

            if (I < Imin || (I == Imin && SI < SImin)) {
                bestRanking = clone(ranks);
                Imin = I;
                SImin = SI;
                //stop1 = false;
            }
            tries++;

            if (SImin > 0 && tries < TMAX) {
                stop1 = false;
                for (int j = 0; j < ranks.size(); j++) {
                    for (int i = 0; i < j; i++) {
                        if (d[ranks.get(j)][ranks.get(i)] > 0) {
                            int random = (int) Math.random() * j;
                            int temp = ranks.get(random);
                            ranks.set(random, ranks.get(j));
                            ranks.set(j, temp);
                        }
                    }
                }
            } else {
                stop1 = true;
            }
        }

        //create output
        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < bestRanking.size(); i++) {
            result.put(m.labels.get(bestRanking.get(i)), i + 1);
        }
        return result;
    }

    @Override
    public Map<String, Double> getScores(LabeledMatrix m) {
        Map<String, Double> scores = new HashMap<>();
        Map<String, Integer> ranks = getRanks(m);
        for (String s : ranks.keySet()) {
            scores.put(s, new Double(m.data.getRowDimension() - ranks.get(s) + 1));
        }
        return scores;
    }

    public String getName()
    {
        return "ISI";
    }
    
    public static void main(String args[]) {
//        String path = "E:/Work/Projects/2014/HARBOR/MS/male_dominance_data (MS).txt";
        String path = "E:/Work/Projects/2014/HARBOR/ShizukaMcDonald_Data/Collias1950-1.csv";
        LabeledMatrix m = new LabeledMatrix(path, null);
//        System.out.println(m);
        Map<String, Integer> ranking = new ISIRanking().getRanks(m);
        System.out.println(ranking);

    }
}
