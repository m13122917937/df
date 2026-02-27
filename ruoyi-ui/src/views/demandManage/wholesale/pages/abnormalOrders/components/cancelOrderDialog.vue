<template>
  <el-dialog 
    title="提示" 
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
    <div class="cancel-order-dialog-content">
      <!-- 撤销说明 -->
      <div class="cancel-info">
        <p>撤销后,此订单将转到'待发布'是否确认撤销</p>
      </div>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :loading="loading">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { revokeOrderApi } from '@/api/wholesale';

export default {
  name: 'CancelOrderDialog',
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
      loading: false
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
  methods: {
    // 确认撤销订单
    async handleConfirm() {
      if (!this.currentOrder) {
        this.$message.error('订单信息不存在');
        return;
      }

      this.loading = true;
      try {
        const params = {
          orderCodeList: [this.currentOrder.orderCode],
        };
        
        const res = await revokeOrderApi(params);
        if (res.code == 200) {
          this.$message.success('订单已撤销');
          this.$emit('success');
          this.handleClose();
        } else {
          this.$message.error(res.msg || '撤销订单失败');
        }
      } catch (error) {
        console.error('撤销订单失败:', error);
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
    }
  }
}
</script>

<style scoped lang="scss">
.cancel-order-dialog-content {
  .cancel-info {
    margin-bottom: 20px;
    padding: 15px;
    background-color: #f5f7fa;
    border-radius: 4px;
    border-left: 4px solid #e6a23c;
    
    p {
      margin: 0;
      font-size: 14px;
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
