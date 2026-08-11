package com.ruoyi.subsidy.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.subsidy.domain.GbProductImage;
import com.ruoyi.subsidy.mapper.GbProductImageMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/** 国补商品轮播图领域服务。 */
@Service
public class GbProductImageService extends ServiceImpl<GbProductImageMapper, GbProductImage> {
    /** 查询商品轮播图。 */
    public List<GbProductImage> listByProductId(final Long productId) { return baseMapper.selectByProductId(productId); }
}
