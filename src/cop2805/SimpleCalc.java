package cop2805;

/*
 * SimpleCalc.java
 * Copyright (c) 2026 Steve Curtis, Six Actual Studios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 */

/* This class builds and displays the GUI window. It extends
 * JFrame and holds all the visual components as public fields
 * so the listener can access them directly. The methods have
 * been broken up to make them more manageable and reusable.
 */

import javax.swing.*;
import java.awt.*;

public class SimpleCalc extends JFrame 
{

    //Added serialized version UID
	private static final long serialVersionUID = 7957494412640248040L;
	public JTextField fieldOne;
    public JTextField fieldTwo;
    public JComboBox<String> operationBox;
    public JButton calculateButton;
    public JLabel resultLabel;

    public SimpleCalc() 
    {
        super("Simple Calculator");
        initComponents();
        layoutComponents();
        finalizeFrame();
    }

    //This method instantiates all GUI components with default values.
    private void initComponents() 
    {
        fieldOne        = new JTextField(10);
        fieldTwo        = new JTextField(10);
        operationBox    = new JComboBox<>(new String[]{"Add", "Subtract", "Multiply", "Divide"});
        calculateButton = new JButton("Calculate");
        resultLabel     = new JLabel("Result: ");

        calculateButton.addActionListener(new CalcListener(this));
    }

    //This method arranges all of the components inside of the frame using a GridBagLayout.
    //I followed your recommendation and utilized a grid layout.
    private void layoutComponents() 
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 6, 6, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;

        //Row 0 – First number label + field
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Number 1:"), gbc);
        gbc.gridx = 1;
        panel.add(fieldOne, gbc);

        //Row 1 – Second number label + field
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Number 2:"), gbc);
        gbc.gridx = 1;
        panel.add(fieldTwo, gbc);

        //Row 2 – Operation label + combo box
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Operation:"), gbc);
        gbc.gridx = 1;
        panel.add(operationBox, gbc);

        //Row 3 – Calculate button that spans both columns and is centered
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill      = GridBagConstraints.NONE;
        gbc.anchor    = GridBagConstraints.CENTER;
        panel.add(calculateButton, gbc);

        //Row 4 – Results label that spans both columns
        gbc.gridy = 4;
        panel.add(resultLabel, gbc);

        add(panel);
    }

    //This method sets the common frame properties and makes the window visible.
    private void finalizeFrame() 
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //This is the main application entry point.
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(SimpleCalc::new);
    }
}