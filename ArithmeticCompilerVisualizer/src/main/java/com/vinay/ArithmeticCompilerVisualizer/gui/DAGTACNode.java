package com.vinay.ArithmeticCompilerVisualizer.gui;

import java.util.*;
public class DAGTACNode {
    public String op; // "+", "*", etc. or null for leaves
    public String value; // variable or constant name
    public List<DAGTACNode> children = new ArrayList<>();
    public Set<String> labels = new HashSet<>(); // t1, t2, etc.

    public DAGTACNode(String op, String value) {
        this.op = op;
        this.value = value;
    }
}