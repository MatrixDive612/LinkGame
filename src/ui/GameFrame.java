package ui;

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

    public GameFrame(String title, int width, int height, boolean isHardMode) {
        super(title);
        this.setResizable(true);
        this.isHardMode = isHardMode;
        
        this.title = title;
        this.width = width;
        this.height = height;
        this.setLayout(null);
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        
        // 根据难度生成棋盘
        BoardPanel boardPanel = createBoardPanel();
        
        // 创建状态面板
        this.statusPanel = new StatusPanel(0, 0, width, 100);
        
        // 创建控制面板（传入boardPanel引用）
        this.controlPanel = new ControlPanel(statusPanel, boardPanel, 0, height - 100, width, 100, isHardMode);
        
        // 设置相互引用
        boardPanel.setStatusPanel(statusPanel);
        boardPanel.setControlPanel(controlPanel);
        
        // 添加组件到窗口
        this.add(this.statusPanel);
        this.add(boardPanel);
        this.add(this.controlPanel);
        
        this.setVisible(true);
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
            
            boardPanel = new BoardPanel(new GameBoard(12, 12, board), 0, 100, width, height - 200, true);
        }

        else {
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
            
            boardPanel = new BoardPanel(new GameBoard(11, 11, board), 0, 100, width, height - 200, false);
        }
        
        return boardPanel;
    }
}
