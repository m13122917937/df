package com.ruoyi.web.controller.esign;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.biz.contract.ContractBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * e签宝异步回调入口
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Slf4j
@RestController
@RequestMapping("/esign/callback")
public class EsignCallbackController extends BaseController {

    @Autowired
    private ContractBizService contractBizService;

    /**
     * 合同状态回调（contractId 在 URL 中透传，回调体中带 e 签宝 flowId）
     *
     * @param contractId 合同主键
     * @param body       回调原始体
     * @return 统一返回 success（e 签宝按 HTTP 200 判定）
     */
    @PostMapping("/contract/{contractId}")
    public AjaxResult contractCallback(@PathVariable("contractId") Long contractId,
                                       @RequestBody(required = false) String body) {
        log.info("收到合同回调，contractId:{}，body:{}", contractId, body);
        String flowId = parseFlowId(body);
        contractBizService.handleCallback(contractId, flowId);
        return AjaxResult.success();
    }

    private String parseFlowId(String body) {
        if (StrUtil.isBlank(body)) {
            return null;
        }
        try {
            JsonNode json = JacksonUtil.readTree(body);
            String flowId = json.path("flowId").asText(null);
            if (StrUtil.isNotBlank(flowId)) {
                return flowId;
            }
            JsonNode action = json.path("action");
            if (action.isObject()) {
                return action.path("flowId").asText(null);
            }
        } catch (Exception e) {
            log.warn("回调体解析flowId失败：{}", e.getMessage());
        }
        return null;
    }
}
