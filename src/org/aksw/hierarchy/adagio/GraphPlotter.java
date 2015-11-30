/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simba.hierarchy.adagio;

import Jama.Matrix;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * Simply plots graphs. Used for the output. Seems not to work for very large 
 * graphs.
 * @author ngonga
 */
public class GraphPlotter {

    public static mxGraph getGraph(Matrix m, Map<Integer, String> labels, String path) {
        mxGraph graph = new mxGraph();

        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> rounded = new Hashtable<String, Object>();
        rounded.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        rounded.put(mxConstants.STYLE_OPACITY, 50);
        rounded.put(mxConstants.STYLE_FILLCOLOR, "#FF5240");
        rounded.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        stylesheet.putCellStyle("ROUNDED", rounded);

        Hashtable<String, Object> rectangle = new Hashtable<String, Object>();
        rectangle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        rectangle.put(mxConstants.STYLE_OPACITY, 50);
        rectangle.put(mxConstants.STYLE_FILLCOLOR, "#5FEB3B");
        rectangle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        stylesheet.putCellStyle("RECTANGLE", rectangle);

        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            List<Object> vertices = new ArrayList<Object>();

            for (int i = 0; i < m.getRowDimension(); i++) {
                vertices.add(graph.insertVertex(parent, null, labels.get(i), 20, 40, 20, 40, "ROUNDED"));
            }

            for (int i = 0; i < m.getRowDimension(); i++) {
                for (int j = 0; j < m.getColumnDimension(); j++) {
                    if (m.get(i, j) > 0) {
                        graph.insertEdge(parent, null, "", vertices.get(i), vertices.get(j));
                    }
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }
        mxGraphLayout layout = new mxHierarchicalLayout(graph);
//         mxGraphLayout layout = new mxSwimlaneLayout(graph);
//        mxGraphLayout layout = new mxEdgeLabelLayout(graph);

//        layout.setHorizontal(false);
        try {
            layout.execute(graph.getDefaultParent());
        } catch (Exception e) {
            System.err.println("Error printing graph to file. Too many nodes?");
        }
        return graph;
    }

    public static void draw(Matrix m, Map<Integer, String> labels, String path) {
        mxGraph graph = getGraph(m, labels, path);
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.getViewport().setOpaque(false);
        graphComponent.setBackground(Color.WHITE);

        //set all properties
//        layout.setMinDistanceLimit(10);
//        layout.setInitialTemp(10);
//        layout.setForceConstant(10);
//        layout.setDisableEdgeStyle(true);

        JFrame frame = new JFrame();
//        JFrame f = new JFrame();
        frame.setSize(500, 500);
        frame.setLocation(300, 200);
        frame.setBackground(Color.white);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(graphComponent);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            frame.paint(graphics2D);
            ImageIO.write(image, "jpeg", new File(path + ".jpg"));
        } catch (Exception exception) {
            System.err.println("Could not write image " + path + ".jpg");
        }
    }

    public static void writeToFile(Matrix m, Map<Integer, String> labels, String path) {
        mxGraph graph = getGraph(m, labels, path);
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.getViewport().setOpaque(false);
        graphComponent.setBackground(Color.WHITE);

        //set all properties
//        layout.setMinDistanceLimit(10);
//        layout.setInitialTemp(10);
//        layout.setForceConstant(10);
//        layout.setDisableEdgeStyle(true);

        JFrame frame = new JFrame();
//        JFrame f = new JFrame();
        frame.setSize(500, 500);
        frame.setLocation(300, 200);
        frame.setBackground(Color.white);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(graphComponent);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            frame.paint(graphics2D);
            ImageIO.write(image, "jpeg", new File(path + ".jpg"));
        } catch (Exception exception) {
            System.err.println("Could not write image " + path + ".jpg");
        }
        frame.setVisible(false);
        frame.dispose();
    }
}
