# 吉客云开放平台签名生成说明

## 概述

本文档详细说明了调用吉客云开放平台API时，如何生成请求签名(`sign`)。签名机制用于确保请求的合法性和数据的完整性。

---

## 签名生成步骤

### 第一步：准备参数字符串

将除 `sign` 和 `contextid` 参数外的所有 **“参数名 + 参数值”** 进行**字典排序**，然后拼接成一个字符串。

**具体要求：**
- **参与参数**：所有公共请求参数（如 `appkey`, `bizcontent`, `method`, `timestamp`, `version` 等），但**不包括** `sign` 和 `contextid`
- **排序规则**：按参数名的ASCII码升序排序
- **字符编码**：UTF-8

> **⚠️ 重要提示**：
> 为了避免特殊符号造成签名错误，在传输业务参数 `bizcontent` 时，务必**先将其进行URL编码**之后再传输。但是，**参与签名的 `bizcontent` 仍然是未编码之前的原始JSON字符串**。

### 第二步：添加AppSecret并MD5加密

1. **获取AppSecret**：您的应用密钥
2. **加到首尾**：将 `AppSecret` 加到上一步生成的字符串的**最开头**和**最末尾**
3. **转小写**：将整个字符串转换为**小写**
4. **MD5加密**：对转换小写后的字符串进行MD5加密，得到的值即为最终的 `sign`

---

## 完整计算示例

### 示例数据

假设：
- **AppSecret**：`f0d1483f1f2e49cea3dfd48c8d7e3c23`
- **公共参数**如下：

| 参数 | 值 |
| :--- | :--- |
| `method` | `erp.goods.skuimport` |
| `appkey` | `187657` |
| `version` | `v1.0` |
| `contenttype` | `json` |
| `timestamp` | `2019-07-30 15:18:30` |
| `bizcontent` | `{"brandName":"衣服","goodsNo":"H06-2"}` |

### 步骤详解

#### 1. 排序并拼接参数

将参数按参数名ASCII码排序后，拼接成如下字符串（此处为方便阅读添加了逗号，实际字符串中**没有逗号**）：

`appkey187657` + `bizcontent{"brandName":"衣服","goodsNo":"H06-2"}` + `contenttypejson` + `methoderp.goods.skuimport` + `timestamp2019-07-30 15:18:30` + `versionv1.0`

**实际字符串（无逗号）：**