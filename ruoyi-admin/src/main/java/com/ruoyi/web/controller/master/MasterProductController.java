package com.ruoyi.web.controller.master;

import com.ruoyi.biz.master.MasterProductBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.web.convert.master.MasterProductWebConvert;
import com.ruoyi.web.vo.master.MasterProductQueryRequest;
import com.ruoyi.web.vo.master.MasterProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品主数据接口。
 */
@RestController
@RequestMapping("/master/product")
@RequiredArgsConstructor
public class MasterProductController extends BaseController {

    private final MasterProductBizService masterProductBizService;

    /**
     * 分页查询商品列表。
     *
     * @param request 查询请求
     * @return 商品分页数据
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('master:product:list')")
    public TableDataInfo list(final MasterProductQueryRequest request) {
        PageBO<ProductSkuBO> page = masterProductBizService.page(
                MasterProductWebConvert.INSTANCE.toQuery(request), startParamV2("create_time desc"));
        List<MasterProductVO> rows = MasterProductWebConvert.INSTANCE.toVOList(page.getData());
        return getDataTable(rows, page.getTotal());
    }
}
