package com.ruoyi.order.model.consts;


import com.ruoyi.common.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public interface TabConsts {


    @Getter
    @AllArgsConstructor
    enum TabType {

        APPLE_PHONE("苹果", "苹果", List.of()),
        HW_PHONE("华为", "华为", List.of()),
        HONOR_PHONE("荣耀", "荣耀", List.of()),
        MI_PHONE("小米", "小米", List.of()),
        OPPO_PHONE("OPPO", "OPPO", List.of()),
        VIVO_PHONE("VIVO", "VIVO", List.of()),
        ;

        private String tabName;
        private String brand;
        private List<String> categoryList;


        public static TabType getTabType(String tabName) {
            for (TabType value : values()) {
                if (value.tabName.equals(tabName)) {
                    return value;
                }
            }
            throw new ServiceException("枚举不存在");
        }
    }

}
