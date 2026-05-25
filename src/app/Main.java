package app;

import app.UserManager;
import ui.GameFrame;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        /*        SwingUtilities.invokeLater(() -> {
         *//*GameFrame frame = new GameFrame("连连看", 800, 1000);
            frame.repaint();*//*
            JFrame loginFrame=new JFrame("登录");
            //loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setDefaultCloseOperation(3);
            //loginFrame.setSize(400,200);
            //mine:
            loginFrame.setSize(800,800);
            loginFrame.setLayout(null);

            *//*JTextField textField=new JTextField();//文本对象，即输入框
            textField.setSize(100,50);
            textField.setLocation(50,50);*//*

            JLabel labelUser = new JLabel("用户：");
            JLabel labelPwd = new JLabel("密码：");
            labelUser.setSize(100,50);
            labelPwd.setSize(100,50);
            labelUser.setLocation(50,50);
            labelPwd.setLocation(50,125);

            JLabel labelPic = new JLabel(new ImageIcon(".\\resource\\1.png"));
            labelPic.setSize(200,200);
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
        });*/

        //Yi:
        // 在事件调度线程中启动GUI，确保线程安全
        SwingUtilities.invokeLater(() -> {
            // 创建登录窗口
            JFrame loginFrame = new JFrame("连连看 - 登录");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口时退出程序
            loginFrame.setSize(800, 600); // 设置窗口大小
            loginFrame.setLocationRelativeTo(null); // 窗口居中显示
            loginFrame.setLayout(null); // 使用绝对布局

/*//            这个背景图会挡住字，暂时还不知道怎么改？
            //添加背景照片
            JLabel labelPic = new JLabel(new ImageIcon(".\\resource\\0.png"));
            labelPic.setSize(800, 600);
            labelPic.setLocation(0, 0);
            loginFrame.add(labelPic);*/

            // 创建标题标签
            JLabel titleLabel = new JLabel("连连看", SwingConstants.CENTER);
            titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
            titleLabel.setBounds(200, 50, 400, 50);
            loginFrame.add(titleLabel);

            // 创建用户名标签和输入框
            JLabel userLabel = new JLabel("用户名:");
            userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            userLabel.setBounds(150, 150, 100, 30);
            loginFrame.add(userLabel);

            JTextField usernameField = new JTextField();
            usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            usernameField.setBounds(250, 150, 250, 35);
            loginFrame.add(usernameField);

            // 创建密码标签和输入框
            JLabel pwdLabel = new JLabel("密码:");
            pwdLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            pwdLabel.setBounds(150, 210, 100, 30);
            loginFrame.add(pwdLabel);

            JPasswordField passwordField = new JPasswordField();
            passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            passwordField.setBounds(250, 210, 250, 35);
            loginFrame.add(passwordField);

            // 创建登录按钮
            JButton loginButton = new JButton("登录");
            loginButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
            loginButton.setBounds(150, 300, 120, 40);
            loginButton.addActionListener(e -> {
                // 获取用户输入的用户名和密码
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());

                // 验证输入是否为空
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "请输入用户名和密码", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 调用UserManager进行登录验证
                UserManager userManager = UserManager.getInstance();
                if (userManager.login(username, password)) {
                    // 登录成功，关闭登录窗口，显示难度选择窗口
                    loginFrame.dispose();
                    showDifficultySelection();
                } else {
                    // 登录失败，显示错误提示
                    JOptionPane.showMessageDialog(loginFrame, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
                }
            });
            loginFrame.add(loginButton);

            // 创建注册按钮
            JButton registerButton = new JButton("注册");
            registerButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
            registerButton.setBounds(300, 300, 120, 40);
            registerButton.addActionListener(e -> {
                // 获取用户输入的用户名和密码
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());

                // 验证用户名不为空
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "用户名不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 验证密码不为空
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 验证密码长度至少8位
                if (password.length() < 8) {
                    JOptionPane.showMessageDialog(loginFrame, "密码长度至少8位", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 验证密码必须包含字母和数字
                boolean hasLetter = false;
                boolean hasDigit = false;
                for (char c : password.toCharArray()) {
                    if (Character.isLetter(c)) {
                        hasLetter = true;
                    }
                    if (Character.isDigit(c)) {
                        hasDigit = true;
                    }
                }

                if (!hasLetter || !hasDigit) {
                    JOptionPane.showMessageDialog(loginFrame, "密码必须同时包含字母和数字", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 调用UserManager进行注册
                UserManager userManager = UserManager.getInstance();
                if (userManager.register(username, password)) {
                    // 注册成功，清空密码框
                    JOptionPane.showMessageDialog(loginFrame, "注册成功！请登录", "成功", JOptionPane.INFORMATION_MESSAGE);
                    passwordField.setText("");
                } else {
                    // 注册失败（用户名已存在）
                    JOptionPane.showMessageDialog(loginFrame, "用户名已存在", "注册失败", JOptionPane.ERROR_MESSAGE);
                }
            });
            loginFrame.add(registerButton);

            // 创建游客模式按钮
            JButton guestButton = new JButton("游客模式");
            guestButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
            guestButton.setBounds(450, 300, 120, 40);
            guestButton.setBackground(new Color(200, 200, 200));
            guestButton.addActionListener(e -> {
                // 弹出确认对话框
                int confirm = JOptionPane.showConfirmDialog(
                        loginFrame,
                        "提示：游客模式不会保存游戏进度\n是否继续？",
                        "游客模式确认",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                // 如果用户点击"是"，则进入难度选择
                if (confirm == JOptionPane.YES_OPTION) {
                    // 设置游客身份
                    UserManager userManager = UserManager.getInstance();
                    userManager.loginAsGuest();
                    // 关闭登录窗口，显示难度选择窗口
                    loginFrame.dispose();
                    showDifficultySelection();
                }
                // 如果点击"否"，则不做任何操作，返回登录界面
            });
            loginFrame.add(guestButton);

            // 显示登录窗口
            loginFrame.setVisible(true);
        });
    }

    // 显示难度选择窗口
    private static void showDifficultySelection() {
        JFrame difficultyFrame = new JFrame("连连看 - 选择难度");
        difficultyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        difficultyFrame.setSize(800, 600);
        difficultyFrame.setLocationRelativeTo(null);
        difficultyFrame.setLayout(null);

        // 添加背景照片
        JLabel labelPic = new JLabel(new ImageIcon(".\\resource\\2.png"));
        labelPic.setSize(800, 600);
        labelPic.setLocation(0, 0);
        difficultyFrame.add(labelPic);

        // 创建标题标签
        JLabel titleLabel = new JLabel("选择难度", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
        titleLabel.setBounds(200, 100, 400, 50);
        difficultyFrame.add(titleLabel);

        // 创建简单模式按钮
        JButton easyButton = new JButton("简单模式");
        easyButton.setFont(new Font("微软雅黑", Font.BOLD, 20));
        easyButton.setBounds(300, 220, 200, 60);
        easyButton.addActionListener(e -> {
            // 点击简单模式，进入游戏
            GameFrame frame = new GameFrame("连连看", 1000, 1000, false);
            frame.repaint();
            difficultyFrame.dispose();
        });
        difficultyFrame.add(easyButton);

        // 创建困难模式按钮
        JButton hardButton = new JButton("困难模式");
        hardButton.setFont(new Font("微软雅黑", Font.BOLD, 20));
        hardButton.setBounds(300, 320, 200, 60);
        hardButton.addActionListener(e -> {
            // 点击困难模式，进入游戏
            GameFrame frame = new GameFrame("连连看", 1000, 1000, true);
            frame.repaint();
            difficultyFrame.dispose();
        });
        difficultyFrame.add(hardButton);

        // 显示难度选择窗口
        difficultyFrame.setVisible(true);
    }
}