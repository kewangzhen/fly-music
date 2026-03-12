package com.example.flymusic.service.impl;

import com.example.flymusic.entity.Artist;
import com.example.flymusic.entity.Category;
import com.example.flymusic.entity.Song;
import com.example.flymusic.entity.dto.CategoryRecommendationDTO;
import com.example.flymusic.repository.CategoryRepository;
import com.example.flymusic.repository.SongRepository;
import com.example.flymusic.service.CategoryRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类推荐服务实现类
 * 实现基于音乐分类的歌曲推荐功能
 * 
 * 实现逻辑：
 * 1. 根据分类ID查询歌曲列表
 * 2. 按播放量降序排列
 * 3. 转换为DTO返回
 * 
 * @author fly-music
 * @since 2026-03-12
 */
@Service
public class CategoryRecommendationServiceImpl implements CategoryRecommendationService {
    
    @Autowired
    private SongRepository songRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * 根据分类ID获取推荐歌曲
     * 
     * 实现步骤：
     * 1. 调用Repository查询指定分类的歌曲
     * 2. 按播放量降序排列
     * 3. 转换为DTO对象返回
     * 
     * @param categoryId 分类ID
     * @param limit 返回数量限制
     * @return 分类下的歌曲列表
     */
    @Override
    public List<CategoryRecommendationDTO> getCategoryRecommendations(Long categoryId, int limit) {
        // 查询指定分类的歌曲，按播放量降序排列
        List<Song> songs = songRepository.findByCategoryIdOrderByPlayCountDesc(
            categoryId, PageRequest.of(0, limit));
        
        // 转换为DTO并返回
        return songs.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 根据标签名称获取推荐歌曲
     * 
     * 实现步骤：
     * 1. 根据标签名称查询分类
     * 2. 获取分类ID
     * 3. 调用getCategoryRecommendations方法获取歌曲
     * 
     * @param tag 标签名称
     * @param limit 返回数量限制
     * @return 分类下的歌曲列表
     */
    @Override
    public List<CategoryRecommendationDTO> getCategoryRecommendationsByTag(String tag, int limit) {
        // 根据标签名称查询分类
        Category category = categoryRepository.findByName(tag);
        
        // 如果分类不存在，返回空列表
        if (category == null) {
            return List.of();
        }
        
        // 根据分类ID获取推荐歌曲
        return getCategoryRecommendations(category.getId(), limit);
    }
    
    /**
     * 将Song实体转换为DTO
     * 
     * @param song 歌曲实体
     * @return 分类推荐DTO
     */
    private CategoryRecommendationDTO convertToDTO(Song song) {
        // 获取艺术家名称
        String artistName = getArtistNames(song);
        
        // 获取分类名称
        String categoryName = song.getCategory() != null ? song.getCategory().getName() : "未知分类";
        
        // 构建DTO对象
        return new CategoryRecommendationDTO(
            song.getId(),
            song.getTitle(),
            song.getCover(),
            artistName,
            song.getDuration(),
            categoryName,
            song.getPlayCount() != null ? song.getPlayCount() : 0L
        );
    }
    
    /**
     * 获取歌曲的艺术家名称
     * 如果有多个艺术家，用逗号连接
     * 
     * @param song 歌曲实体
     * @return 艺术家名称
     */
    private String getArtistNames(Song song) {
        if (song.getArtists() != null && !song.getArtists().isEmpty()) {
            return song.getArtists().stream()
                .map(Artist::getName)
                .collect(Collectors.joining(", "));
        }
        return "未知艺术家";
    }
}
