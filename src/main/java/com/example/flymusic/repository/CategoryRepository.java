package com.example.flymusic.repository;

import com.example.flymusic.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 分类Repository
 * 提供分类相关的数据库操作
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * 根据名称搜索分类
     */
    List<Category> findByNameContaining(String name);
    
    /**
     * 根据父分类ID查询子分类
     */
    List<Category> findByParentId(Long parentId);
    
    /**
     * 根据分类级别查询分类
     */
    List<Category> findByLevel(Integer level);
    
    /**
     * 根据状态查询分类
     */
    List<Category> findByStatus(Integer status);
    
    /**
     * 根据名称精确查询
     */
    Category findByName(String name);
}