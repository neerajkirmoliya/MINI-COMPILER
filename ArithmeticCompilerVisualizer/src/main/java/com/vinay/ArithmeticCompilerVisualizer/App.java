package com.vinay.ArithmeticCompilerVisualizer;

import com.vinay.ArithmeticCompilerVisualizer.gui.MainFrame;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame();
        }); }
}
