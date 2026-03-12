package com.example.flymusic.entity.enums;

/**
 * 榜单类型枚举
 * 定义热门榜单的不同类型
 * 
 * @author fly-music
 * @since 2026-03-12
 */
public enum RankingType {
    
    /**
     * 全局热门榜
     * 根据所有用户的行为数据计算出的综合热门歌曲排行
     */
    GLOBAL("global", "全局热门榜"),
    
    /**
     * 分类热门榜
     * 按音乐分类（如流行、摇滚、民谣等）分别计算热门歌曲排行
     */
    CATEGORY("category", "分类热门榜"),
    
    /**
     * 新歌榜
     * 最近7天内发布的新歌热度排行
     */
    NEW("new", "新歌榜"),
    
    /**
     * 飙升榜
     * 24小时内热度上升最快的歌曲排行，反映当前趋势
     */
    SOARING("soaring", "飙升榜");

    /**
     * 榜单类型编码
     * 用于数据库存储
     */
    private final String code;
    
    /**
     * 榜单类型描述
     * 用于显示
     */
    private final String desc;

    RankingType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取榜单类型编码
     * @return 编码值
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取榜单类型描述
     * @return 描述文本
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 根据编码获取枚举值
     * @param code 榜单类型编码
     * @return 对应的枚举类型，默认返回GLOBAL
     */
    public static RankingType fromCode(String code) {
        for (RankingType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return GLOBAL;
    }
}
