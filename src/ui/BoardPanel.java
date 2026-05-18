package ui;

import model.*;
import model.Rectangle;
import utils.Utils;

import javax.swing.*;
import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

//绘制面板：
public class BoardPanel extends JPanel {
    int offSetX;
    int offSetY;

    List<Image> imageList = new ArrayList<>();
    GameBoard gameBoard;
    List<Line> lineList = new ArrayList<>();
    int totalRow;
    int totalCol;
    boolean lineVisible;
    int width;
    int height;
    int cellWidth;
    int cellHeight;
    Position firstSelected = null;
    Position secondSelected = null;
    boolean animating = false;
    public Position getPositionByPoint(int x, int y) {

        int col = x / cellWidth;
        int row = y / cellHeight;
        if (row < 0 || row >= totalRow || col < 0 || col >= totalCol) {
            return null;
        }
        return new Position(row, col);
    }

    public boolean isAdjacent(Position p1, Position p2) {
        /*int dr = Math.abs(p1.getRow() - p2.getRow());
        int dc = Math.abs(p1.getCol() - p2.getCol());
        return dr + dc == 1;*/
        //mine:
        //return Utils.findZeroTurn(gameBoard, p1, p2) || Utils.findOneTurn(gameBoard, p1, p2);
        Cell c1 = gameBoard.getCell(p1.getRow(), p1.getCol());
        Cell c2 = gameBoard.getCell(p2.getRow(), p2.getCol());
        if(c1.getIconIndex() != c2.getIconIndex()) return false;
        else return Utils.canLinkAB(gameBoard, p1, p2);
    }

    public void showLine(Cell c1, Cell c2) {
        lineList.clear();
        lineList.add(new Line(c1, c2));
        lineVisible = true;
        repaint();
    }

    public void clearLine() {
        lineVisible = false;
        lineList.clear();
        repaint();
    }

    public BoardPanel(GameBoard gameBoard, int offSetX, int offSetY, int width, int height) {
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.setBounds(offSetX, offSetY, width, height);
        this.totalRow = gameBoard.getRowCnt();
        this.totalCol = gameBoard.getColCnt();
        this.width = width;
        this.height = height;
        this.setLayout(new GridLayout(this.totalRow, this.totalCol));
        this.gameBoard = gameBoard;
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.cellWidth = this.width / totalCol;
        this.cellHeight = this.height / totalRow;
        File dir = new File("resource");
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".png")) {
                ImageIcon icon = new ImageIcon(file.getPath());
                imageList.add(icon.getImage());
            }
        }
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX() , e.getY());
            }
        });
    }
    //handleClick 建议认真理解
    public void handleClick(int x, int y) {
        if (animating) {
            return;
        }

        Position pos = getPositionByPoint(x, y);
        if (pos == null) {
            return;
        }

        Cell clickedCell = gameBoard.getCell(pos.getRow(), pos.getCol());
        if (clickedCell == null || clickedCell.isEmpty()) {
            return;
        }

        if (firstSelected == null) {
            gameBoard.clearAllChosen();
            clickedCell.setChosen(true);
            firstSelected = pos;
            repaint();
            return;
        }

        if (firstSelected.equals(pos)) {
            clickedCell.setChosen(false);
            firstSelected = null;
            secondSelected = null;
            repaint();
            return;
        }

        secondSelected = pos;
        Cell secondCell = gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol());

        secondCell.setChosen(true);
        repaint();
        if (isAdjacent(firstSelected, secondSelected)) {
            animating = true;
            showLine(
                    gameBoard.getCell(firstSelected.getRow(), firstSelected.getCol()),
                    gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol())
            );
            Timer timer = new Timer(300, e -> {
                Cell c1 = gameBoard.getCell(firstSelected.getRow(), firstSelected.getCol());
                Cell c2 = gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol());
                c1.setEmpty(true);
                c2.setEmpty(true);
                c1.setChosen(false);
                c2.setChosen(false);
                lineVisible = false;
                lineList.clear();
                firstSelected = null;
                secondSelected = null;
                animating = false;
                repaint();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            gameBoard.clearAllChosen();
            secondCell.setChosen(true);
            firstSelected = secondSelected;
            secondSelected = null;
            repaint();
        }
    }
    public Rectangle getRectangle(Position position) {
        int x = position.getCol() * cellWidth;
        int y = position.getRow() * cellHeight;
        return new Rectangle(x, y, cellWidth, cellHeight);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < gameBoard.getRowCnt(); i++) {
            for (int j = 0; j < gameBoard.getColCnt(); j++) {
                Rectangle rec = getRectangle(new Position(i, j));
                g2.drawImage(
                        imageList.get(gameBoard.getCell(i, j).getIconIndex()),
                        rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight(),
                        this
                );
                if (gameBoard.getCell(i, j).getIsChosen()) {
                    //以下可不用修改：
                    g2.setColor(Color.RED);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRect(
                            rec.getX() + 1,
                            rec.getY() + 1,
                            rec.getWidth() - 3,
                            rec.getHeight() - 3
                    );
                } else {
                    g2.setColor(Color.GRAY);
                    g2.setStroke(new BasicStroke(1));
                    g2.drawRect(
                            rec.getX(),
                            rec.getY(),
                            rec.getWidth() - 1,
                            rec.getHeight() - 1
                    );
                    //
                }
            }
        }
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3));
        if (lineVisible) {
            for (Line line: lineList) {
                Rectangle rec1 = getRectangle(line.getCell1().getPos());
                Rectangle rec2 = getRectangle(line.getCell2().getPos());
                g.drawLine((int) rec1.getCenterPosition().getX(), (int) rec1.getCenterPosition().getY(), (int) rec2.getCenterPosition().getX(), (int) rec2.getCenterPosition().getY());
            }
        }

    }
}
