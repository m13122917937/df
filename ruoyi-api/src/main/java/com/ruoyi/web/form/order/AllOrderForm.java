package com.ruoyi.web.form.order;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data

public class AllOrderForm {


    private List<String> orderCodeList;


    private List<String> originalOrderIdList;


    private List<String> erpOrderIdList;


    private String brand;


    private String category;


    private Date createStartTime;


    private Date createEndTime;


    private Date createTimeStart;


    private Date createTimeEnd;


    private Date sendStartTime;


    private Date sendNedTime;


    private Date signedStartTime;


    private Date signedEndTime;


    private Long companyId;


    private List<Integer> statusList;


    private Integer orderType;
}
