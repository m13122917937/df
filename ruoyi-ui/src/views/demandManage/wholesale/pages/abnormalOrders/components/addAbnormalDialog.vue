<template>
  <el-dialog 
    title="转为异常订单" 
    :visible.sync="dialogVisible" 
    width="500px" 
    :close-on-click-modal="false"
    :modal="true"
    :append-to-body="true"
    :z-index="99999"
    center
    custom-class="center-dialog"
    @close="handleClose"
  >
    <div class="add-abnormal-dialog-content">
      <!-- 内部单号输入 -->
      <div class="form-item">
        <span class="form-label">内部单号：</span>
        <el-input 
          v-model="form.warehouseOrderNumber" 
          placeholder="请输入内部单号"
          style="width: 300px"
          :class="{ 'is-error': form.warehouseOrderNumberError }"
        />
        <div v-if="form.warehouseOrderNumberError" class="error-text">请输入</div>
      </div>
      
      <!-- 异常类型选择 -->
      <div class="form-item">
        <span class="form-label">异常类型：</span>
        <el-select 
          v-model="form.abnormalType" 
          placeholder="请选择"
          style="width: 300px"
          @change="handleAbnormalTypeChange"
        >
          <el-option 
            v-for="item in abnormalTypeOptions" 
            :key="item.value" 
            :label="item.label" 
            :value="item.value"
          />
        </el-select>
      </div>
      
      <!-- 异常原因选择 -->
      <div class="form-item" v-if="form.abnormalType">
        <span class="form-label">异常原因：</span>
        <div class="radio-group">
          <label 
            v-for="reason in currentReasonOptions" 
            :key="reason.value"
            class="radio-item"
          >
            <input 
              type="radio" 
              name="abnormalReason" 
              :value="reason.value" 
              v-model="form.abnormalReason"
            />
            <span class="radio-label">{{ reason.label }}</span>
          </label>
        </div>
      </div>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :loading="loading">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { addErrorApi } from '@/api/wholesale';

export default {
  name: 'AddAbnormalDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      form: {
        warehouseOrderNumber: '',
        abnormalType: '',
        abnormalReason: '',
        warehouseOrderNumberError: false
      },
      // 异常类型选项
      abnormalTypeOptions: [
        { label: '物流异常', value: 'logistics' },
      ],
      // 异常原因映射
      abnormalReasonMap: {
        serial: [
          { label: '全部异常串码 ', value: '74' },
          { label: '部分异常串码', value: '75' },
        ],
        logistics: [
          { label: '物流无流转信息', value: '71' },
          { label: '签收异常', value: '72' },
          { label: '手机号后四位错误', value: '73' }
        ],
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
    // 当前异常原因选项
    currentReasonOptions() {
      return this.abnormalReasonMap[this.form.abnormalType] || [];
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
    // 异常类型变化
    handleAbnormalTypeChange(value) {
      this.form.abnormalReason = ''; // 清空异常原因选择
    },

    // 确认转为异常订单
    async handleConfirm() {
      // 验证内部单号
      if (!this.form.warehouseOrderNumber.trim()) {
        this.form.warehouseOrderNumberError = true;
        this.$message.warning('请输入内部单号');
        return;
      } else {
        this.form.warehouseOrderNumberError = false;
      }

      // 验证异常类型
      if (!this.form.abnormalType) {
        this.$message.warning('请选择异常类型');
        return;
      }

      // 验证异常原因
      if (!this.form.abnormalReason) {
        this.$message.warning('请选择异常原因');
        return;
      }

      this.loading = true;
      try {
        const params = {
          orderCode: this.form.warehouseOrderNumber,
          errorCode: Number(this.form.abnormalReason),
        };
        
        const res = await addErrorApi(params);
        if (res.code == 200) {
          this.$message.success('转为异常订单成功');
          this.$emit('success');
          this.handleClose();
        } else {
          this.$message.error(res.msg || '转为异常订单失败');
        }
      } catch (error) {
        console.error('转为异常订单失败:', error);
      } finally {
        this.loading = false;
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
      this.form = {
        warehouseOrderNumber: '',
        abnormalType: '',
        abnormalReason: '',
        warehouseOrderNumberError: false
      };
    }
  }
}
</script>

<style scoped lang="scss">
.add-abnormal-dialog-content {
  .form-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: 20px;
    position: relative;
    
    .form-label {
      width: 100px;
      font-weight: 500;
      color: #606266;
      margin-top: 8px;
    }
    
    .error-text {
      position: absolute;
      top: 100%;
      left: 100px;
      color: #f56c6c;
      font-size: 12px;
      margin-top: 4px;
    }
  }
  
  .radio-group {
    display: flex;
    flex-direction: column;
    gap: 12px;
    
    .radio-item {
      display: flex;
      align-items: center;
      cursor: pointer;
      
      input[type="radio"] {
        margin-right: 8px;
        width: 16px;
        height: 16px;
        cursor: pointer;
      }
      
      .radio-label {
        font-size: 14px;
        color: #606266;
        cursor: pointer;
      }
    }
  }
}

/* 输入框错误状态 */
.is-error {
  :deep(.el-input__inner) {
    border-color: #f56c6c !important;
  }
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
