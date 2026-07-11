package com.ruoyi.user.model.consts;

public interface UserRedisKey {


    String ROOT_KEY = "fy:user:";

    interface Pay {

        String ROOT_KEY = "PAY";

        String CLIENT = "CLIENT";

        String NOTIFY_PROCESS = "NOTIFY_PROCESS";

        String NOTIFY_RESULT = "NOTIFY_RESULT";
    }

    String USER_LOGIN_QR_CODE = ROOT_KEY + "USER_LOGIN_QR_CODE";
}
