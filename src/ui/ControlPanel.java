package ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    StatusPanel statusPanel;
    JButton startButton;
    int offSetX;
    int offSetY;
    int width;
    int height;
    public ControlPanel(StatusPanel statusPanel, int offSetX, int offSetY,int width, int height) {
        this.setLayout(null);
        this.setBounds(offSetX, offSetY, width, height);
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.width = width;
        this.height = height;
        this.startButton = new JButton("start");
        this.statusPanel = statusPanel;
        int btnWidth = 150;
        int btnHeight = 50;
        int x = (width - btnWidth) / 2;
        int y = (height - btnHeight) / 2;
        startButton.setBounds(x, y, btnWidth, btnHeight);
        startButton.setFont(new Font("Arial", Font.BOLD, 25));
        startButton.setFocusPainted(false);
        this.add(startButton);
        this.startButton.addActionListener(e -> {
            statusPanel.setStatus("RUN");
        });
    }

}
