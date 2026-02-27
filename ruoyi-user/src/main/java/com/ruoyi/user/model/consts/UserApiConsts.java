package com.ruoyi.user.model.consts;

public interface UserApiConsts {

    /**
     * 渠道二维码
     */
    Integer QR_CODE_EXPIRE_SECONDS = 2592000;

    Integer QR_CODE_EXPIRE_DEFAULT_SECONDS = 25200;

    String QR_CODE_SCENE_TYPE = "type";
    String QR_CODE_SCENE_DEPTID = "deptId";
    String QR_CODE_SCENE_UUID = "uuid";
    String QR_CODE_SCENE_PHONE = "phone";
    String QR_CODE_SCENE_USERNAME = "username";
    String QR_CODE_SCENE_OWNER = "owner";


    /**
     * 业务类型。渠道二维码业务类型
     */
    int LOGIN_QR_CODE_TYPE = 1;

    int INVITATION_SUPPLIER_QR_CODE = 2;
    // 邀请添加账号
    int INVITATION_SUB_QR_CODE = 3;




}
