/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ngonga
 */
public class CsvReader {

//    public static String TOKEN = "\t";
    public static String TOKEN = ",";
    public static double[][] readData(String path, int n) {
        double[][] matrix = new double[n][n];
        try {
            BufferedReader buf = new BufferedReader(new FileReader(path));
            String line = buf.readLine();
            line = buf.readLine();
            int i = 0;
            while (line != null) {
                String[] split = line.split(TOKEN);
                for (int j = 1; j < split.length; j++) {
                    if (split[j].isEmpty()) {
                        matrix[i][j - 1] = 0d;
                    } else {
                        matrix[i][j - 1] = Double.parseDouble(split[j]);
                    }
                }
                line = buf.readLine();
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matrix;
    }

    public static Map<Integer, String> readLabels(String path) {
        Map<Integer, String> result = new HashMap<>();
        try {
            BufferedReader buf = new BufferedReader(new FileReader(path));
            String line = buf.readLine();
            if (line != null && !line.isEmpty()) {
                String[] split = line.split(TOKEN);
                for (int i = 1; i < split.length; i++) {
                    result.put(i-1, split[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("Population size = "+result.keySet().size());
        return result;
    }
}
