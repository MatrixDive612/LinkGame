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

// 绘制面板：负责游戏棋盘的显示和交互
public class BoardPanel extends JPanel {
    int offSetX;
    int offSetY;

    List<Image> imageList = new ArrayList<>(); // 图片资源列表
    GameBoard gameBoard; // 游戏棋盘数据
    List<Line> lineList = new ArrayList<>(); // 连线列表
    int totalRow; // 总行数
    int totalCol; // 总列数
    boolean lineVisible; // 连线是否可见
    int width;
    int height;
    int cellWidth; // 单元格宽度
    int cellHeight; // 单元格高度
    Position firstSelected = null; // 第一个选中的位置
    Position secondSelected = null; // 第二个选中的位置
    boolean animating = false; // 是否正在动画中
    boolean isHardMode; // 是否为困难模式
    StatusPanel statusPanel; // 状态面板引用
    ControlPanel controlPanel; // 控制面板引用
    int comboCount = 0; // 连消计数
    long lastEliminateTime = 0; // 上次消除时间
    String theme; // 当前主题（fruit/number/animal）
    
    // 根据鼠标坐标获取棋盘位置
    public Position getPositionByPoint(int x, int y) {
        int col = x / cellWidth;
        int row = y / cellHeight;
        if (row < 0 || row >= totalRow || col < 0 || col >= totalCol) {
            return null;
        }
        return new Position(row, col);
    }

    // 判断两个位置是否可以连接（图案相同且路径合法）
    public boolean canConnect(Position p1, Position p2) {
        Cell c1 = gameBoard.getCell(p1.getRow(), p1.getCol());
        Cell c2 = gameBoard.getCell(p2.getRow(), p2.getCol());
        // 图案必须相同
        if(c1.getIconIndex() != c2.getIconIndex()) return false;
        // 检查路径是否合法（0/1/2折）
        return Utils.canLinkAB(gameBoard, p1, p2);
    }

    // 显示连接线
    public void showLine(Cell c1, Cell c2) {
        lineList.clear();
        lineList.add(new Line(c1, c2));
        lineVisible = true;
        repaint();
    }

    // 清除连接线
    public void clearLine() {
        lineVisible = false;
        lineList.clear();
        repaint();
    }

    // 构造函数
    public BoardPanel(GameBoard gameBoard, int offSetX, int offSetY, int width, int height, boolean isHardMode, String theme) {
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.setBounds(offSetX, offSetY, width, height);
        this.totalRow = gameBoard.getRowCnt();
        this.totalCol = gameBoard.getColCnt();
        this.width = width;
        this.height = height;
        this.isHardMode = isHardMode;
        this.theme = theme != null ? theme : "fruit"; // 默认水果主题
        this.setLayout(new GridLayout(this.totalRow, this.totalCol));
        this.gameBoard = gameBoard;
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.cellWidth = this.width / totalCol;
        this.cellHeight = this.height / totalRow;
        
        // 加载指定主题的图片资源
        loadThemeImages();
        
        // 添加鼠标点击事件监听
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }
    
    // 加载主题图片
    private void loadThemeImages() {
        imageList.clear();
        File themeDir = new File("resource/" + theme);
        
        if (!themeDir.exists()) {
            System.err.println("主题文件夹不存在: " + themeDir.getPath());
            // 如果主题文件夹不存在，尝试加载默认主题
            themeDir = new File("resource/fruit");
        }
        
        File[] files = themeDir.listFiles();
        if (files != null) {
            // 按文件名排序
            java.util.Arrays.sort(files, (f1, f2) -> {
                String name1 = f1.getName().replace(".png", "");
                String name2 = f2.getName().replace(".png", "");
                try {
                    return Integer.parseInt(name1) - Integer.parseInt(name2);
                } catch (NumberFormatException e) {
                    return f1.getName().compareTo(f2.getName());
                }
            });
            
            for (File file : files) {
                if (file.getName().endsWith(".png")) {
                    ImageIcon icon = new ImageIcon(file.getPath());
                    imageList.add(icon.getImage());
                }
            }
        }
        
        System.out.println("已加载主题: " + theme + ", 图片数量: " + imageList.size());
    }
    
    // 设置主题
    public void setTheme(String newTheme) {
        if (this.theme.equals(newTheme)) {
            return; // 主题相同，无需切换
        }
        
        this.theme = newTheme;
        loadThemeImages(); // 重新加载图片
        repaint(); // 重绘界面
    }
    
    // 设置状态面板引用
    public void setStatusPanel(StatusPanel statusPanel) {
        this.statusPanel = statusPanel;
        // 设置状态面板后立即更新剩余对数显示
        if (this.gameBoard != null) {
            updateRemainingPairs();
        }
    }
    
    // 设置控制面板引用
    public void setControlPanel(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }
    
    // 重新开始游戏：重置棋盘、时间、分数
    public void restartGame() {
        generateNewBoard(); // 重新生成棋盘
        firstSelected = null;
        secondSelected = null;
        lineVisible = false;
        lineList.clear();
        animating = false;
        comboCount = 0;
        repaint();
    }
    
    // 重新生成棋盘
    private void generateNewBoard() {
        int rows, cols, iconTypes;
        
        // 根据难度设置棋盘大小和图标种类
        if (isHardMode) {
            rows = 12;
            cols = 12;
            iconTypes = 12;  // 困难模式使用1-12共12种图案
        } else {
            rows = 11;
            cols = 11;
            iconTypes = 5;   // 简单模式使用1-5共5种图案
        }
        
        this.totalRow = rows;
        this.totalCol = cols;
        this.gameBoard = new GameBoard(rows, cols, new Cell[rows][cols]);
        
        // 初始化边框为空
        for (int i = 0; i < rows; ++i)
            for (int j = 0; j < cols; ++j) 
                gameBoard.setCell(i, j, new Cell(new Position(i, j), true, 0));
        
        java.util.Random random = new java.util.Random();
        // 生成可解的棋盘
        do {
            if (isHardMode) {
                // 困难模式：10x10区域，使用1-12的图案
                for (int i = 1; i <= 10; i++) {
                    for (int j = 1; j <= 10; j++) {
                        gameBoard.setCell(i, j, new Cell(new Position(i, j), false, random.nextInt(1, iconTypes + 1)));
                    }
                }
            } else {
                // 简单模式：两个4x4区域，使用1-5的图案
                for (int i = 1; i <= 4; i++) {
                    for (int j = 1; j <= 4; j++) {
                        gameBoard.setCell(i, j, new Cell(new Position(i, j), false, random.nextInt(1, iconTypes + 1)));
                    }
                }
                for (int i = 6; i <= 9; i++) {
                    for (int j = 6; j <= 9; j++) {
                        gameBoard.setCell(i, j, new Cell(new Position(i, j), false, random.nextInt(1, iconTypes + 1)));
                    }
                }
            }
        } while (!Utils.isSolvable(gameBoard, isHardMode));
        
        // 更新剩余对数显示
        updateRemainingPairs();
    }
    
    // 计算并更新剩余可消除对数
    private void updateRemainingPairs() {
        int maxIconType = isHardMode ? 12 : 5;  // 简单模式5种，困难模式12种
        int[] count = new int[maxIconType + 1];
        
        // 统计每种图标的数量
        for (int i = 1; i < totalRow - 1; i++) {
            for (int j = 1; j < totalCol - 1; j++) {
                Cell cell = gameBoard.getCell(i, j);
                if (!cell.isEmpty()) {
                    count[cell.getIconIndex()]++;
                }
            }
        }
        
        // 计算对数（每种图标数量除以2）
        int pairs = 0;
        for (int i = 1; i < count.length; i++) {
            pairs += count[i] / 2;
        }
        
        // 更新状态面板显示
        if (statusPanel != null) {
            statusPanel.setRemainingPairs(pairs);
        }
    }
    
    // 获取棋盘状态用于存档
    public int[][] getBoardStateForSave() {
        int[][] state = new int[totalRow][totalCol];
        for (int i = 0; i < totalRow; i++) {
            for (int j = 0; j < totalCol; j++) {
                Cell cell = gameBoard.getCell(i, j);
                if (cell.isEmpty()) {
                    state[i][j] = 0; // 空位置用0表示
                } else {
                    state[i][j] = cell.getIconIndex(); // 有图标的位置保存图标索引
                }
            }
        }
        return state;
    }
    
    // 从存档加载棋盘状态
    public void loadBoardFromState(int[][] boardState) {
        int rows = boardState.length;
        int cols = boardState[0].length;
        
        this.totalRow = rows;
        this.totalCol = cols;
        this.gameBoard = new GameBoard(rows, cols, new Cell[rows][cols]);
        
        // 恢复棋盘状态
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Position pos = new Position(i, j);
                if (boardState[i][j] == 0) {
                    // 空位置
                    gameBoard.setCell(i, j, new Cell(pos, true, 0));
                } else {
                    // 有图标的位置
                    gameBoard.setCell(i, j, new Cell(pos, false, boardState[i][j]));
                }
            }
        }
        
        // 重置选择状态
        firstSelected = null;
        secondSelected = null;
        lineVisible = false;
        lineList.clear();
        animating = false;
        comboCount = 0;
        
        // 更新剩余对数
        updateRemainingPairs();
        repaint();
    }
    
    // 检查是否还有可消除的对（死局检测）
    public boolean hasAvailableMoves() {
        for (int i = 1; i < totalRow - 1; i++) {
            for (int j = 1; j < totalCol - 1; j++) {
                Cell c1 = gameBoard.getCell(i, j);
                if (c1.isEmpty()) continue;
                
                for (int k = 1; k < totalRow - 1; k++) {
                    for (int m = 1; m < totalCol - 1; m++) {
                        if (i == k && j == m) continue;
                        
                        Cell c2 = gameBoard.getCell(k, m);
                        if (c2.isEmpty()) continue;
                        
                        // 图案相同且可以连接
                        if (c1.getIconIndex() == c2.getIconIndex() && 
                            Utils.canLinkAB(gameBoard, new Position(i, j), new Position(k, m))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // 处理鼠标点击事件
    public void handleClick(int x, int y) {
        // 只有在游戏进行中才允许操作
        if (statusPanel != null && !"运行中".equals(statusPanel.getStatus())) {
            return;
        }
        
        // 动画进行中不允许操作
        if (animating) {
            return;
        }

        // 获取点击位置
        Position pos = getPositionByPoint(x, y);
        if (pos == null) {
            return;
        }

        // 获取点击的单元格
        Cell clickedCell = gameBoard.getCell(pos.getRow(), pos.getCol());
        if (clickedCell == null || clickedCell.isEmpty()) {
            return;
        }

        // 第一次选择
        if (firstSelected == null) {
            gameBoard.clearAllChosen();
            clickedCell.setChosen(true);
            firstSelected = pos;
            repaint();
            return;
        }

        // 点击同一个位置，取消选择
        if (firstSelected.equals(pos)) {
            clickedCell.setChosen(false);
            firstSelected = null;
            secondSelected = null;
            repaint();
            return;
        }

        // 第二次选择
        secondSelected = pos;
        Cell secondCell = gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol());
        secondCell.setChosen(true);
        repaint();
        
        // 检查是否可以连接
        if (canConnect(firstSelected, secondSelected, lineList)) {
            // 可以连接，执行消除
            animating = true;
            showLine(
                gameBoard.getCell(firstSelected.getRow(), firstSelected.getCol()),
                gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol())
            );
            
            // 计算Combo
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastEliminateTime < 3000) { // 3秒内连续消除
                comboCount++;
            } else {
                comboCount = 1;
            }
            lastEliminateTime = currentTime;
            
            // 计算分数（基础10分，Combo递增）
            int baseScore = 10;
            int comboBonus = (comboCount - 1) * 5; // 每多一个combo额外+5分
            int totalScore = baseScore + comboBonus;

            //每0.3秒刷新一次界面？来让连线消失？还有其他什么消失了？
            Timer timer = new Timer(300, e -> {
                Cell c1 = gameBoard.getCell(firstSelected.getRow(), firstSelected.getCol());
                Cell c2 = gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol());
                
                // 消除图案
                c1.setEmpty(true);
                c2.setEmpty(true);
                c1.setChosen(false);
                c2.setChosen(false);
                lineVisible = false;//连线消失
                lineList.clear();//连线消失
                firstSelected = null;
                secondSelected = null;
                animating = false;
                
                // 更新分数
                if (statusPanel != null) {
                    statusPanel.addScore(totalScore);
                    
                    // 更新操作记录
                    String iconName = "图案" + c1.getIconIndex();
                    if (comboCount >= 3) {
                        statusPanel.setLastAction("消除了[" + iconName + "] +" + totalScore + "分 连消" + comboCount);
                    } else {
                        statusPanel.setLastAction("消除了[" + iconName + "] +" + totalScore + "分");
                    }
                    
                    // 更新剩余对数
                    updateRemainingPairs();
                    
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
                        
                        // 通知控制面板游戏结束，启用读取按钮
                        if (controlPanel != null) {
                            controlPanel.notifyGameEnded();
                        }
                        
                        JOptionPane.showMessageDialog(BoardPanel.this, 
                            "恭喜通关！\n得分: " + statusPanel.getScore() + "\n用时: " + formatTime(statusPanel.getElapsedTime()), 
                            "胜利", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else if (!hasAvailableMoves()) {
                        // 死局检测
                        statusPanel.setStatus("死局！");
                        statusPanel.stopTimer();
                        
                        // 通知控制面板游戏结束，启用读取按钮
                        if (controlPanel != null) {
                            controlPanel.notifyGameEnded();
                        }
                        
                        JOptionPane.showMessageDialog(BoardPanel.this, 
                            "没有可消除的对了！\n即将重新生成棋盘", 
                            "死局", 
                            JOptionPane.WARNING_MESSAGE);
                        generateNewBoard(); // 重新生成棋盘
                        statusPanel.setStatus("运行中");
                        statusPanel.startTimer();
                        
                        // 重新生成后禁用读取按钮
                        if (controlPanel != null) {
                            controlPanel.refreshButtons();
                        }
                    }
                }
                
                repaint();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            // 不能连接
            if (statusPanel != null) {
                Cell c1 = gameBoard.getCell(firstSelected.getRow(), firstSelected.getCol());
                Cell c2 = gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol());
                
                if (c1.getIconIndex() != c2.getIconIndex()) {
                    // 图案不同，取消第一个选择，保留第二个
                    statusPanel.setLastAction("图案不同，请重新选择");
                    gameBoard.clearAllChosen();
                    secondCell.setChosen(true);
                    firstSelected = secondSelected;
                    secondSelected = null;
                } else {
                    // 图案相同但无法连接
                    statusPanel.setLastAction("无法连接，请重新选择");
                    JOptionPane.showMessageDialog(this, 
                        "图案相同但无法连接！", 
                        "提示", 
                        JOptionPane.WARNING_MESSAGE);
                    gameBoard.clearAllChosen();
                    firstSelected = null;
                    secondSelected = null;
                }
                repaint();
            }
        }
    }
    
    // 格式化时间显示
    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }
    
    // 获取单元格矩形区域
    public Rectangle getRectangle(Position position) {
        int x = position.getCol() * cellWidth;
        int y = position.getRow() * cellHeight;
        return new Rectangle(x, y, cellWidth, cellHeight);
    }
    
    // 绘制组件
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // 绘制所有单元格
        for (int i = 0; i < gameBoard.getRowCnt(); i++) {
            for (int j = 0; j < gameBoard.getColCnt(); j++) {
                Rectangle rec = getRectangle(new Position(i, j));
                g2.drawImage(
                    imageList.get(gameBoard.getCell(i, j).getIconIndex()),
                    rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight(),
                    this
                );
                
                // 绘制选中框
                if (gameBoard.getCell(i, j).getIsChosen()) {
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
                }
            }
        }
        
        // 绘制连接线
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3));
        if (lineVisible) {
            for (Line line: lineList) {
                Rectangle rec1 = getRectangle(line.getCell1().getPos());
                Rectangle rec2 = getRectangle(line.getCell2().getPos());
                g.drawLine(
                    (int) rec1.getCenterPosition().getX(), 
                    (int) rec1.getCenterPosition().getY(), 
                    (int) rec2.getCenterPosition().getX(), 
                    (int) rec2.getCenterPosition().getY()
                );
            }
        }
    }
}
