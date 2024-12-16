import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ExpenseReportWithChart {
    public static void generateReport(JFrame parentFrame, ArrayList<String[]> records) {
        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "No record to generate General Report！", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate the total amount and percentage of category
        HashMap<String, Double> categoryTotals = new HashMap<>();
        double totalExpense = 0;

        for (String[] record : records) {
            String category = record[2];
            double amount = Double.parseDouble(record[3]);

            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
            totalExpense += amount;
        }

        // 建立報表框架
        JFrame reportFrame = new JFrame("財務報表");
        reportFrame.setSize(400, 300);
        reportFrame.setLayout(new BorderLayout());

        // 建立報表內容
        DefaultTableModel reportTableModel = new DefaultTableModel(new String[]{"分類", "總金額", "百分比"}, 0);
        for (String category : categoryTotals.keySet()) {
            double total = categoryTotals.get(category);
            double percentage = (total / totalExpense) * 100;
            reportTableModel.addRow(new Object[]{category, String.format("%.2f", total), String.format("%.2f%%", percentage)});
        }
        JTable reportTable = new JTable(reportTableModel);
        JScrollPane reportScrollPane = new JScrollPane(reportTable);

        // 將報表內容加入框架
        reportFrame.add(reportScrollPane, BorderLayout.CENTER);

        // 顯示報表框架
        reportFrame.setVisible(true);
    }
}

class ExpenseTracker {
    public static void main(String[] args) {
        // The list of record
        ArrayList<String[]> records = new ArrayList<>();

        // The main window
        JFrame frame = new JFrame("Bookeeeping Program");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        // The era of above section
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Create a new record"));

        // Date Input
        JLabel dateLabel = new JLabel("日期:");
        JTextField dateField = new JTextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        inputPanel.add(dateLabel);
        inputPanel.add(dateField);

        // Time Input
        JLabel timeLabel = new JLabel("時間:");
        JTextField timeField = new JTextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        inputPanel.add(timeLabel);
        inputPanel.add(timeField);

        // Category choice
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Food", "Clothes", "Entertainment"};
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryComboBox);

        // Enter the Amount
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();
        inputPanel.add(amountLabel);
        inputPanel.add(amountField);

        // The area of the button
        JButton addButton = new JButton("Add new record");
        JButton reportButton = new JButton("Generate Report");
        inputPanel.add(addButton);
        inputPanel.add(reportButton);

        // 下方表格區域
        String[] columnNames = {"Date", "Time", "Category", "Amount"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // 將輸入區域與表格區域加入框架
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        // 新增紀錄按鈕的事件處理
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = dateField.getText();
                String time = timeField.getText();
                String category = (String) categoryComboBox.getSelectedItem();
                String amount = amountField.getText();

                // 驗證金額是否正確
                try {
                    double parsedAmount = Double.parseDouble(amount);
                    String[] record = {date, time, category, String.format("%.2f", parsedAmount)};
                    records.add(record);

                    // 更新表格
                    tableModel.addRow(record);

                    // 清空輸入框
                    dateField.setText("");
                    timeField.setText("");
                    amountField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "請輸入有效的金額！", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Generate 按鈕的事件處理
        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExpenseReportWithChart.generateReport(frame, records);
            }
        });

        // Time Filter Panel
        JPanel filterPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter by Time Range"));

        JLabel startDateLabel = new JLabel("Start Date:");
        JTextField startDateField = new JTextField(LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        filterPanel.add(startDateLabel);
        filterPanel.add(startDateField);

        JLabel endDateLabel = new JLabel("End Date:");
        JTextField endDateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        filterPanel.add(endDateLabel);
        filterPanel.add(endDateField);

        JButton filterButton = new JButton("Filter by Time");
        filterPanel.add(filterButton);

        frame.add(filterPanel, BorderLayout.SOUTH);

        // Filter button action listener
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startDate = startDateField.getText();
                String endDate = endDateField.getText();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                try {
                    LocalDate start = LocalDate.parse(startDate, formatter);
                    LocalDate end = LocalDate.parse(endDate, formatter);

                    // 重新過濾記錄
                    DefaultTableModel filterReportModel = new DefaultTableModel(new String[]{"日期", "分類", "金額", "百分比"}, 0);
                    HashMap<String, Double> filteredTotals = new HashMap<>();
                    double totalFilteredExpense = 0;

                    for (String[] record : records) {
                        LocalDate recordDate = LocalDate.parse(record[0], formatter);
                        if ((recordDate.isEqual(start) || recordDate.isAfter(start)) &&
                                (recordDate.isEqual(end) || recordDate.isBefore(end))) {

                            String category = record[2];
                            double amount = Double.parseDouble(record[3]);
                            filteredTotals.put(category, filteredTotals.getOrDefault(category, 0.0) + amount);
                            totalFilteredExpense += amount;

                            // 新增篩選記錄到表格
                            filterReportModel.addRow(new Object[]{record[0], category, String.format("%.2f", amount), ""});
                        }
                    }

                    // 計算百分比並更新表格
                    for (int i = 0; i < filterReportModel.getRowCount(); i++) {
                        String category = (String) filterReportModel.getValueAt(i, 1);
                        double total = filteredTotals.get(category);
                        double percentage = (total / totalFilteredExpense) * 100;
                        filterReportModel.setValueAt(String.format("%.2f%%", percentage), i, 3);
                    }

                    JTable filterReportTable = new JTable(filterReportModel);
                    JScrollPane filterReportScrollPane = new JScrollPane(filterReportTable);

                    JFrame filterReportFrame = new JFrame("Filtered Report");
                    filterReportFrame.setSize(500, 400);
                    filterReportFrame.add(filterReportScrollPane, BorderLayout.CENTER);
                    filterReportFrame.setVisible(true);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "請輸入有效的日期範圍！", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }
}
