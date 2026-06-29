package com.ruoyi.web.form.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AllOrderForm {


    private List<String> orderCodeList;


    private List<String> originalOrderIdList;


    private List<String> erpOrderIdList;


    private Long companyId;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createStartTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createEndTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeStart;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeEnd;



    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendStartTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendNedTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date signedStartTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date signedEndTime;


    private List<Integer> statusList;


    private Integer orderType;


    /**
     * 品牌
     */
    private String brand;


    /**
     * 产品型号
     */
    private String productName;


    /**
     * 规格
     */
    private String skuName;


    /**
     * 店铺名
     */
    private String shopName;


    /**
     * 付款主体ID
     */
    private Long payerId;


    /**
     * 供应商名称
     */
    private String supplierName;


    /**
     * 串码（SN）
     */
    private String sn;
}
