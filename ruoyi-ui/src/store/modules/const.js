import { getProvinceValue } from "@/utils/methods/common";
import { getProvinces } from "@/api/address";
import store from "@/store";

// 订单状态枚举
const ORDER_STATUS = {
  NEW: { value: 1, label: "新建中" },
  WAIT: { value: 2, label: "待发布" },
  TRADING: { value: 3, label: "报价中" },
  DELIVERY_ING: { value: 4, label: "发货中" },
  DELIVERY_END: { value: 5, label: "当日发货" },
  TRANSIT: { value: 6, label: "在途" },
  CHASE_ORDER: { value: 7, label: "追单" },
  ERROR: { value: 8, label: "异常订单" },
  ENDING: { value: 9, label: "已完成" },
  REVOKE: { value: 10, label: "撤销" }
};

let getProvinceCount = 0;

// 获取省份
export async function getProvince(status = null) {
  let area = store.state.globalData.areas;
  if (!getProvinceCount) area = "";
  if (!area) {
    // 根据状态参数构建请求参数
    const params = status ? { status } : {};
    let res = await getProvinces(params)
    // let res = {
    //   code: 200,
    //   msg: "操作成功",
    //   data: [
    //     {
    //       districtId: "110000",
    //       district: "北京",
    //     },
    //     {
    //       districtId: "120000",
    //       district: "天津",
    //     },
    //     {
    //       districtId: "130000",
    //       district: "河北省",
    //     },
    //     {
    //       districtId: "140000",
    //       district: "山西省",
    //     },
    //     {
    //       districtId: "150000",
    //       district: "内蒙古自治区",
    //     },
    //     {
    //       districtId: "210000",
    //       district: "辽宁省",
    //     },
    //     {
    //       districtId: "220000",
    //       district: "吉林省",
    //     },
    //     {
    //       districtId: "230000",
    //       district: "黑龙江省",
    //     },
    //     {
    //       districtId: "310000",
    //       district: "上海",
    //     },
    //     {
    //       districtId: "320000",
    //       district: "江苏省",
    //     },
    //     {
    //       districtId: "330000",
    //       district: "浙江省",
    //     },
    //     {
    //       districtId: "340000",
    //       district: "安徽省",
    //     },
    //     {
    //       districtId: "350000",
    //       district: "福建省",
    //     },
    //     {
    //       districtId: "360000",
    //       district: "江西省",
    //     },
    //     {
    //       districtId: "370000",
    //       district: "山东省",
    //     },
    //     {
    //       districtId: "410000",
    //       district: "河南省",
    //     },
    //     {
    //       districtId: "420000",
    //       district: "湖北省",
    //     },
    //     {
    //       districtId: "430000",
    //       district: "湖南省",
    //     },
    //     {
    //       districtId: "440000",
    //       district: "广东省",
    //     },
    //     {
    //       districtId: "450000",
    //       district: "广西壮族自治区",
    //     },
    //     {
    //       districtId: "460000",
    //       district: "海南省",
    //     },
    //     {
    //       districtId: "500000",
    //       district: "重庆",
    //     },
    //     {
    //       districtId: "510000",
    //       district: "四川省",
    //     },
    //     {
    //       districtId: "520000",
    //       district: "贵州省",
    //     },
    //     {
    //       districtId: "530000",
    //       district: "云南省",
    //     },
    //     {
    //       districtId: "540000",
    //       district: "西藏自治区",
    //     },
    //     {
    //       districtId: "610000",
    //       district: "陕西省",
    //     },
    //     {
    //       districtId: "620000",
    //       district: "甘肃省",
    //     },
    //     {
    //       districtId: "630000",
    //       district: "青海省",
    //     },
    //     {
    //       districtId: "640000",
    //       district: "宁夏回族自治区",
    //     },
    //     {
    //       districtId: "650000",
    //       district: "新疆维吾尔自治区",
    //     },
    //     {
    //       districtId: "710000",
    //       district: "台湾省",
    //     },
    //     {
    //       districtId: "810000",
    //       district: "香港特别行政区",
    //     },
    //     {
    //       districtId: "820000",
    //       district: "澳门特别行政区",
    //     },
    //   ],
    //   traceId: "1757755663919",
    // };
    if (res && res.code === 200 && res.data) {
      area = res.data.map((o) => {
        return {
          ...o,
          value: o.districtId,
          label: getProvinceValue(o.district),
        };
      });
      
      // 根据状态添加特殊选项
      if (status) {
        const statusInfo = Object.values(ORDER_STATUS).find(s => s.value === status);
        if (statusInfo) {
          area.unshift({ 
            value: 1, 
            label: statusInfo.label === "在途" ? "醒市无仓" : `醒市无仓(${statusInfo.label})` 
          });
        } else {
          area.unshift({ value: 1, label: "醒市无仓" });
        }
      } else {
        area.unshift({ value: 1, label: "醒市无仓" });
      }
    }
  }
  getProvinceCount++;
  return area;
}

// 导出订单状态枚举供其他模块使用
export { ORDER_STATUS };
