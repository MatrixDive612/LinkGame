package ui;

import app.User;
import app.UserManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardFrame extends JFrame {
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;
    
    public LeaderboardFrame() {
        super("游戏排行榜");
        this.setSize(600, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        
        // 创建表格模型
        String[] columns = {"排名", "用户名", "最高分数", "游戏次数", "获胜次数", "胜率"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 不可编辑
            }
        };
        
        // 创建表格
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        leaderboardTable.setRowHeight(30);
        leaderboardTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        leaderboardTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // 设置列宽
        leaderboardTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // 排名
        leaderboardTable.getColumnModel().getColumn(1).setPreferredWidth(150); // 用户名
        leaderboardTable.getColumnModel().getColumn(2).setPreferredWidth(100); // 最高分数
        leaderboardTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // 游戏次数
        leaderboardTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // 获胜次数
        leaderboardTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // 胜率
        
        // 加载排行榜数据
        loadLeaderboard();
        
        // 添加到滚动面板
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        this.add(scrollPane, BorderLayout.CENTER);
        
        // 添加标题
        JLabel titleLabel = new JLabel("🏆 游戏排行榜 🏆", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        this.add(titleLabel, BorderLayout.NORTH);
        
        // 添加刷新按钮
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadLeaderboard());
        buttonPanel.add(refreshButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        this.add(buttonPanel, BorderLayout.SOUTH);
        
        this.setVisible(true);
    }
    
    // 加载排行榜数据
    private void loadLeaderboard() {
        // 清空表格
        tableModel.setRowCount(0);
        
        // 获取所有用户
        UserManager userManager = UserManager.getInstance();
        List<User> users = userManager.getAllUsers();
        
        // 按最高分数降序排序
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return Integer.compare(u2.getHighestScore(), u1.getHighestScore());
            }
        });
        
        // 添加数据到表格
        int rank = 1;
        for (User user : users) {
            String username = user.getUsername();
            int highestScore = user.getHighestScore();
            int totalGames = user.getTotalGames();
            int wins = user.getWins();
            
            // 计算胜率
            double winRate = totalGames > 0 ? (double) wins / totalGames * 100 : 0;
            String winRateStr = String.format("%.1f%%", winRate);
            
            tableModel.addRow(new Object[]{
                rank++,
                username,
                highestScore,
                totalGames,
                wins,
                winRateStr
            });
        }
    }
}
