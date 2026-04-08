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

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SongRepository songRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getActiveCategories() {
        return categoryRepository.findByStatus(1);
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category createCategory(Category category) {
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getLevel() == null) {
            category.setLevel(1);
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        Optional<Category> existing = categoryRepository.findById(id);
        if (existing.isPresent()) {
            Category c = existing.get();
            if (category.getName() != null) {
                c.setName(category.getName());
            }
            if (category.getIcon() != null) {
                c.setIcon(category.getIcon());
            }
            if (category.getParentId() != null) {
                c.setParentId(category.getParentId());
            }
            if (category.getLevel() != null) {
                c.setLevel(category.getLevel());
            }
            if (category.getSortOrder() != null) {
                c.setSortOrder(category.getSortOrder());
            }
            if (category.getStatus() != null) {
                c.setStatus(category.getStatus());
            }
            return categoryRepository.save(c);
        }
        return null;
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<Category> searchCategories(String keyword) {
        return categoryRepository.findByNameContaining(keyword);
    }

    @Override
    public List<Category> getCategorySongs(Long categoryId) {
        return null;
    }

    @Override
    public void addSongToCategory(Long categoryId, Long songId) {
    }

    @Override
    public void removeSongFromCategory(Long categoryId, Long songId) {
    }
}
