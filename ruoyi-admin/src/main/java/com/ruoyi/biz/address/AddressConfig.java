package com.ruoyi.biz.address;

import com.ruoyi.biz.sys.IDictDistrictBizService;
import com.ruoyi.system.model.bo.DictDistrictBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AddressConfig {
    @Autowired
    IDictDistrictBizService dictDistrictBizService;

    @Bean
    public SmartParse smartParse(AddressDataLoader addressDataLoader) {
        return new SmartParse(addressDataLoader);
    }

    @Bean
    @Autowired
    public AddressDataLoader addressDataLoader(IDictDistrictBizService dictDistrictBizService) {
        return new AddressDataLoader() {
            @Override
            public List<Address> loadData() {
                List<AddressDataLoader.Address> provinces = new ArrayList<>();
                List<DictDistrictBO> dictDistrictBOS = dictDistrictBizService.listProvince();
                for (DictDistrictBO province : dictDistrictBOS) {
                    Address address = new Address().setCode(province.getDistrictId().toString()).setId(province.getDistrictId().toString()).setName(province.getDistrict()).setChildren(new ArrayList<>());
                    for (DictDistrictBO cityBO : dictDistrictBizService.listCity(province.getDistrictId())) {
                        Address city = new Address().setCode(cityBO.getDistrictId().toString()).setId(cityBO.getDistrictId().toString()).setName(cityBO.getDistrict()).setParentId(cityBO.getPid().toString());
                        address.getChildren().add(city);
                    }
                    provinces.add(address);
                }
                return provinces;
            }
        };
    }
}
