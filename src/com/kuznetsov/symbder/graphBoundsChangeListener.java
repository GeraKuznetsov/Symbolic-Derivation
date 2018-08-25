package com.kuznetsov.symbder;

import static com.kuznetsov.symbder.main.graph;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

  class graphBoundsChangeListener implements DocumentListener, FocusListener {

        JTextField feild;
        int bound;

        graphBoundsChangeListener(JTextField f, int b) {
            feild = f;
            bound = b;
        }

        void update() {
            graph.parseBound(bound, feild);
            graph.repaint();
        }

        @Override
        public void insertUpdate(DocumentEvent de) {
            update();
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            update();
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            update();
        }

        //focus
        @Override
        public void focusGained(FocusEvent fe) {
        }

        @Override
        public void focusLost(FocusEvent fe) {
            feild.setText(Double.toString(graph.bounds[bound]));
            feild.setForeground(Color.black);
        }

    }