package com.ruoyi.sn;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.sn.model.*;
import com.ruoyi.sn.properties.SnProperties;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class SnQueryClient {

    private static final String BASE_URL = "https://data.06api.com/api.php";

    private SnProperties snProperties;

    public SnQueryClient(final SnProperties snProperties) {
        this.snProperties = snProperties;
    }

    /**
     * 通用查询方法
     */
    private <T extends WarrantyResult> T query(String type, String sn, Class<T> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Map<String, Object> params = new HashMap<>();
        params.put("key", snProperties.getApiKey());
        params.put("type", type);
        params.put("sn", sn);
        log.info("请求URL: {},请求参数：{}", BASE_URL, JacksonUtil.toJson(params));
        String response = HttpUtil.get(BASE_URL, params);
        log.info("返回结果: {}", response);
        ApiResponse<T> result = (ApiResponse) JacksonUtil.parseGeneric(response, ApiResponse.class, clazz);
        if (result.getCode() == 0) {
            return result.getData();
        } else {
            if (Objects.equals(result.getCode(), 611)) {
                T t = clazz.getDeclaredConstructor().newInstance();
                t.setExits(Boolean.FALSE);
                return t;
            }
            if (Objects.equals(result.getCode(), 603)) {
                log.info("资金账户余额不足，");
                T t = clazz.getDeclaredConstructor().newInstance();
                t.setExits(Boolean.FALSE);
                return t;
            }
            log.info("鸭宝接口提示:{}", result.getCode());
            T t = clazz.getDeclaredConstructor().newInstance();
            t.setExits(Boolean.FALSE);
            return t;
        }

    }

    // ==================== 品牌查询方法 ====================

    public AppleResult queryApple(String snOrImei) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        AppleResult appleWarrantyPro = query("apple_warranty_pro", snOrImei, AppleResult.class);
        appleWarrantyPro.setExits(ObjectUtil.defaultIfNull(appleWarrantyPro.getExits(), true));
        if (appleWarrantyPro.getExits()) {
            appleWarrantyPro.setModel(appleWarrantyPro.getModel() + " " + appleWarrantyPro.getColor() + " " + appleWarrantyPro.getStorage());
        }
        return appleWarrantyPro;
    }

    public HuaweiResult queryHuawei(String snOrImei) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        HuaweiResult huawei = query("huawei", snOrImei, HuaweiResult.class);
        huawei.setExits(ObjectUtil.defaultIfNull(huawei.getExits(), true));
        if (huawei.getExits()) {
            huawei.setModel(huawei.getModel() + " " + huawei.getColor() + " " + huawei.getStorage());
        }
        return huawei;
    }


    public OppoResult queryOppo(String snOrImei) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        OppoResult oppo = query("oppo", snOrImei, OppoResult.class);
        oppo.setExits(ObjectUtil.defaultIfNull(oppo.getExits(), true));
        if (oppo.getExits()) {
            oppo.setModel(oppo.getModel() + " " + oppo.getColor() + " " + oppo.getStorage());
        }
        return oppo;
    }

    public VivoResult queryVivo(String snOrImei) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        VivoResult vivo = query("vivo", snOrImei, VivoResult.class);
        vivo.setExits(ObjectUtil.defaultIfNull(vivo.getExits(), true));
        if (vivo.getExits()) {
            vivo.setModel(vivo.getModel() + " " + vivo.getColor() + " " + vivo.getStorage());
        }
        return vivo;
    }

    public HonorResult queryHonor(String snOrImei) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        HonorResult honor = query("honor", snOrImei, HonorResult.class);
        honor.setExits(ObjectUtil.defaultIfNull(honor.getExits(), true));
        if (honor.getExits()) {
            honor.setModel(honor.getModel() + " " + honor.getColor() + " " + honor.getStorage());
        }
        return honor;
    }

    public XiaomiResult queryXiaomi(String snOrImei) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        XiaomiResult xiaomi = query("xiaomi", snOrImei, XiaomiResult.class);
        xiaomi.setExits(ObjectUtil.defaultIfNull(xiaomi.getExits(), true));
        if (xiaomi.getExits()) {
            xiaomi.setModel(xiaomi.getModel() + " " + xiaomi.getColor() + " " + xiaomi.getStorage());
        }
        return xiaomi;
    }


    public WarrantyResult query(String snOrImei, String brand) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (Objects.equals("苹果", brand)) {
            return queryApple(snOrImei);
        } else if (Objects.equals("华为", brand)) {
            return queryHuawei(snOrImei);
        } else if (Objects.equals("小米", brand)) {
            return queryXiaomi(snOrImei);
        } else if (Objects.equals("荣耀", brand)) {
            return queryHonor(snOrImei);
        } else if ("Oppo".equalsIgnoreCase(brand)) {
            return queryOppo(snOrImei);
        } else if ("一加".equalsIgnoreCase(brand)) {
            return queryOppo(snOrImei);
        } else if ("vivo".equalsIgnoreCase(brand)) {
            return queryVivo(snOrImei);
        } else if ("IQOO".equalsIgnoreCase(brand)) {
            return queryVivo(snOrImei);
        }
        throw new ServiceException("暂不支持此品牌");
    }


}