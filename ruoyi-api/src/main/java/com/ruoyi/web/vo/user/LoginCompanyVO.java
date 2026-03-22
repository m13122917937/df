package com.ruoyi.web.vo.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)

public class LoginCompanyVO {


    private Boolean isLogin;


    private Boolean newUser;


    private String uuid;


    private String userName;


    private List<CompanyVO> companyVOList;


    private String roles;



}
