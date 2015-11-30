/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.ranking;

import java.util.Map;
import org.simba.hierarchy.adagio.Adagio;
import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 * Implements Adagio+p
 * @author ngonga
 */
public class AdagioPlusPreprocessingRanking extends AdagioRanking {
    
    public Map<String, Integer> getRanks(LabeledMatrix m) {
        //simply use tie-resolved m and not m
        Adagio d = new Adagio(m.getTieResolvedMatrix().data.getArrayCopy());
        d.run();
        d.clean();
        return d.getRankMap(m.labels);
    }
    
    public String getName()
    {
        return "AdagioP";
    }
}
