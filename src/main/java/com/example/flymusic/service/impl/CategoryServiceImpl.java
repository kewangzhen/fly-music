package com.example.flymusic.service.impl;

import com.example.flymusic.entity.Category;
import com.example.flymusic.entity.Song;
import com.example.flymusic.repository.CategoryRepository;
import com.example.flymusic.repository.SongRepository;
import com.example.flymusic.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 分类服务实现类
 * 实现分类的增删改查、歌曲管理等功能
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SongRepository songRepository;

    /**
     * 获取所有分类
     * @return 分类列表
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类对象
     */
    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * 保存分类
     * @param category 分类对象
     * @return 保存后的分类
     */
    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * 删除分类
     * @param id 分类ID
     */
    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    /**
     * 搜索分类
     * @param name 搜索关键词
     * @return 分类列表
     */
    @Override
    public List<Category> searchCategories(String name) {
        return categoryRepository.findByNameContaining(name);
    }

    /**
     * 获取分类下的歌曲
     * @param categoryId 分类ID
     * @return 歌曲列表
     */
    @Override
    public List<Song> getCategorySongs(Long categoryId) {
        return songRepository.findByCategoryId(categoryId);
    }
}