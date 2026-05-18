package ui;

import model.Cell;
import model.GameBoard;
import model.Position;

import javax.swing.*;
import java.awt.*;

import java.util.Random;

public class GameFrame extends JFrame{
    int width;
    int height;
    String title;
    StatusPanel statusPanel;
    ControlPanel controlPanel;
    Random random = new Random();

    public GameFrame(String title, int width, int height) {
        super(title);
        //this.setResizable(false);
        this.setResizable(true);
        int size = 3;
        //Cell[][] board = new Cell[size + 2][size + 2];
        //mine:
        Cell[][] board = new Cell[12][12];
        //placing the empty boxes:
        /*for (int i = 0; i < size + 2; i++) {
            for (int j = 0; j < size + 2; j++) {
                if (i == 0 || i == size + 1 || j == 0 || j == size + 1) {
                    board[i][j] = new Cell(new Position(i, j), true, 0); /// 边框
                }
            }
        }*/
        for (int i = 0; i < 12; ++i)
            for (int j = 0; j < 12; ++j) board[i][j] = new Cell(new Position(i, j), true, 0);
        //placing the chess:
        for (int i = 1; i <= 10/*size*/; i++) {
            for (int j = 1; j <= 10/*size*/; j++) {
                //board[i][j] = new Cell(new Position(i, j), false, 1);
                //mine:
                board[i][j] = new Cell(new Position(i, j), false, random.nextInt(1,12));
            }
        }
        //my little experiment:
        //board[0][4] = new Cell(new Position(0, 4), false, 1);

        //BoardPanel boardPanel = new BoardPanel(new GameBoard(5, 5, board), 0, 100, 800, 800);
        BoardPanel boardPanel = new BoardPanel(new GameBoard(12, 12, board), 0, 100, 1000, 800);
        this.title = title;
        this.width = width;
        this.height = height;
        this.setLayout(null);
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        /*this.statusPanel = new StatusPanel(0, 0, 800, 100);
        this.controlPanel = new ControlPanel(statusPanel, 0, 900, 800, 100);*/
        this.statusPanel = new StatusPanel(0, 0, 1000, 100);
        this.controlPanel = new ControlPanel(statusPanel, 0, 900, 1000, 100);
        //mine:
        //this.controlPanel = new ControlPanel(statusPanel, 0, 800, 800, 100);
        this.add(this.statusPanel);
        this.add(this.controlPanel);
        this.add(boardPanel);
        //mine:
        /*this.add(boardPanel);
        this.add(this.controlPanel);*/
    }

}
