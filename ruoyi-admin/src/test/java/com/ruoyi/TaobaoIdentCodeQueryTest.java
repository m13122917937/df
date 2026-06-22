package com.ruoyi;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAscpLogisticsIdentcodeQueryRequest;
import com.taobao.api.response.AlibabaAscpLogisticsIdentcodeQueryResponse;
import com.taobao.api.response.AlibabaAscpLogisticsIdentcodeQueryResponse.ResultDTO;
import com.taobao.api.response.AlibabaAscpLogisticsIdentcodeQueryResponse.TopIdentCodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

/**
 * 淘宝串码二销验证接口测试。
 *
 * @author ruoyi
 */
@Slf4j
@ContextConfiguration
@SpringBootTest(classes = AdminApplication.class)
public class TaobaoIdentCodeQueryTest {

    @Autowired
    private TaobaoClient taobaoClient;


    /**
     * 调用 alibaba.ascp.logistics.identcode.query 查询单个串码是否可销售。
     *
     * @throws ApiException 淘宝 SDK 调用异常
     */
    @Test
    public void queryIdentCodeAvailable() throws ApiException {
//        Assumptions.assumeTrue(StrUtil.isNotBlank(defaultSessionKey), "请配置 fy.taobao.default-session-key");
//        Assumptions.assumeTrue(StrUtil.isNotBlank(identCode), "请配置 fy.taobao.test.ident-code");

        AlibabaAscpLogisticsIdentcodeQueryRequest request = new AlibabaAscpLogisticsIdentcodeQueryRequest();
        request.setIdentCodeList(JSONUtil.toJsonStr(Collections.singletonList("AF2QVB6205002644")));
        request.setIdentType("SN");
        request.setRootCatId("1512");
        request.setBrandId("590022244");
        request.setAvailable(Boolean.TRUE);

        AlibabaAscpLogisticsIdentcodeQueryResponse response = taobaoClient.execute(request);
        log.info("淘宝串码二销验证测试响应：{}", response.getBody());

        Assertions.assertTrue(response.isSuccess(), response.getSubCode() + ":" + response.getSubMsg());
        ResultDTO result = response.getResult();
        Assertions.assertNotNull(result, "淘宝接口 result 为空");
        Assertions.assertTrue(CollectionUtil.isNotEmpty(result.getIdentCodeList()), "淘宝接口未返回串码结果");

        TopIdentCodeDTO matched = result.getIdentCodeList().stream()
                .filter(item -> StrUtil.equals("AF2QVB6205002644", item.getIdentCode()))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(matched, "淘宝接口返回结果中未包含测试串码");
        Assertions.assertNotNull(matched.getAvailable(), "淘宝接口未返回 available 判断结果");
        log.info("淘宝串码二销验证测试结果：identCode={}, identType={}, rootCatId={}, brandId={}, available={}, reason={}",
                matched.getIdentCode(), matched.getIdentType(), matched.getRootCatId(), matched.getBrandId(),
                matched.getAvailable(), matched.getUnAvailableReason());
    }
}
