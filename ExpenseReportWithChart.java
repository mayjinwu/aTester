import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ExpenseReportWithChart {
    public static void generateReport(JFrame parentFrame, ArrayList<String[]> records) {
        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "尚無紀錄可生成報表！", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 計算分類總金額和百分比
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
        // 紀錄清單
        ArrayList<String[]> records = new ArrayList<>();

        // 創建主框架
        JFrame frame = new JFrame("記帳程式");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        // 上方輸入區域
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        // 日期輸入
        JLabel dateLabel = new JLabel("日期:");
        JTextField dateField = new JTextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        inputPanel.add(dateLabel);
        inputPanel.add(dateField);

        // 時間輸入
        JLabel timeLabel = new JLabel("時間:");
        JTextField timeField = new JTextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        inputPanel.add(timeLabel);
        inputPanel.add(timeField);

        // 分類選擇
        JLabel categoryLabel = new JLabel("分類:");
        String[] categories = {"食物", "衣服", "娛樂"};
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryComboBox);

        // 金額輸入
        JLabel amountLabel = new JLabel("金額:");
        JTextField amountField = new JTextField();
        inputPanel.add(amountLabel);
        inputPanel.add(amountField);

        // 按鈕區域
        JButton addButton = new JButton("新增紀錄");
        JButton reportButton = new JButton("生成報表");
        inputPanel.add(addButton);
        inputPanel.add(reportButton);

        // 下方表格區域
        String[] columnNames = {"日期", "時間", "分類", "金額"};
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
        // 生成報表按鈕的事件處理
        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExpenseReportWithChart.generateReport(frame, records);
            }
        });

        frame.setVisible(true);
    }
}
