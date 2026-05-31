package ui;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

// 背景音乐管理器
public class MusicManager {
    private static MusicManager instance;
    private Clip currentClip;
    private String currentMusic;
    
    private MusicManager() {
        this.currentMusic = null;
    }
    
    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }
    
    // 播放背景音乐
    public void playMusic(String musicName) {
        // 如果正在播放相同的音乐，不重复播放
        if (currentMusic != null && currentMusic.equals(musicName)) {
            return;
        }
        
        // 停止当前音乐
        stopMusic();
        
        try {
            // 加载音乐文件（尝试多种格式）
            File musicFile = new File("resource/music/" + musicName + ".wav");
            
            // 如果WAV不存在，尝试MP3
            if (!musicFile.exists()) {
                musicFile = new File("resource/music/" + musicName + ".mp3");
            }
            
            if (!musicFile.exists()) {
                System.err.println("音乐文件不存在: resource/music/" + musicName);
                return;
            }
            
            // 创建音频输入流
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            
            // 获取音频格式
            AudioFormat format = audioStream.getFormat();
            
            // 如果不是PCM格式，转换为PCM
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED && 
                format.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED) {
                AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(),
                    16,
                    format.getChannels(),
                    format.getChannels() * 2,
                    format.getSampleRate(),
                    false
                );
                audioStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);
            }
            
            // 创建数据行信息
            DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());
            
            // 创建Clip对象
            currentClip = (Clip) AudioSystem.getLine(info);
            
            // 打开音频流
            currentClip.open(audioStream);
            
            // 循环播放
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            
            // 开始播放
            currentClip.start();
            
            currentMusic = musicName;
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("不支持的音频格式: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("读取音频文件失败: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("音频设备不可用: " + e.getMessage());
        }
    }
    
    // 停止音乐
    public void stopMusic() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
            currentMusic = null;
        }
    }
    
    // 暂停音乐
    public void pauseMusic() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
        }
    }
    
    // 恢复音乐
    public void resumeMusic() {
        if (currentClip != null && !currentClip.isRunning()) {
            currentClip.start();
        }
    }
    
    // 获取当前音乐
    public String getCurrentMusic() {
        return currentMusic;
    }
}
