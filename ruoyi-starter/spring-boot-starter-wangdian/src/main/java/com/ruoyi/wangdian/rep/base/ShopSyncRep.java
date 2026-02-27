package com.ruoyi.wangdian.rep.base;

import com.ruoyi.wangdian.rep.WDRep;
import lombok.Data;

import java.util.List;

/**
 * 店铺同步请求参数实体类
 * 用于批量同步店铺信息到ERP系统
 */
@Data
public class ShopSyncRep extends WDRep {

    // ==================== 店铺列表 - shopList ====================

    private List<ShopSyncInfoRep> shopList;


}
