// ===== MEMBER 1 START =====
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

/* ---------- Model ---------- */
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

/* ---------- Main UI ---------- */
public class ExpenseTracker extends JFrame {

    private JTextField tfAmount, tfDate;
    private JComboBox<String> cbCategory;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private ArrayList<Expense> expenseList = new ArrayList<>();
    private JLabel lblTotal;

    private static final String DATA_FILE = "expenses_data.csv";

    public ExpenseTracker() {
        setTitle("💰 Expense Tracker");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // ---------- Background Panel with Gradient ----------
        JPanel backgroundPanel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(20, 20, 20),
                        getWidth(), getHeight(), new Color(40, 40, 40)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(backgroundPanel);

        // ---------- Top Header ----------
        JLabel heading = new JLabel("Expense Tracker Dashboard", JLabel.CENTER);
        heading.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);
        heading.setBorder(new EmptyBorder(20, 0, 10, 0));
        backgroundPanel.add(heading, BorderLayout.NORTH);

        // ---------- Left Input Panel ----------
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(8, 1, 15, 15));
        inputPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        inputPanel.setOpaque(false);

        cbCategory = new JComboBox<>(new String[]{"Food", "Transport", "Bills", "Shopping", "Others"});
        cbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cbCategory.setBackground(Color.WHITE);
        cbCategory.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));

        tfAmount = new JTextField();
        tfAmount.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tfAmount.setToolTipText("Enter amount (numbers only)");

        ((AbstractDocument) tfAmount.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string.matches("[0-9.]+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text.matches("[0-9.]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        tfDate = new JTextField(LocalDate.now().toString());
        tfDate.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tfDate.setToolTipText("Enter date in YYYY-MM-DD format");

        AnimatedButton btnAdd = new AnimatedButton("➕ Add Expense", new Color(255, 51, 102), new Color(255, 102, 153));
        AnimatedButton btnEdit = new AnimatedButton("✏️ Edit Selected", new Color(80, 160, 255), new Color(120,190,255));
        AnimatedButton btnDelete = new AnimatedButton("🗑️ Delete Selected", new Color(204, 51, 51), new Color(255, 80, 80));
        AnimatedButton btnExport = new AnimatedButton("📤 Export CSV", new Color(90, 190, 90), new Color(140, 230, 140));
        AnimatedButton btnWeekly = new AnimatedButton("📊 Weekly Pie", new Color(155, 89, 182), new Color(200, 130, 230));

        inputPanel.add(createLabel("Category"));
        inputPanel.add(cbCategory);
        inputPanel.add(createLabel("Amount (₹)"));
        inputPanel.add(tfAmount);
        inputPanel.add(createLabel("Date (YYYY-MM-DD)"));
        inputPanel.add(tfDate);
        inputPanel.add(btnAdd);
        inputPanel.add(btnEdit);
        inputPanel.add(btnDelete);
        inputPanel.add(btnExport);
        inputPanel.add(btnWeekly);

        backgroundPanel.add(inputPanel, BorderLayout.WEST);
        // ===== MEMBER 1 END =====
// ===== MEMBER 2 START =====
        // ---------- Table Panel ----------
        tableModel = new DefaultTableModel(new Object[]{"Category", "Amount (₹)", "Date"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Double.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        expenseTable = new JTable(tableModel);
        expenseTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        expenseTable.setRowHeight(30);
        expenseTable.setBackground(new Color(28, 28, 28));
        expenseTable.setForeground(Color.WHITE);
        expenseTable.setGridColor(new Color(60, 60, 60));
        expenseTable.setAutoCreateRowSorter(true);

        expenseTable.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        expenseTable.getTableHeader().setBackground(new Color(255, 51, 102));
        expenseTable.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < expenseTable.getColumnCount(); i++) {
            expenseTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            private NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            @Override
            public void setValue(Object value) {
                if (value instanceof Number) {
                    setText(nf.format(((Number) value).doubleValue()));
                } else {
                    super.setValue(value);
                }
            }
        };
        currencyRenderer.setHorizontalAlignment(JLabel.CENTER);
        expenseTable.getColumnModel().getColumn(1).setCellRenderer(currencyRenderer);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        scrollPane.getViewport().setBackground(new Color(24, 24, 24));
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        lblTotal = new JLabel("Total: ₹0.00");
        lblTotal.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        lblTotal.setForeground(Color.WHITE);
        bottomPanel.add(lblTotal);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);
        // ===== MEMBER 2 END =====

