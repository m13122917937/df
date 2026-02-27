<template>
  <el-dialog 
    title="转为已收货" 
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
    <div class="turn-to-received-dialog-content">
      <!-- 内部单号输入 -->
      <div class="form-item">
        <span class="form-label">内部单号：</span>
        <el-input 
          v-model="form.warehouseOrderNumber" 
          placeholder="请输入内部单号"
          style="width: 300px"
        />
      </div>
      
      <!-- 保证金选择 -->
      <div class="form-item">
        <span class="form-label">保证金：</span>
        <div class="radio-group">
          <label class="radio-item">
            <input 
              type="radio" 
              name="deposit" 
              value="1" 
              v-model="form.depositType"
            />
            <span class="radio-label">退保证金</span>
          </label>
          <label class="radio-item">
            <input 
              type="radio" 
              name="deposit" 
              value="2" 
              v-model="form.depositType"
            />
            <span class="radio-label">不退保证金</span>
          </label>
        </div>
      </div>
      
      <!-- 提示信息 -->
      <div class="info-text">
        <p>1. 请谨慎操作,转为已收货后,会生成财务账单,排款结算</p>
        <p>2. 可转已收货的订单状态:在途、物流异常</p>
      </div>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :loading="loading">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { turnToEndingApi } from '@/api/wholesale';

export default {
  name: 'TurnToReceivedDialog',
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
        depositType: '1' // 默认选择退保证金
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
    // 确认转为已收货
    async handleConfirm() {
      if (!this.form.warehouseOrderNumber.trim()) {
        this.$message.warning('请输入内部单号');
        return;
      }

      this.loading = true;
      try {
        const params = {
          orderCode: this.form.warehouseOrderNumber,
          refund: Number(this.form.depositType)
        };
        
        const res = await turnToEndingApi(params);
        if (res.code == 200) {
          this.$message.success('转为已收货成功');
          this.$emit('success');
          this.handleClose();
        } else {
          this.$message.error(res.msg || '转为已收货失败');
        }
      } catch (error) {
        console.error('转为已收货失败:', error);
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
        depositType: '1'
      };
    }
  }
}
</script>

<style scoped lang="scss">
.turn-to-received-dialog-content {
  .form-item {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    
    .form-label {
      width: 100px;
      font-weight: 500;
      color: #606266;
    }
  }
  
  .radio-group {
    display: flex;
    gap: 20px;
    
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
  
  .info-text {
    margin-top: 20px;
    padding: 15px;
    background-color: #f5f7fa;
    border-radius: 4px;
    border-left: 4px solid #409eff;
    
    p {
      margin: 5px 0;
      font-size: 13px;
      color: #606266;
      line-height: 1.5;
    }
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
