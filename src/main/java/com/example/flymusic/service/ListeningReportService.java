package com.example.flymusic.service;

import com.example.flymusic.entity.dto.ListeningReportDTO;

/**
 * 听歌报告服务接口
 * 提供用户听歌报告的生成和查询功能
 * 
 * 功能说明：
 * - 根据用户行为数据生成年度报告
 * - 支持查询指定年份的报告
 * - 提供简要统计信息
 * 
 * @author fly-music
 * @since 2026-03-12
 */
public interface ListeningReportService {
    
    /**
     * 获取当前年份的听歌报告
     * 
     * @param userId 用户ID
     * @return 听歌报告DTO
     */
    ListeningReportDTO getListeningReport(Long userId);
    
    /**
     * 获取指定年份的听歌报告
     * 
     * @param userId 用户ID
     * @param year 年份
     * @return 听歌报告DTO
     */
    ListeningReportDTO getListeningReportByYear(Long userId, Integer year);
    
    /**
     * 获取简要统计信息
     * 
     * @param userId 用户ID
     * @return 简要统计DTO
     */
    ListeningReportDTO getSummary(Long userId);
}
