package com.ruoyi.web.form.contract;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 合同列表查询表单（供应商端）
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Data
@Accessors(chain = true)
public class ContractQueryForm {

    /**
     * 合同名称（模糊）
     */
    private String contractName;

    /**
     * 合同类型集合
     */
    private List<Integer> contractTypeList;

    /**
     * 状态集合
     */
    private List<Integer> statusList;
}
