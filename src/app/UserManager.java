package app;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// 用户管理类，负责用户的注册、登录、数据持久化
// 使用单例模式，确保全局只有一个用户管理器实例
public class UserManager {
    private static final String USER_DATA_FILE = "data/users.dat"; // 用户数据文件路径
    private static UserManager instance; // 单例实例
    
    private Map<String, User> users; // 存储所有用户的Map，键为用户名
    private User currentUser; // 当前登录的用户
    
    // 私有构造函数，防止外部直接创建实例
    private UserManager() {
        users = new HashMap<>();
        loadUsers(); // 初始化时从文件加载用户数据
    }
    
    // 获取单例实例（线程安全）
    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    // 从本地文件加载用户数据
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        try {
            File file = new File(USER_DATA_FILE);
            if (file.exists()) {
                // 读取序列化文件
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                users = (Map<String, User>) ois.readObject(); // 反序列化用户Map
                ois.close();
                fis.close();
            }
        } catch (Exception e) {
            System.err.println("加载用户数据失败: " + e.getMessage());
            users = new HashMap<>(); // 加载失败则创建空Map
        }
    }
    
    // 保存用户数据到本地文件
    public void saveUsers() {
        try {
            File file = new File(USER_DATA_FILE);
            file.getParentFile().mkdirs(); // 创建父目录（如果不存在）
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(users); // 序列化用户Map到文件
            oos.close();
            fos.close();
        } catch (Exception e) {
            System.err.println("保存用户数据失败: " + e.getMessage());
        }
    }
    
    // 注册用户
    public boolean register(String username, String password) {
        // 验证用户名不为空
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        // 检查用户名是否已存在
        if (users.containsKey(username)) {
            return false;
        }
        
        // 创建新用户并保存
        User newUser = new User(username, password);
        users.put(username, newUser);
        saveUsers(); // 持久化到文件
        return true;
    }
    
    // 用户登录
    public boolean login(String username, String password) {
        User user = users.get(username); // 从Map中查找用户
        // 验证用户存在、不是游客、密码正确
        if (user != null && !user.isGuest() && user.getPassword().equals(password)) {
            currentUser = user; // 设置为当前用户
            return true;
        }
        return false;
    }
    
    // 以游客身份登录
    public void loginAsGuest() {
        currentUser = new User(); // 创建游客用户
    }
    
    // 登出当前用户
    public void logout() {
        currentUser = null;
    }
    
    // 获取当前登录用户
    public User getCurrentUser() {
        return currentUser;
    }
    
    // 检查是否有用户登录
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    // 检查当前用户是否为注册用户（非游客）
    public boolean isRegisteredUser() {
        return currentUser != null && !currentUser.isGuest();
    }
    
    // 更新当前用户的统计数据
    public void updateCurrentUserStats(int score, boolean won, int playTime) {
        if (currentUser != null && !currentUser.isGuest()) {
            currentUser.updateStats(score, won, playTime);
            saveUsers(); // 更新后立即保存到文件
        }
    }
    
    // 获取所有注册用户（用于排行榜）
    public java.util.List<User> getAllUsers() {
        java.util.List<User> userList = new java.util.ArrayList<>();
        for (User user : users.values()) {
            if (!user.isGuest()) {
                userList.add(user);
            }
        }
        return userList;
    }
}
