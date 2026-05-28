package app;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// 存档管理类，负责游戏存档的保存和读取
// 支持3个独立存档槽位
public class SaveManager {
    private static final String SAVE_DIR = "data/saves/"; // 存档目录路径
    private static final int MAX_SAVE_SLOTS = 3; // 最大存档槽位数
    
    // 保存游戏存档到指定槽位
    public static boolean saveGame(GameData gameData, int slot) {
        // 验证存档数据有效
        if (gameData == null || gameData.getUsername() == null) {
            return false;
        }
        
        // 验证槽位编号有效（1-3）
        if (slot < 1 || slot > MAX_SAVE_SLOTS) {
            System.err.println("无效的存档槽位: " + slot);
            return false;
        }
        
        try {
            File dir = new File(SAVE_DIR);
            dir.mkdirs(); // 创建存档目录（如果不存在）
            
            // 生成存档文件名：用户名_save_槽位.dat
            String fileName = SAVE_DIR + gameData.getUsername() + "_save_" + slot + ".dat";
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
    
    // 从指定槽位读取游戏存档
    public static GameData loadGame(String username, int slot) {
        // 验证用户名有效
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        
        // 验证槽位编号有效
        if (slot < 1 || slot > MAX_SAVE_SLOTS) {
            System.err.println("无效的存档槽位: " + slot);
            return null;
        }
        
        try {
            String fileName = SAVE_DIR + username + "_save_" + slot + ".dat";
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
    
    // 检查指定槽位是否有存档
    public static boolean hasSave(String username, int slot) {
        if (username == null) return false;
        if (slot < 1 || slot > MAX_SAVE_SLOTS) return false;
        
        String fileName = SAVE_DIR + username + "_save_" + slot + ".dat";
        return new File(fileName).exists();
    }
    
    // 删除指定槽位的存档
    public static boolean deleteSave(String username, int slot) {
        if (username == null) return false;
        if (slot < 1 || slot > MAX_SAVE_SLOTS) return false;
        
        String fileName = SAVE_DIR + username + "_save_" + slot + ".dat";
        File file = new File(fileName);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
    
    // 获取所有可用的存档槽位信息
    public static List<String> getSaveInfo(String username) {
        List<String> infoList = new ArrayList<>();
        
        for (int i = 1; i <= MAX_SAVE_SLOTS; i++) {
            if (hasSave(username, i)) {
                GameData data = loadGame(username, i);
                if (data != null) {
                    String timeStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new java.util.Date(data.getTimestamp()));
                    infoList.add("存档" + i + ": " + timeStr + " 分数:" + data.getScore() + " 难度:" + data.getDifficulty());
                } else {
                    infoList.add("存档" + i + ": [损坏]");
                }
            } else {
                infoList.add("存档" + i + ": [空]");
            }
        }
        
        return infoList;
    }
    
    // 获取最大存档槽位数
    public static int getMaxSaveSlots() {
        return MAX_SAVE_SLOTS;
    }
}
