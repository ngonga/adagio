/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.ranking;

import java.util.Map;
import org.simba.hierarchy.adagio.BottomUpAdagio;
import org.simba.hierarchy.adagio.LabeledMatrix;

/**
 * Implements Adagio+p+b
 * @author ngonga
 */
public class AdagioBottomUpPlusPreprocessing extends AdagioRanking{
    @Override
    public Map<String, Integer> getRanks(LabeledMatrix m) {
        BottomUpAdagio d = new BottomUpAdagio(m.getTieResolvedMatrix().data.getArrayCopy());
        d.run();
        d.clean();
        return d.getRankMap(m.labels);
    }
    
    public String getName()
    {
        return "AdagioPB";
    }
}
