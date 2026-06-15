package com.ruoyi.jky.param.vendor;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class VendorListParam {

    /** 页码，默认0 */
    private Integer pageIndex = 0;

    /** 每页记录数，默认50 */
    private Integer pageSize = 50;

    /** 供应商编号，模糊查询 */
    private String code;

    /** 供应商编号，逗号分隔，精准查询 */
    private String codes;

    /** 供应商名称，模糊查询 */
    private String name;

    /** 供应商名称，逗号分隔，精准查询 */
    private String names;

    /** 创建时间起，格式：yyyy-MM-dd HH:mm:ss */
    private String gmtCreateStart;

    /** 创建时间止，格式：yyyy-MM-dd HH:mm:ss */
    private String gmtCreateEnd;

    /** 修改时间起，格式：yyyy-MM-dd HH:mm:ss */
    private String gmtModifiedStart;

    /** 修改时间止，格式：yyyy-MM-dd HH:mm:ss */
    private String gmtModifiedEnd;

    /** 是否返回所有收款账户：0=仅返回默认收款账户，1=返回所有收款账户 */
    private Integer needAllPayAccount;

    /** 是否包含已删除/停用数据：1=需要返回，0=不需要返回 */
    private Integer includeDeleteAndBlockup;

}
