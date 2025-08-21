package com.vinay.ArithmeticCompilerVisualizer.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class DAGPanel extends ZoomablePanel {
    private DAGTACNode root;

    public DAGPanel(DAGTACNode root) {
        this.root = root;
        setPreferredSize(new Dimension(1200, 900));
        setBackground(Color.WHITE);
    }

    public void setRoot(DAGTACNode root) {
        this.root = root;
        repaint();
    }

    @Override
    protected void paintZoomed(Graphics2D g2d) {
        if (root != null) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int nodeWidth = 60, hGap = 30;
            int totalWidth = computeSubtreeWidth(root, nodeWidth, hGap);
            drawNode(g2d, root, totalWidth / 2 + 50, 30, nodeWidth, hGap, new HashMap<>(), new HashSet<>());
        }
    }

    private void drawNode(Graphics2D g, DAGTACNode node, int x, int y, int nodeWidth, int hGap, Map<DAGTACNode, Point> nodePositions, Set<DAGTACNode> drawn) {
        if (node == null) return;
        boolean isReused = nodePositions.containsKey(node);
        nodePositions.put(node, new Point(x, y));
        drawn.add(node);

        String label = node.op != null
                ? String.join(",", node.labels) + " [" + node.op + "]"
                : String.join(",", node.labels) + (node.value != null ? " " + node.value : "");
        int boxWidth = nodeWidth;
        int boxHeight = 36;

        // Node color: light green
        g.setColor(new Color(200, 255, 200));
        g.fillRoundRect(x - boxWidth / 2, y, boxWidth, boxHeight, 16, 16);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x - boxWidth / 2, y, boxWidth, boxHeight, 16, 16);

        // Draw label
        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        g.setColor(Color.DARK_GRAY);
        int strWidth = g.getFontMetrics().stringWidth(label);
        g.drawString(label, x - strWidth / 2, y + boxHeight / 2 + 6);

        // Draw children (edges: red, curved if reused)
        int childCount = node.children.size();
        if (childCount > 0) {
            int[] widths = new int[childCount];
            int totalWidth = 0;
            for (int i = 0; i < childCount; i++) {
                widths[i] = computeSubtreeWidth(node.children.get(i), nodeWidth, hGap);
                totalWidth += widths[i];
            }
            totalWidth += hGap * (childCount - 1);

            int startX = x - totalWidth / 2;
            int childX = startX;
            for (int i = 0; i < childCount; i++) {
                DAGTACNode child = node.children.get(i);
                int childY = y + 70;
                g.setColor(Color.RED);
                g.setStroke(new BasicStroke(2));
                if (nodePositions.containsKey(child)) {
                    Point p = nodePositions.get(child);
                    int ctrlX = (x + p.x) / 2 + 40 * (i % 2 == 0 ? 1 : -1);
                    int ctrlY = (y + p.y) / 2;
                    QuadCurve2D q = new QuadCurve2D.Float();
                    q.setCurve(x, y + boxHeight, ctrlX, ctrlY, p.x, p.y);
                    g.draw(q);
                } else {
                    g.drawLine(x, y + boxHeight, childX + widths[i] / 2, childY);
                    drawNode(g, child, childX + widths[i] / 2, childY, nodeWidth, hGap, nodePositions, drawn);
                }
                childX += widths[i] + hGap;
            }
        }
    }

    private int computeSubtreeWidth(DAGTACNode node, int nodeWidth, int hGap) {
        if (node == null || node.children == null || node.children.isEmpty()) return nodeWidth;
        int total = 0;
        for (int i = 0; i < node.children.size(); i++) {
            total += computeSubtreeWidth(node.children.get(i), nodeWidth, hGap);
            if (i > 0) total += hGap;
        }
        return total;
    }
}