/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.evaluation;

import Jama.Matrix;
import java.io.File;
import java.util.*;
import org.simba.hierarchy.adagio.Adagio;
import org.simba.hierarchy.adagio.BottomUpAdagio;
import org.simba.hierarchy.adagio.LabeledMatrix;
import org.simba.hierarchy.ranking.ISIRanking;

/**
 * This class is deprecated. Please use the Evaluation class. This code only
 * exists for reference purposes.
 * @author ngonga
 */
public class DeprecatedEvaluation {

    public static double e = 2d;
    /**
     * Generates linear ranks for a given population size
     *
     * @param populationSize Size of group
     * @return Mapping from individuals to rank
     * @deprecated
     */
    public static Map<String, Integer> generateLinearRanks(int populationSize) {
        Map<String, Integer> ranks = new HashMap<String, Integer>();
        //generate random data

        for (int i = 0; i < populationSize; i++) {
            ranks.put((char) ('A' + i) + "", i + 1);
        }
        return ranks;

    }

    /**
     * Computes mean squared error of a solution
     *
     * @param population Size of the population
     * @param ranks
     * @param interactions
     * @return
     * @deprecated
     */
    public static double getMinLinearSquaredError(Map<String, Integer> ranks, LabeledMatrix m) {
        List<Integer> list = new ArrayList<>();
        for (String r : ranks.keySet()) {
            list.add(ranks.get(r));
        }
        Collections.sort(list);
        double error = 0d;
        for (int i = 0; i < list.size(); i++) {
            error = error + (list.get(i) - i) * (list.get(i) - i);
        }
        return Math.sqrt(error) / ranks.size();
    }

    /**
     * Computes mean squared error of I&SI ranking
     *
     * @param population Size of the population
     * @param ranks
     * @param interactions
     * @return
     */
    public static double getISISquaredError(Map<String, Integer> ranks, LabeledMatrix m) {
        Map<String, Integer> result = new ISIRanking().getRanks(m);
        double error = 0d;
        for (String s : ranks.keySet()) {
            error = error + (ranks.get(s) - result.get(s)) * (ranks.get(s) - result.get(s));
        }
        //System.out.println(m);
        //System.out.println(ranks + "=>" + Math.sqrt(error) / populationSize);
        //System.out.println(result + "=>" + Math.sqrt(error) / populationSize);
        return Math.sqrt(error) / ranks.size();
    }

    /**
     * Computes mean squared error of a solution
     *
     * @param population Size of the population
     * @param ranks
     * @param interactions
     * @return
     * @deprecated
     */
    public static double getDagMeanSquaredError(Map<String, Integer> ranks, LabeledMatrix m) {
        //System.out.println(ranks);
        Adagio d = new Adagio(m.data.getArrayCopy());
        d.run();
        d.clean();
        Map<String, Integer> result = d.getRankMap(m.labels);
        double error = 0d;
        for (String s : ranks.keySet()) {
            error = error + (ranks.get(s) - result.get(s)) * (ranks.get(s) - result.get(s));
        }
        //System.out.println(m);
        //System.out.println(ranks + "=>" + Math.sqrt(error) / populationSize);
        //System.out.println(result + "=>" + Math.sqrt(error) / populationSize);
        return Math.sqrt(error) / ranks.size();
    }

    //@deprecated 
    public static double getBottumUpDagMeanSquaredError(Map<String, Integer> ranks, LabeledMatrix m) {
        //System.out.println(ranks);
        BottomUpAdagio d = new BottomUpAdagio(m.data.getArrayCopy());
        d.run();
        d.clean();
        Map<String, Integer> result = d.getRankMap(m.labels);
        double error = 0d;
        for (String s : ranks.keySet()) {
            error = error + (ranks.get(s) - result.get(s)) * (ranks.get(s) - result.get(s));
        }
        //System.out.println(m);
        //System.out.println(ranks + "=>" + Math.sqrt(error) / populationSize);
        //System.out.println(result + "=>" + Math.sqrt(error) / populationSize);
        return Math.sqrt(error) / ranks.size();
    }

    /**
     * Computes mean squared error of a solution
     *
     * @param population Size of the population
     * @param ranks
     * @param interactions
     * @return
     * @deprecated
     */
    public static double getDavidDagMeanSquaredError(Map<String, Integer> ranks, LabeledMatrix m) {
        //System.out.println(ranks);
        Adagio d = new Adagio(m.data.getArrayCopy());

        double[][] P = new double[ranks.size()][ranks.size()];

        //compute P
        for (int i = 0; i < ranks.size(); i++) {
            for (int j = 0; j < ranks.size(); j++) {
                if (i == j) {
                    P[i][j] = 0;
                } else {
                    if (m.data.get(i, j) + m.data.get(j, i) == 0) {
                        P[i][j] = 0d;
                    } else {
                        P[i][j] = (m.data.get(i, j)) / (m.data.get(i, j) + m.data.get(j, i));
                    }
                }
            }
        }

        Matrix matrix = new Matrix(P);
        d.data = matrix;
        d.run();
        d.clean();
        Map<String, Integer> result = d.getRankMap(m.labels);
        double error = 0d;
        for (String s : ranks.keySet()) {
            error = error + (ranks.get(s) - result.get(s)) * (ranks.get(s) - result.get(s));
        }
        //System.out.println(ranks + "=>" + Math.sqrt(error) / populationSize);
        //System.out.println(result + "=>" + Math.sqrt(error) / populationSize);
        return Math.sqrt(error) / ranks.size();
    }

    //@deprecated 
    public static double getRandomMeanSquaredError(Map<String, Integer> ranks) {
        double error = 0d;
        double population = (double) ranks.size();
        //for each individual
        for (String s : ranks.keySet()) {
            int r = ranks.get(s);
            double e = 0;
            //compute average error of random assignment
            for (int i = 1; i <= population; i++) {
                e = e + (double) ((population - r) * (population - r));
            }
            error = error + Math.sqrt(e / population);
        }
        return error / population;
    }

    public static double getEloMeanSquaredError(Map<String, Integer> ranks, LabeledMatrix m) {
        int basicScore = 1000;
        Map<String, Integer> eloScores = new HashMap<>();
        Map<String, Integer> eloRanks = new HashMap<>();
        for (int i = 0; i < ranks.size(); i++) {
            //individual name
            String name = m.labels.get(i);
            //get eloScore
            int score = 0;
            int games = 0;
            for (int j = 0; j < ranks.size(); j++) {
                score = score + (int) (m.data.get(i, j) - m.data.get(j, i));
                games = games + (int) (m.data.get(i, j) + m.data.get(j, i));
            }
            if (games != 0) {
                score = (basicScore * games + 400 * score) / games;
            } else {
                score = basicScore;
            }
            eloScores.put(name, score);
        }
        //now sort by score and assign rank
        int counter = 1;
        //System.out.println(eloScores);
        while (eloRanks.size() < ranks.size()) {
            int max = -1;
            String name = "";
            if (eloScores.size() == 1) {
                name = eloScores.keySet().iterator().next();
            } else {
                for (String s : eloScores.keySet()) {
                    if (eloScores.get(s) > max) {
                        name = s;
                        max = eloScores.get(s);
                    }
                }
            }
            eloRanks.put(name, counter);
            counter++;
            eloScores.remove(name);
        }
        //System.out.println(ranks);
        //System.out.println(eloRanks);
        //finally compute error
        double error = 0d;
        for (String s : ranks.keySet()) {
            error = error + (ranks.get(s) - eloRanks.get(s)) * (ranks.get(s) - eloRanks.get(s));
        }
        return Math.sqrt(error) / ranks.size();
    }

    public static double getDavidMeanSquaredError(Map<String, Integer> ranks, LabeledMatrix m) {
        return getDavidMeanSquaredError(ranks, m, false);
    }

    public static double getCorrectedDavidMeanSquaredError(Map<String, Integer> ranks, LabeledMatrix m) {
        return getDavidMeanSquaredError(ranks, m, true);
    }

    public static double getDavidMeanSquaredError(Map<String, Integer> ranks, LabeledMatrix m, boolean corrected) {
        Map<String, Double> davidScores = new HashMap<>();
        Map<String, Integer> davidRanks = new HashMap<>();
        double[][] P = new double[ranks.size()][ranks.size()];
        double p;
        //compute P
        for (int i = 0; i < ranks.size(); i++) {
            for (int j = 0; j < ranks.size(); j++) {
                if (i == j) {
                    P[i][j] = 0;
                } else {
                    if (m.data.get(i, j) + m.data.get(j, i) == 0) {
                        P[i][j] = 0d;
                    } else {
                        p = (m.data.get(i, j)) / (m.data.get(i, j) + m.data.get(j, i));
                        P[i][j] = (m.data.get(i, j)) / (m.data.get(i, j) + m.data.get(j, i));
                        if (corrected) {
                            P[i][j] = P[i][j] - ((P[i][j] - 0.5) * (1 + (m.data.get(i, j) + m.data.get(j, i) + 1)));
                        }
                    }
                }
            }
        }

        //compute W & L
        List<Double> W = new ArrayList<>();
        List<Double> L = new ArrayList<>();

        for (int i = 0; i < ranks.size(); i++) {
            double w = 0d;
            double l = 0d;
            for (int j = 0; j < ranks.size(); j++) {
                w = w + P[i][j];
                l = l + P[j][i];
            }
            W.add(w);
            L.add(l);
        }

        for (int i = 0; i < ranks.size(); i++) {
            double score = W.get(i) - L.get(i);
            for (int j = 0; j < ranks.size(); j++) {
                score = score + P[i][j] * W.get(j) - P[j][i] * L.get(j);
            }
            davidScores.put(m.labels.get(i), score);
        }
        //now sort by score and assign rank
        int counter = 1;
//        System.out.println("DS = "+ davidScores);
        while (davidRanks.size() < ranks.size()) {
            double max = Integer.MIN_VALUE;
            String name = "";
            if (davidScores.size() == 1) {
                name = davidScores.keySet().iterator().next();
            } else {
                for (String s : davidScores.keySet()) {
                    if (davidScores.get(s) > max) {
                        name = s;
                        max = davidScores.get(s);
                    }
                }
            }
            davidRanks.put(name, counter);
            counter++;
            davidScores.remove(name);
        }
        //System.out.println(ranks);
//        System.out.println(davidRanks);
//                System.out.println("Scores = "+davidScores);
        //finally compute error
        double error = 0d;
        for (String s : ranks.keySet()) {
            error = error + (ranks.get(s) - davidRanks.get(s)) * (ranks.get(s) - davidRanks.get(s));
        }
        return Math.sqrt(error) / ranks.size();
    }

    public static void main(String args[])
    {
        computeRanking();
    }
    
    //deprecated
    public static void computeRanking() {

        int repetitions = 100;
        int populationSize = 20;
        int log = 10;
        int interactions = populationSize;

        String base = "E:/tmp/";

        StringBuilder dag = new StringBuilder();
        StringBuilder elo = new StringBuilder();
        StringBuilder david = new StringBuilder();
        StringBuilder symDag = new StringBuilder();
        StringBuilder bottomUpDag = new StringBuilder();
        StringBuilder bottomUpSymDag = new StringBuilder();
        StringBuilder correctedDavid = new StringBuilder();
        StringBuilder minLin = new StringBuilder();
        StringBuilder isi = new StringBuilder();
        String data = "exp"; //can be "lin" or "exp"

        for (int k = 0; k < log; k++) {
            interactions = interactions * 2;
            symDag.append(interactions).append("\t");
            bottomUpDag.append(interactions).append("\t");
            bottomUpSymDag.append(interactions).append("\t");
            dag.append(interactions).append("\t");
            elo.append(interactions).append("\t");
            david.append(interactions).append("\t");
            correctedDavid.append(interactions).append("\t");
            minLin.append(interactions).append("\t");
            isi.append(interactions).append("\t");

            for (int i = 0; i < repetitions; i++) {
                System.out.println("Repetition: " + i);
                Map<String, Integer> ranks = Evaluation.generateRanks(populationSize);
//                Map<String, Integer> ranks = generateLinearRanks(populationSize);
                LabeledMatrix m;
                if (data.equals("lin")) {
                    m = Evaluation.generateData(ranks, interactions);
                } else {
                    m = Evaluation.generateExponentialData(ranks, interactions);
                }
                elo.append(getEloMeanSquaredError(ranks, m)).append("\t");
                david.append(getDavidMeanSquaredError(ranks, m)).append("\t");
                correctedDavid.append(getCorrectedDavidMeanSquaredError(ranks, m)).append("\t");
                symDag.append(getDagMeanSquaredError(ranks, m.getTieResolvedMatrix())).append("\t");
                bottomUpDag.append(getBottumUpDagMeanSquaredError(ranks, m)).append("\t");
                bottomUpSymDag.append(getBottumUpDagMeanSquaredError(ranks, m.getTieResolvedMatrix())).append("\t");
                dag.append(getDagMeanSquaredError(ranks, m)).append("\t");
                minLin.append(getMinLinearSquaredError(ranks, m)).append("\t");
                isi.append(getISISquaredError(ranks, m)).append("\t");
            }
            symDag.append("\n");
            dag.append("\n");
            elo.append("\n");
            david.append("\n");
            correctedDavid.append("\n");
            bottomUpDag.append("\n");
            bottomUpSymDag.append("\n");
            minLin.append("\n");
            isi.append("\n");
        }

        String file;
        if (data.equals("lin")) {
            file = base + "/pop" + populationSize + "-rep" + repetitions + "-data" + data + "/";
        } else {
            file = base + "/pop" + populationSize + "-rep" + repetitions + "-data" + data + e + "/";
        }
        File f = new File(file);
        f.mkdirs();
        System.out.println(".");
        Evaluation.print(file + "dag", dag);

        System.out.println(".");
        Evaluation.print(file + "symDag", symDag);

        System.out.println(".");
        Evaluation.print(file + "bottomUpDag", bottomUpDag);

        System.out.println(".");
        Evaluation.print(file + "symBottomUpDag", bottomUpSymDag);

        System.out.println(".");
        Evaluation.print(file + "elo", elo);

        System.out.println(".");
        Evaluation.print(file + "david", david);

        System.out.println(".");
        Evaluation.print(file + "minlin", minLin);

        System.out.println(".");
        Evaluation.print(file + "correctedData", correctedDavid);

        System.out.println(".");
        Evaluation.print(file + "isi", isi);

        /*
         * System.out.println("Our Approach\n" + dag); System.out.println("Our
         * Approach (+p)\n" + symDag); System.out.println("Our Approach (+b)\n"
         * + bottomUpDag); System.out.println("Our Approach (+b+p)\n" +
         * bottomUpSymDag); System.out.println("ELO\n" + elo);
         * System.out.println("DAVID\n" + david);
         * System.out.println("MinLinearError\n" + minLin);
         * System.out.println("Corrected DAVID\n" + correctedDavid);
         */
    }
}
