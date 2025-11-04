import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
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

public class ExpenseTracker extends JFrame {

    private JTextField tfAmount, tfDate;
    private JComboBox<String> cbCategory;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private ArrayList<Expense> expenseList = new ArrayList<>();
    private JLabel lblTotal;

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
        inputPanel.setLayout(new GridLayout(7, 1, 15, 15));
        inputPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        inputPanel.setOpaque(false);

        cbCategory = new JComboBox<>(new String[]{"Food", "Transport", "Bills", "Shopping", "Others"});
        cbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cbCategory.setBackground(Color.WHITE);
        cbCategory.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));

        tfAmount = new JTextField();
        tfAmount.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tfAmount.setToolTipText("Enter amount (numbers only)");

        // Restrict to numbers only
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
        AnimatedButton btnDelete = new AnimatedButton("🗑️ Delete Selected", new Color(204, 51, 51), new Color(255, 80, 80));

        inputPanel.add(createLabel("Category"));
        inputPanel.add(cbCategory);
        inputPanel.add(createLabel("Amount (₹)"));
        inputPanel.add(tfAmount);
        inputPanel.add(createLabel("Date (YYYY-MM-DD)"));
        inputPanel.add(tfDate);
        inputPanel.add(btnAdd);
        inputPanel.add(btnDelete);

        backgroundPanel.add(inputPanel, BorderLayout.WEST);

        // ---------- Table Panel ----------
        tableModel = new DefaultTableModel(new String[]{"Category", "Amount (₹)", "Date"}, 0);
        expenseTable = new JTable(tableModel);
        expenseTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        expenseTable.setRowHeight(30);
        expenseTable.setBackground(new Color(28, 28, 28));
        expenseTable.setForeground(Color.WHITE);
        expenseTable.setGridColor(new Color(60, 60, 60));

        expenseTable.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        expenseTable.getTableHeader().setBackground(new Color(255, 51, 102));
        expenseTable.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < expenseTable.getColumnCount(); i++) {
            expenseTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        scrollPane.getViewport().setBackground(new Color(24, 24, 24));
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // ---------- Bottom Panel ----------
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        lblTotal = new JLabel("Total: ₹0.0");
        lblTotal.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        lblTotal.setForeground(Color.WHITE);
        bottomPanel.add(lblTotal);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        // ---------- Button Actions ----------
        btnAdd.addActionListener(e -> addExpense());
        btnDelete.addActionListener(e -> deleteSelected());
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        label.setForeground(Color.WHITE);
        return label;
    }

    private void addExpense() {
        String category = cbCategory.getSelectedItem().toString();
        String date = tfDate.getText().trim();
        double amount;

        try {
            amount = Double.parseDouble(tfAmount.getText().trim());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive amount!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Expense exp = new Expense(category, amount, date);
        expenseList.add(exp);
        tableModel.addRow(new Object[]{category, "₹" + String.format("%.2f", amount), date});
        tfAmount.setText("");
        updateTotal();
    }

    private void deleteSelected() {
        int selected = expenseTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select an entry to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
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
        lblTotal.setText("Total: ₹" + String.format("%.2f", total));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTracker().setVisible(true));
    }
}

/* ---------- Animated Button Class ---------- */
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
