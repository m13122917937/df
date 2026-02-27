const commodity = {
  state: {
    commodityInfo: {
      product: {}
      /* product: {
        createTime: '2024-01-07 15:28:38',
        updateTime: '2024-01-07 15:28:38',
        createBy: 1,
        updateBy: 1,
        seriesId: 80,
        seriesName: 'Y系列',
        categoryId: 1207,
        categoryPath: '手机通讯/手机/手机',
        brandId: 7,
        brandName: 'vivo',
        id: 20675,
        productCode: 'XS248WA282GX4',
        productName: 'vivo Y11',
        productShortName: 'vivo Y11',
        price: 21000,
        productStatus: 1,
        productStatusName: '正常',
        productPic: '/2024/01/07/9687fffe-4f9a-4991-aac7-d1e301a20ee9.jpg',
        unit: '个'
      },
      productImageList: [
        {
          id: 200,
          productId: 20675,
          imageUrl: '/2024/01/07/9687fffe-4f9a-4991-aac7-d1e301a20ee9.jpg',
          isMaster: 1,
          sortOrder: 1,
          createTime: '2024-01-07 15:28:38',
          updateTime: '2024-01-07 15:28:38'
        },
        { imageUrl: '/2024/01/12/1e7c71b6-b2d9-42dc-9229-23e5428f8da9.jpeg' },
        { imageUrl: '/2024/01/12/8d3f1a32-d4c6-48a5-b598-b6ee79ac41e1.jpeg' },
        { imageUrl: '/2024/01/12/2acc8bba-93ea-4470-a90c-a1c25feae19b.png' },
        { imageUrl: '/2024/01/12/f6dc93e0-e95b-48c8-876c-effbc4135521.png' }
      ],
      productArgumentList: [
        {
          attrId: 5919063,
          attrName: '套餐类型',
          inputType: 'select',
          attrType: 2,
          attrValueId: '94648647',
          attrValueName: '5框',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 20930,
          attrName: '售后服务',
          inputType: 'select',
          attrType: 2,
          attrValueId: '60513',
          attrValueName: '店铺保修',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 122276315,
          attrName: '款式',
          inputType: 'select',
          attrType: 2,
          attrValueId: '3273240',
          attrValueName: '背心',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 148784156,
          attrName: '上市时间',
          inputType: 'input',
          attrType: 2,
          attrValueId: '-1',
          attrValueName: 'other',
          extendValue: {},
          attrUnit: { other: '年月日' },
          isExtend: 0
        },
        {
          attrId: 46742100,
          attrName: '增值服务',
          inputType: 'select',
          attrType: 2,
          attrValueId: '',
          attrValueName: '',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 216452309,
          attrName: 'CPU型号',
          inputType: 'select',
          attrType: 2,
          attrValueId: '17212363633',
          attrValueName: '骁龙8 Gen 1',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 227400731,
          attrName: '充电功率',
          inputType: 'select',
          attrType: 2,
          attrValueId: '-1',
          attrValueName: 'other',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 122216427,
          attrName: '屏幕刷新率',
          inputType: 'select',
          attrType: 2,
          attrValueId: '49385730',
          attrValueName: '300HZ',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 11999837,
          attrName: '后置摄像头',
          inputType: 'select',
          attrType: 2,
          attrValueId: '26439691',
          attrValueName: '1900万',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 148794100,
          attrName: '屏幕尺寸',
          inputType: 'input',
          attrType: 2,
          attrValueId: '-1',
          attrValueName: 'other',
          extendValue: {},
          attrUnit: { other: '英寸' },
          isExtend: 0
        },
        {
          attrId: 149128818,
          attrName: '电池容量',
          inputType: 'input',
          attrType: 2,
          attrValueId: '12217299',
          attrValueName: '5000mAh',
          extendValue: {},
          attrUnit: { '30000mAh': '', '5000mAh': 'mAh' },
          isExtend: 0
        },
        {
          attrId: 190084097,
          attrName: '机身厚度',
          inputType: 'input',
          attrType: 2,
          attrValueId: '',
          attrValueName: '',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 29656,
          attrName: '分辨率',
          inputType: 'select',
          attrType: 2,
          attrValueId: '33098',
          attrValueName: '不支持',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 122216452,
          attrName: '操作系统',
          inputType: 'select',
          attrType: 2,
          attrValueId: '282850346',
          attrValueName: 'Larena 3.0',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 17026407,
          attrName: '最大光圈',
          inputType: 'select',
          attrType: 2,
          attrValueId: '3264982',
          attrValueName: 'F2.0',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 139248429,
          attrName: 'CPU品牌',
          inputType: 'select',
          attrType: 2,
          attrValueId: '21420',
          attrValueName: '苹果',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 14543160,
          attrName: '屏幕材质',
          inputType: 'select',
          attrType: 2,
          attrValueId: '57629644',
          attrValueName: 'AMOLED',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 31028,
          attrName: '蓝牙版本',
          inputType: 'select',
          attrType: 2,
          attrValueId: '6316475',
          attrValueName: '5.4',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 122276283,
          attrName: '屏幕类型',
          inputType: 'select',
          attrType: 2,
          attrValueId: '191738738',
          attrValueName: '折叠屏',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 227041887,
          attrName: '指纹识别方式',
          inputType: 'checkbox',
          attrType: 2,
          attrValueId: [],
          attrValueName: [],
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 227053107,
          attrName: '后壳材质',
          inputType: 'input',
          attrType: 2,
          attrValueId: '',
          attrValueName: '',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 122216579,
          attrName: '耳机插头类型',
          inputType: 'select',
          attrType: 2,
          attrValueId: '3236313',
          attrValueName: '3.5mm',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 227055949,
          attrName: '是否支持无线充电',
          inputType: 'select',
          attrType: 2,
          attrValueId: '',
          attrValueName: '',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 29029,
          attrName: '接口类型',
          inputType: 'checkbox',
          attrType: 2,
          attrValueId: [126972, 126973, '27127830334', '101489'],
          attrValueName: ['佳能口', '宾得FA 645口'],
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 227094505,
          attrName: '是否支持NFC',
          inputType: 'select',
          attrType: 2,
          attrValueId: '',
          attrValueName: '',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 251049400,
          attrName: '前置摄像头像素',
          inputType: 'input',
          attrType: 2,
          attrValueId: 116821,
          attrValueName: '1000',
          extendValue: {},
          attrUnit: { 1000: '万像素' },
          isExtend: 0
        },
        {
          attrId: 272022963,
          attrName: '商品系列',
          inputType: 'select',
          attrType: 2,
          attrValueId: '20213',
          attrValueName: '其他',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 31942071,
          attrName: '网络模式',
          inputType: 'checkbox',
          attrType: 2,
          attrValueId: [126180, 126179, '-1', '525058184'],
          attrValueName: ['双卡双待单通', 'other'],
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 46280669,
          attrName: '电信设备进网许可证编号',
          inputType: 'input',
          attrType: 2,
          attrValueId: '-1',
          attrValueName: 'other',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 33187,
          attrName: '核心数',
          inputType: 'select',
          attrType: 2,
          attrValueId: '3273422',
          attrValueName: '双核',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 31922181,
          attrName: '摄像头类型',
          inputType: 'select',
          attrType: 2,
          attrValueId: '',
          attrValueName: '',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 31910981,
          attrName: '手机类型',
          inputType: 'checkbox',
          attrType: 2,
          attrValueId: [126226],
          attrValueName: ['4G手机'],
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 170390721,
          attrName: '版本是否包含中国大陆',
          inputType: 'select',
          attrType: 2,
          attrValueId: '21958',
          attrValueName: '是',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 232561237,
          attrName: '保修期',
          inputType: 'input',
          attrType: 2,
          attrValueId: '',
          attrValueName: '',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 10004,
          attrName: '网络类型',
          inputType: 'select',
          attrType: 2,
          attrValueId: '17446870',
          attrValueName: '联通3G',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 122216431,
          attrName: '版本类型',
          inputType: 'select',
          attrType: 2,
          attrValueId: '27409',
          attrValueName: '美国',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        },
        {
          attrId: 11111111111,
          attrName: '3C证书编号',
          inputType: 'input',
          attrType: 2,
          attrValueId: '',
          attrValueName: '',
          extendValue: {},
          attrUnit: {},
          isExtend: 0
        }
      ],
      productAttrImageList: [],
      attrImageList: [],
      salesAttrList: [
        {
          isRequired: 1,
          isColor: 1,
          attrId: 1627207,
          attrName: '颜色分类',
          attrType: 1,
          attrValueIdName: [{ attrValueId: '130164', attrValueName: '花色' }]
        },
        {
          isRequired: 0,
          isColor: 0,
          attrId: 12304035,
          attrName: '存储容量',
          attrType: 1,
          attrValueIdName: [
            { attrValueId: '21485', attrValueName: '64MB', canEdit: false }
          ]
        },
        {
          isRequired: 0,
          isColor: 0,
          attrId: 10004,
          attrName: '网络类型',
          attrType: 1,
          attrValueIdName: [
            {
              attrValueId: '1162302684',
              attrValueName: '移动4G+/联通4G+/电信4G+'
            }
          ]
        },
        {
          isRequired: 0,
          isColor: 0,
          attrId: 5919063,
          attrName: '套餐类型',
          attrType: 1,
          attrValueIdName: [
            { attrValueId: '3284565', attrValueName: '套餐六' }
          ]
        },
        {
          isRequired: 0,
          isColor: 0,
          attrId: 122216431,
          attrName: '版本类型',
          attrType: 1,
          attrValueIdName: [{ attrValueId: '27023', attrValueName: '日本' }]
        }
      ],
      skuList: [
        {
          10004: '移动4G+/联通4G+/电信4G+',
          1627207: '花色',
          5919063: '套餐六',
          12304035: '64MB',
          122216431: '日本',
          '1627207Id': '130164',
          '12304035Id': '21485',
          '10004Id': '1162302684',
          '5919063Id': '3284565',
          '122216431Id': '27023',
          saleProps:
            '颜色分类:花色;存储容量:64MB;网络类型:移动4G+/联通4G+/电信4G+;套餐类型:套餐六;版本类型:日本',
          specName: '花色-64MB-移动4G+/联通4G+/电信4G+-套餐六-日本',
          skuProps:
            '1627207:130164;12304035:21485;10004:1162302684;5919063:3284565;122216431:27023',
          skuImageList: [],
          stockNum: '10000',
          price: '10000',
          discountPrice: '10000',
          groupPrice: '10000',
          skuCode: 'XS248WA282GX407'
        }
      ],
      imgConfig: [
        {
          picAttrValueId: 'mainImg',
          label: '商品主图',
          btn: { text: '一键复用致所有SKU图片' },
          remark:
            '建议上传一张主图，尺寸至少为800px*800px，仅支持jpgjpegpng格式，可拖拽图片调整排序',
          images: [
            {
              id: 200,
              productId: 20675,
              imageUrl: '/2024/01/07/9687fffe-4f9a-4991-aac7-d1e301a20ee9.jpg',
              isMaster: 1,
              sortOrder: 1,
              createTime: '2024-01-07 15:28:38',
              updateTime: '2024-01-07 15:28:38'
            },
            {
              imageUrl: '/2024/01/12/1e7c71b6-b2d9-42dc-9229-23e5428f8da9.jpeg'
            },
            {
              imageUrl: '/2024/01/12/8d3f1a32-d4c6-48a5-b598-b6ee79ac41e1.jpeg'
            },
            {
              imageUrl: '/2024/01/12/2acc8bba-93ea-4470-a90c-a1c25feae19b.png'
            },
            {
              imageUrl: '/2024/01/12/f6dc93e0-e95b-48c8-876c-effbc4135521.png'
            }
          ],
          event: {
            _custom: {
              type: 'function',
              display:
                '<span style="opacity:.5;">function</span> bound copyAllPic()',
              tooltip: '<pre>function () { [native code] }</pre>',
              _reviveId: 17
            }
          }
        },
        {
          picAttrValueId: '130164',
          attrId: 1627207,
          label: '花色',
          btn: { text: '使用商品主图' },
          remark:
            '选填，若需要发布至电商平台，则需要上传图片（尽可能适配各大电商平台规范要求），尺寸至少为800px*800px，仅支持jpgjpegpng格式，可拖拽图片调整排序',
          images: [
            {
              id: 200,
              productId: 20675,
              imageUrl: '/2024/01/07/9687fffe-4f9a-4991-aac7-d1e301a20ee9.jpg',
              isMaster: 1,
              sortOrder: 1,
              createTime: '2024-01-07 15:28:38',
              updateTime: '2024-01-07 15:28:38'
            },
            {
              imageUrl: '/2024/01/12/1e7c71b6-b2d9-42dc-9229-23e5428f8da9.jpeg'
            },
            {
              imageUrl: '/2024/01/12/8d3f1a32-d4c6-48a5-b598-b6ee79ac41e1.jpeg'
            },
            {
              imageUrl: '/2024/01/12/2acc8bba-93ea-4470-a90c-a1c25feae19b.png'
            },
            {
              imageUrl: '/2024/01/12/f6dc93e0-e95b-48c8-876c-effbc4135521.png'
            }
          ],
          event: {
            _custom: {
              type: 'function',
              display:
                '<span style="opacity:.5;">function</span> bound useMainPic()',
              tooltip: '<pre>function () { [native code] }</pre>',
              _reviveId: 18
            }
          }
        }
      ],
      productTitle: '123' */
    },
    updateCommodityPic: false,
    saleOptions: [],
    editStatus: "add"
  },
  mutations: {
    SET_COMMODITY_INFO: (state, data) => {
      state.commodityInfo = data
    },
    SET_SALE_OPTIONS: (state, data) => {
      state.saleOptions = data
    },
    NOTICE_COMMODITY_PIC: state => {
      state.updateCommodityPic = true
      setTimeout(() => {
        state.updateCommodityPic = false
      }, 300)
    },
    SET_EDIT_STATUS: (state, status) => {
      console.log(status, "status")
      state.editStatus = status
    }
  },
  actions: {
    setCommodityInfo({ commit }, data) {
      commit("SET_COMMODITY_INFO", data)
    },
    noticeCommodityPic({ commit }) {
      commit("NOTICE_COMMODITY_PIC")
    },
    setSaleOptions({ commit }, data) {
      commit("SET_SALE_OPTIONS", data)
    }
  }
}
export default commodity
