/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.adagio;

import Jama.Matrix;

/**
 * Computes the Frobenius norm of a matrix
 * @author ngonga
 */
public class FrobeniusNorm {

    public static double getSquaredNorm(Matrix m1, Matrix m2) {
        double error = 0d;
        for (int i = 0; i < m1.getRowDimension(); i++) {
            for (int j = 0; j < m1.getColumnDimension(); j++) {
                  error = error + (m1.get(i, j) - m2.get(i, j))*(m1.get(i, j) - m2.get(i, j));
            }
        }
        return error;
    }

    public static double getNorm(Matrix m1, Matrix m2) {
       return Math.sqrt(getSquaredNorm(m1,m2));
    }
    
    public static double getSquaredNorm(Matrix m1) {
        double norm = 0d;
        for (int i = 0; i < m1.getRowDimension(); i++) {
            for (int j = 0; j < m1.getColumnDimension(); j++) {
                  norm = norm + m1.get(i, j)*m1.get(i, j);
            }
        }
        return norm;
    }
    
    public static double getNorm(Matrix m1) {        
        return Math.sqrt(getSquaredNorm(m1));
    }
    
     public static double getRemovedEdges(Matrix m1, Matrix m2) {
        double error = 0d;
        for (int i = 0; i < m1.getRowDimension(); i++) {
            for (int j = 0; j < m2.getColumnDimension(); j++) {
                  if((m1.get(i, j) == 0 && m2.get(i, j)!=0)
                          ||(m1.get(i, j)!=0 && m2.get(i, j)==0))
                      error++;
            }
        }
        return error;
    }
    public static double getNumberofEdges(Matrix m1) {
        double norm = 0d;
        for (int i = 0; i < m1.getRowDimension(); i++) {
            for (int j = 0; j < m1.getColumnDimension(); j++) {
                  if(m1.get(i, j)!=0) norm++;
            }
        }
        return norm;
    }
}
