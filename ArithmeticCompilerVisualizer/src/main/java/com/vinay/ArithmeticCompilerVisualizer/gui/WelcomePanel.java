package com.vinay.ArithmeticCompilerVisualizer.gui;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {
    public WelcomePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Heading
        JLabel heading = new JLabel("Arithmetic Compiler Visualizer PBL Project", SwingConstants.CENTER);
        heading.setFont(new Font("Serif", Font.BOLD, 32));
        heading.setForeground(new Color(60, 60, 180));
        heading.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));
        add(heading, BorderLayout.NORTH);

        // Center panel for logo, team, and button
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // College Logo
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/college_logo.png"));
            Image img = icon.getImage().getScaledInstance(600, 180, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            logoLabel.setText("[College Logo]");
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(logoLabel);

        // Team Members
        String members = "<html><div style='text-align:center;'>"
                + "<b>Team Members:</b><br>"
                + "Akshat Pant <b>(Team Leader)</b><br>"
                + "Neeraj Kirmoliya<br>"
                + "Isha Bharti<br>"
                + "Vinay Singh Rawat<br><br>"
                + "<b>B.Tech CSE 6th Sem</b>"
                + "</div></html>";
        JLabel teamLabel = new JLabel(members, SwingConstants.CENTER);
        teamLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        teamLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        teamLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(teamLabel);

        // Continue Button
        JButton continueButton = new JButton("Continue to Compiler");
        continueButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        continueButton.setBackground(new Color(60, 60, 180));
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(continueButton);

        add(centerPanel, BorderLayout.CENTER);

        // Expose the button for MainFrame to add its action
        putClientProperty("continueButton", continueButton);
    }
}