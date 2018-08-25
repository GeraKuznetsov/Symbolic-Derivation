package com.kuznetsov.symbder;

import com.kuznetsov.symbder.Operations.Node;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

public class main {

    static JFrame frame;
    static JPanel masterPanel;
    final static String DERIVATIVE_TEXT = "ƒ′(x) =";
    static JButton drawGraphButton = new JButton("Graph");
    static JTextField functionInput;
    public static final int XMIN = 0, YMIN = 1, XMAX = 2, YMAX = 3;
    static JTextField[] boundButtons = {new JTextField("-10.0", 8), new JTextField("-10.0", 8), new JTextField("10.0", 8), new JTextField("10.0", 8)};
    static JTextField derOutput;
    static JLabel rangeStart = new JLabel("Graph Lower Left: (");
    static JLabel rangeCom1 = new JLabel(",");
    static JLabel rangeMid = new JLabel(")    Graph Upper Right: (");
    static JLabel rangeCom2 = new JLabel(",");
    static JLabel rangeEnd = new JLabel(")");
    static Graph graph;

    public static void main(String[] args) {
        frame = new JFrame();
        masterPanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        masterPanel.setLayout(layout);

        JLabel labelInput = new JLabel("ƒ(x) =");
        layout.putConstraint(SpringLayout.NORTH, labelInput, 15, SpringLayout.NORTH, masterPanel);
        layout.putConstraint(SpringLayout.WEST, labelInput, 10, SpringLayout.WEST, masterPanel);

        functionInput = new JTextField();
        layout.putConstraint(SpringLayout.NORTH, functionInput, 15, SpringLayout.NORTH, masterPanel);
        layout.putConstraint(SpringLayout.WEST, functionInput, 5, SpringLayout.EAST, labelInput);
        layout.putConstraint(SpringLayout.EAST, functionInput, -5, SpringLayout.WEST, drawGraphButton);

        layout.putConstraint(SpringLayout.NORTH, drawGraphButton, 15, SpringLayout.NORTH, masterPanel);
        layout.putConstraint(SpringLayout.WEST, drawGraphButton, -105, SpringLayout.EAST, masterPanel);
        layout.putConstraint(SpringLayout.EAST, drawGraphButton, -10, SpringLayout.EAST, masterPanel);

        derOutput = new JTextField(DERIVATIVE_TEXT);
        derOutput.setEditable(false);
        //derOutput.setOpaque(true);
        layout.putConstraint(SpringLayout.NORTH, derOutput, 15, SpringLayout.SOUTH, labelInput);
        layout.putConstraint(SpringLayout.WEST, derOutput, 10, SpringLayout.WEST, masterPanel);
        layout.putConstraint(SpringLayout.EAST, derOutput, -5, SpringLayout.EAST, masterPanel);

        //masterPanel.add(graph);
        inputFilter filter = new inputFilter();

        for (int i = 0; i < 4; i++) {
            graphBoundsChangeListener xMinListener = new graphBoundsChangeListener(boundButtons[i], i);
            boundButtons[i].getDocument().addDocumentListener((DocumentListener) xMinListener);
            boundButtons[i].addFocusListener(xMinListener);
            ((AbstractDocument) boundButtons[i].getDocument()).setDocumentFilter(filter);
        }

        final int ofset = -10;
        final int ofsetFromOfset = -4;
        layout.putConstraint(SpringLayout.WEST, rangeStart, 5, SpringLayout.WEST, masterPanel);
        layout.putConstraint(SpringLayout.SOUTH, rangeStart, ofset + ofsetFromOfset, SpringLayout.SOUTH, masterPanel);
        layout.putConstraint(SpringLayout.WEST, boundButtons[XMIN], 5, SpringLayout.EAST, rangeStart);
        layout.putConstraint(SpringLayout.SOUTH, boundButtons[XMIN], ofset, SpringLayout.SOUTH, masterPanel);
        layout.putConstraint(SpringLayout.WEST, rangeCom1, 5, SpringLayout.EAST, boundButtons[XMIN]);
        layout.putConstraint(SpringLayout.SOUTH, rangeCom1, ofset + ofsetFromOfset, SpringLayout.SOUTH, masterPanel);
        layout.putConstraint(SpringLayout.WEST, boundButtons[YMIN], 5, SpringLayout.EAST, rangeCom1);
        layout.putConstraint(SpringLayout.SOUTH, boundButtons[YMIN], ofset, SpringLayout.SOUTH, masterPanel);
        layout.putConstraint(SpringLayout.WEST, rangeMid, 5, SpringLayout.EAST, boundButtons[YMIN]);
        layout.putConstraint(SpringLayout.SOUTH, rangeMid, ofset + ofsetFromOfset, SpringLayout.SOUTH, masterPanel);
        layout.putConstraint(SpringLayout.WEST, boundButtons[XMAX], 5, SpringLayout.EAST, rangeMid);
        layout.putConstraint(SpringLayout.SOUTH, boundButtons[XMAX], ofset, SpringLayout.SOUTH, masterPanel);
        layout.putConstraint(SpringLayout.WEST, rangeCom2, 5, SpringLayout.EAST, boundButtons[XMAX]);
        layout.putConstraint(SpringLayout.SOUTH, rangeCom2, ofset + ofsetFromOfset, SpringLayout.SOUTH, masterPanel);
        layout.putConstraint(SpringLayout.WEST, boundButtons[YMAX], 5, SpringLayout.EAST, rangeCom2);
        layout.putConstraint(SpringLayout.SOUTH, boundButtons[YMAX], ofset, SpringLayout.SOUTH, masterPanel);
        layout.putConstraint(SpringLayout.WEST, rangeEnd, 5, SpringLayout.EAST, boundButtons[YMAX]);
        layout.putConstraint(SpringLayout.SOUTH, rangeEnd, ofset + ofsetFromOfset, SpringLayout.SOUTH, masterPanel);
        //  layout.putConstraint(SpringLayout.WEST, null, 5, SpringLayout.EAST, masterPanel);

        graph = new Graph();
        layout.putConstraint(SpringLayout.NORTH, graph, 15, SpringLayout.SOUTH, derOutput);
        layout.putConstraint(SpringLayout.WEST, graph, 5, SpringLayout.WEST, masterPanel);
        layout.putConstraint(SpringLayout.SOUTH, graph, -15, SpringLayout.NORTH, boundButtons[XMIN]);
        layout.putConstraint(SpringLayout.EAST, graph, -5, SpringLayout.EAST, masterPanel);  // (5,5) to (-5,-5)

        masterPanel.add(labelInput);
        masterPanel.add(functionInput);
        masterPanel.add(derOutput);
        drawGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    Node parseFunction = Node.parseFunction(functionInput.getText().toLowerCase());
                    graph.function = parseFunction;
                    System.out.println("input optimized: " + parseFunction.toString());
                    System.out.println("unoptimized der: " + parseFunction.getDerivative().toString());
                    graph.functionDir = parseFunction.getDerivative().Optimize();
                    derOutput.setText(DERIVATIVE_TEXT + graph.functionDir.toString());
                    graph.repaint();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,  ex.getMessage(), "Error" , 0);
                    ex.printStackTrace();
                }
            }
        });
        masterPanel.add(drawGraphButton);
        masterPanel.add(graph);

        masterPanel.add(rangeStart);
        masterPanel.add(boundButtons[0]);
        masterPanel.add(rangeCom1);
        masterPanel.add(boundButtons[1]);
        masterPanel.add(rangeMid);
        masterPanel.add(boundButtons[2]);
        masterPanel.add(rangeCom2);
        masterPanel.add(boundButtons[3]);
        masterPanel.add(rangeEnd);

        frame.getContentPane().add(masterPanel);

        frame.pack();
        frame.setSize(new Dimension(700, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    static void setBound(int bound, double value) {
        boundButtons[bound].setText(Double.toString(value));
    }
}
