package com.vinay.ArithmeticCompilerVisualizer.gui;

import org.antlr.v4.runtime.tree.ParseTree;
import javax.swing.*;
import java.awt.*;

public class ParseTreePanel extends ZoomablePanel {
    private ParseTree tree;

    public ParseTreePanel(ParseTree tree) {
        this.tree = tree;
        setPreferredSize(new Dimension(1200, 900));
        setBackground(Color.WHITE);
    }

    public void setTree(ParseTree tree) {
        this.tree = tree;
        repaint();
    }

    @Override
    protected void paintZoomed(Graphics2D g2d) {
        if (tree != null) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int nodeWidth = 60, hGap = 30;
            int totalWidth = computeSubtreeWidth(tree, nodeWidth, hGap);
            drawNode(g2d, tree, totalWidth / 2 + 50, 30, nodeWidth, hGap, true);
        }
    }

    private void drawNode(Graphics2D g, ParseTree node, int x, int y, int nodeWidth, int hGap, boolean isRoot) {
        String label = node.getClass().getSimpleName().replace("Context", "");
        if (node.getChildCount() == 0) label = node.getText();

        int boxWidth = nodeWidth;
        int boxHeight = 36;

        // Node color: brown for root, yellow for rules, green for leaves
        Color fillColor;
        if (isRoot) {
            fillColor = new Color(220, 180, 120); // root: light brown
        } else if (node.getChildCount() == 0) {
            fillColor = new Color(200, 255, 200); // leaf: light green
        } else {
            fillColor = new Color(255, 255, 180); // rule: light yellow
        }
        Color borderColor = Color.BLACK;
        Font font = g.getFont().deriveFont(Font.PLAIN, 14f);

        // Draw box
        g.setColor(fillColor);
        g.fillRoundRect(x - boxWidth / 2, y, boxWidth, boxHeight, 16, 16);
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x - boxWidth / 2, y, boxWidth, boxHeight, 16, 16);

        // Draw label
        g.setFont(font);
        g.setColor(Color.DARK_GRAY);
        int strWidth = g.getFontMetrics().stringWidth(label);
        g.drawString(label, x - strWidth / 2, y + boxHeight / 2 + 6);

        // Draw children (edges: gray)
        int childCount = node.getChildCount();
        if (childCount > 0) {
            int[] widths = new int[childCount];
            int totalWidth = 0;
            for (int i = 0; i < childCount; i++) {
                widths[i] = computeSubtreeWidth(node.getChild(i), nodeWidth, hGap);
                totalWidth += widths[i];
            }
            totalWidth += hGap * (childCount - 1);

            int startX = x - totalWidth / 2;
            int childX = startX;
            for (int i = 0; i < childCount; i++) {
                int childY = y + 70;
                g.setColor(Color.GRAY);
                g.setStroke(new BasicStroke(2));
                g.drawLine(x, y + boxHeight, childX + widths[i] / 2, childY);
                drawNode(g, node.getChild(i), childX + widths[i] / 2, childY, nodeWidth, hGap, false);
                childX += widths[i] + hGap;
            }
        }
    }

    private int computeSubtreeWidth(ParseTree node, int nodeWidth, int hGap) {
        if (node.getChildCount() == 0) return nodeWidth;
        int total = 0;
        for (int i = 0; i < node.getChildCount(); i++) {
            total += computeSubtreeWidth(node.getChild(i), nodeWidth, hGap);
            if (i > 0) total += hGap;
        }
        return total;
    }
}