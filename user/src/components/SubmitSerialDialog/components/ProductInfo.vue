<template>
  <div class="product-info">
    <div class="product-name">
      {{ currentOrder.productName || '' }} {{ currentOrder.skuName || '' }}
      <el-tag v-if="currentOrder.category" size="mini" :type="getCategoryTagType()" style="margin-left: 8px;">{{ currentOrder.category }}</el-tag>
      <el-tag size="mini" style="margin-left: 8px;">{{ currentOrder.provinceName || '' }}</el-tag>
      <el-tag size="mini" style="margin-left: 4px;">{{ currentOrder.cityName || '' }}</el-tag>
      <el-tag size="mini" style="margin-left: 4px;">{{ currentOrder.quantity || 0 }}台</el-tag>
    </div>
    <div class="recipient-info">
      <div class="recipient">
        <!-- 收件人基本信息 -->
        <div class="recipient-basic">
          <div class="recipient-name-section">
            <span class="label">收件人:</span>
            <span class="recipient-name">{{ currentOrder.addressee || '' }}</span>
          </div>
          <div class="phone-section">
            <span class="label">电话:</span>
            <span class="phone-number">{{ currentOrder.phone || '' }}</span>
          </div>
        </div>

        <!-- 地址信息 -->
        <div class="address-section">
          <div class="address-main">
            <span class="label">地址:</span>
            <span class="address-text">{{ currentOrder.receivingAddress || '' }}</span>
          </div>
        </div>

        <!-- 警告信息和操作按钮 -->
        <div class="warning-actions-row">
          <!-- 重要提醒 -->
          <!-- <div class="address-warning">
            <i class="el-icon-warning" />
            <span class="warning-text">不要放到兔喜快递超市!!!!</span>
          </div> -->

          <!-- 操作按钮 -->
          <div class="recipient-actions">
            <el-button
              size="mini"
              type="primary"
              plain
              icon="el-icon-document-copy"
              class="copy-btn"
              @click="handleCopyAddressWithRecipientInfo"
            >
              复制地址
            </el-button>
            <!-- <el-button
              size="mini"
              type="danger"
              plain
              icon="el-icon-warning"
              class="scalper-btn"
            >
              标为黄牛
            </el-button> -->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { copyText } from '@/utils/copy'

export default {
  name: 'ProductInfo',
  props: {
    currentOrder: {
      type: Object,
      required: true
    }
  },
  methods: {
    getCategoryTagType() {
      const category = (this.currentOrder.category || '').toString().trim()
      // 如果是手机类目，显示为 warning 类型（橙色），其他类目显示为 success 类型（绿色）
      return category.includes('手机') ? 'warning' : 'success'
    },

    handleCopyAddressWithRecipientInfo() {
      const recipientInfo = `收件人: ${this.currentOrder.addressee || ''} 电话: ${this.currentOrder.phone || ''}`
      const fullAddress = `${recipientInfo}\n${this.currentOrder.receivingAddress || ''}`
      this.handleCopyText(fullAddress.trim())
    },

    // 复制文本
    handleCopyText(text) {
      copyText(text, this)
    }
  }
}
</script>

<style lang="scss" scoped>
.product-info {
  margin-bottom: 24px;
  padding: 10px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .product-name {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 16px;
    color: #2c3e50;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;

    .el-tag {
      border-radius: 6px;
      font-weight: 500;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }
  }

  .recipient-info {
    .recipient {
      margin-bottom: 12px;
      padding: 16px;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 8px;
      border: 1px solid #e4e7ed;

      .recipient-basic {
        display: flex;
        gap: 24px;
        margin-bottom: 12px;
        flex-wrap: wrap;

        .recipient-name-section,
        .phone-section {
          display: flex;
          align-items: center;
          gap: 8px;

          .label {
            font-weight: 600;
            color: #606266;
            font-size: 14px;
            min-width: 50px;
          }

          .recipient-name {
            font-weight: 600;
            color: #2c3e50;
            font-size: 15px;
          }

          .phone-number {
            font-weight: 600;
            color: #409EFF;
            font-size: 15px;
            background: rgba(64, 158, 255, 0.1);
            padding: 4px 8px;
            border-radius: 4px;
          }
        }
      }

      .address-section {
        margin-bottom: 16px;

        .address-main {
          display: flex;
          align-items: flex-start;
          gap: 8px;

          .label {
            font-weight: 600;
            color: #606266;
            font-size: 14px;
            min-width: 50px;
            margin-top: 2px;
          }

          .address-text {
            color: #2c3e50;
            font-size: 14px;
            line-height: 1.5;
            flex: 1;
          }
        }
      }

      .warning-actions-row {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
        margin-top: 12px;

        .address-warning {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 8px 12px;
          background: #fdf6ec;
          border: 1px solid #f5dab1;
          border-radius: 4px;
          color: #e6a23c;

          .el-icon-warning {
            font-size: 16px;
          }

          .warning-text {
            font-size: 13px;
            font-weight: 500;
          }
        }

        .recipient-actions {
          display: flex;
          gap: 8px;

          .copy-btn {
            border-radius: 6px;
            font-weight: 500;
          }

          .scalper-btn {
            border-radius: 6px;
            font-weight: 500;
          }
        }
      }
    }
  }
}
</style>
