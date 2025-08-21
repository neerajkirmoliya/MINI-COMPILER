package com.vinay.ArithmeticCompilerVisualizer.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class ZoomablePanel extends JPanel {
    protected double scale = 1.0;
    protected int offsetX = 0, offsetY = 0;
    private int lastDragX, lastDragY;

    public ZoomablePanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1200, 900));
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragX = e.getX();
                lastDragY = e.getY();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                offsetX += e.getX() - lastDragX;
                offsetY += e.getY() - lastDragY;
                lastDragX = e.getX();
                lastDragY = e.getY();
                repaint();
            }
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double oldScale = scale;
                if (e.getPreciseWheelRotation() < 0) {
                    scale *= 1.1;
                } else {
                    scale /= 1.1;
                }
                // Zoom to mouse pointer
                int mx = e.getX(), my = e.getY();
                offsetX = (int) (mx - (mx - offsetX) * (scale / oldScale));
                offsetY = (int) (my - (my - offsetY) * (scale / oldScale));
                repaint();
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);
        paintZoomed(g2d);
        g2d.dispose();
    }

    // Subclasses must override this to draw the tree/graph
    protected abstract void paintZoomed(Graphics2D g2d);
}