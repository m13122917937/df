package com.ruoyi.quote.convert;

import com.ruoyi.quote.domain.QuoteBrand;
import com.ruoyi.quote.domain.QuoteCategory;
import com.ruoyi.quote.domain.QuotePriceHistory;
import com.ruoyi.quote.domain.QuoteProduct;
import com.ruoyi.quote.model.bo.QuoteBrandBO;
import com.ruoyi.quote.model.bo.QuoteCategoryBO;
import com.ruoyi.quote.model.bo.QuotePriceHistoryBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.param.QuoteBrandParam;
import com.ruoyi.quote.model.param.QuoteCategoryParam;
import com.ruoyi.quote.model.param.QuotePriceHistoryParam;
import com.ruoyi.quote.model.param.QuoteProductParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 报价领域对象转换器。
 */
@Mapper
public interface QuoteConvert {

    QuoteConvert INSTANCE = Mappers.getMapper(QuoteConvert.class);

    /**
     * 转换品牌参数为实体。
     *
     * @param source 品牌参数
     * @return 品牌实体
     */
    QuoteBrand toBrandDomain(QuoteBrandParam source);

    /**
     * 批量转换品牌业务对象。
     *
     * @param source 品牌实体集合
     * @return 品牌业务对象集合
     */
    List<QuoteBrandBO> toBrandBOList(List<QuoteBrand> source);

    /**
     * 转换品类参数为实体。
     *
     * @param source 品类参数
     * @return 品类实体
     */
    QuoteCategory toCategoryDomain(QuoteCategoryParam source);

    /**
     * 批量转换品类业务对象。
     *
     * @param source 品类实体集合
     * @return 品类业务对象集合
     */
    List<QuoteCategoryBO> toCategoryBOList(List<QuoteCategory> source);

    /**
     * 转换商品参数为实体。
     *
     * @param source 商品参数
     * @return 商品实体
     */
    QuoteProduct toProductDomain(QuoteProductParam source);

    /**
     * 批量转换商品业务对象。
     *
     * @param source 商品实体集合
     * @return 商品业务对象集合
     */
    List<QuoteProductBO> toProductBOList(List<QuoteProduct> source);

    /**
     * 转换报价流水参数为实体。
     *
     * @param source 报价流水参数
     * @return 报价流水实体
     */
    QuotePriceHistory toPriceHistoryDomain(QuotePriceHistoryParam source);

    /**
     * 转换报价流水实体为业务对象。
     *
     * @param source 报价流水实体
     * @return 报价流水业务对象
     */
    QuotePriceHistoryBO toPriceHistoryBO(QuotePriceHistory source);

    /**
     * 批量转换报价流水业务对象。
     *
     * @param source 报价流水实体集合
     * @return 报价流水业务对象集合
     */
    List<QuotePriceHistoryBO> toPriceHistoryBOList(List<QuotePriceHistory> source);

}
