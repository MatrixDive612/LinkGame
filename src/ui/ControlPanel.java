package ui;

import app.GameData;
import app.SaveManager;
import app.UserManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ControlPanel extends JPanel {
    StatusPanel statusPanel; // 状态面板引用
    BoardPanel boardPanel; // 棋盘面板引用
    JButton restartButton; // 重新开始按钮
    JButton saveButton; // 保存按钮
    JButton loadButton; // 读取按钮
    JButton leaderboardButton; // 排行榜按钮
    int offSetX;
    int offSetY;
    int width;
    int height;
    UserManager userManager; // 用户管理器
    boolean isHardMode; // 难度模式
    
    public ControlPanel(StatusPanel statusPanel, BoardPanel boardPanel, int offSetX, int offSetY, int width, int height, boolean isHardMode) {
        this.setLayout(null);
        this.setBounds(offSetX, offSetY, width, height);
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.width = width;
        this.height = height;
        this.statusPanel = statusPanel;
        this.boardPanel = boardPanel;
        this.isHardMode = isHardMode;
        this.userManager = UserManager.getInstance();
        
        // 创建按钮（4个按钮）
        int btnWidth = 120;
        int btnHeight = 45;
        int spacing = 20;
        int totalWidth = btnWidth * 4 + spacing * 3;
        int startX = (width - totalWidth) / 2;
        int startY = (height - btnHeight) / 2 - 10;
        
        // 重新开始按钮
        restartButton = new JButton("重新开始");
        restartButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        restartButton.setBounds(startX, startY, btnWidth, btnHeight);
        restartButton.setFocusPainted(false);
        this.add(restartButton);
        
        // 保存按钮
        saveButton = new JButton("保存游戏");
        saveButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        saveButton.setBounds(startX + btnWidth + spacing, startY, btnWidth, btnHeight);
        saveButton.setFocusPainted(false);
        this.add(saveButton);
        
        // 读取按钮
        loadButton = new JButton("读取存档");
        loadButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        loadButton.setBounds(startX + (btnWidth + spacing) * 2, startY, btnWidth, btnHeight);
        loadButton.setFocusPainted(false);
        this.add(loadButton);
        
        // 排行榜按钮
        leaderboardButton = new JButton("排行榜");
        leaderboardButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        leaderboardButton.setBounds(startX + (btnWidth + spacing) * 3, startY, btnWidth, btnHeight);
        leaderboardButton.setFocusPainted(false);
        this.add(leaderboardButton);
        
        // 更新按钮状态（游客不可用保存/读取）
        updateButtonStates();
        
        // 重新开始按钮事件
        this.restartButton.addActionListener(e -> {
            handleRestart();
        });
        
        // 保存按钮事件
        this.saveButton.addActionListener(e -> {
            handleSave();
        });
        
        // 读取按钮事件
        this.loadButton.addActionListener(e -> {
            handleLoad();
        });
        
        // 排行榜按钮事件
        this.leaderboardButton.addActionListener(e -> {
            handleLeaderboard();
        });
    }
    
    // 更新按钮状态
    private void updateButtonStates() {
        boolean isRegistered = userManager.isRegisteredUser();
        String currentStatus = statusPanel.getStatus();
        
        // 保存按钮：注册用户且游戏进行中可用
        saveButton.setEnabled(isRegistered && "运行中".equals(currentStatus));
        
        // 读取按钮：只有在游戏结束（胜利/失败）时才可用
        boolean gameEnded = "胜利！".equals(currentStatus) || "失败！".equals(currentStatus) || "死局！".equals(currentStatus);
        loadButton.setEnabled(isRegistered && gameEnded && hasAnySave());
        
        // 设置提示信息
        if (!isRegistered) {
            saveButton.setToolTipText("仅注册用户可使用");
            loadButton.setToolTipText("仅注册用户可使用");
        } else if (!"运行中".equals(currentStatus)) {
            saveButton.setToolTipText("游戏进行中才能保存");
        } else {
            saveButton.setToolTipText("保存当前游戏进度到3个存档槽位之一");
        }
        
        if (gameEnded) {
            loadButton.setToolTipText("读取存档复盘之前的游戏");
        } else {
            loadButton.setToolTipText("游戏结束后才能读取存档");
        }
    }
    
    // 检查是否有任何存档
    private boolean hasAnySave() {
        if (!userManager.isRegisteredUser()) return false;
        String username = userManager.getCurrentUser().getUsername();
        
        for (int i = 1; i <= SaveManager.getMaxSaveSlots(); i++) {
            if (SaveManager.hasSave(username, i)) {
                return true;
            }
        }
        return false;
    }
    
    // 处理重新开始
    private void handleRestart() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定要重新开始吗？当前进度将丢失。", 
            "重新开始", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            doRestart();
        }
    }
    
    // 从失败状态重新开始（公开方法，无需确认）
    public void handleRestartFromFailure() {
        doRestart();
    }
    
    // 执行重新开始操作
    private void doRestart() {
        if (boardPanel != null) {
            boardPanel.restartGame(); // 重置棋盘
        }
        statusPanel.setScore(0); // 重置分数
        statusPanel.resetTimer(); // 重置时间
        statusPanel.setStatus("运行中"); // 重置状态
        statusPanel.setLastAction("无"); // 重置操作记录
        statusPanel.startTimer(); // 重新启动计时器
        updateButtonStates(); // 更新按钮状态
    }
    
    // 处理排行榜
    private void handleLeaderboard() {
        new LeaderboardFrame();
    }
    
    // 处理保存游戏（支持3个存档槽位）
    private void handleSave() {
        // 检查是否为注册用户
        if (!userManager.isRegisteredUser()) {
            JOptionPane.showMessageDialog(this, 
                "只有注册用户才能保存游戏！", 
                "无法保存", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 检查游戏是否在进行中
        if (!"运行中".equals(statusPanel.getStatus())) {
            JOptionPane.showMessageDialog(this, 
                "游戏进行中才能保存！", 
                "无法保存", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (boardPanel == null) {
            JOptionPane.showMessageDialog(this, 
                "没有可保存的游戏！", 
                "保存失败", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String username = userManager.getCurrentUser().getUsername();
            
            // 显示存档槽位选择对话框
            String[] options = {"存档1", "存档2", "存档3", "取消"};
            
            // 构建存档信息列表
            StringBuilder message = new StringBuilder("请选择存档槽位：\n\n");
            for (int i = 1; i <= 3; i++) {
                if (SaveManager.hasSave(username, i)) {
                    GameData data = SaveManager.loadGame(username, i);
                    if (data != null) {
                        String timeStr = new java.text.SimpleDateFormat("MM-dd HH:mm")
                            .format(new java.util.Date(data.getTimestamp()));
                        message.append("存档").append(i).append(": ")
                              .append(timeStr).append(" 分数:").append(data.getScore())
                              .append("\n");
                    }
                } else {
                    message.append("存档").append(i).append(": [空]\n");
                }
            }
            
            int choice = JOptionPane.showOptionDialog(this,
                message.toString(),
                "选择存档槽位",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            // 用户取消了选择
            if (choice == -1 || choice == 3) {
                return;
            }
            
            int slot = choice + 1; // 转换为槽位编号（1-3）
            
            // 如果该槽位已有存档，确认是否覆盖
            if (SaveManager.hasSave(username, slot)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "存档" + slot + "已存在，是否覆盖？",
                    "确认覆盖",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (confirm != JOptionPane.YES_OPTION) {
                    return; // 用户选择不覆盖
                }
            }
            
            // 获取棋盘状态
            int[][] boardState = boardPanel.getBoardStateForSave();
            
            // 创建存档数据
            GameData gameData = new GameData(
                username,
                boardState,
                statusPanel.getScore(),
                statusPanel.getRemainingTime(),
                1,
                isHardMode ? "HARD" : "EASY"
            );
            
            // 保存到指定槽位
            if (SaveManager.saveGame(gameData, slot)) {
                JOptionPane.showMessageDialog(this, 
                    "游戏已保存到存档" + slot + "！\n可以继续游戏，结束后再读取复盘。", 
                    "保存成功", 
                    JOptionPane.INFORMATION_MESSAGE);
                updateButtonStates();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "保存游戏失败！", 
                    "保存失败", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "保存游戏时发生错误: " + e.getMessage(), 
                "保存失败", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 处理读取存档（支持3个存档槽位，仅游戏结束后可用）
    private void handleLoad() {
        // 检查是否为注册用户
        if (!userManager.isRegisteredUser()) {
            JOptionPane.showMessageDialog(this, 
                "只有注册用户才能读取存档！", 
                "无法读取", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 检查游戏是否已结束
        String currentStatus = statusPanel.getStatus();
        if (!"胜利！".equals(currentStatus) && !"失败！".equals(currentStatus) && !"死局！".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, 
                "游戏结束后才能读取存档复盘！\n当前状态：" + currentStatus, 
                "无法读取", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String username = userManager.getCurrentUser().getUsername();
        
        // 检查是否有任何存档
        if (!hasAnySave()) {
            JOptionPane.showMessageDialog(this, 
                "没有找到任何存档！", 
                "读取失败", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // 显示存档槽位选择对话框
            String[] options = {"存档1", "存档2", "存档3", "取消"};
            
            // 构建存档信息列表
            StringBuilder message = new StringBuilder("请选择要读取的存档进行复盘：\n\n");
            for (int i = 1; i <= 3; i++) {
                if (SaveManager.hasSave(username, i)) {
                    GameData data = SaveManager.loadGame(username, i);
                    if (data != null) {
                        String timeStr = new java.text.SimpleDateFormat("MM-dd HH:mm")
                            .format(new java.util.Date(data.getTimestamp()));
                        message.append("存档").append(i).append(": ")
                              .append(timeStr).append(" 分数:").append(data.getScore())
                              .append("\n");
                    }
                } else {
                    message.append("存档").append(i).append(": [空]\n");
                }
            }
            
            int choice = JOptionPane.showOptionDialog(this,
                message.toString(),
                "选择存档槽位",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            // 用户取消了选择
            if (choice == -1 || choice == 3) {
                return;
            }
            
            int slot = choice + 1; // 转换为槽位编号（1-3）
            
            // 检查该槽位是否有存档
            if (!SaveManager.hasSave(username, slot)) {
                JOptionPane.showMessageDialog(this, 
                    "存档" + slot + "为空！", 
                    "读取失败", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 读取存档
            GameData gameData = SaveManager.loadGame(username, slot);
            
            if (gameData == null) {
                JOptionPane.showMessageDialog(this, 
                    "存档" + slot + "读取失败或已损坏！", 
                    "读取失败", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "读取存档" + slot + "进行复盘（仅查看，不可继续游戏）。\n是否继续？", 
                "读取存档复盘", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (boardPanel != null) {
                    // 恢复棋盘状态
                    boardPanel.loadBoardFromState(gameData.getBoardState());
                    // 恢复分数
                    statusPanel.setScore(gameData.getScore());
                    // 恢复剩余时间（但不启动计时器）
                    statusPanel.setTotalTime(gameData.getRemainingTime());
                    // 恢复关卡
                    statusPanel.setLevel(gameData.getLevel());
                    // 设置状态为胜利（表示这是复盘，不能继续玩）
                    statusPanel.setStatus("胜利！");
                    // 注意：不调用 startTimer()，所以时间不会走动
                    
                    JOptionPane.showMessageDialog(this, 
                        "存档" + slot + "读取成功！\n这是复盘模式，您可以查看当时的游戏状态。", 
                        "读取成功", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    updateButtonStates(); // 更新按钮状态
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "存档无效或格式错误！", 
                "读取失败", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 公开方法：供外部调用更新按钮状态（例如游戏结束时）
    public void notifyGameEnded() {
        updateButtonStates();
        
        // 如果是失败状态，更新用户统计
        String currentStatus = statusPanel.getStatus();
        if ("失败！".equals(currentStatus) && userManager.isRegisteredUser()) {
            userManager.updateCurrentUserStats(
                statusPanel.getScore(),
                false, // 失败
                statusPanel.getElapsedTime()
            );
        }
    }
    
    // 刷新按钮状态
    public void refreshButtons() {
        updateButtonStates();
    }
    
    // 清空所有存档
    public void clearAllSaves() {
        if (!userManager.isRegisteredUser()) {
            return;
        }
        
        String username = userManager.getCurrentUser().getUsername();
        
        for (int i = 1; i <= 3; i++) {
            java.io.File saveFile = new java.io.File("data/saves/" + username + "_save_" + i + ".dat");
            if (saveFile.exists()) {
                saveFile.delete();
            }
        }
    }
}
