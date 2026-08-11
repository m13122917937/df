package com.ruoyi.web.controller.subsidy;

import com.ruoyi.biz.subsidy.SubsidyProductBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.web.form.subsidy.SubsidyProductForm;
import com.ruoyi.web.vo.subsidy.SubsidyProductVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 国补后台商品接口。 */
@RestController
@RequestMapping("/subsidy/products")
public class SubsidyProductController extends BaseController {
    private final SubsidyProductBizService productBizService;

    public SubsidyProductController(final SubsidyProductBizService productBizService) {
        this.productBizService = productBizService;
    }

    /** 分页查询商品。 */
    @GetMapping
    public TableDataInfo page(@RequestParam(required = false) final String productName,
                              @RequestParam(required = false) final Integer status) {
        PageBO<SubsidyProductVO> page = productBizService.page(productName, status, startParamV2("create_time desc"));
        return getDataTable(page.getData(), page.getTotal());
    }

    /** 新增商品。 */
    @PostMapping
    public AjaxResult save(@RequestBody final SubsidyProductForm form) {
        return AjaxResult.success(productBizService.save(form));
    }

    /** 更新商品。 */
    @PutMapping("/{productId}")
    public AjaxResult update(@PathVariable final Long productId, @RequestBody final SubsidyProductForm form) {
        productBizService.update(productId, form);
        return AjaxResult.success();
    }
}
