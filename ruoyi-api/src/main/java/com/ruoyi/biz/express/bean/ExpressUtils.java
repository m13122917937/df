//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ruoyi.biz.express.bean;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.web.vo.express.DetailInfoVO;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public final class ExpressUtils {
    public static final String EXPRESS_CALLBACK_URL = "%s/express/callbackExpress?orderId=1&expressCode=1&expressNo=1&cellphone=1";
    public static final String LOGISTICS_SALES_NODE_STR = "应客户要求,快件正在退回中";
    public static final int EXPRESS_NO_MIN = 6;
    public static final int EXPRESS_NO_MAX = 32;
    public static final String UNDERLINE = "_";
    private static final Integer COLLECT_TIMEOUT = 24;
    private static final Integer APPLY_TIMEOUT_TIME = 22;

    public static Boolean applyTimeout(Date shipmentsTime) {
        Date nowDate = DateUtil.date();
        return DateUtil.between(shipmentsTime, nowDate, DateUnit.HOUR, true) >= (long)APPLY_TIMEOUT_TIME;
    }

    public static Boolean collectTimeout(Date startTime, Date endTime) {
        return DateUtil.between(startTime, endTime, DateUnit.HOUR, true) >= (long)COLLECT_TIMEOUT;
    }

    public static String phoneCutOut(String cellphone) {
        if (StrUtil.isBlank(cellphone)) {
            return null;
        } else {
            String phone = ReUtil.getGroup0(PatternPool.MOBILE, cellphone);
            if (Objects.isNull(phone)) {
                phone = cellphone.replaceAll(" ", "").replaceAll("[\"'\\[\\]]+", "");
            }

            return phone;
        }
    }

    public static String returnExpressNo(String expressNo) {
        return expressNo.replaceAll("[^((?!\\da-zA-Z).)*]", "");
    }

    public static void checkExpressNo(String expressNo) {
        if (expressNo.length() >= 6 && expressNo.length() <= 32) {
            if (Validator.isMatchRegex(PatternPool.GENERAL, expressNo)) {
                if (StrUtil.contains(expressNo, "_")) {
                    throw new ServiceException(StrUtil.format("录入快递单号[{}]格式不正确", new Object[]{expressNo}));
                }
            } else {
                throw new ServiceException(StrUtil.format("录入快递单号[{}]格式不正确", new Object[]{expressNo}));
            }
        } else {
            throw new ServiceException("填写快递单号[" + expressNo + "]不支持");
        }
    }

    public static DetailInfoVO defaultItem(Date shipTime) {
        DetailInfoVO item = new DetailInfoVO();
        item.setContext("已下单");
        item.setFtime(DateUtil.formatDateTime(shipTime));
        return item;
    }

    private ExpressUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
