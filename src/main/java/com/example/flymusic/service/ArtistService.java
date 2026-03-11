package com.example.flymusic.service;

import com.example.flymusic.entity.Artist;
import com.example.flymusic.entity.Song;
import java.util.List;
import java.util.Optional;

/**
 * 艺术家服务接口
 * 提供艺术家的增删改查、歌曲管理等功能
 */
public interface ArtistService {
    /**
     * 获取所有艺术家
     * @return 艺术家列表
     */
    List<Artist> getAllArtists();
    
    /**
     * 根据ID获取艺术家
     * @param id 艺术家ID
     * @return 艺术家对象
     */
    Optional<Artist> getArtistById(Long id);
    
    /**
     * 保存艺术家
     * @param artist 艺术家对象
     * @return 保存后的艺术家
     */
    Artist saveArtist(Artist artist);
    
    /**
     * 删除艺术家
     * @param id 艺术家ID
     */
    void deleteArtist(Long id);
    
    /**
     * 根据名称搜索艺术家
     * @param name 搜索关键词
     * @return 艺术家列表
     */
    List<Artist> searchArtists(String name);
    
    /**
     * 获取艺术家的歌曲
     * @param artistId 艺术家ID
     * @return 歌曲列表
     */
    List<Song> getArtistSongs(Long artistId);
}