package com.ruoyi.biz.address;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 地址数据加载器
 */
public interface AddressDataLoader {

    List<Address> loadData();

    @Data
    @Accessors(chain = true)
    class Address {
        private String id;
        private String name;
        private String code;
        private Serializable parentId;
        private List<Address> children;
    }
}
