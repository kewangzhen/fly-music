package com.example.flymusic.service.impl;

import com.example.flymusic.entity.Artist;
import com.example.flymusic.entity.Song;
import com.example.flymusic.repository.ArtistRepository;
import com.example.flymusic.repository.SongRepository;
import com.example.flymusic.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 艺术家服务实现类
 * 实现艺术家的增删改查、歌曲管理等功能
 */
@Service
public class ArtistServiceImpl implements ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SongRepository songRepository;

    /**
     * 获取所有艺术家
     * @return 艺术家列表
     */
    @Override
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    /**
     * 根据ID获取艺术家
     * @param id 艺术家ID
     * @return 艺术家对象
     */
    @Override
    public Optional<Artist> getArtistById(Long id) {
        return artistRepository.findById(id);
    }

    /**
     * 保存艺术家
     * @param artist 艺术家对象
     * @return 保存后的艺术家
     */
    @Override
    public Artist saveArtist(Artist artist) {
        return artistRepository.save(artist);
    }

    /**
     * 删除艺术家
     * @param id 艺术家ID
     */
    @Override
    public void deleteArtist(Long id) {
        artistRepository.deleteById(id);
    }

    /**
     * 根据名称搜索艺术家
     * @param name 搜索关键词
     * @return 艺术家列表
     */
    @Override
    public List<Artist> searchArtists(String name) {
        return artistRepository.findByNameContaining(name);
    }

    /**
     * 获取艺术家的歌曲
     * @param artistId 艺术家ID
     * @return 歌曲列表
     */
    @Override
    public List<Song> getArtistSongs(Long artistId) {
        return songRepository.findByArtistId(artistId);
    }
}