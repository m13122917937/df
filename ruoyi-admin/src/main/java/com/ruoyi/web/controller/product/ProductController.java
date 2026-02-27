package com.ruoyi.web.controller.product;

import com.ruoyi.biz.product.CategoryBrandUtils;
import com.ruoyi.biz.product.ProductSkuBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import com.ruoyi.mapper.product.ProductConvert;
import com.ruoyi.product.facade.IProductSkuFacade;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.query.ProductSkuQuery;
import com.ruoyi.web.form.product.ProductSkuForm;
import com.ruoyi.web.form.product.ProductSkuQForm;
import com.ruoyi.web.vo.product.ProductSkuVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Api(tags = "商品管理接口")
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {


    @Autowired
    private ProductSkuBizService productSkuBizService;

    @ApiOperation("获取品牌列表")
    @GetMapping("/brand")
    public AjaxResult listBrand() {
        return AjaxResult.success(CategoryBrandUtils.getAllBrandNames());
    }

    @ApiOperation("根据品牌获取类别列表")
    @GetMapping("/category")
    public AjaxResult listCategory() {
        return AjaxResult.success(CategoryBrandUtils.getAllCategoryNames());
    }

    @ApiOperation("根据品牌获取商品列表")
    @GetMapping("/productName")
    public AjaxResult listProductName(@RequestParam("brand") String brand, @RequestParam(required = false, value = "productName") String productName) {
        return AjaxResult.success(productSkuBizService.listProductName(brand, productName));
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = ProductSkuVO.class)
    })
    @ApiOperation("分页查询商品SKU列表")
    @PostMapping("/sku/page/list")
    public TableDataInfo pageList(@RequestBody ProductSkuQForm productSkuQForm) {

        PageBO<ProductSkuVO> productSkuVOPageBO = productSkuBizService.pageList(productSkuQForm.getBrand(),
                productSkuQForm.getCategory(), productSkuQForm.getProductName(), startParamV2("create_time desc"));

        return getDataTable(productSkuVOPageBO.getData(), productSkuVOPageBO.getTotal());
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = ProductSkuVO.class)
    })
    @ApiOperation("新增商品SKU")
    @PostMapping("/sku")
    public AjaxResult add(@Validated(AddGroup.class) @RequestBody ProductSkuForm productSkuForm) throws IOException {
        ProductSkuVO result = productSkuBizService.add(productSkuForm);
        return AjaxResult.success(result);
    }


}
