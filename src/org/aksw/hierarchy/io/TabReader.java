/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.io;

import org.simba.hierarchy.io.CsvReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ngonga
 */
public class TabReader extends CsvReader {

    public static Map<String, Integer> reversedMap;

    public static Map<Integer, String> readLabels(String path) {
        Map<Integer, String> result = new HashMap<>();
        reversedMap = new HashMap<>();
        int count = 0;
        Set<String> labels = new HashSet<>();
        String line = "";
        try {
            BufferedReader buf = new BufferedReader(new FileReader(path));
            line = buf.readLine(); // skip first line             
            if (line != null && !line.isEmpty()) {
                line = buf.readLine();
                while (line != null) {
                    String[] split = line.split(TOKEN);
                    for (int i = 0; i < 2; i++) {
                        if (!labels.contains(split[i])) {
                            labels.add(split[i]);
                            result.put(count, split[i]);
                            reversedMap.put(split[i], count);
                            count++;
                            //System.out.println(path+"\t"+line);
                        }
                    }
                    line = buf.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(line);
        }
        //System.out.println("Population size = " + result.keySet().size());
        return result;
    }

    public static double[][] readData(String path, int n) {
        Map<Integer, String> labels = readLabels(path);
        n = labels.keySet().size();
        double[][] matrix = new double[n][n];
        //init
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = 0;
            }
        }

        //get data
        try {
            BufferedReader buf = new BufferedReader(new FileReader(path));
            String line = buf.readLine();
            line = buf.readLine();
            int i = 0;
            while (line != null) {
                String[] split = line.split(TOKEN);
                int source = reversedMap.get(split[0]);
                int target = reversedMap.get(split[1]);
                matrix[source][target] = Integer.parseInt(split[2]);
                line = buf.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matrix;
    }

    public static void main(String args[]) {
        CsvReader.TOKEN = "\t";
        System.out.println(readLabels("E:/Work/Projects/2014/HARBOR/Data/df_1.txt"));
        double[][] mat = readData("E:/Work/Projects/2014/HARBOR/Data/df_1.txt", 1);
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat.length; j++) {
                System.out.print(mat[i][j] + "\t");
            }
            System.out.println("\n");
        }
    }
}
