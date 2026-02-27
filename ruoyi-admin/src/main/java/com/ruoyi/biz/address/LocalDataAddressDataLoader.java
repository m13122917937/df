package com.ruoyi.biz.address;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.ruoyi.common.utils.JacksonUtil;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class LocalDataAddressDataLoader implements AddressDataLoader {

    private final List<Address> addressList;

    public LocalDataAddressDataLoader() {
        URL url = ResourceUtil.getResource("areaData.json");
        String jsonData = FileUtil.readString(url.toString(), Charset.defaultCharset());
        this.addressList = JacksonUtil.parseList(jsonData,Address.class);
    }

    @Override
    public List<Address> loadData() {
        return addressList;
    }

}
