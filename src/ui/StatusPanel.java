package ui;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    JLabel statusLabel; // 状态标签（准备/运行中/胜利）
    JLabel timeLabel; // 时间标签
    JLabel scoreLabel; // 分数标签
    JLabel pairsLabel; // 剩余对数标签
    JLabel levelLabel; // 关卡标签
    JLabel lastActionLabel; // 上一步操作记录标签
    Timer timer;
    int seconds;
    int minutes;
    int hours;
    int offSetX;
    int offSetY;
    int width;
    int height;
    int score; // 当前分数
    int remainingPairs; // 剩余可消除对数
    int currentLevel; // 当前关卡
    int totalTime; // 总时间（秒）
    private boolean timerStarted = false; // 避免重复启动计时器
    ControlPanel controlPanel; // 控制面板引用
    
    public StatusPanel(int offSetX, int offSetY, int width, int height) {
        this.setLayout(null);
        this.setBounds(offSetX, offSetY, width, height);
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.width = width;
        this.height = height;
        this.score = 0; // 初始化分数为0
        this.remainingPairs = 0; // 初始化剩余对数为0
        this.currentLevel = 1; // 初始化关卡为1
        this.totalTime = 300; // 默认总时间300秒（5分钟）
        
        // 创建各个显示标签
        statusLabel = new JLabel("准备");
        timeLabel = new JLabel("剩余: 05:00");
        scoreLabel = new JLabel("分数: 0");
        pairsLabel = new JLabel("剩余对数: 0");
        levelLabel = new JLabel("关卡: 1");
        lastActionLabel = new JLabel("暂无操作");
        
        // 创建计时器，每秒更新一次
        timer = new Timer(1000, e -> {
            if ("运行中".equals(statusLabel.getText())) {
                seconds++;
                if (seconds == 60) {
                    minutes++;
                    seconds = 0;
                    if (minutes == 60) {
                        minutes = 0;
                        hours++;
                    }
                }
                updateTimeDisplay(); // 更新时间显示
            }
        });
        
        // 设置字体
        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        timeLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        scoreLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        pairsLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        levelLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        lastActionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        // 设置标签位置
        statusLabel.setBounds(20, 10, 150, 30);
        levelLabel.setBounds(180, 10, 100, 30);
        scoreLabel.setBounds(300, 10, 150, 30);
        pairsLabel.setBounds(470, 10, 150, 30);
        timeLabel.setBounds(640, 10, 150, 30);
        lastActionLabel.setBounds(20, 50, 750, 30);
        
        // 添加标签到面板
        this.add(statusLabel);
        this.add(levelLabel);
        this.add(scoreLabel);
        this.add(pairsLabel);
        this.add(timeLabel);
        this.add(lastActionLabel);
    }

    // 设置状态文本
    public void setStatus(String text) {
        statusLabel.setText(text);
        repaint();
        
        // 如果状态变为结束状态，通知控制面板
        if (controlPanel != null && ("胜利！".equals(text) || "失败！".equals(text) || "死局！".equals(text))) {
            controlPanel.notifyGameEnded();
        }
    }
    
    // 设置控制面板引用
    public void setControlPanel(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }
    
    // 获取状态文本
    public String getStatus() {
        return statusLabel.getText();
    }

    // 启动计时器
    public void startTimer() {
        if (!timerStarted) {
            timer.start();
            timerStarted = true;
        }
    }
    
    // 停止计时器
    public void stopTimer() {
        timer.stop();
        timerStarted = false;
    }
    
    // 重置计时器
    public void resetTimer() {
        timer.stop();
        seconds = 0;
        minutes = 0;
        hours = 0;
        timerStarted = false;
        updateTimeDisplay();
    }
    
    // 设置总时间
    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
        resetTimer();
    }
    
    // 更新时间显示（显示剩余时间）
    private void updateTimeDisplay() {
        int elapsed = hours * 3600 + minutes * 60 + seconds;
        int remaining = totalTime - elapsed;
        if (remaining < 0) remaining = 0;
        
        int remMin = remaining / 60;
        int remSec = remaining % 60;
        timeLabel.setText(String.format("剩余: %02d:%02d", remMin, remSec));
        repaint();
        
        // 检查倒计时是否结束
        if (remaining == 0 && "运行中".equals(statusLabel.getText())) {
            stopTimer();
            statusLabel.setText("失败！");
            repaint();
            
            // 通知控制面板游戏结束
            if (controlPanel != null) {
                controlPanel.notifyGameEnded();
            }
            
            // 创建自定义失败弹窗
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "游戏失败", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(500, 300);
            dialog.setLocationRelativeTo(this); // 居中显示
            
            // 创建主面板
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            
            // 标题
            JLabel titleLabel = new JLabel("时间到！游戏失败！", SwingConstants.CENTER);
            titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // 得分
            JLabel scoreLabel = new JLabel("得分: " + score, SwingConstants.CENTER);
            scoreLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
            scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            scoreLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
            
            // 按钮面板
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
            
            JButton restartButton = new JButton("重新开始");
            restartButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
            restartButton.setPreferredSize(new Dimension(150, 45));
            restartButton.addActionListener(e -> {
                dialog.dispose();
                if (controlPanel != null) {
                    controlPanel.handleRestartFromFailure();
                }
            });
            
            JButton menuButton = new JButton("返回菜单");
            menuButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
            menuButton.setPreferredSize(new Dimension(150, 45));
            menuButton.addActionListener(e -> {
                dialog.dispose();
                java.awt.Window window = SwingUtilities.getWindowAncestor(StatusPanel.this);
                if (window != null) {
                    window.dispose();
                    app.Main.showDifficultySelection();
                }
            });
            
            buttonPanel.add(restartButton);
            buttonPanel.add(menuButton);
            
            // 组装面板
            mainPanel.add(titleLabel);
            mainPanel.add(scoreLabel);
            mainPanel.add(buttonPanel);
            
            dialog.add(mainPanel, BorderLayout.CENTER);
            dialog.setVisible(true);
        }
    }
    
    // 获取已用时间（秒）
    public int getElapsedTime() {
        return hours * 3600 + minutes * 60 + seconds;
    }
    
    // 获取剩余时间（秒）
    public int getRemainingTime() {
        int elapsed = hours * 3600 + minutes * 60 + seconds;
        return totalTime - elapsed;
    }
    
    // 添加分数
    public void addScore(int points) {
        this.score += points;
        scoreLabel.setText("分数: " + this.score);
        repaint();
    }
    
    // 设置分数
    public void setScore(int score) {
        this.score = score;
        scoreLabel.setText("分数: " + this.score);
        repaint();
    }
    
    // 获取当前分数
    public int getScore() {
        return score;
    }
    
    // 设置剩余对数
    public void setRemainingPairs(int pairs) {
        this.remainingPairs = pairs;
        pairsLabel.setText("剩余对数: " + pairs);
        repaint();
    }
    
    // 获取剩余对数
    public int getRemainingPairs() {
        return remainingPairs;
    }
    
    // 设置关卡
    public void setLevel(int level) {
        this.currentLevel = level;
        levelLabel.setText("关卡: " + level);
        repaint();
    }
    
    // 更新上一步操作记录
    public void setLastAction(String action) {
        lastActionLabel.setText(action);
        repaint();
    }
}
