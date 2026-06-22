# 查询合同模板中控件详情

> 更新时间：2026-06-11 11:47:13

## 接口描述
通过合同模板ID（`docTemplateId`）获取模板中设置的所有控件信息，用于后续通过【填写模板生成文件】接口填充模板。

## 接口地址与请求方法
- **接口地址：** `https://{host}/v3/doc-templates/{docTemplateId}`
- **请求方法：** `GET`

## 请求头格式
具体请求头参数，请查看公共请求头格式。

## 请求参数 (Path)
| 参数名称 | 参数类型 | 必选 | 参数说明 |
| :--- | :--- | :--- | :--- |
| `docTemplateId` | string | 是 | 合同模板ID（通过【获取制作文件模板页面链接】接口获取）。 |

## 响应参数
| 参数名称 | 参数类型 | 必选 | 参数说明 |
| :--- | :--- | :--- | :--- |
| `code` | int32 | 是 | 业务码，`0` 表示成功。 |
| `message` | string | 否 | 业务信息，请根据 `code` 判断错误。 |
| `data` | object | 否 | 业务数据对象 |
| `data.docTemplateId` | string | 否 | 合同模板ID。 |
| `data.docTemplateName` | string | 否 | 合同模板名称。 |
| `data.createTime` | int64 | 否 | 模板创建时间（Unix时间戳，毫秒）。 |
| `data.updateTime` | int64 | 否 | 模板更新时间（Unix时间戳，毫秒）。 |
| `data.fileDownloadUrl` | string | 否 | 模板底稿PDF下载链接（有效期60分钟）。**HTML模板返回 `null`**。 |
| `data.components` | array | 否 | 模板中的控件列表信息。 |
| `data.components[].componentId` | string | 否 | 控件ID（系统生成）。 |
| `data.components[].componentKey` | string | 否 | 控件Key（用户自定义）。 |
| `data.components[].componentName` | string | 否 | 控件名称。 |
| `data.components[].required` | boolean | 否 | 控件是否必填（`true`/`false`）。 |
| `data.components[].componentType` | int32 | 否 | **控件类型**。<br> 1-单行文本，2-数字，3-日期，6-签章区域，8-多行文本，9-复选，10-单选，11-图片，14-下拉框，15-勾选框，16-身份证，17-备注区域，18-动态表格，19-手机号，21-签署日期，23-人民币大写（关联数字控件）。 |
| `data.components[].componentPosition` | object | 否 | 控件位置信息。 |
| `...componentPositionX` | float | 否 | X横坐标。 |
| `...componentPositionY` | float | 否 | Y纵坐标。 |
| `...componentPageNum` | int32 | 否 | 所在页码。 |
| `data.components[].componentSize` | object | 否 | 控件尺寸（宽度/高度，单位px）。 |
| `data.components[].componentSpecialAttribute` | object | 否 | 控件特有属性（详见下方说明）。 |

### componentSpecialAttribute 对象说明
| 参数名称 | 类型 | 说明 |
| :--- | :--- | :--- |
| `dateFormat` | string | 日期格式（日期控件特有）。 |
| `imageType` | string | 图片类型（图片控件特有）：`IDCard_widthwise`(横向), `IDCard_longitudinal`(纵向), `other`(自由)。 |
| `options` | array | 选项列表（下拉框/单选/复选控件特有），包含 `optionContent`(内容), `optionOrder`(顺序), `selected`(是否默认选中)。 |
| `tableContent` | array | 表格行列内容（动态表格特有），格式：`[{"column1":"value1","column2":"value2"}]`。 |
| `numberFormat` | string | 数字格式（数字控件特有）：`0`(整数), `0.0`(一位小数), `0.00`(两位小数)。 |
| `componentMaxLength` | string | 可填充长度上限（单位：中文字符，1中文=2英文），仅PDF模板返回。 |
| `componentMaxRows` | string | 多行文本行数上限，仅PDF模板返回。 |
| `signerRole` | string | 签署方角色标识（签署区控件）。 |
| `tickComponentType` | string | 勾选控件样式：`tick`(勾), `cross`(叉), `unspecified`(不限制)。 |
| `componentAssociatedId` | list | 关联控件ID（如人民币大写关联的数字控件ID）。 |

### normalSignField（签章区属性）说明
| 参数名称 | 类型 | 说明 |
| :--- | :--- | :--- |
| `showSignDate` | int32 | 是否显示签署日期：`0`-不显示，`1`-显示。 |
| `dateFormat` | string | 日期格式（如 `yyyy-MM-dd`）。 |
| `signFieldStyle` | int32 | 签章样式：`1`-单页签章，`2`-骑缝签章。 |
| `sealSpecs` | int32 | 落章规则：`1`-以实际印章规格，`2`-自定义规格适配。 |

### remarkSignField（备注区属性）说明
| 参数名称 | 类型 | 说明 |
| :--- | :--- | :--- |
| `inputType` | int32 | 输入方式：`1`-手写抄录，`2`-自由输入。 |
| `aiCheck` | int32 | AI校验：`0`-不开启，`1`-开启，`2`-强制。 |
| `remarkContent` | string | 预设手写抄录信息。 |
| `remarkFontSize` | string | 字号（单位pt，默认12pt）。 |

### componentTextFormat（控件字符样式）说明
| 参数名称 | 类型 | 说明 |
| :--- | :--- | :--- |
| `font` | int32 | 字体：`1`-宋体, `2`-新宋体, `4`-黑体, `5`-楷体。 |
| `fontSize` | float | 字号（默认12.0）。 |
| `textColor` | string | 颜色（默认 `#000000`）。 |
| `bold` | boolean | 是否加粗。 |
| `italic` | boolean | 是否斜体。 |
| `horizontalAlignment` | string | 水平对齐：`LEFT`, `CENTER`, `RIGHT`。 |
| `verticalAlignment` | string | 垂直对齐：`TOP`, `MIDDLE`, `BOTTOM`（多行文本）。 |
| `textLineSpacing` | float | 行间距（默认1.0，最大2.0）。 |

## 请求示例
```http
GET https://openapi.esign.cn/v3/doc-templates/061778***701b7