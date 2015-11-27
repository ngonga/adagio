/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.adagio;

import Jama.Matrix;

/**
 *
 * @author ngonga
 */
public class ManhattanNorm {
   
    public static double getNorm(Matrix m1, Matrix m2) {
              double error = 0d;
        for (int i = 0; i < m1.getRowDimension(); i++) {
            for (int j = 0; j < m1.getColumnDimension(); j++) {
                  error = error + Math.abs(m1.get(i, j) - m2.get(i, j));
            }
        }
        return error;
    }
    
    
    public static double getNorm(Matrix m1) {        
        double norm = 0d;
        for (int i = 0; i < m1.getRowDimension(); i++) {
            for (int j = 0; j < m1.getColumnDimension(); j++) {
                  norm = norm +  Math.abs(m1.get(i, j));
            }
        }
        return norm;
    }
    
}
