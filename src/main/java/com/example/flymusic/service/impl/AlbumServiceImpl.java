package com.example.flymusic.service.impl;

import com.example.flymusic.entity.Album;
import com.example.flymusic.entity.Song;
import com.example.flymusic.repository.AlbumRepository;
import com.example.flymusic.repository.SongRepository;
import com.example.flymusic.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 专辑服务实现类
 * 实现专辑的增删改查、歌曲管理等功能
 */
@Service
public class AlbumServiceImpl implements AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongRepository songRepository;

    /**
     * 获取所有专辑
     * @return 专辑列表
     */
    @Override
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    /**
     * 根据ID获取专辑
     * @param id 专辑ID
     * @return 专辑对象
     */
    @Override
    public Optional<Album> getAlbumById(Long id) {
        return albumRepository.findById(id);
    }

    /**
     * 保存专辑
     * @param album 专辑对象
     * @return 保存后的专辑
     */
    @Override
    public Album saveAlbum(Album album) {
        return albumRepository.save(album);
    }

    /**
     * 删除专辑
     * @param id 专辑ID
     */
    @Override
    public void deleteAlbum(Long id) {
        albumRepository.deleteById(id);
    }

    /**
     * 根据艺术家ID获取专辑
     * @param artistId 艺术家ID
     * @return 专辑列表
     */
    @Override
    public List<Album> getAlbumsByArtistId(Long artistId) {
        return albumRepository.findByArtistId(artistId);
    }

    /**
     * 搜索专辑
     * @param keyword 搜索关键词
     * @return 专辑列表
     */
    @Override
    public List<Album> searchAlbums(String keyword) {
        return albumRepository.findByTitleContaining(keyword);
    }

    /**
     * 获取专辑中的歌曲
     * @param albumId 专辑ID
     * @return 歌曲列表
     */
    @Override
    public List<Song> getAlbumSongs(Long albumId) {
        return songRepository.findByAlbumId(albumId);
    }
}