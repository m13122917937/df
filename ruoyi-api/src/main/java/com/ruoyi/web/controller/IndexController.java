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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@Api(tags = "首页接口")
@RestController
@RequestMapping("index")
public class IndexController extends BaseController {

    @Autowired
    IndexBizService indexBizService;

    @ApiResponses({@ApiResponse(code = 200, message = "成功", response = IndexTabVO.class)})
    @GetMapping("tab")
    @ApiOperation("首页tab分类")
    public AjaxResult index() {
        return AjaxResult.success(indexBizService.tab());
    }

    @ApiResponses({@ApiResponse(code = 200, message = "成功", response = ProvinceVO.class)})
    @ApiOperation("首页省市下面的数据")
    @PostMapping("province")
    public AjaxResult province(@RequestBody TabProvinceForm tabProvinceForm) {

        List<ProvinceVO> provinceVOList = indexBizService.provinceAndCity(tabProvinceForm.getTabName(), tabProvinceForm.getProductName(), tabProvinceForm.getSkuName());

        return AjaxResult.success(provinceVOList);
    }


    @ApiResponses({@ApiResponse(code = 200, message = "成功", response = IndexProductVO.class)})
    @ApiOperation("首页查询tab下面的product list")
    @PostMapping("tab/product")
    public AjaxResult product(@RequestBody ProductForm productForm) {

        ValidatorUtils.validateEntity(productForm);
        if (Objects.equals(productForm.getProvince(), 0L)) {
            productForm.setProvince(null);
        }

        List<IndexProductVO> indexProductVOS = indexBizService.productList(productForm);

        return AjaxResult.success(indexProductVOS);
    }


    @ApiResponses({@ApiResponse(code = 200, message = "成功", response = IndexSkuVO.class)})
    @ApiOperation("首页商品下面的sku数据")
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

    @ApiResponses({@ApiResponse(code = 200, message = "成功", response = IndexSkuListVO.class)})
    @ApiOperation("首页商品下面的sku-list")
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


    @ApiResponses({@ApiResponse(code = 200, message = "成功", response = IndexOrderInfoVO.class)})
    @ApiOperation("sku,订单详情信息，弹窗")
    @GetMapping("sku/{orderId}/{hangingId}")
    public AjaxResult infoSku(@PathVariable("orderId") String orderId, @PathVariable("hangingId") Long hangingId) {

        IndexOrderInfoVO indexOrderInfoVO = indexBizService.skuInfo(orderId, hangingId);

        return AjaxResult.success(indexOrderInfoVO);
    }


    @ApiResponses({@ApiResponse(code = 200, message = "成功", response = IndexOrderInfoVO.class)})
    @ApiOperation("抢单接口")
    @PostMapping("trade")
    public AjaxResult trade(@RequestBody TradeForm tradeForm) {

        ValidatorUtils.validateEntity(tradeForm);

        indexBizService.trade(tradeForm, getLoginUser());

        return AjaxResult.success();
    }


}
