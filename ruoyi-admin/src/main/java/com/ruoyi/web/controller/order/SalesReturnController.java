package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.SalesReturnBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.web.form.order.SalesReturnListForm;
import com.ruoyi.web.form.order.SalesReturnSaveForm;
import com.ruoyi.web.vo.order.SalesReturnOrderDetailVO;
import com.ruoyi.web.vo.order.SalesReturnVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 销售退货Controller
 *
 * @author ruoyi
 * @date 2026-06-30
 */
@Slf4j
@RestController
@RequestMapping("/sales/return")
public class SalesReturnController extends BaseController {

    @Autowired
    private SalesReturnBizService salesReturnBizService;

    /**
     * 分页查询销售退货列表
     */
    @PostMapping("list")
    public TableDataInfo list(@RequestBody SalesReturnListForm listForm) {
        PageParamV2 pageParam = startParamV2("create_time desc");
        PageBO<SalesReturnVO> pageBO = salesReturnBizService.listPage(listForm, pageParam);
        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }

    /**
     * 新增销售退货单
     */
    @PostMapping("save")
    public AjaxResult save(@RequestBody SalesReturnSaveForm form) {
        salesReturnBizService.save(form, getLoginUser());
        return AjaxResult.success();
    }

    /**
     * 根据内部单号或商家单号查询订单详情（自动带出用）
     */
    @GetMapping("order-detail")
    public AjaxResult getOrderDetail(@RequestParam(value = "orderCode", required = false) String orderCode,
                                     @RequestParam(value = "originalOrderId", required = false) String originalOrderId) {
        SalesReturnOrderDetailVO vo = salesReturnBizService.getOrderDetail(orderCode, originalOrderId);
        if (vo == null) {
            return AjaxResult.error("未找到订单信息");
        }
        return AjaxResult.success(vo);
    }

    /**
     * 查询退货单详情
     */
    @GetMapping("{id}")
    public AjaxResult getById(@PathVariable("id") Long id) {
        SalesReturnVO vo = salesReturnBizService.getById(id);
        return AjaxResult.success(vo);
    }
}
