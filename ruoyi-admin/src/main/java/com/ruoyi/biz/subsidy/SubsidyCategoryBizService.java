package com.ruoyi.biz.subsidy;

import com.ruoyi.subsidy.facade.IGbCategoryFacade;
import com.ruoyi.subsidy.model.query.GbCategoryQuery;
import com.ruoyi.web.form.subsidy.SubsidyCategoryForm;
import com.ruoyi.web.mapper.subsidy.SubsidyCategoryWebConvert;
import com.ruoyi.web.vo.subsidy.SubsidyCategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** 国补后台分类应用编排。 */
@Component
@RequiredArgsConstructor
public class SubsidyCategoryBizService {
    private final IGbCategoryFacade categoryFacade;

    /** 查询全部分类。 */
    public List<SubsidyCategoryVO> list() {
        return SubsidyCategoryWebConvert.INSTANCE.toVOList(categoryFacade.list(new GbCategoryQuery()));
    }

    /** 新增分类。 */
    public void save(final SubsidyCategoryForm form) {
        categoryFacade.save(SubsidyCategoryWebConvert.INSTANCE.toParam(form));
    }

    /** 更新分类。 */
    public void update(final Long categoryId, final SubsidyCategoryForm form) {
        categoryFacade.update(categoryId, SubsidyCategoryWebConvert.INSTANCE.toParam(form));
    }
}
