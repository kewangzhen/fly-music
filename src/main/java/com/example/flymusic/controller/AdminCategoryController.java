package com.example.flymusic.controller;

import com.example.flymusic.entity.Category;
import com.example.flymusic.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Category> categories = categoryService.getAllCategories();
            response.put("code", 200);
            response.put("data", categories);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取分类列表失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategory(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Category category = categoryService.getCategoryById(id).orElse(null);
            if (category != null) {
                response.put("code", 200);
                response.put("data", category);
            } else {
                response.put("code", 404);
                response.put("message", "分类不存在");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取分类失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody Category category) {
        Map<String, Object> response = new HashMap<>();
        try {
            Category created = categoryService.createCategory(category);
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", created);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Map<String, Object> response = new HashMap<>();
        try {
            Category updated = categoryService.updateCategory(id, category);
            if (updated != null) {
                response.put("code", 200);
                response.put("message", "更新成功");
                response.put("data", updated);
            } else {
                response.put("code", 404);
                response.put("message", "分类不存在");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            categoryService.deleteCategory(id);
            response.put("code", 200);
            response.put("message", "删除成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
