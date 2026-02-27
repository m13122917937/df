<!-- 退货追单弹框组件 -->
<template>
  <el-dialog
    title="退货追单"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    width="550px"
    :append-to-body="true"
    :z-index="99999"
    @close="handleClose"
  >
    <!-- 订单信息展示 -->
    <!-- <div class="order-info-section" v-if="currentOrder">
      <div class="order-info-item">
        <span class="label">订单号：</span>
        <span class="value">{{ currentOrder.orderCode || '-' }}</span>
        <i class="el-icon-copy-document copy-icon" @click="handleCopyText(currentOrder.orderCode)" title="复制"></i>
      </div>
      <div class="order-info-item">
        <span class="label">原始单号：</span>
        <span class="value">{{ currentOrder.originalOrderId || '-' }}</span>
        <i class="el-icon-copy-document copy-icon" @click="handleCopyText(currentOrder.originalOrderId)" title="复制"></i>
      </div>
      <div class="order-info-item">
        <span class="label">订单类型：</span>
        <span class="value">一件代发</span>
      </div>
      <div class="order-info-item">
        <span class="label">商品型号：</span>
        <span class="value">{{ currentOrder.productName || '-' }}</span>
      </div>
      <div class="order-info-item">
        <span class="label">商品编号：</span>
        <span class="value">{{ currentOrder.skuCode || '-' }}</span>
      </div>
      <div class="order-info-item">
        <span class="label">订单数量：</span>
        <span class="value">{{ currentOrder.quantity || 0 }}台</span>
      </div>
    </div> -->

    <!-- 退货追单原因选择 -->
    <div class="reason-section">
      <span class="reason-label">退货追单原因：</span>
      <el-select
        v-model="internalReturnReason"
        placeholder="请选择"
        clearable
        @change="handleReasonChange"
        style="width: 100%"
      >
        <el-option
          v-for="item in returnReasonOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </div>


    <!-- 按钮区域 -->
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button
        type="primary"
        :loading="returnOrderLoading"
        @click="handleConfirm"
      >
        确定
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { revokeOrderApi } from '@/api/wholesale';
import { copyText } from '@/utils/wholesaleUtils';

export default {
  name: 'ReturnOrderDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    currentOrder: {
      type: Array,
      default: []
    },
    returnReason: {
      type: Number,
      default: 5
    },
  },
  data() {
    return {
      returnOrderLoading: false,
      internalReturnReason: this.returnReason,
      returnReasonOptions: [
        { value: 0, label: '店铺后台急速退款' },
        { value: 1, label: '顾客拒签/拒收' },
        { value: 2, label: '派送未联系到顾客，退回' },
        { value: 3, label: '24小时物流无揽收信息' },
        { value: 4, label: '供应商私自拦截' },
        { value: 5, label: '已经从其他渠道发货' }
      ]
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      }
    },
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.resetForm();
      }
    },
    returnReason(newVal) {
      this.internalReturnReason = newVal;
    }
  },
  methods: {
    // 退货原因变化处理
    handleReasonChange(value) {
      this.internalReturnReason = value;
      this.$emit('update:returnReason', value);
    },

    // 复制文本
    handleCopyText(text) {
      copyText(text, this);
    },

    // 确认退货追单
    async handleConfirm() {
      this.returnOrderLoading = true;
      try {
        const orderCodeArr = this.currentOrder.map(val=>{
          return val.orderCode
        })
        const params = {
          orderCodeList: orderCodeArr,
          revokeCode: Number(this.returnReason)
        };

        const res = await revokeOrderApi(params);
        if (res.code == 200) {
          this.$message.success('退货追单成功');
          this.$emit('success');
          this.handleClose();
        } else {
          this.$message.error(res.msg || '退货追单失败');
        }
      } catch (error) {
        console.error('退货追单失败:', error);
        this.$message.error('退货追单失败');
      } finally {
        this.returnOrderLoading = false;
      }
    },

    // 取消
    handleCancel() {
      this.handleClose();
    },

    // 关闭弹框
    handleClose() {
      this.$emit('update:visible', false);
      this.resetForm();
    },

    // 重置表单
    resetForm() {
      const defaultReason = 5;
      this.internalReturnReason = defaultReason;
      this.$emit('update:returnReason', defaultReason);
    }
  }
}
</script>

<style scoped lang="scss">
.order-info-section {
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.order-info-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;

  &:last-child {
    margin-bottom: 0;
  }
}

.label {
  width: 80px;
  color: #606266;
  font-weight: 500;
  margin-right: 8px;
  flex-shrink: 0;
}

.value {
  flex: 1;
  color: #303133;
  margin-right: 8px;
}

.copy-icon {
  cursor: pointer;
  color: #c0c4cc;
  transition: color 0.3s;
  font-size: 14px;

  &:hover {
    color: #409eff;
  }
}

.description-text {
  margin-bottom: 20px;
  padding: 12px 16px;
  background-color: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 4px;
  color: #856404;
  font-size: 14px;
}

.reason-section {
  margin-bottom: 20px;
}

.reason-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #303133;
}

.deposit-warning {
  margin-bottom: 20px;
  padding: 12px 16px;
  background-color: #fef0f0;
  border: 1px solid #fbc4c4;
  border-radius: 4px;
  color: #f56c6c;
  font-size: 14px;
  font-weight: 500;
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.dialog-footer .el-button {
  margin-left: 10px;
}
</style>
