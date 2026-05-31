package app;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    private Map<String, Integer> items; // 当前拥有的道具及数量
    private boolean doubleScoreActive; // 双倍分数是否激活
    private int doubleScoreRemainingTime; // 双倍分数剩余时间（秒）
    
    // 默认构造函数
    public GameData() {
        this.timestamp = System.currentTimeMillis();
        this.items = new HashMap<>();
        this.doubleScoreActive = false;
        this.doubleScoreRemainingTime = 0;
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
        this.items = new HashMap<>();
        this.doubleScoreActive = false;
        this.doubleScoreRemainingTime = 0;
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
    
    public Map<String, Integer> getItems() { return items; }
    public void setItems(Map<String, Integer> items) { this.items = items; }
    
    public boolean isDoubleScoreActive() { return doubleScoreActive; }
    public void setDoubleScoreActive(boolean active) { this.doubleScoreActive = active; }
    
    public int getDoubleScoreRemainingTime() { return doubleScoreRemainingTime; }
    public void setDoubleScoreRemainingTime(int time) { this.doubleScoreRemainingTime = time; }
}
