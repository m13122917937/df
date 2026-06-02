package com.ruoyi.biz.product;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.mapper.product.ProductConvert;
import com.ruoyi.product.facade.IProductSkuFacade;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.param.ProductSkuParam;
import com.ruoyi.product.model.query.ProductSkuQuery;
import com.ruoyi.web.form.product.ProductSkuForm;
import com.ruoyi.web.vo.product.ProductSkuVO;
import com.ruoyi.web.vo.product.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商品SKU业务服务
 *
 * @author ruoyi
 * @date 2025-12-17
 */
@Slf4j
@Component
public class ProductSkuBizService {

    @Autowired
    private IProductSkuFacade productSkuFacade;

    /**
     * 分页查询商品SKU列表
     *
     * @param brand       品牌
     * @param category    类别
     * @param productName 商品名称
     * @return 分页结果
     */
    public PageBO<ProductSkuVO> pageList(String brand, String category, String productName, PageParamV2 pageParamV2) {
        ProductSkuQuery query = new ProductSkuQuery().setBrand(brand).setCategory(category).setProductNameLike(productName);
//
        PageBO<ProductSkuBO> productSkuBOList = productSkuFacade.pageList(query, pageParamV2);
        List<ProductVO> voList = ProductConvert.INSTANCE.toVOList(productSkuBOList.getData());
        return new PageBO(voList, productSkuBOList.getTotal());
    }


    /**
     * 新增商品SKU
     *
     * @param form SKU表单
     * @return 新增的SKU信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ProductSkuVO add(ProductSkuForm form) {
        log.info("开始新增商品SKU 商品名称: {}",  form.getProductName());

        ProductSkuParam param = ProductConvert.INSTANCE.toParam(form);

        validateExits(param);

        param.setSortOrder(DateUtil.currentSeconds());
        this.genSkuCode(param);
        productSkuFacade.save(param);

        log.info("商品SKU新增成功，SKU编码: {}", param.getSkuCode());
        return null;
    }

    private void validateExits(ProductSkuParam param) {


        long count = productSkuFacade.count(new ProductSkuQuery().setProductName(param.getProductName())
                .setSpecName(param.getSpecName()));
        if(count>0){
            throw new ServiceException("商品SKU已存在");
        }
    }

    private String genSkuCode(ProductSkuParam param) {
        String brandCode = CategoryBrandUtils.getBrandCode(param.getBrand());
        String categoryCode = CategoryBrandUtils.getCategoryCode(param.getCategory());
        String productCode = ChineseToFirstLetterWithOthers.convert(param.getProductName());
        String specCode = ChineseToFirstLetterWithOthers.convert(param.getSpecName());
        String skuCode = brandCode + "-" + categoryCode + "-" + productCode + "-" + specCode;
        param.setSkuCode(skuCode);
        param.setSpuCode(productCode);
        ProductSkuBO one = productSkuFacade.getOne(new ProductSkuQuery().setSkuCode(skuCode));
        if (Objects.nonNull(one)) {
            throw new ServiceException("商品SKU编码已存在");
        }
        return skuCode;
    }


    /**
     *
     * @param brand
     * @param productName
     * @return
     */
    public List<String> listProductName(String brand, String productName) {
        List<ProductSkuBO> list = productSkuFacade.list(new ProductSkuQuery().setBrand(brand).setProductNameLike(productName).setLimit(50).setGroup("product_name"), null);
        if(CollectionUtil.isEmpty( list)){
            return Collections.EMPTY_LIST;
        }
        return list.stream().map(ProductSkuBO::getProductName).collect(Collectors.toList());
    }
}