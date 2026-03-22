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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {


    @Autowired
    private ProductSkuBizService productSkuBizService;


    @GetMapping("/brand")
    public AjaxResult listBrand() {
        return AjaxResult.success(CategoryBrandUtils.getAllBrandNames());
    }


    @GetMapping("/category")
    public AjaxResult listCategory() {
        return AjaxResult.success(CategoryBrandUtils.getAllCategoryNames());
    }


    @GetMapping("/productName")
    public AjaxResult listProductName(@RequestParam("brand") String brand, @RequestParam(required = false, value = "productName") String productName) {
        return AjaxResult.success(productSkuBizService.listProductName(brand, productName));
    }





    @PostMapping("/sku/page/list")
    public TableDataInfo pageList(@RequestBody ProductSkuQForm productSkuQForm) {

        PageBO<ProductSkuVO> productSkuVOPageBO = productSkuBizService.pageList(productSkuQForm.getBrand(),
                productSkuQForm.getCategory(), productSkuQForm.getProductName(), startParamV2("create_time desc"));

        return getDataTable(productSkuVOPageBO.getData(), productSkuVOPageBO.getTotal());
    }





    @PostMapping("/sku")
    public AjaxResult add(@Validated(AddGroup.class) @RequestBody ProductSkuForm productSkuForm) throws IOException {
        ProductSkuVO result = productSkuBizService.add(productSkuForm);
        return AjaxResult.success(result);
    }


}
