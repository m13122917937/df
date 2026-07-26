package com.ruoyi.web.controller.master;

import com.ruoyi.biz.master.MasterSubjectBankBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.vo.master.MasterSubjectBankListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 主体银行卡配置接口：查看主体下银行卡、设置默认银行卡。
 */
@RestController
@RequestMapping("/master/subject/bank")
@RequiredArgsConstructor
public class MasterSubjectBankController extends BaseController {

    private final MasterSubjectBankBizService masterSubjectBankBizService;

    /**
     * 查询主体下的银行卡列表及当前默认卡。
     *
     * @param subjectId 经营主体ID
     * @return 银行卡列表响应
     */
    @GetMapping("/list/{subjectId}")
    @PreAuthorize("@ss.hasPermi('master:subject:bank:list')")
    public AjaxResult list(@PathVariable Long subjectId) {
        MasterSubjectBankListVO result = masterSubjectBankBizService.listBanks(subjectId);
        return AjaxResult.success(result);
    }

    /**
     * 设置主体默认银行卡。
     *
     * @param subjectId 经营主体ID
     * @param payerId 银行卡ID
     * @return 操作结果
     */
    @PutMapping("/default/{subjectId}/{payerId}")
    @PreAuthorize("@ss.hasPermi('master:subject:setDefaultBank')")
    public AjaxResult setDefaultBank(@PathVariable Long subjectId, @PathVariable Long payerId) {
        masterSubjectBankBizService.setDefaultBank(subjectId, payerId);
        return AjaxResult.success();
    }
}
