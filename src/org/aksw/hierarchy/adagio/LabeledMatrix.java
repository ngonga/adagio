/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.adagio;

import org.simba.hierarchy.io.CsvReader;
import Jama.Matrix;
import java.util.Map;
import org.simba.hierarchy.io.TabReader;
import org.simba.hierarchy.structureindex.AdjustedLandauIndex;
import org.simba.hierarchy.structureindex.DirectionalConsistencyIndex;
import org.simba.hierarchy.structureindex.Index;
import org.simba.hierarchy.structureindex.LandauIndex;

/**
 * Quadratic matrix that contains labels for individuals that correspond to 
 * each row and column
 * @author ngonga
 */
public class LabeledMatrix {

    public Matrix data;
    public Map<Integer, String> labels;

    public LabeledMatrix getTieResolvedMatrix()
    {
        LabeledMatrix m = new LabeledMatrix(data.getColumnDimension());
        m.labels = labels;
        m.data = new Matrix(data.getArrayCopy());
        for(int i=0; i<m.data.getRowDimension(); i++)
        {
            for(int j=0; j<i; j++)
            {
                if(m.data.get(i, j) >= m.data.get(j, i))
                {
                    m.data.set(i, j, m.data.get(i, j) - m.data.get(j, i));
                    m.data.set(j, i, 0d);
                }
                else
                {
                    m.data.set(j, i, m.data.get(j, i) - m.data.get(i, j));
                    m.data.set(i, j, 0d);
                }
            }
        }
        return m;
    }
    
    public LabeledMatrix(String path, String format) {
        if (format == "tab") {
            labels = TabReader.readLabels(path);
            data = new Matrix(TabReader.readData(path, labels.keySet().size()));
        } else {
            labels = CsvReader.readLabels(path);
            data = new Matrix(CsvReader.readData(path, labels.keySet().size()));
        }
        //DagDetector.writeGraphToFile(data, labels, path);
    }

    public LabeledMatrix(int n, Map<Integer, String> l) {
        data = new Matrix(n, n);
        labels = l;
    }

    public LabeledMatrix(int n) {
        data = new Matrix(n, n);
    }

    public String toString() {
        String r = "\t";
        for (int i : labels.keySet()) {
            r = r + labels.get(i) + "\t";
        }
        //remove last "\t" and then add "\n"
        r = r.substring(0, r.length()-1) + "\n";
        for (int i = 0; i < data.getRowDimension(); i++) {
            r = r + labels.get(i) + "\t";
            for (int j = 0; j < data.getColumnDimension()-1; j++) {
                r = r + data.get(i, j) + "\t";
            }
            r = r + data.get(i, data.getColumnDimension()-1) + "\n";
        }
        return r;
    }

//    public double getLandau() {
//        double score = 0d, va;
//        for (int i = 0; i < data.getRowDimension(); i++) {
//            va = 0d;
//            for (int j = 0; j < data.getColumnDimension(); j++) {
//                if (i != j) {
//                    if (data.get(i, j) > data.get(j, i)) {
//                        va = va + 1;
//                    } else if (data.get(i, j) == data.get(j, i)) {
//                        va = va + 0.5;
//                    }
//                }
//            }
////            System.out.print(va + "\t");
////            System.out.println(Math.pow(va - 0.5 * ((double) data.getRowDimension() - 1), 2));
//            score = score + Math.pow(va - 0.5 * ((double) data.getRowDimension() - 1), 2);
//        }
//        return (double) score * 12 / (Math.pow((double) data.getRowDimension(), 3) - (double) data.getRowDimension());
//    }
//
//    public Map<String, Double> getAdjustedLandau() {
//        double h = getLandau();
//        double count = 10000d;
//        double rCount = 0d;
//        double lCount = 0d;
//        double l0, lr, l = 0d;
//        for (int i = 0; i < count; i++) {
//            LabeledMatrix h0 = new LabeledMatrix(data.getRowDimension());
//            LabeledMatrix hr = new LabeledMatrix(data.getRowDimension());
//            //h0.data = new Matrix(data.getArrayCopy());
//            for (int j = 0; j < data.getRowDimension(); j++) {
//                for (int k = 0; k < data.getColumnDimension(); k++) {
//                    if (j == k) {
//                        h0.data.set(j, k, 0d);
//                    } else if (data.get(j, k) == 0 && data.get(k, j) == 0) {
//                        if (Math.random() >= 0.5) {
//                            h0.data.set(j, k, 1d);
//                            h0.data.set(k, j, 0d);
//                        } else {
//                            h0.data.set(j, k, 0d);
//                            h0.data.set(k, j, 1d);
//                        }
//                    } else {
//                        if (data.get(j, k) > data.get(k, j)) {
//                            h0.data.set(j, k, 1d);
//                            h0.data.set(k, j, 0d);
//                        } 
//                        else if (data.get(j, k) == data.get(k, j)) {
//                            h0.data.set(j, k, 0.5);
//                            h0.data.set(k, j, 0.5);
//                        }
//                        else {
//                            h0.data.set(j, k, 0d);
//                            h0.data.set(k, j, 1d);
//                        }
//                    }
//
//                }
//            }
//
//            for (int j = 0; j < data.getRowDimension(); j++) {
//                for (int k = 0; k < data.getColumnDimension(); k++) {
//                    if (j == k) {
//                        hr.data.set(j, k, 0);
//                    } else {
//                        if (Math.random() > 0.5) {
//                            hr.data.set(j, k, 1d);
//                            hr.data.set(k, j, 1d);
//                        } else {
//                            hr.data.set(j, k, 0d);
//                            hr.data.set(k, j, 1d);
//                        }
//                    }
//                }
//            }
//            l0 = h0.getLandau();
//            lr = hr.getLandau();
//            if (lr >= l0) {
//                rCount++;
//            }
//            if (lr <= l0) {
//                lCount++;
//            }
//            l = l + h0.getLandau();
//            
//        }
//        Map<String, Double> result = new HashMap<>();
//        result.put("index", l / count);
//        result.put("pr", rCount/count);
//        result.put("pl", lCount/count);
//        return result;
//    }
    public static void main(String args[]) {
        LabeledMatrix m = new LabeledMatrix("E:/Work/Projects/2014/HARBOR/tieClean.txt",null);
        System.out.println(m);
        System.out.println(m.getTieResolvedMatrix());
        Index li = new LandauIndex();
        Index al = new AdjustedLandauIndex();
        Index dci = new DirectionalConsistencyIndex();
        System.out.println(li.getValue(m));
        System.out.println(al.getValue(m));
        System.out.println(dci.getValue(m));
    }
}
