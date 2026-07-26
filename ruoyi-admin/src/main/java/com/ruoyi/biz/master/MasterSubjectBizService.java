package com.ruoyi.biz.master;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.master.facade.IMasterSubjectFacade;
import com.ruoyi.master.model.bo.MasterSubjectBO;
import com.ruoyi.master.model.query.MasterSubjectQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 经营主体应用编排服务。
 */
@Component
@RequiredArgsConstructor
public class MasterSubjectBizService {

    private final IMasterSubjectFacade masterSubjectFacade;

    /**
     * 分页查询经营主体。
     *
     * @param query 查询条件
     * @param pageParam 分页参数
     * @return 经营主体分页数据
     */
    public PageBO<MasterSubjectBO> page(final MasterSubjectQuery query, final PageParamV2 pageParam) {
        return masterSubjectFacade.page(query, pageParam);
    }
}
