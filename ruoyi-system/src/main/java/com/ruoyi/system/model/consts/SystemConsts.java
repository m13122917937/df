package com.ruoyi.system.model.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class SystemConsts {

    @Getter
    @AllArgsConstructor
    public enum ConfigKey {
        SIMULATE_CONFIG("pay.channel.simulate", "false");

        private final String code;

        private final String defaultValue;
    }


}
