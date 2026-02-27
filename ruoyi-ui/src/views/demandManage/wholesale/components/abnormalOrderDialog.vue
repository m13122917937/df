<!-- 转为异常订单弹框组件 -->
<template>
  <el-dialog
    title="转为异常订单"
    :visible.sync="dialogVisible"
    width="450px"
    :close-on-click-modal="false"
    :modal="true"
    :append-to-body="true"
    :z-index="3000"
    center
    custom-class="center-dialog"
    @close="handleClose"
  >
    <div class="abnormal-dialog-content">
      <!-- 异常类型选择 -->
      <div class="form-item">
        <span class="form-label">异常类型：</span>
        <el-select v-model="abnormalForm.abnormalType" placeholder="请选择异常类型" style="width: 200px" @change="handleAbnormalTypeChange">
          <el-option v-for="item in abnormalTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>

      <!-- 异常原因选择 -->
      <div class="form-item">
        <span class="form-label">异常原因：</span>
        <div class="custom-radio-group">
          <label
            v-for="item in currentReasonOptions"
            :key="item.value"
            class="custom-radio-item"
            :class="{ 'is-checked': abnormalForm.abnormalReason === item.value }"
          >
            <input
              type="radio"
              :value="item.value"
              v-model="abnormalForm.abnormalReason"
              @change="handleReasonChange"
              class="custom-radio-input"
            />
            <span class="custom-radio-label">{{ item.label }}</span>
          </label>
        </div>
      </div>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getErrorApi } from '@/api/wholesale';

export default {
  name: 'AbnormalOrderDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    currentOrder: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      abnormalForm: {
        warehouseOrderNumber: '',
        abnormalType: '物流异常',
        abnormalReason: ''
      },
      abnormalTypeOptions: [
        { label: '物流异常', value: '物流异常' },
        { label: '串码异常', value: '串码异常' }
      ],
      // 不同异常类型对应的原因选项
      abnormalReasonMap: {
        '物流异常': [
          { label: '物流无流转信息', value: '71' },
          { label: '签收异常', value: '72' },
          { label: '手机号后四位错误', value: '73' }
        ],
        '串码异常': [
          { label: '全部异常串码 ', value: '74' },
          { label: '部分异常串码', value: '75' },
        ]
      }
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
    // 当前异常类型对应的原因选项
    currentReasonOptions() {
      return this.abnormalReasonMap[this.abnormalForm.abnormalType] || [];
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.resetForm();
      }
    }
  },
  methods: {
    // 异常类型变化处理
    handleAbnormalTypeChange(value) {
      // 清空之前选择的异常原因
      this.abnormalForm.abnormalReason = '';
    },

    // 异常原因变化处理
    handleReasonChange() {
      // 可以在这里添加其他逻辑
    },

    // 确认转异常
    async handleConfirm() {
      if (!this.abnormalForm.abnormalReason) {
        this.$message.warning('请选择异常原因');
        return;
      }

      try {
        const params = {
          orderCode: this.currentOrder.orderCode,
          errorCode: this.abnormalForm.abnormalReason,
        };

        const res = await getErrorApi(params);
        if (res.code == 200) {
          this.$message.success('转异常成功');
          this.$emit('success');
          this.handleClose();
        } else {
          this.$message.error(res.msg || '转异常失败');
        }
      } catch (error) {
        console.error('转异常失败:', error);
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
      this.abnormalForm = {
        warehouseOrderNumber: '',
        abnormalType: '物流异常',
        abnormalReason: ''
      };
    }
  }
}
</script>

<style scoped lang="scss">
.abnormal-dialog-content {
  padding: 20px 0;
}

.form-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.form-label {
  width: 80px;
  font-weight: 500;
  color: #303133;
  margin-right: 12px;
  flex-shrink: 0;
  line-height: 32px;
}

.custom-radio-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.custom-radio-item {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s;

  &:hover {
    border-color: #409eff;
  }

  &.is-checked {
    border-color: #409eff;
    background-color: #f0f9ff;
  }
}

.custom-radio-input {
  margin-right: 8px;
  margin: 0;
}

.custom-radio-label {
  font-size: 14px;
  color: #606266;
  user-select: none;
  margin-left: 8px;
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
