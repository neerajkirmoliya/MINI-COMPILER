package com.vinay.ArithmeticCompilerVisualizer.gui;

import javax.swing.*;
import java.awt.*;

public class ASTPanel extends ZoomablePanel {
    private ASTNode root;

    public ASTPanel(ASTNode root) {
        this.root = root;
        setPreferredSize(new Dimension(1200, 900));
        setBackground(Color.WHITE);
    }

    public void setRoot(ASTNode root) {
        this.root = root;
        repaint();
    }

    @Override
    protected void paintZoomed(Graphics2D g2d) {
        if (root != null) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int nodeWidth = 60, hGap = 30;
            int totalWidth = computeSubtreeWidth(root, nodeWidth, hGap);
            drawNode(g2d, root, totalWidth / 2 + 50, 30, nodeWidth, hGap, true);
        }
    }

    private void drawNode(Graphics2D g, ASTNode node, int x, int y, int nodeWidth, int hGap, boolean isRoot) {
        if (node == null) return;
        String label = node.label;
        int boxWidth = nodeWidth;
        int boxHeight = 36;

        // Node color: blue for operators, orange for numbers, purple for root
        Color fillColor;
        if (isRoot) {
            fillColor = new Color(200, 180, 255); // root: light purple
        } else if ("+-*/".contains(label)) {
            fillColor = new Color(180, 220, 255); // operator: light blue
        } else {
            fillColor = new Color(255, 220, 180); // number: light orange
        }
        Color borderColor = Color.BLACK;
        Font font = g.getFont().deriveFont(Font.BOLD, 16f);

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

        // Draw children (edges: dark blue)
        int childCount = 0;
        if (node.left != null) childCount++;
        if (node.right != null) childCount++;
        int[] widths = new int[childCount];
        int totalWidth = 0;
        if (node.left != null) {
            widths[0] = computeSubtreeWidth(node.left, nodeWidth, hGap);
            totalWidth += widths[0];
        }
        if (node.left != null && node.right != null) totalWidth += hGap;
        if (node.right != null) {
            widths[childCount - 1] = computeSubtreeWidth(node.right, nodeWidth, hGap);
            totalWidth += widths[childCount - 1];
        }
        int startX = x - totalWidth / 2;
        int i = 0;
        if (node.left != null) {
            int childX = startX + widths[0] / 2;
            int childY = y + 70;
            g.setColor(new Color(60, 60, 180));
            g.setStroke(new BasicStroke(2));
            g.drawLine(x, y + boxHeight, childX, childY);
            drawNode(g, node.left, childX, childY, nodeWidth, hGap, false);
            i++;
        }
        if (node.right != null) {
            int childX = startX + (node.left != null ? widths[0] + hGap : 0) + widths[childCount - 1] / 2;
            int childY = y + 70;
            g.setColor(new Color(60, 60, 180));
            g.setStroke(new BasicStroke(2));
            g.drawLine(x, y + boxHeight, childX, childY);
            drawNode(g, node.right, childX, childY, nodeWidth, hGap, false);
        }
    }

    private int computeSubtreeWidth(ASTNode node, int nodeWidth, int hGap) {
        if (node == null || (node.left == null && node.right == null)) return nodeWidth;
        int total = 0;
        if (node.left != null) total += computeSubtreeWidth(node.left, nodeWidth, hGap);
        if (node.left != null && node.right != null) total += hGap;
        if (node.right != null) total += computeSubtreeWidth(node.right, nodeWidth, hGap);
        return total;
    }
}