import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

class Expense {
    String category;
    double amount;
    String date;

    public Expense(String category, double amount, String date) {
        this.category = category;
        this.amount = amount;
        this.date = date;
    }
}

public class expensetracker extends JFrame {

    private JTextField tfAmount, tfDate;
    private JComboBox<String> cbCategory;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private ArrayList<Expense> expenseList = new ArrayList<>();
    private JLabel lblTotal;

    public expensetracker() {
        setTitle("Expense Tracker");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Label
        JLabel heading = new JLabel("Personal Expense Tracker", JLabel.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setForeground(new Color(0, 102, 153));
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(heading, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        inputPanel.setBackground(new Color(230, 245, 255));

        cbCategory = new JComboBox<>(new String[]{"Food", "Transport", "Bills", "Shopping", "Others"});
        tfAmount = new JTextField();
        tfDate = new JTextField(LocalDate.now().toString());

        JButton btnAdd = new JButton("Add Expense");
        JButton btnDelete = new JButton("Delete Selected");

        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(cbCategory);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(tfAmount);
        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        inputPanel.add(tfDate);
        inputPanel.add(btnAdd);
        inputPanel.add(btnDelete);

        add(inputPanel, BorderLayout.WEST);

        // Table Panel
        tableModel = new DefaultTableModel(new String[]{"Category", "Amount", "Date"}, 0);
        expenseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: ₹0.0");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        bottomPanel.add(lblTotal);
        add(bottomPanel, BorderLayout.SOUTH);

        // Event Listeners
        btnAdd.addActionListener(e -> addExpense());
        btnDelete.addActionListener(e -> deleteSelected());
    }

    private void addExpense() {
        String category = cbCategory.getSelectedItem().toString();
        String date = tfDate.getText().trim();
        double amount;

        try {
            amount = Double.parseDouble(tfAmount.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Expense exp = new Expense(category, amount, date);
        expenseList.add(exp);
        tableModel.addRow(new Object[]{category, "₹" + amount, date});
        tfAmount.setText("");
        updateTotal();
    }

    private void deleteSelected() {
        int selected = expenseTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }
        expenseList.remove(selected);
        tableModel.removeRow(selected);
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (Expense e : expenseList) {
            total += e.amount;
        }
        lblTotal.setText("Total: ₹" + total);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new expensetracker().setVisible(true));
    }
}