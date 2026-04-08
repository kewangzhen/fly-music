package com.example.flymusic.service;

import com.example.flymusic.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 歌曲Service接口
 * 定义歌曲相关的业务方法
 */
public interface SongService {
    
    /**
     * 获取所有歌曲
     */
    List<Song> getAllSongs();
    
    /**
     * 根据ID获取歌曲
     */
    Optional<Song> getSongById(Long id);
    
    /**
     * 保存歌曲
     */
    Song saveSong(Song song);
    
    /**
     * 更新歌曲
     */
    Song updateSong(Long id, Song song);
    
    /**
     * 删除歌曲
     */
    void deleteSong(Long id);
    
    /**
     * 根据分类ID获取歌曲
     */
    List<Song> getSongsByCategoryId(Long categoryId, int limit);
    
    /**
     * 根据艺术家ID获取歌曲
     */
    List<Song> getSongsByArtistId(Long artistId);
    
    /**
     * 根据专辑ID获取歌曲
     */
    List<Song> getSongsByAlbumId(Long albumId);
    
    /**
     * 根据用户ID获取原创歌曲
     */
    List<Song> getOriginalSongsByUserId(Long userId);
    
    /**
     * 搜索歌曲
     */
    List<Song> searchSongs(String keyword);
    
    /**
     * 获取热门歌曲
     */
    List<Song> getPopularSongs(int limit);
    
    /**
     * 获取最新歌曲
     */
    List<Song> getLatestSongs(int limit);
    
    /**
     * 获取推荐歌曲
     */
    List<Song> getRecommendedSongs(Long userId, int limit);
    
    /**
     * 增加播放次数
     */
    void incrementPlayCount(Long songId);
    
    /**
     * 审核歌曲
     */
    void approveSong(Long songId);
    
    /**
     * 下架歌曲
     */
    void rejectSong(Long songId);
    
    /**
     * 分页获取歌曲
     */
    Page<Song> getSongsByPage(Integer status, Pageable pageable);
    
    /**
     * 根据分类ID分页获取歌曲
     */
    Page<Song> getSongsByCategoryIdPage(Long categoryId, Pageable pageable);
    
    /**
     * 统计歌曲总数
     */
    long countSongs(Integer status);
    
    /**
     * 从MP3文件提取并更新歌曲元数据
     */
    Song extractAndUpdateMetadata(Long songId);
}