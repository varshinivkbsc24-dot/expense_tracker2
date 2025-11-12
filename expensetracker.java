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
        // ===== MEMBER 2 END =====//
                // ===== MEMBER 3 START =====
        // ---------- Button Actions ----------
        btnAdd.addActionListener(e -> addExpense());
        btnDelete.addActionListener(e -> deleteSelected());
        btnEdit.addActionListener(e -> editSelected());
        btnExport.addActionListener(e -> exportToCSV());
        btnWeekly.addActionListener(e -> {
            Map<String, Double> weekly = getWeeklyCategoryTotals();
            PieChartPanel pie = new PieChartPanel();
            pie.setData(weekly);
            JDialog dlg = new JDialog(this, "Weekly Expenses by Category (last 7 days)", true);
            dlg.getContentPane().add(pie);
            dlg.pack();
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);
        });

        loadExpensesFromFile();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveExpensesToFile();
            }
        });
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        label.setForeground(Color.WHITE);
        return label;
    }

    private void addExpense() {
        String category = cbCategory.getSelectedItem().toString();
        String dateStr = tfDate.getText().trim();
        double amount;

        try {
            amount = Double.parseDouble(tfAmount.getText().trim());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive amount!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Please enter date in YYYY-MM-DD format.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Expense exp = new Expense(category, amount, dateStr);
        expenseList.add(exp);
        tableModel.addRow(new Object[]{category, amount, dateStr});
        tfAmount.setText("");
        updateTotal();
        saveExpensesToFile();
    }

    private void deleteSelected() {
        int[] selected = expenseTable.getSelectedRows();
        if (selected.length == 0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmed = JOptionPane.showConfirmDialog(this, "Delete selected items?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmed != JOptionPane.YES_OPTION) return;

        java.util.Arrays.sort(selected);
        for (int i = selected.length - 1; i >= 0; i--) {
            int row = selected[i];
            expenseList.remove(row);
            tableModel.removeRow(row);
        }
        updateTotal();
        saveExpensesToFile();
    }

    private void editSelected() {
        int selected = expenseTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (expenseTable.getRowSorter() != null) {
            selected = expenseTable.convertRowIndexToModel(selected);
        }

        Expense e = expenseList.get(selected);
        JTextField amountField = new JTextField(String.valueOf(e.amount));
        JTextField dateField = new JTextField(e.date);
        JComboBox<String> catField = new JComboBox<>(new String[]{"Food", "Transport", "Bills", "Shopping", "Others"});
        catField.setSelectedItem(e.category);

        JPanel panel = new JPanel(new GridLayout(0,1));
        panel.add(new JLabel("Category:")); panel.add(catField);
        panel.add(new JLabel("Amount:")); panel.add(amountField);
        panel.add(new JLabel("Date (YYYY-MM-DD):")); panel.add(dateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Expense", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double newAmt = Double.parseDouble(amountField.getText().trim());
                String newDate = dateField.getText().trim();
                LocalDate.parse(newDate);
                String newCat = catField.getSelectedItem().toString();

                e.amount = newAmt;
                e.date = newDate;
                e.category = newCat;
                tableModel.setValueAt(newCat, selected, 0);
                tableModel.setValueAt(newAmt, selected, 1);
                tableModel.setValueAt(newDate, selected, 2);
                updateTotal();
                saveExpensesToFile();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("expenses_export.csv"));
        int res = fc.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File chosen = fc.getSelectedFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(chosen))) {
            pw.println("category,amount,date");
            for (Expense e : expenseList) {
                pw.printf("%s,%.2f,%s%n", e.category.replace(",", " "), e.amount, e.date);
            }
            JOptionPane.showMessageDialog(this, "Exported successfully!", "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveExpensesToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            pw.println("category,amount,date");
            for (Expense e : expenseList) {
                pw.printf("%s,%.2f,%s%n", e.category.replace(",", " "), e.amount, e.date);
            }
        } catch (IOException ex) {
            System.err.println("Error saving: " + ex.getMessage());
        }
    }

    private void loadExpensesFromFile() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;
        expenseList.clear();
        tableModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;
                Expense e = new Expense(parts[0], Double.parseDouble(parts[1]), parts[2]);
                expenseList.add(e);
                tableModel.addRow(new Object[]{parts[0], Double.parseDouble(parts[1]), parts[2]});
            }
            updateTotal();
        } catch (Exception ex) {
            System.err.println("Error loading: " + ex.getMessage());
        }
    }

    private Map<String, Double> getWeeklyCategoryTotals() {
        Map<String, Double> totals = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        for (int i = 0; i < cbCategory.getItemCount(); i++) {
            totals.put(cbCategory.getItemAt(i), 0.0);
        }
        for (Expense e : expenseList) {
            try {
                LocalDate d = LocalDate.parse(e.date);
                if (!d.isBefore(weekStart) && !d.isAfter(today)) {
                    totals.put(e.category, totals.get(e.category) + e.amount);
                }
            } catch (DateTimeParseException ignored) {}
        }
        totals.entrySet().removeIf(en -> en.getValue() == 0.0);
        return totals;
    }
    // Member 3 ends //
     // ===== MEMBER 4 START =====
    private void updateTotal() {
        double total = 0;
        for (Expense e : expenseList) {
            total += e.amount;
        }
        lblTotal.setText("Total: ₹" + String.format("%.2f", total));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTracker().setVisible(true));
    }
}

/* ---------- Pie Chart Panel ---------- */
class PieChartPanel extends JPanel {
    private java.util.Map<String, Double> data = new java.util.LinkedHashMap<>();
    private java.util.List<Color> palette;

    public PieChartPanel() {
        setPreferredSize(new Dimension(420, 320));
        setBackground(new Color(34,34,34));
        palette = java.util.Arrays.asList(
            new Color(255,99,132),
            new Color(54,162,235),
            new Color(255,206,86),
            new Color(75,192,192),
            new Color(153,102,255),
            new Color(255,159,64)
        );
    }

    public void setData(java.util.Map<String, Double> map) {
        data.clear();
        for (var e : map.entrySet()) {
            if (e.getValue() > 0) data.put(e.getKey(), e.getValue());
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data.isEmpty()) {
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            drawCenteredString(g, "No expenses in the last 7 days", getWidth(), getHeight());
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int pieSize = Math.min(w, h) * 55 / 100;
        int pieX = 20, pieY = 20;

        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        double curAngle = 0;
        int idx = 0;

        for (var e : data.entrySet()) {
            double value = e.getValue();
            double angle = value / total * 360.0;
            Color c = palette.get(idx % palette.size());
            g2.setColor(c);
            g2.fillArc(pieX, pieY, pieSize, pieSize, (int) curAngle, (int) angle);
            curAngle += angle;
            idx++;
        }

          // draw legend on right
        int legendX = pieX + pieSize + 20;
        int legendY = pieY + 10;
        int box = 14;
        idx = 0;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        for (java.util.Map.Entry<String, Double> e : data.entrySet()) {
            Color c = palette.get(idx % palette.size());
            g2.setColor(c);
            g2.fillRect(legendX, legendY + idx * 28, box, box);
            g2.setColor(Color.WHITE);
            String label = String.format("%s — ₹%.2f", e.getKey(), e.getValue());
            g2.drawString(label, legendX + box + 8, legendY + idx * 28 + box - 2);
            idx++;
        }

        // draw donut center total
        g2.setColor(new Color(34,34,34));
        int inner = pieSize * 48 / 100;
        int innerX = pieX + (pieSize - inner) / 2;
        int innerY = pieY + (pieSize - inner) / 2;
        g2.fillOval(innerX, innerY, inner, inner);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        drawCenteredString(g2, "Total\n₹" + String.format("%.2f", total), inner, inner, innerX, innerY);
    }

    private void drawCenteredString(Graphics g, String text, int width, int height) {
        drawCenteredString(g, text, width, height, 0, 0);
    }

    private void drawCenteredString(Graphics g, String text, int width, int height, int offsetX, int offsetY) {
        Graphics2D g2 = (Graphics2D) g;
        String[] lines = text.split("\n");
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight();
        int totalH = lines.length * lineHeight;
        int y = offsetY + (height - totalH) / 2 + fm.getAscent();
        g2.setColor(Color.WHITE);
        for (String line : lines) {
            int strW = fm.stringWidth(line);
            int x = offsetX + (width - strW) / 2;
            g2.drawString(line, x, y);
            y += lineHeight;
        }
    }
}
// === END ADDED BY CHATGPT ===

/* ---------- Animated Button Class (unchanged) ---------- */
class AnimatedButton extends JButton {
    private Color color1, color2;
    private boolean hovered = false;

    public AnimatedButton(String text, Color color1, Color color2) {
        super(text);
        this.color1 = color1;
        this.color2 = color2;
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(10, 20, 10, 20));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        if (hovered) {
            g2.setColor(new Color(255, 255, 255, 60));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.setColor(new Color(255, 255, 255, 90));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);
        }

        // Shadow Effect
        g2.setColor(new Color(0, 0, 0, hovered ? 120 : 70));
        g2.fillRoundRect(3, 3, getWidth(), getHeight(), 20, 20);

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 4;
        g2.setColor(getForeground());
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}

