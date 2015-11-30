/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.adagio;

import Jama.Matrix;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import org.simba.hierarchy.evaluation.DeprecatedEvaluation;
import org.simba.hierarchy.io.TabReader;

/**
 * Implements the basic version of Adagio. Can be fed a normal label matrix or with
 * a tie resolved matrix. The specialization in BottomUpDagDetector simply changes
 * the approach through which the ranks are computed. Hence, 4 variations of the
 * approach can be used.    
 * @author ngonga
 */
public class Adagio {

    public Matrix data;

    public Adagio(double[][] input) {
        if (input != null) {
            data = new Matrix(input);
        } else {
            data = null;
        }
        //data = data.transpose();
    }

    public void normalizeColumnWise() {

        double l;
        for (int j = 0; j < data.getColumnDimension(); j++) {
            l = 0d;
            for (int i = 0; i < data.getRowDimension(); i++) {
                l = l + data.get(i, j);
            }
            if (l > 0d) {
                l = Math.log(l + 1d);

                for (int i = 0; i < data.getRowDimension(); i++) {
                    data.set(i, j, Math.log(data.get(i, j) + 1d) / l);
                }
            }
        }
    }

    public void normalize() {
        double l;

        for (int i = 0; i < data.getRowDimension(); i++) {
            l = 0d;
            for (int j = 0; j < data.getColumnDimension(); j++) {
                l = l + data.get(i, j);
            }
            if (l > 0d) {
                l = Math.log(l + 1d);
                for (int j = 0; j < data.getColumnDimension(); j++) {
                    data.set(i, j, Math.log(data.get(i, j) + 1d) / l);
                }
            }
        }

        for (int i = 0; i < data.getRowDimension(); i++) {
            for (int j = i + 1; j < data.getColumnDimension(); j++) {
                if (data.get(i, j) > data.get(j, i)) {
                    data.set(i, j, data.get(i, j) - data.get(j, i));
                    data.set(j, i, 0d);
                } else if (data.get(j, i) > data.get(i, j)) {
                    data.set(j, i, data.get(j, i) - data.get(i, j));
                    data.set(i, j, 0d);
                } else {
                    data.set(i, j, 0d);
                    data.set(j, i, 0d);
                }
            }
        }
    }

    public int[][] getAdjacencyMatrix(Matrix m) {
        double[][] matrix = m.getArrayCopy();
        int[][] graph = new int[matrix.length + 1][matrix.length + 1];
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                if (i == 0 || j == 0) {
                    graph[i][j] = 0;
                } else {
                    if (matrix[i - 1][j - 1] > 0) {
                        graph[i][j] = 1;
                    } else {
                        graph[i][j] = 0;
                    }
                }
            }
        }
        return graph;
    }

    public void removeMin() {
        int minRow = -1, minColumn = -1;
        boolean found = false;
        double minValue = Double.MAX_VALUE;
        int[][] graph = getAdjacencyMatrix(data);
        Set<Set<Integer>> components = StrongConnectedComponents.getStrongComponents(graph);
        if(components.size() == 0) return;
        //System.out.println("Components = " + components);
        Set<List<Integer>> minList = new HashSet<>();
        for (int i = 0; i < data.getRowDimension(); i++) {
            for (int j = 0; j < data.getColumnDimension(); j++) {
                if (data.get(i, j) > 0 && data.get(i, j) == minValue) {
                    if (check(i, j, components)) {
                        minList.add(new ArrayList<>(Arrays.asList(new Integer(i), new Integer(j))));
                    }
                } else if (data.get(i, j) > 0 && data.get(i, j) < minValue) {
                    //check whether i && j are in the same connected component
                    if (check(i, j, components)) {
                        minList = new HashSet<>();
                        minList.add(new ArrayList<>(Arrays.asList(new Integer(i), new Integer(j))));
                        minValue = data.get(i, j);
                        found = true;
                    }
                }
            }
        }
        if (found) {
            for (List<Integer> l : minList) {
                data.set(l.get(0), l.get(1), 0d);
            }
        }
    }

    
    public void removeMin2(Set<Set<Integer>> components) {
        int minRow = -1, minColumn = -1;
        boolean found = false;
        double minValue = Double.MAX_VALUE;
        
//        if(components.size() == 0) return;
        //System.out.println("Components = " + components);
        Set<List<Integer>> minList = new HashSet<>();
        for (int i = 0; i < data.getRowDimension(); i++) {
            for (int j = 0; j < data.getColumnDimension(); j++) {
                if (data.get(i, j) > 0 && data.get(i, j) == minValue) {
                    if (check(i, j, components)) {
                        minList.add(new ArrayList<>(Arrays.asList(new Integer(i), new Integer(j))));
                    }
                } else if (data.get(i, j) > 0 && data.get(i, j) < minValue) {
                    //check whether i && j are in the same connected component
                    if (check(i, j, components)) {
                        minList = new HashSet<>();
                        minList.add(new ArrayList<>(Arrays.asList(new Integer(i), new Integer(j))));
                        minValue = data.get(i, j);
                        found = true;
                    }
                }
            }
        }
        if (found) {
            for (List<Integer> l : minList) {
                data.set(l.get(0), l.get(1), 0d);
            }
        }
    }
    
    
    public Set<Set<Integer>> checkDag2() {
        int[][] graph = getAdjacencyMatrix(data);
        Set<Set<Integer>> components = StrongConnectedComponents.getStrongComponents(graph);
        if(components.isEmpty()) return null;
        else return components;
    }
    
    public boolean checkDag() {
        double[][] init = new double[data.getRowDimension()][data.getColumnDimension()];
        for (int i = 0; i < data.getRowDimension(); i++) {
            for (int j = 0; j < data.getColumnDimension(); j++) {
                init[i][j] = data.get(i, j);
            }
        }
        //R = M
        Matrix R = new Matrix(init);
        Matrix S = new Matrix(init);
        for (int i = 1; i < data.getRowDimension(); i++) {
            S = S.times(data);
            R = R.plus(S);
        }


        for (int i = 0; i < R.getRowDimension(); i++) {
            if (R.get(i, i) > 0d) {
                return false;
            }
        }
        return true;
    }

    public void run() {
        //System.out.println("Normalizing ...");
        //normalize();
        //normalizeColumnWise();
        int i = 1;
        while (!checkDag() && i < 10000) {
            //System.out.println("Iteration " + i + "...");
            removeMin();
            i++;
        }
    }

    
    public void run2() {
        //System.out.println("Normalizing ...");
        //normalize();
        //normalizeColumnWise();
        int i = 1;
        Set<Set<Integer>> components = checkDag2();
        while (components!=null) {
            //System.out.println("Iteration " + i + "...");
            removeMin2(components);
            components = checkDag2();
        }
    }
    public static void print(Matrix m) {
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = 0; j < m.getColumnDimension(); j++) {
                System.out.print(m.get(i, j) + "\t");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    public static void visualizeGraph(Matrix m, Map<Integer, String> labels, String path) {
        //System.out.println(m.getColumnDimension());
        GraphPlotter.draw(m, labels, path);
    }

    public static void writeGraphToFile(Matrix m, Map<Integer, String> labels, String path) {
        //System.out.println(m.getColumnDimension());
        GraphPlotter.writeToFile(m, labels, path);
    }

    public void clean() {
        boolean flag = true;
        while (flag) {
            flag = false;
            for (int i = 0; i < data.getRowDimension(); i++) {
                for (int j = 0; j < data.getColumnDimension(); j++) {
                    for (int k = 0; k < data.getColumnDimension(); k++) {
                        if (data.get(i, j) > 0 && data.get(j, k) > 0 && data.get(i, k) > 0) {
                            data.set(i, k, 0d);
                            flag = true;
                        }
                    }
                }
            }
        }
    }

    public static void main(String args[]) {
//        String path = "E:/Work/Projects/2014/HARBOR/MS/male_dominance_data (MS).txt";
        String path = "E:/Work/Projects/2014/HARBOR/ShizukaMcDonald/Blatrix2004-1.csv";
        TabReader.TOKEN=",";
//        String path = "E:/Work/Projects/2014/HARBOR/Test/paper_example.txt";
        LabeledMatrix m = new LabeledMatrix(path, "csv");
        System.out.println(m);
        Map<String, Integer> map = new HashMap<>();

        Adagio d = new Adagio(m.getTieResolvedMatrix().data.getArrayCopy());
        d.data = m.data;
        d.run();
        d.clean();
        m.data = d.data;
        System.out.println(m);
        visualizeGraph(d.data, m.labels, path + ".result");
        d.computeRanks(path + ".ranks", m.labels);
        DeprecatedEvaluation.getDavidMeanSquaredError(d.getRankMap(m.labels), m);
    }

    /**
     * Write output of ranking to a file
     *
     * @param file Name of output file
     * @param ranks Ranks
     */
    public void writeMatrix(LabeledMatrix m, String file) {
        try {
            File f = new File(file);
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                String s = m.toString();
                s = s.replaceAll("\t", ",");
                //s = s.replaceAll(",\n", "\n");
                bw.write(m.toString().replaceAll("\t", ","));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean check(int i, int j, Set<Set<Integer>> components) {
        for (Set<Integer> component : components) {
            if (component.contains(i) && component.contains(j)) {
                return true;
            }
        }
        return false;
    }

    public Map<Integer, Integer> computeRanks() {
        Map<Integer, Integer> ranks = new HashMap<>();
        for (int i = 0; i < data.getColumnDimension(); i++) {
            double value = 0d;
            for (int j = 0; j < data.getRowDimension(); j++) {
                value = value + data.get(j, i);
            }
            if (value == 0d) {
                ranks.put(i, 1);
            }
        }

        //propage ranks          
        for (int i = 0; i < data.getRowDimension(); i++) {
            ranks = propagateRanks(ranks);
        }
        return ranks;
    }

    public Map<String, Integer> getRankMap(Map<Integer, String> labels) {
        Map<Integer, Integer> ranks = computeRanks();
        Map<String, Integer> results = new HashMap<String, Integer>();
        for (int i : ranks.keySet()) {
            results.put(labels.get(i), ranks.get(i));
        }
        return results;
    }

    public void computeRanks(String file, Map<Integer, String> labels) {
        //find root
        Map<Integer, Integer> ranks = computeRanks();
        //finally write out ranks
        try {
            File f = new File(file);
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for (Integer i : ranks.keySet()) {
                bw.write(labels.get(i) + "\t" + ranks.get(i) + "\n");
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Map<Integer, Integer> propagateRanks(Map<Integer, Integer> ranks) {
        Set<Integer> keys = new HashSet<>();
        for (int i : ranks.keySet()) {
            keys.add(i);
        }
        for (int i : keys) {
            //get children
            Set<Integer> children = new HashSet<>();
            for (int j = 0; j < data.getColumnDimension(); j++) {
                if (data.get(i, j) > 0) {
                    children.add(j);
                }
            }
            for (int c : children) {
                if (ranks.containsKey(c)) {
                    ranks.put(c, Math.max(ranks.get(c), ranks.get(i) + 1));
                } else {
                    ranks.put(c, ranks.get(i) + 1);
                }
            }
        }
        return ranks;
    }
}
