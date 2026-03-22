package com.ruoyi.web.controller;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.index.IndexBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.web.form.index.ProductForm;
import com.ruoyi.web.form.index.TabProvinceForm;
import com.ruoyi.web.form.index.TradeForm;
import com.ruoyi.web.vo.index.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j

@RestController
@RequestMapping("index")
public class IndexController extends BaseController {

    @Autowired
    IndexBizService indexBizService;


    @GetMapping("tab")

    public AjaxResult index() {
        return AjaxResult.success(indexBizService.tab());
    }



    @PostMapping("province")
    public AjaxResult province(@RequestBody TabProvinceForm tabProvinceForm) {

        List<ProvinceVO> provinceVOList = indexBizService.provinceAndCity(tabProvinceForm.getTabName(), tabProvinceForm.getProductName(), tabProvinceForm.getSkuName());

        return AjaxResult.success(provinceVOList);
    }




    @PostMapping("tab/product")
    public AjaxResult product(@RequestBody ProductForm productForm) {

        ValidatorUtils.validateEntity(productForm);
        if (Objects.equals(productForm.getProvince(), 0L)) {
            productForm.setProvince(null);
        }

        List<IndexProductVO> indexProductVOS = indexBizService.productList(productForm);

        return AjaxResult.success(indexProductVOS);
    }




    @PostMapping("tab/product/sku")
    public AjaxResult skuCount(@RequestBody ProductForm productForm) {

        ValidatorUtils.validateEntity(productForm);
        Assert.notEmpty(productForm.getProductName(), "productName不能为空");
        if (Objects.equals(productForm.getProvince(), 0L)) {
            productForm.setProvince(null);
        }

        List<IndexSkuVO> indexSkuVOList = indexBizService.skuCount(productForm);

        return AjaxResult.success(indexSkuVOList);
    }



    @PostMapping("tab/product/sku/list")
    public TableDataInfo skuList(@RequestBody ProductForm productForm) {

        ValidatorUtils.validateEntity(productForm);
        Assert.notEmpty(productForm.getProductName(), "productName不能为空");
        if (Objects.equals(productForm.getProvince(), 0L)) {
            productForm.setProvince(null);
        }

        PageBO<IndexSkuListVO> pageBO = indexBizService.skuList(productForm, startParamV2("last_shipping_time desc"));

        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }




    @GetMapping("sku/{orderId}/{hangingId}")
    public AjaxResult infoSku(@PathVariable("orderId") String orderId, @PathVariable("hangingId") Long hangingId) {

        IndexOrderInfoVO indexOrderInfoVO = indexBizService.skuInfo(orderId, hangingId);

        return AjaxResult.success(indexOrderInfoVO);
    }




    @PostMapping("trade")
    public AjaxResult trade(@RequestBody TradeForm tradeForm) {

        ValidatorUtils.validateEntity(tradeForm);

        indexBizService.trade(tradeForm, getLoginUser());

        return AjaxResult.success();
    }


}
