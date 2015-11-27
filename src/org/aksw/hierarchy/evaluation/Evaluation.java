/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.evaluation;

import Jama.Matrix;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import org.simba.hierarchy.adagio.BottomUpAdagio;
import org.simba.hierarchy.adagio.Adagio;
import org.simba.hierarchy.adagio.LabeledMatrix;
import org.simba.hierarchy.ranking.*;

/**
 * Runs the evaluation to check out well our approach performs. Needs to reuse
 * exisiting implementations.
 *
 * @author ngonga
 */
public class Evaluation {

    // Exponent for exponential simulation
    public static double e = 2;

    /**
     * Generic evaluation method for a ranking
     *
     * @param ranking Ranking algorithm
     * @param ranks Reference ranks
     * @param m Interaction matrix
     * @return Error
     */
    public static double evaluateRanking(Ranking ranking, Map<String, Integer> ranks, LabeledMatrix m) {
        Map<String, Integer> result = ranking.getRanks(m);
        double error = 0d;
        for (String s : ranks.keySet()) {
            error = error + (ranks.get(s) - result.get(s)) * (ranks.get(s) - result.get(s));
        }
        return Math.sqrt(error)/ ranks.size();
    }

    /**
     * Generates an interaction matrix based on ranks and a given number of
     * interactions.
     *
     * @param ranks Reference ranks
     * @param interactions Number of interactions to simulate
     * @return Interaction matrix according to exponential model
     */
    public static LabeledMatrix generateExponentialData(Map<String, Integer> ranks, int interactions) {
        Map<Integer, String> labels = new HashMap<>();
        int count = 0;
        for (String name : ranks.keySet()) {
            labels.put(count, name);
            count++;
        }

        //init interactions
        int size = ranks.keySet().size();
        LabeledMatrix m = new LabeledMatrix(size, labels);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                m.data.set(i, j, 0d);
            }
        }

        //compute interactions
        int source, target;
        for (int i = 0; i < interactions; i++) {
            // chose individuals
            source = (int) (Math.random() * size);
            target = (int) (Math.random() * size);
            while (source == target) {
                target = (int) (Math.random() * size);
            }

            //get the interaction
            //System.out.println("Source: "+source+"\t Target: "+target);
            double r = Math.random();
            double t = ((double) Math.pow(e, ranks.get(labels.get(source)))) / ((double) (Math.pow(e, ranks.get(labels.get(source)))
                    + Math.pow(e, ranks.get(labels.get(target)))));
            if (r <= t) {
                m.data.set(target, source, m.data.get(target, source) + 1d);
            } else {
                m.data.set(source, target, m.data.get(source, target) + 1d);
            }
        }
        return m;
    }

    /**
     * Generates an interaction matrix based on ranks and a given number of
     * interactions.
     *
     * @param ranks Reference ranks
     * @param interactions Number of interactions to simulate
     * @return Interaction matrix according to linear model
     */
    public static LabeledMatrix generateData(Map<String, Integer> ranks, int interactions) {
        Map<Integer, String> labels = new HashMap<>();
        int count = 0;
        for (String name : ranks.keySet()) {
            labels.put(count, name);
            count++;
        }

        //init interactions
        int size = ranks.keySet().size();
        LabeledMatrix m = new LabeledMatrix(size, labels);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                m.data.set(i, j, 0d);
            }
        }
        int source, target;
        for (int i = 0; i < interactions; i++) {
            // chose individuals
            source = (int) (Math.random() * size);
            target = (int) (Math.random() * size);
            while (source == target) {
                target = (int) (Math.random() * size);
            }

            //get the interaction
            //System.out.println("Source: "+source+"\t Target: "+target);
            double r = Math.random();
            double t = ((double) ranks.get(labels.get(source))) / ((double) ranks.get(labels.get(source)) + ranks.get(labels.get(target)));
            if (r <= t) {
                m.data.set(target, source, m.data.get(target, source) + 1d);
            } else {
                m.data.set(source, target, m.data.get(source, target) + 1d);
            }
        }
        return m;
    }

    /**
     * Generates non-linear ranks for a given population size
     * @param populationSize Size of group
     * @return Mapping from individuals to rank
     */
    public static Map<String, Integer> generateRanks(int populationSize) {
        Map<String, Integer> ranks = new HashMap<String, Integer>();
        //generate random data
        int min = 1;
        for (int i = 0; i < populationSize; i++) {
            int rank = (int) (Math.random() * populationSize) + 1;
            if (rank > min) {
                rank = min;
                min++;
            }
            ranks.put((char) ('A' + i) + "", rank);
        }
        return ranks;

    }
  
    /**
     * Generic method for computing ranking. To be extended for further experiments
     */
    public static void genericBatchRanking() {
        //parameters        
        int repetitions = 100;
        int populationSize = 10;
        int log = 10;
        int interactions = populationSize;

        String base = "E:/tmp/";
        String data = "exp"; //can be "lin" or "exp"

        //only section that needs to be modified for new experiments. Simply
        //add more rankings here to run experiments with different rankings
        Set<Ranking> rankings = new HashSet();
        rankings.add(new EloRating());
        rankings.add(new ISIRanking());
        rankings.add(new DavidScore());
        rankings.add(new AdagioRanking());
        rankings.add(new AdagioBottomUpRanking());
        rankings.add(new AdagioPlusPreprocessingRanking());
        rankings.add(new AdagioBottomUpPlusPreprocessing());

        Map<String, StringBuilder> stringBuilders = new HashMap<String, StringBuilder>();
        for (Ranking r : rankings) {
            stringBuilders.put(r.getName(), new StringBuilder());
        }


        for (int k = 0; k < log; k++) {
            interactions = interactions * 2;
            for (String key : stringBuilders.keySet()) {
                stringBuilders.get(key).append(interactions).append("\t");
            }

            for (int i = 0; i < repetitions; i++) {
                System.out.println("Repetition: " + i);
                Map<String, Integer> ranks = generateRanks(populationSize);
//                Map<String, Integer> ranks = generateLinearRanks(populationSize);
                LabeledMatrix m;
                if (data.equals("lin")) {
                    m = generateData(ranks, interactions);
                } else {
                    m = generateExponentialData(ranks, interactions);
                }
                for (Ranking r : rankings) {
                    stringBuilders.get(r.getName()).append(evaluateRanking(r, ranks, m)).append("\t");
                }

            }
            for (Ranking r : rankings) {
                stringBuilders.get(r.getName()).append("\n");
            }
        }

        String file;
        if (data.equals("lin")) {
            file = base + "/pop" + populationSize + "-rep" + repetitions + "-data" + data + "/";
        } else {
            file = base + "/pop" + populationSize + "-rep" + repetitions + "-data" + data + e + "/";
        }

        File f = new File(file);
        f.mkdirs();
        for (String key : stringBuilders.keySet()) {
            System.out.println("Writing to " + file + key);
            print(file + key, stringBuilders.get(key));
        }
    }

    /**
     * Prints a file from a string builder
     * @param file Name of the file to be printed
     * @param sb StringBuilder (i.e., content) to be printed
     */
    public static void print(String file, StringBuilder sb) {
        try {
            try (PrintWriter printWriter = new PrintWriter(new File(file))) {
                printWriter.print(sb);
                printWriter.close();
            }
        } catch (Exception e) {
            System.err.println("Error writing " + file);
        }
    }

    /**
     * Main
     * @param args No need for arguments really
     */
    public static void main(String args[]) {

        genericBatchRanking();
    }

    

}
