# erp.warehouse.get 开放平台仓库查询

## 接口说明

- **接口名称**: erp.warehouse.get
- **接口类型**: 标准接口
- **授权类型**: 必须授权
- **最近发布时间**: 2025-08-11 09:08:00.0
- **功能描述**: 开放平台仓库查询

---

## 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| method | String | 是 | 32 | 方法名称 | `erp.warehouse.get` |
| appkey | Long | 是 | 32 | 应用编号 | `16242933` |
| version | String | 是 | 32 | 版本号 | `v1.0` |
| contenttype | String | 是 | 32 | 返回格式(暂时只支持json) | `json` |
| timestamp | String | 是 | 32 | 时间 | `2015-01-01 12:00:00` |
| bizcontent | String | 是 | 32 | 业务参数(JSON字符串) | `{"a":"a1","b":"b1"}` |
| contextid | String | 否 | 32 | 上下文报文关联ID(不参与签名) | `131562131` |
| sign | String | 是 | 32 | 签名 | 见签名文档 |
| token | String | 否 | 32 | ISV应用商家授权token(不参与签名) | 见获取token方法 |

## 业务请求参数 (bizcontent)

| 参数 | 类型 | 必填 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| pageIndex | Integer | 是 | 分页页码 | `0` |
| pageSize | Integer | 是 | 分页页数 | `50` |
| code | String | 否 | 仓库编号 | `A0001` |
| name | String | 否 | 仓库名称 | `仓库0001` |
| gmtModifiedStart | String | 否 | 起始修改时间 | `2021-07-27 15:09:18` |
| gmtModifiedEnd | String | 否 | 结束修改时间 | `2019-06-05 17:36:23` |
| includeDeleteAndBlockup | Integer | 否 | 1=返回删除和停用的数据，其它=过滤 | `0` |

### 业务请求参数示例

#### JSON
```json
{
  "code": "A0001",
  "pageIndex": 0,
  "gmtModifiedEnd": "2019-06-05 17:36:23",
  "name": "仓库0001",
  "pageSize": 50,
  "gmtModifiedStart": "2021-07-27 15:09:18",
  "includeDeleteAndBlockup": "0"
}