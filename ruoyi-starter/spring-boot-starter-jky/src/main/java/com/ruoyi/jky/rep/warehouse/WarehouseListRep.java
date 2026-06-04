package com.ruoyi.jky.rep.warehouse;

import lombok.Data;

import java.util.List;

@Data
public class WarehouseListRep {

    private List<WarehouseInfoRep> warehouseInfo;

    @Data
    public static class WarehouseInfoRep {

        private String warehouseId;

        private String warehouseCode;

        private String warehouseName;

        private Integer isBlockup;

        private String warehouseTypeCode;

        private String warehouseTypeName;

        private String countryName;

        private String provinceName;

        private String cityName;

        private String townName;

        private String streetName;

        private String address;

        private String tel;

        private String linkMan;

        private Long gmtCreate;

        private Long gmtModified;

        private Integer isDelete;

        private String warehouseDepartId;

        private String warehouseDepartName;

        private String warehouseDepartCode;

        private String warehouseCompanyId;

        private String warehouseCompanyName;

        private String warehouseCompanyCode;

        private String vendId;

        private String vendName;

    }

}
