package com.ruoyi.jky.rep.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 吉客云公司分页查询响应。
 *
 * <p>注意：吉客云接口返回的 data 为直接数组，因此查询时使用
 * {@code JkyResponse<List<CompanyQueryRep.CompanyInfoRep>>} 接收。</p>
 */
@Data
public class CompanyQueryRep {

    /**
     * 吉客云公司信息。
     */
    @Data
    public static class CompanyInfoRep {

        private Long companyId;

        private String companyCode;

        private String companyName;

        private String companyShortName;

        private String countryName;

        private String provinceName;

        private String cityName;

        private String townName;

        private String address;

        private String linkMan;

        /** 联系人电话。 */
        private String tel;

        /** 税号（吉客云字段名：taxNumber）。 */
        @JsonProperty("taxNumber")
        private String taxIdentifyNumber;

        /** 是否停用（0-正常，1-停用）。 */

        private Integer isDelete;

        private Long gmtCreate;

        private Long gmtModified;
    }
}
