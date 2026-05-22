package app;

import java.io.Serializable;

// 游戏存档数据类，用于保存和恢复游戏状态
// 实现Serializable接口以支持序列化到文件
public class GameData implements Serializable {
    private static final long serialVersionUID = 1L; // 序列化版本号
    
    private String username; // 存档所属用户名
    private int[][] boardState; // 棋盘状态二维数组（0表示空，其他数字表示图标索引）
    private int score; // 当前分数
    private int remainingTime; // 剩余时间（秒）
    private int level; // 当前关卡
    private long timestamp; // 存档时间戳
    private String difficulty; // 难度（EASY或HARD）
    
    // 默认构造函数
    public GameData() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // 完整构造函数，用于创建存档
    public GameData(String username, int[][] boardState, int score, 
                   int remainingTime, int level, String difficulty) {
        this.username = username;
        this.boardState = boardState;
        this.score = score;
        this.remainingTime = remainingTime;
        this.level = level;
        this.difficulty = difficulty;
        this.timestamp = System.currentTimeMillis(); // 记录存档时间
    }
    
    // Getter和Setter方法
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public int[][] getBoardState() { return boardState; }
    public void setBoardState(int[][] boardState) { this.boardState = boardState; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public int getRemainingTime() { return remainingTime; }
    public void setRemainingTime(int remainingTime) { this.remainingTime = remainingTime; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public long getTimestamp() { return timestamp; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}
