package com.ruoyi.web.controller.contract;

import com.ruoyi.biz.contract.ContractBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.web.form.contract.ContractQueryForm;
import com.ruoyi.web.vo.contract.ContractVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 合同（供应商端）
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@RestController
@RequestMapping("/contract")
public class ContractController extends BaseController {

    @Autowired
    private ContractBizService contractBizService;

    /**
     * 分页查询当前供应商的合同列表
     *
     * @param queryForm 查询表单
     * @return 分页结果
     */
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody ContractQueryForm queryForm) {
        PageBO<ContractVO> page = contractBizService.pageList(queryForm, getDeptId(), startParamV2("create_time desc"));
        return getDataTable(page.getData(), page.getTotal());
    }

    /**
     * 查询合同详情
     *
     * @param id 合同主键
     * @return 详情
     */
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable("id") Long id) {
        return AjaxResult.success(contractBizService.detail(id, getDeptId()));
    }

    /**
     * 获取签署URL
     *
     * @param id 合同主键
     * @return 签署URL
     */
    @GetMapping("/{id}/sign-url")
    public AjaxResult signUrl(@PathVariable("id") Long id) {
        return AjaxResult.success(contractBizService.signUrl(id, getDeptId()));
    }
}
