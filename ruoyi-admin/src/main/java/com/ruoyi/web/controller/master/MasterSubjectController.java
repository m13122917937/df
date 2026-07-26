package com.ruoyi.web.controller.master;

import com.ruoyi.biz.master.MasterSubjectBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.master.model.bo.MasterSubjectBO;
import com.ruoyi.web.convert.master.MasterSubjectWebConvert;
import com.ruoyi.web.vo.master.MasterSubjectQueryRequest;
import com.ruoyi.web.vo.master.MasterSubjectVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 经营主体配置接口。
 */
@RestController
@RequestMapping("/master/subject")
@RequiredArgsConstructor
public class MasterSubjectController extends BaseController {

    private final MasterSubjectBizService masterSubjectBizService;

    /**
     * 分页查询经营主体。
     *
     * @param request 查询请求
     * @return 经营主体分页数据
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('master:subject:list')")
    public TableDataInfo list(final MasterSubjectQueryRequest request) {
        PageBO<MasterSubjectBO> page = masterSubjectBizService.page(
                MasterSubjectWebConvert.INSTANCE.toQuery(request), startParamV2("updated_time desc"));
        java.util.List<MasterSubjectVO> rows = MasterSubjectWebConvert.INSTANCE.toVOList(page.getData());
        return getDataTable(rows, page.getTotal());
    }
}
