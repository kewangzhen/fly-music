package com.example.flymusic.service;

import com.example.flymusic.entity.Album;
import com.example.flymusic.entity.Song;
import java.util.List;
import java.util.Optional;

/**
 * 专辑服务接口
 * 提供专辑的增删改查、歌曲管理等功能
 */
public interface AlbumService {
    /**
     * 获取所有专辑
     * @return 专辑列表
     */
    List<Album> getAllAlbums();
    
    /**
     * 根据ID获取专辑
     * @param id 专辑ID
     * @return 专辑对象
     */
    Optional<Album> getAlbumById(Long id);
    
    /**
     * 保存专辑
     * @param album 专辑对象
     * @return 保存后的专辑
     */
    Album saveAlbum(Album album);
    
    /**
     * 删除专辑
     * @param id 专辑ID
     */
    void deleteAlbum(Long id);
    
    /**
     * 根据艺术家ID获取专辑
     * @param artistId 艺术家ID
     * @return 专辑列表
     */
    List<Album> getAlbumsByArtistId(Long artistId);
    
    /**
     * 搜索专辑
     * @param keyword 搜索关键词
     * @return 专辑列表
     */
    List<Album> searchAlbums(String keyword);
    
    /**
     * 获取专辑中的歌曲
     * @param albumId 专辑ID
     * @return 歌曲列表
     */
    List<Song> getAlbumSongs(Long albumId);
}