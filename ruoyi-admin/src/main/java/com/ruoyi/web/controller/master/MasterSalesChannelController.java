package com.ruoyi.web.controller.master;

import com.ruoyi.biz.master.MasterSalesChannelBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.master.model.bo.MasterSalesChannelBO;
import com.ruoyi.web.convert.master.MasterSalesChannelWebConvert;
import com.ruoyi.web.vo.master.MasterSalesChannelDepositRequest;
import com.ruoyi.web.vo.master.MasterSalesChannelQueryRequest;
import com.ruoyi.web.vo.master.MasterSalesChannelVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import javax.validation.Valid;

/**
 * 销售渠道主数据接口。
 */
@RestController
@RequestMapping("/master/sales-channel")
@RequiredArgsConstructor
public class MasterSalesChannelController extends BaseController {

    private final MasterSalesChannelBizService masterSalesChannelBizService;

    /**
     * 分页查询销售渠道。
     *
     * @param request 查询请求
     * @return 销售渠道分页数据
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('master:salesChannel:list')")
    public TableDataInfo list(final MasterSalesChannelQueryRequest request) {
        PageBO<MasterSalesChannelBO> page = masterSalesChannelBizService.page(
                MasterSalesChannelWebConvert.INSTANCE.toQuery(request), startParamV2("updated_time desc"));
        List<MasterSalesChannelVO> rows = MasterSalesChannelWebConvert.INSTANCE.toVOList(page.getData());
        return getDataTable(rows, page.getTotal());
    }

    /**
     * 更新销售渠道保证金。
     *
     * @param request 保证金维护请求
     * @return 操作结果
     */
    @PutMapping("/deposit")
    @PreAuthorize("@ss.hasPermi('master:salesChannel:edit')")
    public AjaxResult updateDeposit(@Valid @RequestBody final MasterSalesChannelDepositRequest request) {
        masterSalesChannelBizService.updateDeposit(MasterSalesChannelWebConvert.INSTANCE.toDepositParam(request));
        return AjaxResult.success();
    }
}
