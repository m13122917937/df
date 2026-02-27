package com.ruoyi.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DictEnum {


    WEB_HOOK_FOLLOW_ORDER("webhook", "FOLLOW_ORDER"),

    WEB_HOOK_AFTER_ORDER("webhook", "AFTER_ORDER"),

    WEB_HOOK_CANCELLATION_ORDER("webhook", "AFTER_ORDER");


    String value;

    String label;

}
