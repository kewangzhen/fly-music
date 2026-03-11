package com.example.flymusic.service;

import com.example.flymusic.entity.Playlist;
import com.example.flymusic.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 播放列表Service接口
 * 定义播放列表相关的业务方法
 */
public interface PlaylistService {
    
    /**
     * 获取所有播放列表
     */
    List<Playlist> getAllPlaylists();
    
    /**
     * 根据ID获取播放列表
     */
    Playlist getPlaylistById(Long id);
    
    /**
     * 创建播放列表
     */
    Playlist createPlaylist(Playlist playlist);
    
    /**
     * 更新播放列表
     */
    Playlist updatePlaylist(Long id, Playlist playlist);
    
    /**
     * 删除播放列表
     */
    void deletePlaylist(Long id);
    
    /**
     * 根据用户ID获取播放列表
     */
    List<Playlist> getPlaylistsByUserId(Long userId);
    
    /**
     * 向播放列表添加歌曲
     */
    void addSongToPlaylist(Long playlistId, Long songId);
    
    /**
     * 从播放列表移除歌曲
     */
    void removeSongFromPlaylist(Long playlistId, Long songId);
    
    /**
     * 获取播放列表中的歌曲
     */
    List<Song> getSongsInPlaylist(Long playlistId);
    
    /**
     * 搜索播放列表
     */
    List<Playlist> searchPlaylists(String keyword);
    
    /**
     * 获取热门播放列表
     */
    List<Playlist> getPopularPlaylists(int limit);
    
    /**
     * 获取心动歌单
     */
    List<Playlist> getHeartPlaylists(int limit);
    
    /**
     * 获取雷达榜单
     */
    List<Playlist> getRadarPlaylists(int limit);
    
    /**
     * 增加播放次数
     */
    void incrementPlayCount(Long playlistId);
    
    /**
     * 分页获取播放列表
     */
    Page<Playlist> getPlaylistsByPage(Integer status, Pageable pageable);
}