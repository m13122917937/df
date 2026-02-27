
package com.ruoyi.express.model.consts;

import java.util.Arrays;

public enum LogisticsCode {
    SHUNFENG("shunfeng", "顺丰快递", 1),
    SHENTONG("shentong", "申通", 14),
    YUANTONG("yuantong", "圆通", 5),
    EMS("ems", "邮政", 15),
    YUNDA("yunda", "韵达", 4),
    TIANTIAN("tiantian", "天天", 8),
    JD("jd", "京东", 17),
    ZHONGTONG("zhongtong", "中通", 3),
    DE_BANG("debangwuliu", "德邦", 16),
    ZS("zs", "自送(仓内调拨)", (Integer) null);

    private final String code;
    private final String msg;
    private final Integer ecssCode;

    public static String getCodeByMsg(String msg) {
        LogisticsCode[] logisticsCodes = values();
        LogisticsCode[] var2 = logisticsCodes;
        int var3 = logisticsCodes.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            LogisticsCode logisticsCode = var2[var4];
            if (msg.equals(logisticsCode.getMsg())) {
                return logisticsCode.getCode();
            }
        }

        return null;
    }

    public static LogisticsCode getByEcssCode(final Integer ecssCode) {
        return (LogisticsCode) Arrays.stream(values()).filter((i) -> {
            return i.getEcssCode().equals(ecssCode);
        }).findFirst().orElse(null);
    }

    public static LogisticsCode getByCode(final String code) {
        return (LogisticsCode) Arrays.stream(values()).filter((i) -> {
            return i.getCode().equals(code);
        }).findFirst().orElse(null);
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public Integer getEcssCode() {
        return this.ecssCode;
    }

    private LogisticsCode(final String code, final String msg, final Integer ecssCode) {
        this.code = code;
        this.msg = msg;
        this.ecssCode = ecssCode;
    }
}
