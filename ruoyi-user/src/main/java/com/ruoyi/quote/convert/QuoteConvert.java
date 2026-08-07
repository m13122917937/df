package com.ruoyi.quote.convert;

import com.ruoyi.quote.domain.QuotePriceTier;
import com.ruoyi.quote.domain.QuoteProduct;
import com.ruoyi.quote.domain.QuoteProductPrice;
import com.ruoyi.quote.model.bo.QuotePriceTierBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.bo.QuoteProductPriceBO;
import com.ruoyi.quote.model.param.QuotePriceTierParam;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.param.QuoteProductPriceParam;
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
     * 转换价格档位参数为实体。
     *
     * @param source 价格档位参数
     * @return 价格档位实体
     */
    QuotePriceTier toTierDomain(QuotePriceTierParam source);

    /**
     * 转换价格档位实体为业务对象。
     *
     * @param source 价格档位实体
     * @return 价格档位业务对象
     */
    QuotePriceTierBO toTierBO(QuotePriceTier source);

    /**
     * 批量转换价格档位业务对象。
     *
     * @param source 价格档位实体集合
     * @return 价格档位业务对象集合
     */
    List<QuotePriceTierBO> toTierBOList(List<QuotePriceTier> source);

    /**
     * 转换商品参数为实体。
     *
     * @param source 商品参数
     * @return 商品实体
     */
    QuoteProduct toProductDomain(QuoteProductParam source);

    /**
     * 转换商品实体为业务对象。
     *
     * @param source 商品实体
     * @return 商品业务对象
     */
    QuoteProductBO toProductBO(QuoteProduct source);

    /**
     * 批量转换商品业务对象。
     *
     * @param source 商品实体集合
     * @return 商品业务对象集合
     */
    List<QuoteProductBO> toProductBOList(List<QuoteProduct> source);

    /**
     * 转换价格明细参数为实体。
     *
     * @param source 价格明细参数
     * @return 价格明细实体
     */
    QuoteProductPrice toPriceDomain(QuoteProductPriceParam source);

    /**
     * 转换价格明细实体为业务对象。
     *
     * @param source 价格明细实体
     * @return 价格明细业务对象
     */
    QuoteProductPriceBO toPriceBO(QuoteProductPrice source);

    /**
     * 批量转换价格明细业务对象。
     *
     * @param source 价格明细实体集合
     * @return 价格明细业务对象集合
     */
    List<QuoteProductPriceBO> toPriceBOList(List<QuoteProductPrice> source);
}
