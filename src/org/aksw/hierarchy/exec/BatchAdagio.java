/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.exec;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.simba.hierarchy.adagio.Adagio;
import org.simba.hierarchy.adagio.BottomUpAdagio;
import org.simba.hierarchy.adagio.FrobeniusNorm;
import org.simba.hierarchy.adagio.LabeledMatrix;
import org.simba.hierarchy.adagio.ManhattanNorm;
import org.simba.hierarchy.ranking.DavidScore;
import org.simba.hierarchy.ranking.EloRating;
import org.simba.hierarchy.ranking.Ranking;
import org.simba.hierarchy.structureindex.*;

/**
 * Runs the tool.
 *
 * @author ngonga
 */
public class BatchAdagio {

    /**
     * Runs the tool and is called from the main method. Basically, we execute
     * all approaches (ADAGIO, I&SI, ELO and David's Score) and linearity
     * measures on all datasets provided by the user. All values are return in a
     * report.
     *
     * @param folder Folder containing data on which the different approaches
     * are to be executed.
     * @param ending Format of input file
     * @param format
     */
    public static void run(String folder, String ending, String format, boolean preprocessing, boolean bottomUpRanking) {
        LabeledMatrix m, m2;
        try {
            File f = new File(folder);
            StringBuilder report = new StringBuilder();
            //list of indexes to return
            Index li = new LandauIndex();
            Index al = new AdjustedLandauIndex();
            Index dci = new DirectionalConsistencyIndex();

            //list of rankings
            Ranking elo = new EloRating();
            Ranking david = new DavidScore();
            Adagio d;
            long begin, end;
            report.append("File name\tPopulation\tLandau's Index\tImproved Landau Index (value)\tImproved Landau Index (pr-value)\tImproved Landau Index (pl-value)\tRelative Error\tWeight Removed\tEdges Removed\tDCI\tProcessing Time\n");
            if (f.isDirectory()) {
                String[] filenames = f.list();
                for (int i = 0; i < filenames.length; i++) {
                    if (filenames[i].endsWith(ending)) {
                        System.out.println("Processing " + filenames[i]);
                        String path = folder + "/" + filenames[i];
//                        if (!new File(path + ".result.jpg").exists()) {
                        m = new LabeledMatrix(path, format);
                        // need to clone the matrix to ensure that the DCI values
                        // and such make sense

                        //set preprocessing
                        if (preprocessing) {
                            m2 = new LabeledMatrix(path, format).getTieResolvedMatrix();
                        } else {
                            m2 = new LabeledMatrix(path, format);
                        }
                        begin = System.currentTimeMillis();

                        // set ranking type
                        if (!bottomUpRanking) {
                            d = new Adagio(m2.data.getArrayCopy());
                        } else {
                            d = new BottomUpAdagio(m2.data.getArrayCopy());
                        }
                        try {
                            //run and output adagio
                            d.run2();
                            end = System.currentTimeMillis();
                            //d.clean();
                            LabeledMatrix copy = new LabeledMatrix(d.data.getRowDimension());
                            copy.data = d.data;
                            copy.labels = m.labels;
                            d.writeMatrix(copy, path + ".adagio.matrix");
                            Adagio.writeGraphToFile(d.data, m.labels, path + ".adagio.result");
                            d.computeRanks(path + ".adagio.ranks", m.labels);

                            //run the others
                            elo.writeRanks(path + ".elo.ranks", elo.getRanks(m));
                            david.writeRanks(path + ".david.ranks", david.getRanks(m));
                            report.append(filenames[i]).append("\t");
                            report.append(m.data.getRowDimension()).append("\t");
                            report.append(li.getValue(m)).append("\t");
                            Map<String, Double> alValues = al.getValues(m);
                            report.append(alValues.get(AbstractIndex.VALUE)).append("\t");
                            report.append(alValues.get("pr")).append("\t");
                            report.append(alValues.get("pl")).append("\t");

                            //add error
                            report.append(FrobeniusNorm.getNorm(copy.data, m2.data) / FrobeniusNorm.getNorm(m2.data)).append("\t");
                            report.append(ManhattanNorm.getNorm(copy.data, m2.data) / ManhattanNorm.getNorm(m2.data)).append("\t");
                            report.append(FrobeniusNorm.getRemovedEdges(copy.data, m2.data) / FrobeniusNorm.getNumberofEdges(m2.data)).append("\t");
                            report.append(dci.getValue(m)).append("\t");
                            report.append(end - begin).append("\n");
                        } catch (Exception e) {
                            System.err.println("Error processing " + filenames[i]);
                            e.printStackTrace();
                        }
//                        }
                    }
                }
            }
            writeOutput(folder + "/adagio.output", report);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main class. Takes the following arguments Arg1 = Folder containing files
     * to process (can be in CSV or tab format). Arg2 = Ending for the file name
     * to process Arg3 = Format for the file (csv is assumed as default)
     *
     * @param args See above
     */
    public static void main(String args[]) {
        Logger logger = Logger.getLogger(BatchAdagio.class.getName());
        try {
            Options options = new Options();
            options.addOption("folder", true, "Folder containing the files to process");
            options.addOption("format", true, "Format of the files to process. Can be");
            options.addOption("ending", true, "Ending of the files to process. Default is \".csv\"");
            options.addOption("preprocessing", true, "Switch for the preprocessing. Default is true");
            options.addOption("ranking", true, "Switch for the bottom-up rank computation. Default is true");
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            String folder = "";
            String format = "csv";
            String ending = "csv";
            boolean preprocessing = true;
            boolean bottomUpRanking = true;
            //set folder
            if (!cmd.hasOption("folder")) {
                logger.log(Level.SEVERE, "No folder set.  Exiting.");
                System.exit(1);
            } else {
                folder = cmd.getOptionValue("folder");
            }
            logger.log(Level.INFO, "Folder set to " + folder);

            //set format
            if (cmd.hasOption("format")) {
                format = cmd.getOptionValue("format");

            }
            logger.log(Level.INFO, "Format set to " + format);
            //set ending
            if (cmd.hasOption("ending")) {
                ending = cmd.getOptionValue("ending");
            }
            logger.log(Level.INFO, "Ending set to " + ending);
            //set preprocessing
            if (cmd.hasOption("preprocessing")) {
                            logger.log(Level.INFO, "Preprocessing input " + cmd.getOptionValue("preprocessing"));
                if (cmd.getOptionValue("preprocessing").toLowerCase().startsWith("f")) {
                    preprocessing = false;
                }
            }
            logger.log(Level.INFO, "Preprocessing set to " + preprocessing);
            //set ranking
            if (cmd.hasOption("ranking")) {
                if (cmd.getOptionValue("ranking").toLowerCase().startsWith("t")) {
                    bottomUpRanking = false;
                }
            }
            logger.log(Level.INFO, "Bottom up ranking set to " + bottomUpRanking);
            
            //run
            run(folder, ending, format, preprocessing, bottomUpRanking);

        } catch (ParseException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Write a StringBuilder to a file
     */
    public static void writeOutput(String file, StringBuilder s) {
        try {
            File f = new File(file);
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(s.toString());
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
