package app;

import model.Cell;
import model.GameBoard;
import model.Position;
import ui.BoardPanel;
import ui.GameFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //111
            /*GameFrame frame = new GameFrame("连连看", 800, 1000);
            frame.repaint();*/
            JFrame loginFrame=new JFrame("登录");
            //loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setDefaultCloseOperation(3);
            //loginFrame.setSize(400,200);
            //mine:
            loginFrame.setSize(800,800);
            loginFrame.setLayout(null);

            /*JTextField textField=new JTextField();//文本对象，即输入框
            textField.setSize(100,50);
            textField.setLocation(50,50);*/

            JLabel labelUser = new JLabel("用户：");
            JLabel labelPwd = new JLabel("密码：");
            labelUser.setSize(100,50);
            labelPwd.setSize(100,50);
            labelUser.setLocation(50,50);
            labelPwd.setLocation(50,125);

            JLabel labelPic = new JLabel((new ImageIcon(".\\resource\\1.png")));
            labelPic.setSize(50,50);
            labelPic.setLocation(0,0);


            JTextField textUser=new JTextField();
            textUser.setSize(100,50);
            textUser.setLocation(150,50);

            JTextField textPwd=new JTextField();
            textPwd.setSize(100,50);
            textPwd.setLocation(150,125);

            JButton login = new JButton("按钮");
            login.setLocation(50,200);
            login.setSize(100, 50);
            login.addActionListener(e -> {
                //添加事件
                String strUser = textUser.getText();
                String strPwd = textPwd.getText();
                System.out.println(strUser+": "+strPwd);
                //GameFrame frame = new GameFrame("连连看", 800, 1000);
                GameFrame frame = new GameFrame("连连看", 1000, 1000);
                frame.repaint();
            });

            loginFrame.add(login);
            //I added this:
            loginFrame.add(labelUser);
            loginFrame.add(labelPwd);
            loginFrame.add(labelPic);
            //
            loginFrame.add(textUser);
            loginFrame.add(textPwd);
            loginFrame.setVisible(true);
        });
    }
}
