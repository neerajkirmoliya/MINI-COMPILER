package com.vinay.ArithmeticCompilerVisualizer.gui;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import parser.ExprLexer;
import parser.ExprParser;
import net.sourceforge.tess4j.*;

import javax.swing.*;
import javax.swing.text.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import java.io.File;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {
    private JTextField inputField;
    private JTextPane outputArea; // For tokens/result
    private JTextArea tacArea;    // For Three Address Code
    private JTextArea asmArea;    // For Assembly Code
    private ParseTreePanel parseTreePanel;
    private ASTPanel astPanel;
    private DAGPanel dagPanel;
    private JLabel imageLabel;

    public MainFrame() {
        setTitle("Arithmetic Compiler Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());

        inputField = new JTextField();
        JButton analyzeButton = new JButton("Analyze");
        JButton imageButton = new JButton("Load Image");

        // Output areas
        outputArea = new JTextPane();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        tacArea = new JTextArea();
        tacArea.setEditable(false);
        tacArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        tacArea.setForeground(Color.WHITE);
        tacArea.setBackground(Color.BLACK);

        asmArea = new JTextArea();
        asmArea.setEditable(false);
        asmArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        asmArea.setForeground(new Color(0, 255, 0));
        asmArea.setBackground(Color.BLACK);

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Enter Expression: "), BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(analyzeButton, BorderLayout.EAST);

        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(imageButton, BorderLayout.EAST);

        parseTreePanel = new ParseTreePanel(null);
        astPanel = new ASTPanel(null);
        dagPanel = new DAGPanel(null);
        imageLabel = new JLabel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Parse Tree", new JScrollPane(parseTreePanel));
        tabbedPane.addTab("Syntax Tree", new JScrollPane(astPanel));
        tabbedPane.addTab("DAG", new JScrollPane(dagPanel));

        // Output panel layout
        JPanel codePanel = new JPanel(new GridLayout(2, 1));
        codePanel.add(new JScrollPane(tacArea));
        codePanel.add(new JScrollPane(asmArea));

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(outputArea), codePanel);
        rightSplit.setDividerLocation(300);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                tabbedPane, rightSplit);
        splitPane.setDividerLocation(700);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                splitPane, new JScrollPane(imageLabel));
        mainSplit.setDividerLocation(600);

        add(topPanel, BorderLayout.NORTH);
        add(mainSplit, BorderLayout.CENTER);

        analyzeButton.addActionListener(this::analyzeExpression);
        imageButton.addActionListener(this::loadImage);

        setVisible(true);

        // Show WelcomePanel as the initial content
        WelcomePanel welcome = new WelcomePanel();
        JButton continueButton = (JButton) welcome.getClientProperty("continueButton");

        getContentPane().removeAll();
        setLayout(new BorderLayout());
        add(welcome, BorderLayout.CENTER);
        revalidate();
        repaint();

        continueButton.addActionListener(ev -> {
            getContentPane().removeAll();
            add(topPanel, BorderLayout.NORTH);
            add(mainSplit, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    }

    private String getTokenTypeName(Token token, ExprLexer lexer) {
        String text = token.getText();
        int type = token.getType();
        String symbolic = lexer.getVocabulary().getSymbolicName(type);

        if ("(".equals(text)) return "Left Parenthesis";
        if (")".equals(text)) return "Right Parenthesis";
        if ("+".equals(text) || "-".equals(text) || "*".equals(text) || "/".equals(text))
            return "Operator";
        if ("INT".equals(symbolic)) return "Integer";
        if ("FLOAT".equals(symbolic)) return "Float";
        if ("EOF".equals(symbolic)) return "End of File";
        if ("WS".equals(symbolic)) return "Whitespace";
        return symbolic != null ? symbolic : "Unknown";
    }

    private void analyzeExpression(ActionEvent e) {
        String expr = inputField.getText();
        if (expr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an expression.");
            return;
        }
        try {
            CharStream input = CharStreams.fromString(expr);
            ExprLexer lexer = new ExprLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ExprParser parser = new ExprParser(tokens);

            // Parse Tree
            ParseTree tree = parser.prog();
            parseTreePanel.setTree(tree);

            // AST (Syntax Tree)
            ASTNode astRoot = buildAST(tree);
            astPanel.setRoot(astRoot);

            // --- Three Address Code with Value Numbering ---
            Map<String, String> subexprMap = new HashMap<>();
            Map<String, String> valueMap = new HashMap<>();
            String tac = generateThreeAddressCode(tree, new int[]{1}, subexprMap, valueMap).code;
            List<String> tacLines = Arrays.asList(tac.split("\n"));

            // --- DAG from TAC ---
            DAGTACNode dagRoot = buildDAGFromTAC(tacLines);
            dagPanel.setRoot(dagRoot);

            // --- Prepare Styled Output for Tokens/Result ---
            StyledDocument doc = outputArea.getStyledDocument();
            outputArea.setText(""); // Clear previous output

            Style defaultStyle = outputArea.addStyle("default", null);
            StyleConstants.setForeground(defaultStyle, Color.BLACK);
            StyleConstants.setBackground(defaultStyle, Color.WHITE);

            doc.insertString(doc.getLength(), "Tokens:\n", defaultStyle);
            tokens.seek(0);
            Token token;
            while ((token = tokens.LT(1)).getType() != Token.EOF) {
                String text = token.getText();
                String typeName = getTokenTypeName(token, lexer);
                doc.insertString(doc.getLength(), text + " (" + typeName + ")\n", defaultStyle);
                tokens.consume();
            }

            // --- Result ---
            StringBuilder resultSb = new StringBuilder();
            int result = evaluate(tree, resultSb);
            doc.insertString(doc.getLength(), "\nResult: " + result + "\n", defaultStyle);

            // --- Three Address Code (TAC) ---
            tacArea.setText(tac);

            // --- Assembly Code (from TAC) ---
            String asm = generateAssemblyFromTAC(tac);
            asmArea.setText(asm);

            outputArea.setCaretPosition(0);

        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    // Assembly code generation from TAC
    private String generateAssemblyFromTAC(String tac) {
        StringBuilder asm = new StringBuilder();
        String[] lines = tac.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("=");
            if (parts.length != 2) continue;
            String left = parts[0].trim();
            String rhs = parts[1].trim();
            String[] tokens = rhs.split(" ");
            if (tokens.length == 3) {
                String op1 = tokens[0];
                String op = tokens[1];
                String op2 = tokens[2];
                asm.append("MOV R1, ").append(op1).append("\n");
                asm.append("MOV R2, ").append(op2).append("\n");
                String asmOp = "";
                switch (op) {
                    case "+": asmOp = "ADD"; break;
                    case "-": asmOp = "SUB"; break;
                    case "*": asmOp = "MUL"; break;
                    case "/": asmOp = "DIV"; break;
                }
                asm.append(asmOp).append(" R3, R1, R2\n");
                asm.append("MOV ").append(left).append(", R3\n");
            } else if (tokens.length == 1) {
                asm.append("MOV ").append(left).append(", ").append(tokens[0]).append("\n");
            }
        }
        return asm.toString();
    }

    private void loadImage(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int ret = fileChooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                // Show image
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(300, 150, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
                imageLabel.setText("");

                // OCR
                Tesseract tesseract = new Tesseract();
                tesseract.setDatapath(getTessDataPath());
                tesseract.setLanguage("eng");
                String result = tesseract.doOCR(file);
                // Clean up result (remove newlines, keep only valid chars)
                result = result.replaceAll("[^0-9+\\-*/()]", "");
                inputField.setText(result);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "OCR Error: " + ex.getMessage());
            }
        }
    }

    // Helper to find tessdata path
    private String getTessDataPath() {
        String env = System.getenv("TESSDATA_PREFIX");
        if (env != null) return env;
        String[] common = {
            "C:/Program Files/Tesseract-OCR/tessdata",
            "/usr/share/tesseract-ocr/4.00/tessdata/",
            "/usr/share/tesseract-ocr/tessdata/",
            "/usr/local/share/tessdata/"
        };
        for (String path : common) {
            if (new File(path).exists()) return path;
        }
        return "."; // fallback
    }

    // --- Evaluation ---
    private int evaluate(ParseTree tree, StringBuilder sb) throws Exception {
        if (tree instanceof parser.ExprParser.ProgContext) {
            return evaluate(((parser.ExprParser.ProgContext) tree).expr(), sb);
        }
        if (tree instanceof parser.ExprParser.AddSubContext) {
            int left = evaluate(((parser.ExprParser.AddSubContext) tree).expr(), sb);
            int right = evaluate(((parser.ExprParser.AddSubContext) tree).term(), sb);
            String op = ((parser.ExprParser.AddSubContext) tree).op.getText();
            if (op.equals("+")) return left + right;
            else return left - right;
        }
        if (tree instanceof parser.ExprParser.MulDivContext) {
            int left = evaluate(((parser.ExprParser.MulDivContext) tree).term(), sb);
            int right = evaluate(((parser.ExprParser.MulDivContext) tree).factor(), sb);
            String op = ((parser.ExprParser.MulDivContext) tree).op.getText();
            if (op.equals("*")) return left * right;
            else {
                if (right == 0) {
                    sb.append("\nSemantic Error: Division by zero!\n");
                    throw new Exception("Division by zero");
                }
                return left / right;
            }
        }
        if (tree instanceof parser.ExprParser.ToTermContext) {
            return evaluate(((parser.ExprParser.ToTermContext) tree).term(), sb);
        }
        if (tree instanceof parser.ExprParser.ToFactorContext) {
            return evaluate(((parser.ExprParser.ToFactorContext) tree).factor(), sb);
        }
        if (tree instanceof parser.ExprParser.IntContext) {
            return Integer.parseInt(tree.getText());
        }
        if (tree instanceof parser.ExprParser.ParensContext) {
            return evaluate(((parser.ExprParser.ParensContext) tree).expr(), sb);
        }
        return 0;
    }

    // --- Three Address Code Generation with Value Numbering ---
    private static class TACResult {
        String code;
        String tempVar;
        String value; // The computed value as a string, if known
        TACResult(String code, String tempVar, String value) {
            this.code = code;
            this.tempVar = tempVar;
            this.value = value;
        }
    }

    private TACResult generateThreeAddressCode(ParseTree tree, int[] temp, Map<String, String> subexprMap, Map<String, String> valueMap) {
        if (tree instanceof parser.ExprParser.ProgContext) {
            return generateThreeAddressCode(((parser.ExprParser.ProgContext) tree).expr(), temp, subexprMap, valueMap);
        }
        if (tree instanceof parser.ExprParser.AddSubContext) {
            TACResult left = generateThreeAddressCode(((parser.ExprParser.AddSubContext) tree).expr(), temp, subexprMap, valueMap);
            TACResult right = generateThreeAddressCode(((parser.ExprParser.AddSubContext) tree).term(), temp, subexprMap, valueMap);
            String op = ((parser.ExprParser.AddSubContext) tree).op.getText();
            String key = op + "(" + left.tempVar + "," + right.tempVar + ")";
            if (subexprMap.containsKey(key)) {
                return new TACResult(left.code + right.code, subexprMap.get(key), null);
            }
            try {
                int lval = Integer.parseInt(left.value);
                int rval = Integer.parseInt(right.value);
                int result = op.equals("+") ? lval + rval : lval - rval;
                String resultStr = String.valueOf(result);
                if (valueMap.containsKey(resultStr)) {
                    return new TACResult(left.code + right.code, valueMap.get(resultStr), resultStr);
                }
                String t = "t" + temp[0]++;
                String code = left.code + right.code + t + " = " + left.tempVar + " " + op + " " + right.tempVar + "\n";
                subexprMap.put(key, t);
                valueMap.put(resultStr, t);
                return new TACResult(code, t, resultStr);
            } catch (Exception ex) {
                String t = "t" + temp[0]++;
                String code = left.code + right.code + t + " = " + left.tempVar + " " + op + " " + right.tempVar + "\n";
                subexprMap.put(key, t);
                return new TACResult(code, t, null);
            }
        }
        if (tree instanceof parser.ExprParser.MulDivContext) {
            TACResult left = generateThreeAddressCode(((parser.ExprParser.MulDivContext) tree).term(), temp, subexprMap, valueMap);
            TACResult right = generateThreeAddressCode(((parser.ExprParser.MulDivContext) tree).factor(), temp, subexprMap, valueMap);
            String op = ((parser.ExprParser.MulDivContext) tree).op.getText();
            String key = op + "(" + left.tempVar + "," + right.tempVar + ")";
            if (subexprMap.containsKey(key)) {
                return new TACResult(left.code + right.code, subexprMap.get(key), null);
            }
            try {
                int lval = Integer.parseInt(left.value);
                int rval = Integer.parseInt(right.value);
                int result = op.equals("*") ? lval * rval : lval / rval;
                String resultStr = String.valueOf(result);
                if (valueMap.containsKey(resultStr)) {
                    return new TACResult(left.code + right.code, valueMap.get(resultStr), resultStr);
                }
                String t = "t" + temp[0]++;
                String code = left.code + right.code + t + " = " + left.tempVar + " " + op + " " + right.tempVar + "\n";
                subexprMap.put(key, t);
                valueMap.put(resultStr, t);
                return new TACResult(code, t, resultStr);
            } catch (Exception ex) {
                String t = "t" + temp[0]++;
                String code = left.code + right.code + t + " = " + left.tempVar + " " + op + " " + right.tempVar + "\n";
                subexprMap.put(key, t);
                return new TACResult(code, t, null);
            }
        }
        if (tree instanceof parser.ExprParser.ToTermContext) {
            return generateThreeAddressCode(((parser.ExprParser.ToTermContext) tree).term(), temp, subexprMap, valueMap);
        }
        if (tree instanceof parser.ExprParser.ToFactorContext) {
            return generateThreeAddressCode(((parser.ExprParser.ToFactorContext) tree).factor(), temp, subexprMap, valueMap);
        }
        if (tree instanceof parser.ExprParser.IntContext) {
            String val = tree.getText();
            if (valueMap.containsKey(val)) {
                return new TACResult("", valueMap.get(val), val);
            }
            String t = "t" + temp[0]++;
            String code = t + " = " + val + "\n";
            valueMap.put(val, t);
            return new TACResult(code, t, val);
        }
        if (tree instanceof parser.ExprParser.ParensContext) {
            return generateThreeAddressCode(((parser.ExprParser.ParensContext) tree).expr(), temp, subexprMap, valueMap);
        }
        return new TACResult("", "", null);
    }

    // --- AST Construction ---
    private ASTNode buildAST(ParseTree tree) {
        if (tree instanceof parser.ExprParser.ProgContext) {
            // The root is always ProgContext, so get its expr child
            return buildAST(((parser.ExprParser.ProgContext) tree).expr());
        }
        if (tree instanceof parser.ExprParser.IntContext) {
            return new ASTNode(tree.getText());
        }
        if (tree instanceof parser.ExprParser.ParensContext) {
            return buildAST(((parser.ExprParser.ParensContext) tree).expr());
        }
        if (tree instanceof parser.ExprParser.AddSubContext) {
            ASTNode node = new ASTNode(((parser.ExprParser.AddSubContext) tree).op.getText());
            node.left = buildAST(((parser.ExprParser.AddSubContext) tree).expr());
            node.right = buildAST(((parser.ExprParser.AddSubContext) tree).term());
            return node;
        }
        if (tree instanceof parser.ExprParser.MulDivContext) {
            ASTNode node = new ASTNode(((parser.ExprParser.MulDivContext) tree).op.getText());
            node.left = buildAST(((parser.ExprParser.MulDivContext) tree).term());
            node.right = buildAST(((parser.ExprParser.MulDivContext) tree).factor());
            return node;
        }
        if (tree instanceof parser.ExprParser.ToTermContext) {
            return buildAST(((parser.ExprParser.ToTermContext) tree).term());
        }
        if (tree instanceof parser.ExprParser.ToFactorContext) {
            return buildAST(((parser.ExprParser.ToFactorContext) tree).factor());
        }
        return null;
    }

    // --- Build DAG from TAC ---
    private DAGTACNode buildDAGFromTAC(List<String> tacLines) {
        Map<String, DAGTACNode> exprMap = new HashMap<>(); // expr string -> node
        Map<String, DAGTACNode> labelMap = new HashMap<>(); // t1, t2, ... -> node

        for (String line : tacLines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("=");
            if (parts.length != 2) continue;
            String label = parts[0].trim();
            String rhs = parts[1].trim();

            String[] tokens = rhs.split(" ");
            if (tokens.length == 3) {
                String left = tokens[0];
                String op = tokens[1];
                String right = tokens[2];

                // Use labelMap to get the actual node objects for children
                DAGTACNode leftNode = labelMap.getOrDefault(left, exprMap.getOrDefault(left, new DAGTACNode(null, left)));
                DAGTACNode rightNode = labelMap.getOrDefault(right, exprMap.getOrDefault(right, new DAGTACNode(null, right)));

                String key = op + "(" + leftNode.hashCode() + "," + rightNode.hashCode() + ")";
                DAGTACNode node = exprMap.get(key);
                if (node == null) {
                    node = new DAGTACNode(op, null);
                    node.children.add(leftNode);
                    node.children.add(rightNode);
                    exprMap.put(key, node);
                }
                node.labels.add(label);
                labelMap.put(label, node);
            } else if (tokens.length == 1) {
                String val = tokens[0];
                DAGTACNode node = labelMap.getOrDefault(val, exprMap.getOrDefault(val, new DAGTACNode(null, val)));
                node.labels.add(label);
                labelMap.put(label, node);
                exprMap.put(val, node); // for reuse
            }
        }
        // The last label is the root
        if (!labelMap.isEmpty()) {
            String lastLabel = tacLines.get(tacLines.size() - 1).split("=")[0].trim();
            return labelMap.get(lastLabel);
        }
        return null;
    }
}