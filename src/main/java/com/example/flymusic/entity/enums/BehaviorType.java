package com.example.flymusic.entity.enums;

/**
 * 用户行为类型枚举
 * 定义用户对歌曲的各种行为操作及对应的权重分值
 * 
 * 权重说明：
 * - 正向行为（播放、收藏、分享）增加分数
 * - 负向行为（切歌、不喜欢）减少分数
 * 
 * @author fly-music
 * @since 2026-03-12
 */
public enum BehaviorType {
    
    /**
     * 播放
     * 权重: +1
     * 用户点击播放歌曲
     */
    PLAY(1, "播放", 1),
    
    /**
     * 完整收听
     * 权重: +2
     * 用户完整播放歌曲（播放完成度 > 90%）
     */
    COMPLETE(2, "完整收听", 2),
    
    /**
     * 收藏
     * 权重: +5
     * 用户主动收藏歌曲
     */
    FAVORITE(3, "收藏", 5),
    
    /**
     * 分享
     * 权重: +3
     * 用户将歌曲分享给他人
     */
    SHARE(4, "分享", 3),
    
    /**
     * 切歌/跳过
     * 权重: -3
     * 用户未完整收听就切换歌曲
     */
    SKIP(5, "切歌", -3),
    
    /**
     * 不喜欢
     * 权重: -5
     * 用户标记不喜欢该歌曲
     */
    UNLIKE(6, "不喜欢", -5);

    /**
     * 行为类型编码
     * 用于数据库存储
     */
    private final Integer code;
    
    /**
     * 行为类型描述
     * 用于显示
     */
    private final String desc;
    
    /**
     * 权重分值
     * 用于计算用户兴趣画像
     */
    private final Integer weight;

    BehaviorType(Integer code, String desc, Integer weight) {
        this.code = code;
        this.desc = desc;
        this.weight = weight;
    }

    /**
     * 获取行为类型编码
     * @return 编码值
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取行为类型描述
     * @return 描述文本
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 获取权重分值
     * @return 权重值
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * 根据编码获取枚举值
     * @param code 行为类型编码
     * @return 对应的枚举类型，默认返回PLAY
     */
    public static BehaviorType fromCode(Integer code) {
        for (BehaviorType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return PLAY;
    }
}
