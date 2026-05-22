package app;

import java.io.Serializable;

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
    
    // 默认构造函数，创建游客用户
    public User() {
        this.isGuest = true;
        this.username = "游客";
        this.totalGames = 0;
        this.wins = 0;
        this.highestScore = 0;
        this.totalPlayTime = 0;
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
    }
    
    // 更新用户统计数据
    public void updateStats(int score, boolean won, int playTime) {
        totalGames++; // 游戏次数+1
        if (won) wins++; // 如果获胜，获胜次数+1
        if (score > highestScore) highestScore = score; // 更新最高分
        totalPlayTime += playTime; // 累加游戏时间
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
}
