package cop2805;

/*
 * CommissionCalc.java
 * Copyright (c) 2025 Steve Curtis, PDCStudios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 */

/* I hope you don't mind but I went a step further and did things how I normally would.
I hope that's ok.
I have separated the logic into separate helper and logic methods to keep things modular,
easy to debug, and reusable. I also created a GUI popup that gives the user the option to
input a specific amount or use the hardcoded default value. I also made it so that more
tiers and rates can be added later to allow for expandability. 
getCommissionList() takes in the salary, computes the pre-defined commission rates and stores
them in a list to make getting the total later easier. Using this approach allows for adding more
tiers and rates later. If adding another tier, TIER_3 would have to be modified slightly to look
like TIER_2 so that TIER_4 would now become the cieling. Currently, the results are shown in the console */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CommCalc {
	public static void main(String[] args) {
        // To keep things clean and neat, main only instantiates the GUI
		// This line ensures that createAndShowGUI() runs on the correct thread
		SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
	
	//Instantiate the GUI
	public static void createAndShowGUI() {
        JFrame frame = new JFrame("Commision Calculator");
        frame.setSize(350, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JLabel label = new JLabel("Base Salary:");
        JTextField salaryInput = new JTextField("5000", 10); 
        JButton btnCalc = new JButton("Calculate");
        JButton btnDefault = new JButton("Calculate Default");

        btnCalc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double customBase = Double.parseDouble(salaryInput.getText());
                    System.out.println("\n--- Generating commision report for base salary: $" + customBase + " ---");
                    runReportLoop(customBase);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number for salary.");
                }
            }
        });

        btnDefault.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("\n--- Generating default commision report (Base: $5,000) ---");
                runReportLoop(5000.00);
            }
        });

        frame.add(label);
        frame.add(salaryInput);
        frame.add(btnCalc);
        frame.add(btnDefault);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

	//Logic to run the report and print the results
    public static void runReportLoop(double baseSalary) {
        System.out.printf("%-15s %-15s\n", "Sales Amount", "Total Income");
        System.out.println("---------------------------------");

        for (double sales = 1000; sales <= 20000; sales += 1000) {
            double income = computeIncome(sales, baseSalary);
            printFormattedRow(sales, income, null, null);
        }
    }

    //Helper method to calculate the results
    public static double computeIncome(double salesAmount, double baseSalary) {
        List<Double> commissionParts = getCommissionList(salesAmount);

        double totalCommission = 0.0;
        for (Double amount : commissionParts) {
            totalCommission += amount;
        }

        return baseSalary + totalCommission;
    }

    // Creates a list of stored commission values
    // This helper was created to allow for
    // the ability to expand later if needed
    // Makes calculations easier to debug and
    // allows for printing a single "pay stub" report.
    // Calculates the total for each tier before adding to list
    public static List<Double> getCommissionList(double sales) {
        List<Double> commissions = new ArrayList<>();
        
        //Can add more tiers and rates later if needed
        final double TIER_1_LIMIT = 5000;
        final double TIER_2_LIMIT = 10000;
        final double RATE_1 = 0.08;
        final double RATE_2 = 0.10;
        final double RATE_3 = 0.12;

        // Tier 1
        double tier1Sales = Math.min(sales, TIER_1_LIMIT);
        commissions.add(tier1Sales * RATE_1);

        // Tier 2
        double salesInTier2 = Math.min(sales, TIER_2_LIMIT) - TIER_1_LIMIT;
        double tier2Sales = Math.max(0, salesInTier2); 
        commissions.add(tier2Sales * RATE_2);

        // Tier 3
        double salesInTier3 = sales - TIER_2_LIMIT;
        double tier3Sales = Math.max(0, salesInTier3);
        commissions.add(tier3Sales * RATE_3);

        return commissions;
    }

    // --- Reusable helper method that formats the input into a pretty print row ---
    public static void printFormattedRow(double sales, double income, Integer width, Integer precision) {
        int w = (width == null) ? 14 : width;
        int p = (precision == null) ? 2 : precision;
        String formatSpecifier = "$%,-" + w + "." + p + "f $%,." + p + "f%n";
        System.out.printf(formatSpecifier, sales, income);
    }
}
