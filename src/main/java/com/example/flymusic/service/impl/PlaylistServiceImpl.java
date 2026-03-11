package com.example.flymusic.service.impl;

import com.example.flymusic.entity.Playlist;
import com.example.flymusic.entity.Song;
import com.example.flymusic.entity.User;
import com.example.flymusic.repository.PlaylistRepository;
import com.example.flymusic.repository.SongRepository;
import com.example.flymusic.repository.UserRepository;
import com.example.flymusic.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * 播放列表Service实现类
 * 实现播放列表相关的业务逻辑
 */
@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;
    
    @Autowired
    private SongRepository songRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * 获取所有播放列表
     */
    @Override
    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    /**
     * 根据ID获取播放列表
     */
    @Override
    public Playlist getPlaylistById(Long id) {
        return playlistRepository.findById(id).orElse(null);
    }

    /**
     * 创建播放列表
     */
    @Override
    @Transactional
    public Playlist createPlaylist(Playlist playlist) {
        if (playlist.getUser() != null && playlist.getUser().getId() != null) {
            Optional<User> userOpt = userRepository.findById(playlist.getUser().getId());
            if (userOpt.isPresent()) {
                playlist.setUser(userOpt.get());
            }
        }
        
        if (playlist.getSongs() == null) {
            playlist.setSongs(new HashSet<>());
        }
        playlist.setSongCount(0);
        playlist.setPlayCount(0L);
        playlist.setStatus(1);
        playlist.setCreatedAt(new Date());
        playlist.setUpdatedAt(new Date());
        
        return playlistRepository.save(playlist);
    }

    /**
     * 更新播放列表
     */
    @Override
    @Transactional
    public Playlist updatePlaylist(Long id, Playlist playlist) {
        Optional<Playlist> existingPlaylistOpt = playlistRepository.findById(id);
        if (!existingPlaylistOpt.isPresent()) {
            return null;
        }
        
        Playlist existingPlaylist = existingPlaylistOpt.get();
        
        if (playlist.getName() != null) {
            existingPlaylist.setName(playlist.getName());
        }
        if (playlist.getDescription() != null) {
            existingPlaylist.setDescription(playlist.getDescription());
        }
        if (playlist.getCover() != null) {
            existingPlaylist.setCover(playlist.getCover());
        }
        if (playlist.getIsPublic() != null) {
            existingPlaylist.setIsPublic(playlist.getIsPublic());
        }
        if (playlist.getType() != null) {
            existingPlaylist.setType(playlist.getType());
        }
        
        existingPlaylist.setUpdatedAt(new Date());
        return playlistRepository.save(existingPlaylist);
    }

    /**
     * 删除播放列表
     */
    @Override
    @Transactional
    public void deletePlaylist(Long id) {
        playlistRepository.deleteById(id);
    }

    /**
     * 根据用户ID获取播放列表
     */
    @Override
    public List<Playlist> getPlaylistsByUserId(Long userId) {
        return playlistRepository.findByUserId(userId);
    }

    /**
     * 向播放列表添加歌曲
     */
    @Override
    @Transactional
    public void addSongToPlaylist(Long playlistId, Long songId) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        Optional<Song> songOpt = songRepository.findById(songId);
        
        if (playlistOpt.isPresent() && songOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            Song song = songOpt.get();
            
            if (playlist.getSongs() == null) {
                playlist.setSongs(new HashSet<>());
            }
            
            playlist.getSongs().add(song);
            playlist.setSongCount(playlist.getSongs().size());
            playlist.setUpdatedAt(new Date());
            playlistRepository.save(playlist);
        }
    }

    /**
     * 从播放列表移除歌曲
     */
    @Override
    @Transactional
    public void removeSongFromPlaylist(Long playlistId, Long songId) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        
        if (playlistOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            if (playlist.getSongs() != null) {
                playlist.getSongs().removeIf(song -> song.getId().equals(songId));
                playlist.setSongCount(playlist.getSongs().size());
                playlist.setUpdatedAt(new Date());
                playlistRepository.save(playlist);
            }
        }
    }

    /**
     * 获取播放列表中的歌曲
     */
    @Override
    public List<Song> getSongsInPlaylist(Long playlistId) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        if (playlistOpt.isPresent() && playlistOpt.get().getSongs() != null) {
            return new ArrayList<>(playlistOpt.get().getSongs());
        }
        return new ArrayList<>();
    }

    /**
     * 搜索播放列表
     */
    @Override
    public List<Playlist> searchPlaylists(String keyword) {
        return playlistRepository.findByNameContaining(keyword);
    }

    /**
     * 获取热门播放列表
     */
    @Override
    public List<Playlist> getPopularPlaylists(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return playlistRepository.findPopularPlaylists(pageable);
    }

    /**
     * 获取心动歌单
     */
    @Override
    public List<Playlist> getHeartPlaylists(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return playlistRepository.findHeartPlaylists(pageable);
    }

    /**
     * 获取雷达榜单
     */
    @Override
    public List<Playlist> getRadarPlaylists(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return playlistRepository.findRadarPlaylists(pageable);
    }

    /**
     * 增加播放次数
     */
    @Override
    @Transactional
    public void incrementPlayCount(Long playlistId) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        if (playlistOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            playlist.setPlayCount(playlist.getPlayCount() != null ? playlist.getPlayCount() + 1 : 1);
            playlistRepository.save(playlist);
        }
    }

    /**
     * 分页获取播放列表
     */
    @Override
    public Page<Playlist> getPlaylistsByPage(Integer status, Pageable pageable) {
        return playlistRepository.findByStatus(status, pageable);
    }
}