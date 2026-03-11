package com.example.flymusic.service;

import com.example.flymusic.entity.Category;
import com.example.flymusic.entity.Song;
import java.util.List;
import java.util.Optional;

/**
 * 分类服务接口
 * 提供分类的增删改查、歌曲管理等功能
 */
public interface CategoryService {
    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> getAllCategories();
    
    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类对象
     */
    Optional<Category> getCategoryById(Long id);
    
    /**
     * 保存分类
     * @param category 分类对象
     * @return 保存后的分类
     */
    Category saveCategory(Category category);
    
    /**
     * 删除分类
     * @param id 分类ID
     */
    void deleteCategory(Long id);
    
    /**
     * 搜索分类
     * @param name 搜索关键词
     * @return 分类列表
     */
    List<Category> searchCategories(String name);
    
    /**
     * 获取分类下的歌曲
     * @param categoryId 分类ID
     * @return 歌曲列表
     */
    List<Song> getCategorySongs(Long categoryId);
}