<template>
  <div>
    <el-dialog
      :visible.sync="visible"
      title="抢单"
      width="800px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :modal-append-to-body="false"
      class="grab-order-dialog"
      @close="handleClose"
    >
      <!-- 质量管理条例弹窗 -->
      <!-- 产品信息区域 -->
      <div class="product-info-section">
        <div class="product-name">{{ productData.productName }} {{ productData.skuName }}</div>

        <div class="order-details">
          <div class="detail-row">
            <span class="detail-label">采购类型：</span>
            <span class="detail-value">{{ productData.purchaseType || 'O2O' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">采购数量：</span>
            <span class="detail-value">{{ productData.quantity }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">收货地址：</span>
            <span class="detail-value">
              {{ productData.receivingAddress }}
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">税票：</span>
            <span class="detail-value">含税(型号对应)</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">账期要求：</span>
            <span class="detail-value">{{ getAccountingPeriodText(productData.accountingPeriod) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">质量要求：</span>
            <span class="detail-value">
              <div v-if="otherRequire.length > 0">
                <el-tag v-for="value in otherRequire" :key="value" class="other-require-tag" size="mini">{{
                  value }}</el-tag>
              </div>
              <div v-else>
                {{ '无特殊要求' }}
              </div>
            </span>
          </div>

        </div>
      </div>

      <!-- 抢单注意事项 -->
      <div class="notice-section">
        <div class="notice-title">
          <i class="el-icon-warning notice-icon" />
          <span>抢单注意事项(您的抢单行为将被视为您接受以下注意事项,请仔细阅读后再抢单)</span>
        </div>
        <div class="notice-content">
          <div class="notice-item">
            <span class="notice-number">1.</span>
            <span class="notice-text">临时扣除[50]元履约保证金，新订单需在当日18:59前上传产品串码和运单号，逾期未上传将取消订单并扣除保证金。</span>
          </div>
          <div class="notice-item">
            <span class="notice-number">2.</span>
            <span class="notice-text">确保产品符合条件（未激活、原封、顺丰发货等），买方不承担审核责任，违规按《<a
              href="#"
              class="notice-link"
              @click="showQualityRegulations"
            >平台商品质量管理条例(新版)</a>》承担相应责任，买方保留限制、暂停或取消抢单权限的权利。</span>
          </div>
          <div class="notice-item">
            <span class="notice-number">3.</span>
            <span class="notice-text">顺丰包邮，禁止到付，禁止包装上出现品牌/平台标识，上传串码需与客户收到串码一致，违规按《<a
              href="#"
              class="notice-link"
              @click="showQualityRegulations"
            >平台商品质量管理条例(新版)</a>》承担相应责任。</span>
          </div>
          <div class="notice-item">
            <span class="notice-number">4.</span>
            <span class="notice-text">乙方需在付款后20个工作日内向甲方开具13%专用增值税发票。</span>
          </div>
          <div v-if="productData.orderStyle === 1" class="notice-item ">
            <span class="notice-number warning-text">5.</span>
            <span class="notice-text warning-text">该订单须使用顺丰微派服务，在派件时，快递小哥会上门激活并拍摄激活照片。</span>
          </div>
        </div>
      </div>

      <!-- 价格选择区域 -->
      <div class="price-section">
        <div
          v-for="option in priceOptions"
          :key="option.time"
          class="price-options"
        >

          <div

            :class="[
              'price-option',
              {
                'selected': selectedObj.time == option.time,
                'flash-sale': option.time == '0',
                'disabled-price': tradePrice && option.price >= tradePrice
              }
            ]"
            @click="selectPrice(option)"
          >
            <div class="time-info">
              <i v-if="option.time !== 'flash'" class="el-icon-time time-icon" />
              <span class="time-text">{{ option.timeText }}</span>
            </div>
            <div class="price-amount">¥{{ option.price }}</div>
          </div>
          <GrabCountdownText
            v-if="option.price == tradePrice"
            :timedata="productData"
            @countdown-end="handleCountdownEnd"
          />
        </div>
      </div>

      <!-- 确认按钮 -->
      <div class="confirm-section">
        <div class="confirm-message">{{ getSelectedTimeText }}</div>
      </div>
      <!-- 底部二维码和最终确认 -->
      <div class="bottom-section">
        <el-button type="primary" class="final-confirm-btn" :disabled="isLoading" @click="tradeGrabOrder">
          确定
        </el-button>
      </div>
    </el-dialog>
    <QualityRegulationsDialog ref="qualityRegulationsDialog" />
  </div>
</template>

<script>
import { apiGetSkuOrderInfo, apiTradeGrabOrder } from '@/api/tradingMarket'
import QualityRegulationsDialog from '@/components/QualityRegulationsDialog'
import GrabCountdownText from './GrabCountdownText.vue'
export default {
  name: 'GrabTheOrder',
  components: {
    QualityRegulationsDialog,
    GrabCountdownText
  },
  filters: {
    setAddress(data) {
      if (!data) return ''
      return data.substring(0, 2)
    }
  },
  data() {
    return {
      visible: false,
      isLoading: false,
      selectedObj: {},
      productData: {},
      otherRequire: [],
      priceOptions: [],
      tradePrice: 0
    }
  },
  computed: {
    // 获取当前选中价格选项的时间文本
    getSelectedTimeText() {
      let text = ''
      if (this.selectedObj.time === '15') {
        text = '确认后, 倒计时15分钟内, 无人再报价则成交'
      } else if (this.selectedObj.time === '10') {
        text = '确认后, 倒计时10分钟内, 无人再报价则成交'
      } else if (this.selectedObj.time === '5') {
        text = '确认后, 倒计时5分钟内, 无人再报价则成交'
      } else if (this.selectedObj.time === '0') {
        text = '确认后, 立即成交'
      }
      return text
    }
  },
  methods: {
    handleCountdownEnd() {
      this.$message.success('倒计时结束，3秒后将关闭弹窗')
      setTimeout(() => {
        this.handleClose()
        this.$emit('confirm')
      }, 3000)
    },
    open(data, price) {
      this.isLoading = false
      const { orderCode, hangingOrderId } = data
      apiGetSkuOrderInfo({ orderCode, hangingOrderId }).then(res => {
        const { data } = res
        console.log('data222', data)
        this.productData = data
        this.tradePrice = data.tradePrice
        this.otherRequire = data.otherRequire || []
        const { priceHighest, priceHign, priceLow, priceLowest } = data
        this.priceOptions = [
          {
            time: '15',
            timeText: '15分钟',
            tradeIndex: 4,
            price: priceHighest
          },
          {
            time: '10',
            timeText: '10分钟',
            tradeIndex: 3,
            price: priceHign
          },
          {
            time: '5',
            timeText: '5分钟',
            tradeIndex: 2,
            price: priceLow
          },
          {
            time: '0',
            timeText: '秒杀成交',
            tradeIndex: 1,
            price: priceLowest
          }
        ]
        this.selectedObj = this.priceOptions.find(option => option.price === price)
        this.visible = true
      })
    },
    // 抢单
    tradeGrabOrder() {
      console.log('this.selectedObj', this.selectedObj)
      console.log('this.selectedObj', this.tradePrice)
      if (this.tradePrice && this.tradePrice <= this.selectedObj.price) {
        this.$message.error('当前价格无法抢单')
        return
      }
      const params = {
        hangingOrderId: this.productData.hangingOrderId,
        orderCode: this.productData.orderCode,
        tradeIndex: this.selectedObj.tradeIndex,
        tradePrice: this.selectedObj.price
      }
      this.isLoading = true
      apiTradeGrabOrder(params).then(res => {
        this.$message.success('抢单成功，3s后将关闭弹窗')
        setTimeout(() => {
          this.handleClose()
          this.isLoading = false
          this.$emit('confirm')
        }, 3000)
      }).catch(err => {
        this.isLoading = false
        if (err.code === 602) {
          this.$confirm('保证金余额不足,是否去充值?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.$router.push({
              path: '/monery/earnest'
            })
          })
        }
      })
    },
    handleClose() {
      this.visible = false
    },

    selectPrice(option) {
      if (this.tradePrice && option.price >= this.tradePrice) {
        return
      }
      this.selectedObj = option
    },

    handleFinalConfirm() {
      // 最终确认逻辑
      console.log('最终确认抢单')
      this.$emit('confirm', {
        productData: this.productData,
        selectedPrice: this.selectedPrice
      })
      this.handleClose()
    },

    getAccountingPeriodText(accountingPeriod) {
      const periodMap = {
        0: 'T+0',
        1: 'T+1',
        2: 'T+2',
        3: 'T+3',
        4: 'T+4',
        5: 'T+5',
        6: 'T+6',
        7: 'T+7'
      }
      return periodMap[accountingPeriod] || '-'
    },

    // 显示质量管理条例弹窗
    showQualityRegulations() {
      this.$refs.qualityRegulationsDialog.open()
    }
  }
}
</script>

<style lang="scss" scoped>
$system-color: #1677FF;
$system-color-light: #e6f4ff;
$system-color-ghost: #f0f7ff;

.grab-order-dialog {
    ::v-deep .el-dialog {
        border-radius: 8px;
        overflow: hidden;
    }

    ::v-deep .el-dialog__header {
        background: #fff;
        padding: 20px 24px 16px;
        border-bottom: 1px solid #e9ecef;

        .el-dialog__title {
            font-size: 18px;
            font-weight: 600;
            color: #212529;
        }

        .el-dialog__close {
            font-size: 18px;
            color: #6c757d;

            &:hover {
                color: $system-color;
            }
        }
    }

    ::v-deep .el-dialog__body {
        padding: 0;
        background: #fff;
    }
}

.product-info-section {
    padding: 20px 24px;
    border-bottom: 1px solid #e9ecef;

    .product-name {
        font-size: 16px;
        font-weight: 600;
        color: #212529;
        margin-bottom: 16px;
        line-height: 1.4;
    }

    .order-details {
        .detail-row {
            display: flex;
            margin-bottom: 8px;
            font-size: 14px;

            .detail-label {
                color: #6c757d;
                width: 80px;
                flex-shrink: 0;
                height: 26px;
                display: flex;
                align-items: center;
            }

            .detail-value {
                color: #212529;
                flex: 1;
                height: 26px;
                display: flex;
                align-items: center;
            }
        }
    }
}

.notice-section {
    padding: 20px 24px;
    border-bottom: 1px solid #e9ecef;
    background: $system-color-light;

    .notice-title {
        display: flex;
        align-items: center;
        margin-bottom: 16px;
        font-size: 14px;
        font-weight: 600;
        color: $system-color;

        .notice-icon {
            margin-right: 8px;
            font-size: 16px;
        }
    }

    .notice-content {
        .notice-item {
            display: flex;
            margin-bottom: 12px;
            font-size: 13px;
            line-height: 1.5;

            .notice-number {
              &.warning-text {
                color: #FF3B30;
                font-weight: 600;
              }
                color: $system-color;
                font-weight: 600;
                margin-right: 8px;
                flex-shrink: 0;
            }

            .notice-text {
              &.warning-text {
                color: #FF3B30;
                font-weight: 600;
              }
                color: #495057;
                flex: 1;

                .notice-link {
                    color: $system-color;
                    text-decoration: none;

                    &:hover {
                        text-decoration: underline;
                    }
                }
            }
        }
    }
}

.price-section {
    padding: 20px 24px;
    display: flex;
    justify-content: space-between;
    .price-options {
        margin-bottom: 20px;
        width: 180px;
        height: 80px;
        flex-shrink: 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        .price-option {
            width: 100%;
            border: 2px solid #e9ecef;
            border-radius: 6px;
            padding: 16px 12px;
            margin-bottom: 10px;
            cursor: pointer;
            transition: all 0.3s ease;
            text-align: center;
            background: #fff;

            &.selected {
                background: $system-color !important;
                color: #fff;
                border-color: $system-color;

                .time-text,
                .price-amount {
                    color: #fff !important;
                }
            }

            &.flash-sale {
                border-color: $system-color;
                background: $system-color-light;
            }

            &.disabled-price {
                border-color: #e9ecef;
                background: #f8f9fa;
                cursor: not-allowed;
                opacity: 0.4;
            }

            .time-info {
                display: flex;
                align-items: center;
                justify-content: center;
                margin-bottom: 8px;

                .time-icon {
                    margin-right: 4px;
                    font-size: 14px;
                }

                .time-text {
                    font-size: 14px;
                    font-weight: 500;
                    color: #495057;
                }
            }

            .price-amount {
                font-size: 18px;
                font-weight: 600;
                color: $system-color;
            }
        }
    }
}

.confirm-section {
    padding: 12px 24px;
    text-align: center;
    border-bottom: 1px solid #e9ecef;
    background: $system-color-ghost;
    margin-bottom: 10px;

    .confirm-message {
        font-size: 14px;
        color: $system-color;
        font-weight: 500;
        line-height: 1.4;
    }
}

.credit-info {
    padding: 16px 24px;
    display: flex;
    align-items: center;
    border-bottom: 1px solid #e9ecef;

    .credit-icon {
        color: #28a745;
        margin-right: 8px;
        font-size: 16px;
    }

    .credit-text {
        font-size: 14px;
        color: #495057;
    }
}

.bottom-section {
    padding: 20px 24px;
    display: flex;
    align-items: center;
    justify-content: center;

    .final-confirm-btn {
        width: 300px;
        height: 36px;
        font-size: 14px;
        font-weight: 500;
    }
}
.other-require-tag{
 margin-right: 10px;
}
</style>
