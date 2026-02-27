package com.ruoyi.user.model.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

public interface UserRedisKey {


    String ROOT_KEY = "fy:user:";

    interface Pay {

        String ROOT_KEY = "PAY";

        String CLIENT = "CLIENT";

        String NOTIFY_PROCESS = "NOTIFY_PROCESS";

        String NOTIFY_RESULT = "NOTIFY_RESULT";
    }


    @Getter
    @AllArgsConstructor
    enum USER_KEY {

        USER_LOGIN_KEY(ROOT_KEY + "USER_LOGIN_QR_CODE", 30, TimeUnit.MINUTES);


        private String key;

        private long timeout;

        private TimeUnit timeUnit;

    }
}
