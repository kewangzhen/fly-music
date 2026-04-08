package com.example.flymusic.service.impl;

import com.example.flymusic.entity.Category;
import com.example.flymusic.entity.Song;
import com.example.flymusic.repository.CategoryRepository;
import com.example.flymusic.repository.SongRepository;
import com.example.flymusic.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 歌曲Service实现类
 * 实现歌曲相关的业务逻辑
 */
@Service
public class SongServiceImpl implements SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * 获取所有歌曲
     */
    @Override
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    /**
     * 根据ID获取歌曲
     */
    @Override
    public Optional<Song> getSongById(Long id) {
        return songRepository.findById(id);
    }

    /**
     * 保存歌曲
     */
    @Override
    @Transactional
    public Song saveSong(Song song) {
        if (song.getCreatedAt() == null) {
            song.setCreatedAt(new Date());
        }
        song.setUpdatedAt(new Date());
        return songRepository.save(song);
    }

    /**
     * 更新歌曲
     */
    @Override
    @Transactional
    public Song updateSong(Long id, Song song) {
        Optional<Song> existingSongOpt = songRepository.findById(id);
        if (!existingSongOpt.isPresent()) {
            throw new RuntimeException("歌曲不存在");
        }
        
        Song existingSong = existingSongOpt.get();
        
        if (song.getTitle() != null) {
            existingSong.setTitle(song.getTitle());
        }
        if (song.getUrl() != null) {
            existingSong.setUrl(song.getUrl());
        }
        if (song.getCover() != null) {
            existingSong.setCover(song.getCover());
        }
        if (song.getLyrics() != null) {
            existingSong.setLyrics(song.getLyrics());
        }
        if (song.getDuration() != null) {
            existingSong.setDuration(song.getDuration());
        }
        if (song.getAlbum() != null) {
            existingSong.setAlbum(song.getAlbum());
        }
        if (song.getCategory() != null) {
            existingSong.setCategory(song.getCategory());
        }
        
        if (song.getCategoryId() != null) {
            Category category = categoryRepository.findById(song.getCategoryId()).orElse(null);
            existingSong.setCategory(category);
        }
        
        if (song.getArtists() != null) {
            existingSong.setArtists(song.getArtists());
        }
        
        existingSong.setUpdatedAt(new Date());
        return songRepository.save(existingSong);
    }

    /**
     * 删除歌曲
     */
    @Override
    @Transactional
    public void deleteSong(Long id) {
        songRepository.deleteById(id);
    }

    /**
     * 根据分类ID获取歌曲
     */
    @Override
    public List<Song> getSongsByCategoryId(Long categoryId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return songRepository.findByCategoryIdOrderByPlayCountDesc(categoryId, pageable);
    }

    /**
     * 根据艺术家ID获取歌曲
     */
    @Override
    public List<Song> getSongsByArtistId(Long artistId) {
        return songRepository.findByArtistId(artistId);
    }

    /**
     * 根据专辑ID获取歌曲
     */
    @Override
    public List<Song> getSongsByAlbumId(Long albumId) {
        return songRepository.findByAlbumId(albumId);
    }

    /**
     * 根据用户ID获取原创歌曲
     */
    @Override
    public List<Song> getOriginalSongsByUserId(Long userId) {
        return songRepository.findByUserId(userId);
    }

    /**
     * 搜索歌曲
     */
    @Override
    public List<Song> searchSongs(String keyword) {
        return songRepository.searchSongs(keyword);
    }

    /**
     * 获取热门歌曲
     */
    @Override
    public List<Song> getPopularSongs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return songRepository.findTopByStatusOrderByPlayCountDesc(1, pageable);
    }

    /**
     * 获取最新歌曲
     */
    @Override
    public List<Song> getLatestSongs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return songRepository.findTopByStatusOrderByCreatedAtDesc(1, pageable);
    }

    /**
     * 获取推荐歌曲
     */
    @Override
    public List<Song> getRecommendedSongs(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return songRepository.findRecommendedSongs(userId, pageable);
    }

    /**
     * 增加播放次数
     */
    @Override
    @Transactional
    public void incrementPlayCount(Long songId) {
        Optional<Song> songOpt = songRepository.findById(songId);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            song.setPlayCount(song.getPlayCount() != null ? song.getPlayCount() + 1 : 1);
            songRepository.save(song);
        }
    }

    /**
     * 审核歌曲
     */
    @Override
    @Transactional
    public void approveSong(Long songId) {
        Optional<Song> songOpt = songRepository.findById(songId);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            song.setStatus(1);
            song.setUpdatedAt(new Date());
            songRepository.save(song);
        }
    }

    /**
     * 下架歌曲
     */
    @Override
    @Transactional
    public void rejectSong(Long songId) {
        Optional<Song> songOpt = songRepository.findById(songId);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            song.setStatus(2);
            song.setUpdatedAt(new Date());
            songRepository.save(song);
        }
    }

    /**
     * 分页获取歌曲
     */
    @Override
    public Page<Song> getSongsByPage(Integer status, Pageable pageable) {
        return songRepository.findByStatus(status, pageable);
    }

    /**
     * 根据分类ID分页获取歌曲
     */
    @Override
    public Page<Song> getSongsByCategoryIdPage(Long categoryId, Pageable pageable) {
        return songRepository.findByCategoryId(categoryId, pageable);
    }
    
    /**
     * 统计歌曲总数
     */
    @Override
    public long countSongs(Integer status) {
        return songRepository.countByStatus(status);
    }
}