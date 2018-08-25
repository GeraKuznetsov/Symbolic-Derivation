package com.kuznetsov.symbder;

import com.kuznetsov.symbder.Operations.Node;
import com.kuznetsov.symbder.Operations.Number;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Graph extends JPanel {

    public static final int XMIN = 0, YMIN = 1, XMAX = 2, YMAX = 3;
    double[] bounds = {-10, -10, 10, 10};
    int width, height;
    Node function = null, functionDir = null;
    private int lastMouseX, lastMouseY;

    Graph() {
        GraphListener graphListener = new GraphListener(this);
        this.addMouseListener(graphListener);
        this.addMouseMotionListener(graphListener);
        this.addMouseWheelListener(graphListener);
    }

    void parseBound(int bound, JTextField f) { //tries to set a bound to the value of the JTextField
        double newValue;
        try {
            newValue = Double.parseDouble(f.getText());
        } catch (java.lang.NumberFormatException e) {
            f.setForeground(Color.red);
            return;
        }
        f.setForeground(Color.black);
        bounds[bound] = newValue;
    }

    private void graphFunction(Node f, Graphics g) {
        /*
        boolean first = true
        in lastY = 0
        for pixX in every horizontal pixel:
            Number.globalX = pixX * (XMAX - XMIN) / graphWidth + XMIN
            int pixelY = ((function.getNumericValue() - YMIN) / (YMAX - YMIN) * graphHeight);
            if(not first):
                drawLine(pixX,graphHeight - lastY, pixX +1, graphHeight - pixelY)
                first = false;
            lastY = pixelY;
         */
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        boolean first = true;
        int lastY = 0;
        for (int i = 0; i < width; i++) {
            Number.globalX = pixelToCoordX(i);
            int pixelY = (int) coordToPixelY(f.getNumericValue());
            if (!first) {
                g2.drawLine(i, height - lastY, i + 1, height - pixelY);
            }
            first = false;
            lastY = pixelY;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        width = this.getWidth();
        height = this.getHeight();

        //max must be bigger then min
        if (bounds[XMAX] < bounds[XMIN]) {
            double swap = bounds[XMAX];
            // bounds[XMAX] = bounds[XMIN];
            //bounds[XMIN] = swap;
            main.setBound(XMAX, bounds[XMIN]);
            main.setBound(XMIN, swap);

        }
        if (bounds[YMAX] < bounds[YMIN]) {
            double swap = bounds[YMAX];
            // bounds[YMAX] = bounds[YMIN];
            // bounds[YMIN] = swap;
            main.setBound(YMAX, bounds[YMIN]);
            main.setBound(YMIN, swap);
        }

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, height);

        //render grid
        g.setColor(Color.BLACK);
        if (deltaCoordX() < 0.5) {
            for (int i = (int) bounds[XMIN]; i < (int) bounds[XMAX] + 1; i++) {
                g.drawLine(coordToPixelX(i), 0, coordToPixelX(i), height);
            }
        }
        if (deltaCoordY() < 0.5) {
            for (int i = (int) bounds[YMIN]; i < (int) bounds[YMAX] + 1; i++) {
                g.drawLine(0, height - coordToPixelY(i), width, height - coordToPixelY(i));
            }
        }
        g.setColor(Color.RED);
        //y-axis
        g.fillRect(coordToPixelX(0), 0, 1, height);
        //x-axis
        g.fillRect(0, height - coordToPixelY(0), width, 1);

        if (function != null) {
            g.setColor(Color.BLUE);
            graphFunction(function, g);

            double coordX = pixelToCoordX(lastMouseX);
            Number.globalX = coordX;
            double coordY = function.getNumericValue();
            g.setColor(Color.MAGENTA);
            g.drawLine(lastMouseX, 0, lastMouseX, height);

            g.drawOval(lastMouseX - 4, height - coordToPixelY(coordY) - 4, 8, 8);

            //draw tangent line
            double slope = functionDir.getNumericValue();

            g.setColor(Color.GREEN);
            g.drawLine(0, height - coordToPixelY(coordY + ((bounds[XMIN] - coordX) * slope)), width, height - coordToPixelY(coordY + ((bounds[XMAX] - coordX) * slope)));
            //System.out.println(XMIN - coordX );

            g.setColor(Color.WHITE);
            String sX = "X = " + coordX;
            String sY = "Y = " + coordY;
            g.drawChars(sX.toCharArray(), 0, sX.length(), 10, height - 10);
            g.drawChars(sY.toCharArray(), 0, sY.length(), 200, height - 10);
        }
        if (functionDir != null) {
            g.setColor(Color.GREEN);
            graphFunction(functionDir, g);
        }

    }

    private double deltaCoordX() { //delta of x coord over 1 pixel
        return (bounds[XMAX] - bounds[XMIN]) / ((double) (width));
    }

    private double deltaCoordY() {//delta of y coord over 1 pixel
        return (bounds[YMAX] - bounds[YMIN]) / ((double) (height));
    }

    public double pixelToCoordX(int pixX) { //find X value at a pixel
        return ((double) (pixX) * deltaCoordX()) + bounds[XMIN];
    }

    public double pixelToCoordY(int pixY) { //find Y value at a pixel
        return ((double) (pixY) * deltaCoordY()) + bounds[YMIN];
    }

    public int coordToPixelX(double coordX) { //find the screen X location of a coordinate
        return (int) ((coordX - bounds[XMIN]) / (bounds[XMAX] - bounds[XMIN]) * (double) width);
    }

    public int coordToPixelY(double coordY) { //find the Y screen location of a coordinate
        return (int) ((coordY - bounds[YMIN]) / (bounds[YMAX] - bounds[YMIN]) * (double) height);
    }

    static class GraphListener implements MouseListener, MouseMotionListener, MouseWheelListener {

        Graph graph;

        GraphListener(Graph g) {
            graph = g;
        }

        @Override
        public void mouseClicked(MouseEvent me) {
        }

        @Override
        public void mousePressed(MouseEvent me) {
            graph.lastMouseX = me.getX();
            graph.lastMouseY = me.getY();
        }

        @Override
        public void mouseReleased(MouseEvent me) {
        }

        @Override
        public void mouseEntered(MouseEvent me) {

        }

        @Override
        public void mouseExited(MouseEvent me) {

        }

        @Override
        public void mouseDragged(MouseEvent me) {
            int x = me.getX();
            int y = me.getY();
            double dX = (graph.lastMouseX - x) * graph.deltaCoordX();
            double dY = -(graph.lastMouseY - y) * graph.deltaCoordY();
            graph.bounds[XMIN] += dX;
            graph.bounds[XMAX] += dX;
            graph.bounds[YMIN] += dY;
            graph.bounds[YMAX] += dY;

            for (int i = 0; i < 4; i++) {
                main.boundButtons[i].setText(Double.toString(graph.bounds[i]));
                main.boundButtons[i].setForeground(Color.black);
            }

            graph.lastMouseX = x;
            graph.lastMouseY = y;
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            graph.lastMouseX = me.getX();
            graph.repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            double delta = mwe.getPreciseWheelRotation() / 25;

            double dX = Math.pow(graph.deltaCoordX(), delta);
            double dY = Math.pow(graph.deltaCoordY(), delta);
            double tX = graph.pixelToCoordX(mwe.getX());
            double tY = graph.pixelToCoordY(graph.height - mwe.getY());
            //System.out.println(dX);
            graph.bounds[XMIN] = ((graph.bounds[XMIN] - tX) * dX) + tX;
            graph.bounds[XMAX] = ((graph.bounds[XMAX] - tX) * dX) + tX;
            graph.bounds[YMIN] = ((graph.bounds[YMIN] - tY) * dY) + tY;
            graph.bounds[YMAX] = ((graph.bounds[YMAX] - tY) * dY) + tY;

            for (int i = 0; i < 4; i++) {
                main.boundButtons[i].setText(Double.toString(graph.bounds[i]));
                main.boundButtons[i].setForeground(Color.black);
            }
        }
    }
}
