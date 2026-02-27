<template>
  <el-dialog
    title="订单转为售后"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    width="500px"
    :append-to-body="true"
    :z-index="99999"
    @close="handleClose"
  >
    <el-form
      ref="orderForm"
      :model="form"
      :rules="rules"
      label-width="0"
      class="order-to-after-sales-content"
    >
      <el-form-item prop="orderCodes" class="custom-form-item">
        <div class="form-row">
          <span class="form-label error-label">内部单号(只有已完成订单，才能发起售后)</span>
          <div class="input-wrapper">
            <el-input
              v-model="form.orderCodes"
              type="textarea"
              :rows="6"
              placeholder="请输入内部单号，每行一个"
            />
          </div>
        </div>
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button
        type="primary"
        :loading="loading"
        @click="handleConfirm"
      >
        确定
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { apiAddAfterSales } from "@/api/creatingOrders";
export default {
  name: 'OrderToAfterSalesDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    // 自定义验证器：验证至少有一行有效内容
    const validateOrderCodes = (rule, value, callback) => {
      if (!value || !value.trim()) {
        callback(new Error('请输入内部单号'));
      } else {
        const lines = value.trim().split('\n').filter(line => line.trim());
        if (lines.length === 0) {
          callback(new Error('请输入至少一个内部单号'));
        } else {
          callback();
        }
      }
    };

    return {
      loading: false,
      form: {
        orderCodes: ''
      },
      rules: {
        orderCodes: [
          { required: true, validator: validateOrderCodes, trigger: ['blur', 'change'] }
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
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.resetForm();
      }
    }
  },
  mounted() {
    // 确保表单引用已创建
    this.$nextTick(() => {
      if (this.$refs.orderForm) {
        this.$refs.orderForm.clearValidate();
      }
    });
  },
  methods: {
    // 确认转售后
    async handleConfirm() {
      // 表单验证
      this.$refs.orderForm.validate(async (valid) => {
        if (!valid) {
          return false;
        }

        // 按换行分割，过滤空行和空白字符
        const trimmed = this.form.orderCodes.trim();
        const orderCodeSet = trimmed
          .split('\n')
          .map(line => line.trim())
          .filter(line => line);

        this.loading = true;
        try {
          const params = {
            orderCodeSet: orderCodeSet
          };
          
          const res = await apiAddAfterSales(params);
          if (res.code == 200) {
            this.$message.success('订单转售后成功');
            this.$emit('success');
            this.handleClose();
          } else {
            this.$message.error(res.msg || '订单转售后失败');
          }
        } catch (error) {
          console.error('订单转售后失败:', error);
          this.$message.error('订单转售后失败');
        } finally {
          this.loading = false;
        }
      });
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
        orderCodes: ''
      };
      // 清除表单验证
      this.$nextTick(() => {
        if (this.$refs.orderForm) {
          this.$refs.orderForm.clearValidate();
        }
      });
    }
  }
}
</script>

<style scoped lang="scss">

.dialog-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.dialog-footer .el-button {
  margin-left: 10px;
}
</style>

