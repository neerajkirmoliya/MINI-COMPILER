package com.vinay.ArithmeticCompilerVisualizer.gui;

public class ASTNode {
    public String label;
    public ASTNode left, right;
    public ASTNode(String label) { this.label = label; }
}