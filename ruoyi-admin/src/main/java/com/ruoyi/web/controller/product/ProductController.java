package com.ruoyi.web.controller.product;

import com.ruoyi.biz.product.ProductSkuBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.web.form.product.ProductSkuForm;
import com.ruoyi.web.form.product.ProductSkuQForm;
import com.ruoyi.web.vo.product.ProductSkuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {


    @Autowired
    private ProductSkuBizService productSkuBizService;


    @GetMapping("/productName")
    public AjaxResult listProductName(@RequestParam("brand") String brand, @RequestParam(required = false, value = "productName") String productName) {
        return AjaxResult.success(productSkuBizService.listProductName(brand, productName));
    }


    @GetMapping("/brand")
    public AjaxResult listBrand() {
        return AjaxResult.success(productSkuBizService.listBrand());
    }



    @PostMapping("/sku/page/list")
    public TableDataInfo pageList(@RequestBody ProductSkuQForm productSkuQForm) {

        PageBO<ProductSkuVO> productSkuVOPageBO = productSkuBizService.pageList(productSkuQForm.getBrand(),
                productSkuQForm.getCategory(), productSkuQForm.getProductName(), startParamV2("create_time desc"));

        return getDataTable(productSkuVOPageBO.getData(), productSkuVOPageBO.getTotal());
    }

}
