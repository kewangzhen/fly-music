package com.example.flymusic.service;

import com.example.flymusic.entity.dto.CategoryRecommendationDTO;
import java.util.List;

/**
 * 分类推荐服务接口
 * 提供基于音乐分类的歌曲推荐功能
 * 
 * 功能说明：
 * - 根据分类ID获取推荐歌曲
 * - 根据标签名称获取推荐歌曲
 * 
 * 算法说明：
 * - 查询指定分类下的所有歌曲
 * - 按播放量降序排列
 * - 返回指定数量的歌曲
 * 
 * @author fly-music
 * @since 2026-03-12
 */
public interface CategoryRecommendationService {
    
    /**
     * 根据分类ID获取推荐歌曲
     * 
     * @param categoryId 分类ID
     * @param limit 返回数量限制
     * @return 分类下的歌曲列表，按播放量降序
     */
    List<CategoryRecommendationDTO> getCategoryRecommendations(Long categoryId, int limit);
    
    /**
     * 根据标签名称获取推荐歌曲
     * 
     * @param tag 标签名称（如：流行、摇滚、民谣等）
     * @param limit 返回数量限制
     * @return 分类下的歌曲列表，按播放量降序
     */
    List<CategoryRecommendationDTO> getCategoryRecommendationsByTag(String tag, int limit);
}
