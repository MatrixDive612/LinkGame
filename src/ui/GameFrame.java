package ui;

import app.GameData;
import app.SaveManager;
import app.UserManager;
import model.Cell;
import model.GameBoard;
import model.Position;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GameFrame extends JFrame{
    int width;
    int height;
    String title;
    StatusPanel statusPanel;
    ControlPanel controlPanel;
    BoardPanel boardPanel;
    Random random = new Random();
    boolean isHardMode; // 是否为困难模式
    String theme; // 当前主题（fruit/number/animal）
    String music; // 当前音乐（calm/cheerful/dynamic）

    public GameFrame(String title, int width, int height, boolean isHardMode) {
        this(title, width, height, isHardMode, null, "fruit", "calm");
    }
    
    public GameFrame(String title, int width, int height, boolean isHardMode, String theme) {
        this(title, width, height, isHardMode, null, theme, "calm");
    }
    
    public GameFrame(String title, int width, int height, boolean isHardMode, String theme, String music) {
        this(title, width, height, isHardMode, null, theme, music);
    }
    
    // 从存档创建游戏窗口的构造函数
    private GameFrame(String title, int width, int height, boolean isHardMode, GameData saveData, String theme, String music) {
        super(title);
        this.setResizable(true);
        this.isHardMode = isHardMode;
        this.theme = theme != null ? theme : "fruit"; // 默认水果主题
        this.music = music != null ? music : "calm"; // 默认宁静音乐
        
        this.title = title;
        this.width = width;
        this.height = height;
        this.setLayout(null);
        this.setSize(width, height);
        
        // 设置自定义关闭操作
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleClose();
            }
        });
        
        this.setLocationRelativeTo(null);
        
        // 根据是否有存档数据决定是生成新棋盘还是加载存档
        if (saveData != null) {
            // 从存档加载
            this.boardPanel = createBoardPanelFromSave(saveData);
            
            // 创建状态面板
            this.statusPanel = new StatusPanel(0, 0, width, 100);
            
            // 创建控制面板
            this.controlPanel = new ControlPanel(statusPanel, boardPanel, 0, height - 100, width, 100, isHardMode, theme, music);
            
            // 设置相互引用（会自动调用 updateRemainingPairs）
            boardPanel.setStatusPanel(statusPanel);
            boardPanel.setControlPanel(controlPanel);
            statusPanel.setControlPanel(controlPanel);
            
            // 添加组件到窗口
            this.add(this.statusPanel);
            this.add(boardPanel);
            this.add(this.controlPanel);
            
            // 恢复存档状态
            this.statusPanel.setScore(saveData.getScore());
            this.statusPanel.setTotalTime(saveData.getRemainingTime());
            this.statusPanel.setLevel(saveData.getLevel());
            this.statusPanel.setStatus("运行中");
            this.statusPanel.startTimer();
            
            // 更新按钮状态
            controlPanel.refreshButtons();
            
            // 播放默认背景音乐
            MusicManager musicManager = MusicManager.getInstance();
            musicManager.playMusic(this.music);
        } else {
            // 新建游戏
            this.boardPanel = createBoardPanel();
            
            // 创建状态面板
            this.statusPanel = new StatusPanel(0, 0, width, 100);
            
            // 创建控制面板
            this.controlPanel = new ControlPanel(statusPanel, boardPanel, 0, height - 100, width, 100, isHardMode, theme, music);
            
            // 设置相互引用（会自动调用 updateRemainingPairs）
            boardPanel.setStatusPanel(statusPanel);
            boardPanel.setControlPanel(controlPanel);
            statusPanel.setControlPanel(controlPanel);
            
            // 添加组件到窗口
            this.add(this.statusPanel);
            this.add(boardPanel);
            this.add(this.controlPanel);
            
            // 直接开始游戏并启动倒计时
            statusPanel.setStatus("运行中");
            statusPanel.startTimer();
            
            // 更新按钮状态（因为状态变成了"运行中"，保存按钮应该可用）
            controlPanel.refreshButtons();
            
            // 播放默认背景音乐
            MusicManager musicManager = MusicManager.getInstance();
            musicManager.playMusic(this.music);
        }
        
        this.setVisible(true);
    }
    
    // 处理窗口关闭事件
    private void handleClose() {
        UserManager userManager = UserManager.getInstance();
        
        // 如果是注册用户
        if (userManager.isRegisteredUser()) {
            String currentStatus = statusPanel.getStatus();
            
            if ("运行中".equals(currentStatus)) {
                // 游戏进行中：先清空之前的存档，再询问是否保存当前进度
                controlPanel.clearAllSaves();
                
                int choice = JOptionPane.showConfirmDialog(
                    this,
                    "检测到游戏正在进行中，是否保存当前进度？",
                    "保存游戏",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (choice == JOptionPane.YES_OPTION) {
                    // 用户选择保存
                    if (saveGameProgress()) {
                        // 保存成功后停止音乐并退出
                        MusicManager musicManager = MusicManager.getInstance();
                        musicManager.stopMusic();
                        System.exit(0);
                    }
                    // 如果保存失败，不退出，让用户继续游戏
                } else {
                    // 用户选择不保存，停止音乐并退出（存档已清空）
                    MusicManager musicManager = MusicManager.getInstance();
                    musicManager.stopMusic();
                    System.exit(0);
                }
            } else {
                // 游戏已结束：清空所有存档后退出
                controlPanel.clearAllSaves();
                MusicManager musicManager = MusicManager.getInstance();
                musicManager.stopMusic();
                System.exit(0);
            }
        } else {
            // 游客模式，停止音乐并直接退出
            MusicManager musicManager = MusicManager.getInstance();
            musicManager.stopMusic();
            System.exit(0);
        }
    }
    
    // 保存游戏进度
    private boolean saveGameProgress() {
        try {
            UserManager userManager = UserManager.getInstance();
            String username = userManager.getCurrentUser().getUsername();
            
            // 查找第一个空的存档槽位，或者使用最新的存档槽位
            int slotToUse = 1;
            long latestTime = 0;
            
            for (int i = 1; i <= 3; i++) {
                if (SaveManager.hasSave(username, i)) {
                    GameData data = SaveManager.loadGame(username, i);
                    if (data != null && data.getTimestamp() > latestTime) {
                        latestTime = data.getTimestamp();
                        slotToUse = i;
                    }
                } else {
                    // 找到空槽位，优先使用
                    slotToUse = i;
                    break;
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
            
            // 保存到槽位
            return SaveManager.saveGame(gameData, slotToUse);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "保存游戏时发生错误: " + e.getMessage(), 
                "保存失败", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // 从存档创建棋盘面板
    private BoardPanel createBoardPanelFromSave(GameData saveData) {
        int[][] boardState = saveData.getBoardState();
        int rows = boardState.length;
        int cols = boardState[0].length;
        
        BoardPanel panel = new BoardPanel(new GameBoard(rows, cols, new Cell[rows][cols]), 0, 100, width, height - 200, isHardMode, theme);
        panel.loadBoardFromState(boardState);
        
        return panel;
    }
    
    // 从存档创建游戏窗口的静态工厂方法
    public static GameFrame createFromSave(GameData saveData) {
        boolean isHardMode = "HARD".equals(saveData.getDifficulty());
        return new GameFrame("连连看", 1000, 1000, isHardMode, saveData, "fruit", "calm");
    }
    
    // 创建棋盘面板
    private BoardPanel createBoardPanel() {
        Cell[][] board;
        
        if (isHardMode) {
            // 困难模式：12x12棋盘
            board = new Cell[12][12];
            for (int i = 0; i < 12; ++i)
                for (int j = 0; j < 12; ++j) 
                    board[i][j] = new Cell(new Position(i, j), true, 0);
            
            do {
                for (int i = 1; i <= 10; i++) {
                    for (int j = 1; j <= 10; j++) {
                        board[i][j] = new Cell(new Position(i, j), false, random.nextInt(1, 13));
                    }
                }
            } while (!Utils.isSolvable(new GameBoard(12, 12, board), true));
            
            boardPanel = new BoardPanel(new GameBoard(12, 12, board), 0, 100, width, height - 200, true, theme);
        } else {
            // 简单模式：11x11棋盘
            board = new Cell[11][11];
            for (int i = 0; i < 11; ++i)
                for (int j = 0; j < 11; ++j) 
                    board[i][j] = new Cell(new Position(i, j), true, 0);
            
            do {
                for (int i = 1; i <= 4; i++) {
                    for (int j = 1; j <= 4; j++) {
                        board[i][j] = new Cell(new Position(i, j), false, random.nextInt(1, 6));
                    }
                }
                for (int i = 6; i <= 9; i++) {
                    for (int j = 6; j <= 9; j++) {
                        board[i][j] = new Cell(new Position(i, j), false, random.nextInt(1, 5));
                    }
                }
            } while (!Utils.isSolvable(new GameBoard(11, 11, board), false));
            
            boardPanel = new BoardPanel(new GameBoard(11, 11, board), 0, 100, width, height - 200, false, theme);
        }
        
        return boardPanel;
    }
}
