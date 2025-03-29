import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class ElectricityBillCalculator extends JFrame {
    private JTextField nameField, accountField, unitsField;
    private JComboBox<String> typeCombo;
    private JTextArea resultArea;
    private JButton calculateBtn, removeBtn, printBtn;
    
    // Current Sri Lanka electricity tariffs (sample values - update with current rates)
    private final double[] DOMESTIC_RATES = {8.00, 10.00, 16.00, 50.00, 75.00};
    private final double[] COMMERCIAL_RATES = {12.00, 15.00, 18.00, 55.00, 80.00};
    private final double[] INDUSTRIAL_RATES = {15.00, 20.00, 25.00, 60.00, 90.00};
    // private final double[] INDUSTRIAL_RATES = {.00, 20.00, 25.00, 60.00, 90.00};
    // private final double[] INDUSTRIAL_RATES = {15.00, 20.00, 25.00, 60.00, 90.00};
    private final int[] SLABS = {30, 60, 90, 120, Integer.MAX_VALUE};
    
    public ElectricityBillCalculator() {
        setTitle("Sri Lanka Electricity Bill Calculator");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        
        inputPanel.add(new JLabel("Customer Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        
        inputPanel.add(new JLabel("Account Number:"));
        accountField = new JTextField();
        inputPanel.add(accountField);
        
        inputPanel.add(new JLabel("Customer Type:"));
        String[] types = {"Domestic", "Commercial", "Industrial","Government","Religious places"};
        typeCombo = new JComboBox<>(types);
        inputPanel.add(typeCombo);
        
        inputPanel.add(new JLabel("Units Consumed (Kwh):"));
        unitsField = new JTextField();
        inputPanel.add(unitsField);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        calculateBtn = new JButton("Calculate Bill");
        calculateBtn.addActionListener(new CalculateListener());
        buttonPanel.add(calculateBtn);
        
        removeBtn = new JButton("Remove");
        removeBtn.addActionListener(new RemoveListener());
        buttonPanel.add(removeBtn);
        
        printBtn = new JButton("Print");
        printBtn.addActionListener(new PrintListener());
        buttonPanel.add(printBtn);
        
        // Result Area
        resultArea = new JTextArea(20, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }
    
    private class CalculateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Get input values
                String name = nameField.getText();
                String account = accountField.getText();
                String type = (String) typeCombo.getSelectedItem();
                int units = Integer.parseInt(unitsField.getText());
                
                if (name.isEmpty() || account.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter customer name and account number", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Calculate bill
                double bill = calculateBill(units, type);
                double fixedCharge = getFixedCharge(type);
                double total = bill + fixedCharge;
                
                // Format output
                DecimalFormat df = new DecimalFormat("#,##0.00");
                
                String result = "Sri Lanka Electricity Bill\n" +
                               "=========================\n" +
                               "Customer Name: " + name + "\n" +
                               "Account No: " + account + "\n" +
                               "Customer Type: " + type + "\n" +
                               "Units Consumed: " + units + "\n" +
                               "=========================\n" +
                               "Energy Charge: Rs. " + df.format(bill) + "\n" +
                               "Fixed Charge: Rs. " + df.format(fixedCharge) + "\n" +
                               "=========================\n" +
                               "Total Amount: Rs. " + df.format(total) + "\n" +
                               "=========================\n";
                
                resultArea.setText(result);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid number for units", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private double calculateBill(int units, String type) {
            double[] rates;
            
            switch(type) {
                case "Commercial":
                    rates = COMMERCIAL_RATES;
                    break;
                case "Industrial":
                    rates = INDUSTRIAL_RATES;
                    break;
                default: // Domestic
                    rates = DOMESTIC_RATES;
            }
            
            double amount = 0;
            int remainingUnits = units;
            
            for (int i = 0; i < SLABS.length; i++) {
                if (remainingUnits <= 0) break;
                
                int slabSize = (i == 0) ? SLABS[i] : (SLABS[i] - SLABS[i-1]);
                int currentSlabUnits = Math.min(remainingUnits, slabSize);
                
                amount += currentSlabUnits * rates[i];
                remainingUnits -= currentSlabUnits;
            }
            
            return amount;
        }
        
        private double getFixedCharge(String type) {
            switch(type) {
                case "Commercial":
                    return 400.00;
                case "Industrial":
                    return 600.00;
                default: // Domestic
                    return 150.00;
            }
        }
    }
    
    private class RemoveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            nameField.setText("");
            accountField.setText("");
            typeCombo.setSelectedIndex(0);
            unitsField.setText("");
            resultArea.setText("");
        }
    }
    
    private class PrintListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                resultArea.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error printing document", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ElectricityBillCalculator app = new ElectricityBillCalculator();
            app.setVisible(true);
        });
    }
}