package ui;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    JLabel statusLabel;
    JLabel timeLabel;
    Timer timer;
    int seconds;
    int minutes;
    int hours;
    int offSetX;
    int offSetY;
    int width;
    int height;
    public StatusPanel(int offSetX, int offSetY,int width, int height) {
        this.setLayout(null);
        this.setBounds(offSetX, offSetY, width, height);
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.width = width;
        this.height = height;
        statusLabel = new JLabel("ready");
        timeLabel = new JLabel("00:00:00");
        timer = new Timer(1000, e -> {
            seconds++;
            if (seconds == 60) {
                minutes++;
                seconds = 0;
                if (minutes == 60) {
                    minutes = 0;
                    hours++;
                }
            }
            timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        });
        timer.start();
        statusLabel.setFont(new Font("Arial", Font.BOLD, 50));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 50));
        Dimension size = statusLabel.getPreferredSize();
        Dimension timeLabelSize = timeLabel.getPreferredSize();
        int x = (width - size.width) / 3 ;
        int y = (height - size.height) / 3;
        int time_x = (width - timeLabelSize.width) * 2 / 3;
        int time_y = (height - timeLabelSize.height) * 2 /3;
        statusLabel.setBounds(x, y, size.width, size.height);
        timeLabel.setBounds(time_x, time_y, timeLabelSize.width, timeLabelSize.height);
        this.add(statusLabel);
        this.add(timeLabel);
    }
    public void setStatus(String text) {
        statusLabel.setText(text);
        Dimension size = statusLabel.getPreferredSize();
        int x = (width - size.width) / 3;
        int y = (height - size.height) / 3;
        statusLabel.setBounds(x, y, size.width, size.height);
        repaint();
    }
}
