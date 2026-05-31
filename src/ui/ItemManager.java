package ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import model.Position;
import model.Cell;

public class ItemManager {
    private static ItemManager instance;
    private StatusPanel statusPanel;
    private BoardPanel boardPanel;
    private ControlPanel controlPanel;
    
    // 道具效果计时器
    private Timer freezeTimer;
    private Timer doubleScoreTimer;
    private boolean freezeActive = false;
    private boolean doubleScoreActive = false;
    
    // 道具价格
    private Map<String, Integer> prices;
    
    private ItemManager() {
        prices = new HashMap<>();
        
        // 设置道具价格（积分）
        prices.put("freeze", 10);
        prices.put("bomb", 20);
        prices.put("double", 30);
        prices.put("clearAll", 50);
    }
    
    public static synchronized ItemManager getInstance() {
        if (instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }
    
    public void setPanels(StatusPanel statusPanel, BoardPanel boardPanel, ControlPanel controlPanel) {
        this.statusPanel = statusPanel;
        this.boardPanel = boardPanel;
        this.controlPanel = controlPanel;
    }
    
    // 获取当前用户
    private app.User getCurrentUser() {
        app.UserManager userManager = app.UserManager.getInstance();
        if (userManager.isRegisteredUser()) {
            return userManager.getCurrentUser();
        }
        return null;
    }
    
    // 获取道具数量（从User对象中读取）
    public int getItemCount(String item) {
        app.User user = getCurrentUser();
        if (user != null) {
            return user.getItemCount(item);
        }
        return 0;
    }
    
    // 增加道具数量（保存到User对象中）
    public void addItem(String item, int count) {
        app.User user = getCurrentUser();
        if (user != null) {
            user.addItem(item, count);
            app.UserManager.getInstance().saveUsers();
        }
    }
    
    // 减少道具数量（保存到User对象中）
    private boolean removeItem(String item, int count) {
        app.User user = getCurrentUser();
        if (user != null) {
            boolean success = user.removeItem(item, count);
            if (success) {
                app.UserManager.getInstance().saveUsers();
            }
            return success;
        }
        return false;
    }
    
    // 使用道具
    public boolean useItem(String item) {
        if (getItemCount(item) <= 0) {
            JOptionPane.showMessageDialog(null, "道具不足！", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        switch (item) {
            case "freeze":
                return useFreeze();
            case "bomb":
                return useBomb();
            case "double":
                return useDoubleScore();
            case "clearAll":
                return useClearAll();
            default:
                return false;
        }
    }
    
    // 使用道具1：冻结时间（暂停倒计时15秒）
    private boolean useFreeze() {
        if (freezeActive) {
            JOptionPane.showMessageDialog(null, "冻结效果已在生效中！", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (!removeItem("freeze", 1)) {
            JOptionPane.showMessageDialog(null, "道具不足！", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        freezeActive = true;
        
        // 停止计时器
        if (statusPanel != null) {
            statusPanel.stopTimer();
        }
        
        JOptionPane.showMessageDialog(null, "时间已冻结15秒！", "道具使用", JOptionPane.INFORMATION_MESSAGE);
        
        // 15秒后恢复
        freezeTimer = new Timer(15000, e -> {
            freezeActive = false;
            if (statusPanel != null && "运行中".equals(statusPanel.getStatus())) {
                statusPanel.startTimer();
            }
            JOptionPane.showMessageDialog(null, "冻结效果结束，时间继续流动！", "提示", JOptionPane.INFORMATION_MESSAGE);
        });
        freezeTimer.setRepeats(false);
        freezeTimer.start();
        
        return true;
    }
    
    // 使用道具2：炸弹（消除选中图案的所有图片）
    private boolean useBomb() {
        if (boardPanel == null || statusPanel == null) {
            return false;
        }
        
        // 检查游戏是否在进行中
        if (!"运行中".equals(statusPanel.getStatus())) {
            JOptionPane.showMessageDialog(null, "游戏进行中才能使用道具！", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // 获取第一个选中的位置
        Position firstSelected = boardPanel.getFirstSelected();
        if (firstSelected == null) {
            JOptionPane.showMessageDialog(null, "请先在棋盘上选择一个图案，然后再使用炸弹道具！", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        Cell selectedCell = boardPanel.getGameBoard().getCell(firstSelected.getRow(), firstSelected.getCol());
        if (selectedCell.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请选择有图案的位置！", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        int iconIndex = selectedCell.getIconIndex();
        
        // 先扣除道具
        if (!removeItem("bomb", 1)) {
            JOptionPane.showMessageDialog(null, "道具不足！", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // 消除所有相同图案
        int eliminatedCount = boardPanel.eliminateAllByIcon(iconIndex);
        
        if (eliminatedCount > 0) {
            // 计算对数
            int eliminatedPairs = eliminatedCount / 2;
            
            // 计算分数（按连消算）
            int baseScore = 10;
            int comboBonus = Math.max(0, (eliminatedPairs - 1) * 5);
            int totalScore = (baseScore + comboBonus) * eliminatedPairs;
            
            // 如果双倍分数激活，分数翻倍
            if (doubleScoreActive) {
                totalScore *= 2;
            }
            
            statusPanel.addScore(totalScore);
            statusPanel.setLastAction("炸弹消除 " + eliminatedCount + "个图案，+" + totalScore + "分");
            
            JOptionPane.showMessageDialog(null, 
                "消除了 " + eliminatedCount + " 个相同图案（" + eliminatedPairs + "对）\n获得 " + totalScore + " 分！", 
                "炸弹效果", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // 检查胜利条件
            if (statusPanel.getRemainingPairs() == 0) {
                statusPanel.setStatus("胜利！");
                statusPanel.stopTimer();
                
                // 更新用户统计
                app.UserManager userManager = app.UserManager.getInstance();
                if (userManager.isRegisteredUser()) {
                    userManager.updateCurrentUserStats(
                        statusPanel.getScore(),
                        true,
                        statusPanel.getElapsedTime()
                    );
                }
            }
            
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "没有找到可消除的图案！", "提示", JOptionPane.WARNING_MESSAGE);
            addItem("bomb", 1); // 恢复道具
            return false;
        }
    }
    
    // 使用道具3：双倍分数（15秒内分数翻倍）
    private boolean useDoubleScore() {
        if (doubleScoreActive) {
            JOptionPane.showMessageDialog(null, "双倍分数效果已在生效中！", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (!removeItem("double", 1)) {
            JOptionPane.showMessageDialog(null, "道具不足！", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        doubleScoreActive = true;
        
        JOptionPane.showMessageDialog(null, "双倍分数已激活15秒！", "道具使用", JOptionPane.INFORMATION_MESSAGE);
        
        // 15秒后失效
        doubleScoreTimer = new Timer(15000, e -> {
            doubleScoreActive = false;
            JOptionPane.showMessageDialog(null, "双倍分数效果已结束！", "提示", JOptionPane.INFORMATION_MESSAGE);
        });
        doubleScoreTimer.setRepeats(false);
        doubleScoreTimer.start();
        
        return true;
    }
    
    // 使用道具4：一键全消（直接全部消除获胜）
    private boolean useClearAll() {
        if (boardPanel == null || statusPanel == null) {
            return false;
        }
        
        // 检查游戏是否在进行中
        if (!"运行中".equals(statusPanel.getStatus())) {
            JOptionPane.showMessageDialog(null, "游戏进行中才能使用道具！", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (!removeItem("clearAll", 1)) {
            JOptionPane.showMessageDialog(null, "道具不足！", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // 计算剩余对数
        int remainingPairs = statusPanel.getRemainingPairs();
        
        // 一口气连消所有的分数计算
        int baseScore = 10;
        int comboBonus = (remainingPairs - 1) * 5;
        int totalScore = (baseScore + comboBonus) * remainingPairs;
        
        // 如果双倍分数激活，分数翻倍
        if (doubleScoreActive) {
            totalScore *= 2;
        }
        
        // 清空棋盘
        boardPanel.clearAllCells();
        statusPanel.addScore(totalScore);
        statusPanel.setStatus("胜利！");
        statusPanel.stopTimer();
        
        JOptionPane.showMessageDialog(null, 
            "一键全消！\n消除了 " + remainingPairs + " 对图案\n获得 " + totalScore + " 分！", 
            "胜利", 
            JOptionPane.INFORMATION_MESSAGE);
        
        return true;
    }
    
    // 检查双倍分数是否激活
    public boolean isDoubleScoreActive() {
        return doubleScoreActive;
    }
    
    // 显示道具商店对话框
    public void showItemShop(int currentPoints) {
        StringBuilder message = new StringBuilder();
        message.append("当前积分: ").append(currentPoints).append("\n\n");
        message.append("道具列表：\n");
        message.append("1. 冻结时间 - 暂停倒计时15秒 - 10积分\n");
        message.append("   拥有: ").append(getItemCount("freeze")).append(" 个\n\n");
        message.append("2. 炸弹 - 消除选中图案的所有图片 - 20积分\n");
        message.append("   拥有: ").append(getItemCount("bomb")).append(" 个\n\n");
        message.append("3. 双倍分数 - 15秒内分数翻倍 - 30积分\n");
        message.append("   拥有: ").append(getItemCount("double")).append(" 个\n\n");
        message.append("4. 一键全消 - 直接全部消除获胜 - 50积分\n");
        message.append("   拥有: ").append(getItemCount("clearAll")).append(" 个\n");
        
        String[] options = {"购买冻结时间", "购买炸弹", "购买双倍分数", "购买一键全消", "取消"};
        
        int choice = JOptionPane.showOptionDialog(null,
            message.toString(),
            "道具商店",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == -1 || choice == 4) {
            return; // 用户取消
        }
        
        String selectedItem = "";
        int price = 0;
        
        switch (choice) {
            case 0:
                selectedItem = "freeze";
                price = 10;
                break;
            case 1:
                selectedItem = "bomb";
                price = 20;
                break;
            case 2:
                selectedItem = "double";
                price = 30;
                break;
            case 3:
                selectedItem = "clearAll";
                price = 50;
                break;
        }
        
        if (currentPoints >= price) {
            addItem(selectedItem, 1);
            
            // 减少用户积分
            app.UserManager userManager = app.UserManager.getInstance();
            if (userManager.isRegisteredUser()) {
                userManager.getCurrentUser().setPoints(currentPoints - price);
                userManager.saveUsers();
            }
            
            JOptionPane.showMessageDialog(null, 
                "购买成功！\n获得 " + getItemName(selectedItem) + " x1\n剩余积分: " + (currentPoints - price), 
                "购买成功", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, 
                "积分不足！需要 " + price + " 积分", 
                "购买失败", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 获取道具名称
    private String getItemName(String item) {
        switch (item) {
            case "freeze": return "冻结时间";
            case "bomb": return "炸弹";
            case "double": return "双倍分数";
            case "clearAll": return "一键全消";
            default: return "未知道具";
        }
    }
    
    // 显示道具使用对话框
    public void showItemUsage() {
        if (boardPanel == null || statusPanel == null) {
            return;
        }
        
        // 检查游戏是否在进行中
        if (!"运行中".equals(statusPanel.getStatus())) {
            JOptionPane.showMessageDialog(null, "游戏进行中才能使用道具！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        StringBuilder message = new StringBuilder();
        message.append("道具背包：\n\n");
        message.append("1. 冻结时间 - 拥有: ").append(getItemCount("freeze")).append(" 个\n");
        message.append("2. 炸弹 - 拥有: ").append(getItemCount("bomb")).append(" 个\n");
        message.append("3. 双倍分数 - 拥有: ").append(getItemCount("double")).append(" 个\n");
        message.append("4. 一键全消 - 拥有: ").append(getItemCount("clearAll")).append(" 个\n");
        
        String[] options = {"使用冻结时间", "使用炸弹", "使用双倍分数", "使用一键全消", "取消"};
        
        int choice = JOptionPane.showOptionDialog(null,
            message.toString(),
            "使用道具",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == -1 || choice == 4) {
            return; // 用户取消
        }
        
        String selectedItem = "";
        switch (choice) {
            case 0:
                selectedItem = "freeze";
                break;
            case 1:
                selectedItem = "bomb";
                break;
            case 2:
                selectedItem = "double";
                break;
            case 3:
                selectedItem = "clearAll";
                break;
        }
        
        useItem(selectedItem);
    }
    
    // 重置道具管理器
    public void reset() {
        if (freezeTimer != null) {
            freezeTimer.stop();
        }
        if (doubleScoreTimer != null) {
            doubleScoreTimer.stop();
        }
        freezeActive = false;
        doubleScoreActive = false;
    }
}

