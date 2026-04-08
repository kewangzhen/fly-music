package com.example.flymusic.service;

import com.example.flymusic.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllCategories();
    List<Category> getActiveCategories();
    Optional<Category> getCategoryById(Long id);
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
    List<Category> searchCategories(String keyword);
    List<Category> getCategorySongs(Long categoryId);
    void addSongToCategory(Long categoryId, Long songId);
    void removeSongFromCategory(Long categoryId, Long songId);
}
