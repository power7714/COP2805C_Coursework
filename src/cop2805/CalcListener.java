package cop2805;

/*
 * CalcListener.java
 * Copyright (c) 2026 Steve Curtis, PDCStudios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 */

/* This class Handles the button click logic.
 * It extends ActionListener, reads the inputs from the frame's
 * components, routes to the correct arithmetic method, and
 * writes the result back to the results label. The methods have
 * been broken up to make them more manageable and reusable.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalcListener implements ActionListener 
{

    private final SimpleCalc frame;

    public CalcListener(SimpleCalc frame) 
    {
        this.frame = frame;
    }

    // ---------------------------------------------------------------
    // ActionListener implementation
    // ---------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        double num1      = parseField(frame.fieldOne.getText());
        double num2      = parseField(frame.fieldTwo.getText());
        String operation = (String) frame.operationBox.getSelectedItem();

        double result    = calculate(num1, num2, operation);
        displayResult(operation, num1, num2, result);
    }

    // ---------------------------------------------------------------
    // Helper methods
    // ---------------------------------------------------------------

    //This is a simple method to parse a string as a double
    private double parseField(String text) 
    {
        return Double.parseDouble(text.trim());
    }

    //This method is a switch to route the calculation to the proper calculation method
    private double calculate(double a, double b, String operation) 
    {
        switch (operation) {
            case "Add":      return add(a, b);
            case "Subtract": return subtract(a, b);
            case "Multiply": return multiply(a, b);
            case "Divide":   return divide(a, b);
            default:
                throw new IllegalArgumentException("Unknown operation: " + operation);
        }
    }

    //This method returns the sum of a and b.
    private double add(double a, double b) 
    {
        return a + b;
    }

    //This method returns the difference of a and b.
    private double subtract(double a, double b) 
    {
        return a - b;
    }

    //This method returns the product of a and b.
    private double multiply(double a, double b) 
    {
        return a * b;
    }

    //This method returns the quotient of a divided by b.
    private double divide(double a, double b) 
    {
        if (b == 0) 
        {
            frame.resultLabel.setText("Result: Error – division by zero");
            return Double.NaN;
        }
        return a / b;
    }

    //This method formats and displays the result in the results label
    private void displayResult(String operation, double a, double b, double result) 
    {
        if (Double.isNaN(result)) return;

        String symbol = operationSymbol(operation);
        String text   = String.format("Result: %.2f %s %.2f = %.2f", a, symbol, b, result);
        frame.resultLabel.setText(text);
    }

    //This method maps an operation to its actual mathematical symbol
    private String operationSymbol(String operation) 
    {
        switch (operation) {
            case "Add":      return "+";
            case "Subtract": return "-";
            case "Multiply": return "*";
            case "Divide":   return "/";
            default:         return "?";
        }
    }
}