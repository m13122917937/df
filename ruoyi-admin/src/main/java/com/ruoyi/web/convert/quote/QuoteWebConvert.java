package com.ruoyi.web.convert.quote;

import com.ruoyi.quote.model.bo.QuoteBrandBO;
import com.ruoyi.quote.model.bo.QuoteCategoryBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.param.QuoteBrandParam;
import com.ruoyi.quote.model.param.QuoteCategoryParam;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.query.QuoteBrandQuery;
import com.ruoyi.quote.model.query.QuoteCategoryQuery;
import com.ruoyi.quote.model.query.QuoteProductQuery;
import com.ruoyi.web.vo.quote.QuoteBrandQueryRequest;
import com.ruoyi.web.vo.quote.QuoteBrandSaveRequest;
import com.ruoyi.web.vo.quote.QuoteBrandVO;
import com.ruoyi.web.vo.quote.QuoteCategoryQueryRequest;
import com.ruoyi.web.vo.quote.QuoteCategorySaveRequest;
import com.ruoyi.web.vo.quote.QuoteCategoryVO;
import com.ruoyi.web.vo.quote.QuoteProductQueryRequest;
import com.ruoyi.web.vo.quote.QuoteProductSaveRequest;
import com.ruoyi.web.vo.quote.QuoteProductVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 报价 Web 层对象转换器。
 */
@Mapper
public interface QuoteWebConvert {

    QuoteWebConvert INSTANCE = Mappers.getMapper(QuoteWebConvert.class);

    /**
     * 转换品牌查询请求。
     *
     * @param source Web 查询请求
     * @return 领域查询条件
     */
    QuoteBrandQuery toBrandQuery(QuoteBrandQueryRequest source);

    /**
     * 转换品牌保存请求。
     *
     * @param source Web 保存请求
     * @return 领域参数
     */
    QuoteBrandParam toBrandParam(QuoteBrandSaveRequest source);

    /**
     * 批量转换品牌响应。
     *
     * @param source 业务对象集合
     * @return Web 响应集合
     */
    List<QuoteBrandVO> toBrandVOList(List<QuoteBrandBO> source);

    /**
     * 转换品类查询请求。
     *
     * @param source Web 查询请求
     * @return 领域查询条件
     */
    QuoteCategoryQuery toCategoryQuery(QuoteCategoryQueryRequest source);

    /**
     * 转换品类保存请求。
     *
     * @param source Web 保存请求
     * @return 领域参数
     */
    QuoteCategoryParam toCategoryParam(QuoteCategorySaveRequest source);

    /**
     * 批量转换品类响应。
     *
     * @param source 业务对象集合
     * @return Web 响应集合
     */
    List<QuoteCategoryVO> toCategoryVOList(List<QuoteCategoryBO> source);

    /**
     * 转换商品查询请求。
     *
     * @param source Web 查询请求
     * @return 领域查询条件
     */
    QuoteProductQuery toProductQuery(QuoteProductQueryRequest source);

    /**
     * 转换商品保存请求。
     *
     * @param source Web 保存请求
     * @return 领域参数
     */
    QuoteProductParam toProductParam(QuoteProductSaveRequest source);

    /**
     * 批量转换商品响应。
     *
     * @param source 业务对象集合
     * @return Web 响应集合
     */
    List<QuoteProductVO> toProductVOList(List<QuoteProductBO> source);
}
