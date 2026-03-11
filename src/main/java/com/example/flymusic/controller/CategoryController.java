package com.example.flymusic.controller;

import com.example.flymusic.entity.Category;
import com.example.flymusic.entity.Song;
import com.example.flymusic.repository.CategoryRepository;
import com.example.flymusic.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 分类Controller
 * 提供分类相关的RESTful API接口
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private SongRepository songRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(createSuccessResponse("获取成功", categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(createSuccessResponse("获取成功", category.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody Category category) {
        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("创建成功", savedCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        category.setId(id);
        Category updatedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(createSuccessResponse("更新成功", updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(createSuccessResponse("删除成功", null));
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCategories(@RequestParam String keyword) {
        List<Category> categories = categoryRepository.findByNameContaining(keyword);
        return ResponseEntity.ok(createSuccessResponse("搜索成功", categories));
    }

    @GetMapping("/{categoryId}/songs")
    public ResponseEntity<Map<String, Object>> getCategorySongs(@PathVariable Long categoryId) {
        List<Song> songs = songRepository.findByCategoryId(categoryId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<Map<String, Object>> getCategoriesByParent(@PathVariable Long parentId) {
        List<Category> categories = categoryRepository.findByParentId(parentId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", categories));
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<Map<String, Object>> getCategoriesByLevel(@PathVariable Integer level) {
        List<Category> categories = categoryRepository.findByLevel(level);
        return ResponseEntity.ok(createSuccessResponse("获取成功", categories));
    }

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        return response;
    }
}