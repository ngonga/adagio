/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.io;

import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 * Reads input data and writes it to labeled matrix
 * @author ngonga
 */
public interface Reader {
    public LabeledMatrix read(String file); 
}
