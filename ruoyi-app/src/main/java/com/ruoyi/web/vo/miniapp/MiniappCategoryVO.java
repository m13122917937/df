package com.ruoyi.web.vo.miniapp;

import lombok.Data;

/**
 * 小程序分类响应。
 */
@Data
public class MiniappCategoryVO {
    private Long id;
    private Long parentId;
    private String categoryName;
    private String iconUrl;
    private Integer sortOrder;
}
