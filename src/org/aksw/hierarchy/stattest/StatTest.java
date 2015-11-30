/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.stattest;

import Jama.Matrix;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 *
 * @author ngonga
 */
public class StatTest {

    public static Map<String, Double> getExpStats(Matrix m) {
        double low, high;
        double exp = 0;
        int count = 0;
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = i + 1; j < m.getColumnDimension(); j++) {
                low = -1;
                high = 1;
                if (m.get(i, j) > m.get(j, i)) {
                    low = m.get(j, i);
                    high = m.get(i, j);
                } else if (m.get(j, i) > m.get(i, j)) {
                    low = m.get(i, j);
                    high = m.get(j, i);
                }
                if (low > 1) {
                    count++;
                    exp = Math.log(high) / low;
                }
            }
        }
        Map<String, Double> map = new HashMap<>();
        map.put("exp", Math.pow(10, exp / count));

        double error = 0;
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = i + 1; j < m.getColumnDimension(); j++) {
                low = -1;
                high = 1;
                if (m.get(i, j) > m.get(j, i)) {
                    low = m.get(j, i);
                    high = m.get(i, j);
                } else if (m.get(j, i) > m.get(i, j)) {
                    low = m.get(i, j);
                    high = m.get(j, i);
                }
                if (low > 1) {
                    error = error + (high - Math.pow(exp, low)) * (high - Math.pow(exp, low));
                }
            }
        }

        map.put("error", Math.sqrt(error));
        return map;
    }

    public static Map<String, Double> getPolynomialStats(Matrix m) {
        double low, high;
        double exp = 0;
        int count = 0;
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = i + 1; j < m.getColumnDimension(); j++) {
                low = -1;
                high = 1;

                if (m.get(i, j) > m.get(j, i)) {
                    low = m.get(j, i);
                    high = m.get(i, j);
                } else if (m.get(j, i) > m.get(i, j)) {
                    low = m.get(i, j);
                    high = m.get(j, i);
                }
                if (low > 1) {
                    //System.out.println("Mij vs Mji:" + m.get(i, j) + " " + m.get(j, i));
                    count++;
                    exp = exp + Math.log(high) / Math.log(low);
                }
            }
        }
        Map<String, Double> map = new HashMap<>();
        map.put("exp", exp / count);

        double error = 0;
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = i + 1; j < m.getColumnDimension(); j++) {
                low = -1;
                high = 1;
                if (m.get(i, j) > m.get(j, i)) {
                    low = m.get(j, i);
                    high = m.get(i, j);
                } else if (m.get(j, i) > m.get(i, j)) {
                    low = m.get(i, j);
                    high = m.get(j, i);
                }
                if (low > 1) {
                    error = error + (high - Math.pow(low, exp)) * (high - Math.pow(low, exp));
                }
            }
        }

        map.put("error", Math.sqrt(error));
        return map;
    }

    public static void main(String args[]) {
//        String path = "E:/Work/Projects/2014/HARBOR/MS/male_dominance_data (MS).txt";
        String ending = "csv";
        String folder = "E:/Work/Projects/2014/HARBOR/ShizukaMcDonald/";
        File f = new File(folder);
        if (f.isDirectory()) {
            String[] filenames = f.list();
            for (int i = 0; i < filenames.length; i++) {
                if (filenames[i].endsWith(ending)) {
                    //System.out.println("Processing " + filenames[i]);
                    String path = folder + "/" + filenames[i];
                    LabeledMatrix m = new LabeledMatrix(path, null);
//        System.out.println(m);
                    Map<String, Double> expstats = getExpStats(m.data);
                    Map<String, Double> polystats = getPolynomialStats(m.data);
                    System.out.println(filenames[i] + "\t" + expstats.get("exp")
                            + "\t" + expstats.get("error") + "\t" + polystats.get("exp")
                            + "\t" + polystats.get("error"));
                    //System.out.println(filenames[i] + "poly error \t" +getPolynomialStats(m.data));
                }
            }
        }
    }
}
