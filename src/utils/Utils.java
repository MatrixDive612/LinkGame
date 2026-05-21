package utils;

import model.Cell;
import model.GameBoard;
import model.Position;
import ui.BoardPanel;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<Cell> getReachablePointsInFourDirections(GameBoard gameBoard, Position posA) {
        List<Cell> res = new ArrayList<>();
        for (int i = posA.getRow() + 1; i < gameBoard.getRowCnt(); i++) {//Is "< gameBoard.getRowCnt()" correct?
            if (gameBoard.getCell(i, posA.getCol()).isEmpty()) {
                res.add(gameBoard.getCell(i, posA.getCol()));
            } else {
                break;
            }
        }
        for (int i = posA.getRow() - 1; i >= 0; i--) {
            if (gameBoard.getCell(i, posA.getCol()).isEmpty()) {
                res.add(gameBoard.getCell(i, posA.getCol()));
            } else {
                break;
            }
        }
        for (int i = posA.getCol() + 1; i < gameBoard.getColCnt(); i++) {
            if (gameBoard.getCell(posA.getRow(), i).isEmpty()) {
                res.add(gameBoard.getCell(posA.getRow(), i));
            } else {
                break;
            }
        }
        for (int i = posA.getCol() - 1; i >= 0; i--) {
            if (gameBoard.getCell(posA.getRow(), i).isEmpty()) {
                res.add(gameBoard.getCell(posA.getRow(), i));
            } else {
                break;
            }
        }
        return res;
    }
    public static boolean findZeroTurn(GameBoard gameBoard, Position posA, Position posB) {
        boolean tmpRes0 = true;
        if (posA.getCol() == posB.getCol()) {
            int smallLine = Math.min(posA.getRow(), posB.getRow());
            int largeLine = Math.max(posA.getRow(), posB.getRow());
            //for (int i = smallLine + 1; i < largeLine - 1; i++) {
            //mine:
            for (int i = smallLine + 1; i < largeLine; i++) {
                if (!gameBoard.getCell(i, posA.getCol()).isEmpty()) {
                    tmpRes0 = false;
                    break;
                }
            }
            if (tmpRes0) {
                return true;
            }
        }
        if (posA.getRow() == posB.getRow()) {
            int smallCol = Math.min(posA.getCol(), posB.getCol());
            int largeCol = Math.max(posA.getCol(), posB.getCol());
            //for (int i = smallCol + 1; i < largeCol - 1; i++) {
            //mine:
            for (int i = smallCol + 1; i < largeCol; i++) {
                if (!gameBoard.getCell(posA.getRow(), i).isEmpty()) {
                    tmpRes0 = false;
                    break;
                }
            }
            if (tmpRes0) {
                return true;
            }
        }
        return false;
    }
    public static boolean findOneTurn(GameBoard gameBoard, Position posA, Position posB) {
        //if (posA.getCol() != posB.getCol() && posA.getRow() != posB.getCol()) {
        //mine:
        if (posA.getCol() != posB.getCol() && posA.getRow() != posB.getRow()) {
            Position cornerPoint1 = new Position(posA.getRow(), posB.getCol());
            Position cornerPoint2 = new Position(posB.getRow(), posA.getCol());
            //missing: check if the two corner points are empty
            if (findZeroTurn(gameBoard, posA, cornerPoint1) && findZeroTurn(gameBoard, posB, cornerPoint1) && gameBoard.getCell(posA.getRow(), posB.getCol()).isEmpty()) {
                return true;
            }
            if (findZeroTurn(gameBoard, posA, cornerPoint2) && findZeroTurn(gameBoard, posB, cornerPoint2) && gameBoard.getCell(posB.getRow(), posA.getCol()).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    public static boolean findTwoTurn(GameBoard gameBoard, Position posA, Position posB) {
        List<Cell> reachablePoints = getReachablePointsInFourDirections(gameBoard, posA);
        for (Cell c: reachablePoints) {
            if (findOneTurn(gameBoard, c.getPos(), posB)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canLinkAB(GameBoard gameBoard, Position posA, Position posB){
        if (findZeroTurn(gameBoard, posA, posB)) {
            return true;
        }
        // 判断1折，检查两个拐点
        if (findOneTurn(gameBoard, posA, posB)) {
            return true;
        }
        // 判断2折
        if (findTwoTurn(gameBoard, posA, posB)) {
            return true;
        }
        return false;
    }

    public static boolean isSolvable(GameBoard gameBoard){
        int rowCnt = gameBoard.getRowCnt();
        int colCnt = gameBoard.getColCnt();
        /*boolean[][] board = new boolean[rowCnt][colCnt];
        for (int i = 0; i < rowCnt; ++i)
            for (int j = 0; j < colCnt; ++j) board[i][j] = false;
        for (int i = 1; i < rowCnt-1; i++)
            for (int j = 1; j < colCnt-1; j++)
                board[i][j] = true;*/

        boolean flag1 = false;
        do {
            flag1 = false;
            for (int i = 1; i < gameBoard.getRowCnt()-1; ++i)
                for (int j = 1; j < gameBoard.getColCnt()-1; ++j){
                    for (int k = i; k < gameBoard.getRowCnt()-1; ++k)
                        for (int m = 1; m < gameBoard.getColCnt()-1; ++m){
                            Cell c1 = gameBoard.getCell(i, j);
                            Cell c2 = gameBoard.getCell(k, m);
                            if (!c1.isEmpty() && !c2.isEmpty() && (i != k || j != m)) {//means that both cells are not deleted, and they're not the same cell
                                if(c1.getIconIndex() == c2.getIconIndex())
                                    if (canLinkAB(gameBoard, new Position(i, j), new Position(k, m))) {
                                        /*board[i][j] = false;
                                        board[k][m] = false;*/
                                        c1.setIsEmpty(true); c2.setIsEmpty(true);
                                        flag1 = true; //indicates that a change is made
                                        break;
                                    }
                            }
                        }
                }
        } while (flag1); //until flag1 == false, which means that no more changes could be made

        boolean flag2 = true;
        for (int i = 1; i < gameBoard.getRowCnt()-1; ++i) {
            for (int j = 1; j < gameBoard.getColCnt()-1; ++j)
                if (!gameBoard.getCell(i, j).isEmpty()) {
                    flag2 = false; //indicates that there's still a cell left, so break the loop and return flag2 = false
                    break;
                }
            if (!flag2) break;
        }

        for (int i = 1; i < rowCnt-1; i++)
            for (int j = 1; j < colCnt-1; j++)
                gameBoard.getCell(i, j).setIsEmpty(false);

        return flag2;
    }
}