package app;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// 用户数据类，用于存储用户信息和游戏统计
// 实现Serializable接口以支持对象序列化（保存到文件）
public class User implements Serializable {
    private static final long serialVersionUID = 1L; // 序列化版本号
    
    private String username; // 用户名
    private String password; // 密码
    private boolean isGuest; // 是否为游客
    private int totalGames; // 总游戏次数
    private int wins; // 获胜次数
    private int highestScore; // 最高分数
    private int totalPlayTime; // 总游戏时间（秒）
    private int points; // 积分（可用于兑换道具）
    private transient Map<String, Integer> items; // 道具库存：<道具名, 数量>
    
    // 默认构造函数，创建游客用户
    public User() {
        this.isGuest = true;
        this.username = "游客";
        this.totalGames = 0;
        this.wins = 0;
        this.highestScore = 0;
        this.totalPlayTime = 0;
        this.points = 0;
        this.items = new HashMap<>();
        initItems();
    }
    
    // 构造函数，创建注册用户
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isGuest = false;
        this.totalGames = 0;
        this.wins = 0;
        this.highestScore = 0;
        this.totalPlayTime = 0;
        this.points = 0;
        this.items = new HashMap<>();
        initItems();
    }
    
    // 初始化道具库存
    private void initItems() {
        this.items.put("freeze", 0);      // 冻结时间
        this.items.put("bomb", 0);        // 炸弹
        this.items.put("double", 0);      // 双倍分数
        this.items.put("clearAll", 0);    // 一键全消
    }
    
    // 自定义反序列化方法，确保items被正确初始化
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject(); // 读取默认字段
        if (items == null) {
            this.items = new HashMap<>();
            initItems();
        }
    }
    
    // 更新用户统计数据
    public void updateStats(int score, boolean won, int playTime) {
        totalGames++; // 游戏次数+1
        if (won) wins++; // 如果获胜，获胜次数+1
        if (score > highestScore) highestScore = score; // 更新最高分
        totalPlayTime += playTime; // 累加游戏时间
        points += score; // 本局得分转换为积分并累加
    }
    
    // 获取道具数量
    public int getItemCount(String item) {
        if (items == null) {
            items = new HashMap<>();
            initItems();
        }
        return items.getOrDefault(item, 0);
    }
    
    // 增加道具数量
    public void addItem(String item, int count) {
        if (items == null) {
            items = new HashMap<>();
            initItems();
        }
        items.put(item, items.getOrDefault(item, 0) + count);
    }
    
    // 减少道具数量
    public boolean removeItem(String item, int count) {
        if (items == null) {
            items = new HashMap<>();
            initItems();
        }
        int current = items.getOrDefault(item, 0);
        if (current >= count) {
            items.put(item, current - count);
            return true;
        }
        return false;
    }
    
    // Getter和Setter方法
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public boolean isGuest() { return isGuest; }
    public void setGuest(boolean guest) { isGuest = guest; }
    
    public int getTotalGames() { return totalGames; }
    public int getWins() { return wins; }
    public int getHighestScore() { return highestScore; }
    public int getTotalPlayTime() { return totalPlayTime; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public Map<String, Integer> getItems() { 
        if (items == null) {
            items = new HashMap<>();
            initItems();
        }
        return items; 
    }
    public void setItems(Map<String, Integer> items) { this.items = items; }
}
