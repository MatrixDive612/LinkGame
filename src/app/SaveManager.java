package app;

import app.GameData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// 存档管理类，负责游戏存档的保存和读取
public class SaveManager {
    private static final String SAVE_DIR = "data/saves/"; // 存档目录路径
    
    // 保存游戏存档
    public static boolean saveGame(GameData gameData) {
        // 验证存档数据有效
        if (gameData == null || gameData.getUsername() == null) {
            return false;
        }
        
        try {
            File dir = new File(SAVE_DIR);
            dir.mkdirs(); // 创建存档目录（如果不存在）
            
            // 生成存档文件名：用户名_save.dat
            String fileName = SAVE_DIR + gameData.getUsername() + "_save.dat";
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(gameData); // 序列化GameData到文件
            oos.close();
            fos.close();
            
            return true;
        } catch (Exception e) {
            System.err.println("保存游戏失败: " + e.getMessage());
            return false;
        }
    }
    
    // 读取游戏存档
    public static GameData loadGame(String username) {
        // 验证用户名有效
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        
        try {
            String fileName = SAVE_DIR + username + "_save.dat";
            File file = new File(fileName);
            
            // 检查存档文件是否存在
            if (!file.exists()) {
                return null;
            }
            
            // 读取并反序列化存档文件
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            GameData gameData = (GameData) ois.readObject();
            ois.close();
            fis.close();
            
            // 验证存档属于当前用户（防止读取他人存档）
            if (!gameData.getUsername().equals(username)) {
                System.err.println("存档用户名不匹配");
                return null;
            }
            
            return gameData;
        } catch (ClassNotFoundException e) {
            // 捕获类未找到异常（存档格式错误）
            System.err.println("存档格式错误");
            return null;
        } catch (Exception e) {
            // 捕获其他异常（文件损坏等）
            System.err.println("读取存档失败: " + e.getMessage());
            return null;
        }
    }
    
    // 检查用户是否有存档
    public static boolean hasSave(String username) {
        if (username == null) return false;
        String fileName = SAVE_DIR + username + "_save.dat";
        return new File(fileName).exists();
    }
    
    // 删除用户存档
    public static boolean deleteSave(String username) {
        if (username == null) return false;
        String fileName = SAVE_DIR + username + "_save.dat";
        File file = new File(fileName);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
