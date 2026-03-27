<template>
  <el-dialog
    title="添加退货追单"
    :visible.sync="dialogVisible"
    width="600px"
    :close-on-click-modal="false"
    :modal="true"
    :append-to-body="true"
    :z-index="99999"
    center
    custom-class="center-dialog"
    @close="handleClose"
  >
    <div class="add-return-dialog-content">
      <div class="form-section">
        <div class="form-item">
          <span class="form-label">内部单号：</span>
          <el-input
            v-model="form.warehouseOrderNumber"
            placeholder="请输入内部单号"
            style="width: 300px"
          />
        </div>

        <div class="form-item">
          <span class="form-label">退货追单原因：</span>
          <el-select
            v-model="form.returnReason"
            placeholder="请选择"
            style="width: 300px"
          >
            <el-option
              v-for="item in returnReasonOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
import { revokeOrderApi } from '@/api/wholesale';

export default {
  name: 'AddReturnDialog',
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
        returnReason: '0'
      },
      // 退货追单原因选项
      returnReasonOptions: [
        { label: '店铺后台急速退款', value: '0' },
        { label: '顾客拒签/拒收', value: '1' },
        { label: '派送未联系到顾客，退回', value: '2' },
        { label: '24小时物流无揽收信息', value: '3' },
        { label: '供应商私自拦截', value: '4' },
        { label: '已经从其他渠道发货', value: '5' },
        { label: '手动追单', value: '6' }
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
    // 确认添加退货追单
    async handleConfirm() {
      if (!this.form.warehouseOrderNumber) {
        this.$message.warning('请输入内部单号');
        return;
      }
      if (!this.form.returnReason && this.form.returnReason !== '0') {
        this.$message.warning('请选择退货追单原因');
        return;
      }

      this.loading = true;
      try {
        const params = {
          orderCodeList: [this.form.warehouseOrderNumber],
          revokeCode: Number(this.form.returnReason)
        };

        const response = await revokeOrderApi(params);
        if (response.code == 200) {
          this.$message.success('退货追单添加成功');
          this.$emit('success');
          this.handleClose();
        } else {
          this.$message.error(response.msg || '退货追单添加失败');
        }
      } catch (error) {
        console.error('添加退货追单失败:', error);
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
        returnReason: '0' // 默认选择"店铺后台急速退款"
      };
    }
  }
}
</script>

<style scoped lang="scss">
.add-return-dialog-content {
  .form-section {
    padding: 20px 0;
  }

  .form-item {
    display: flex;
    align-items: center;
    margin-bottom: 20px;

    .form-label {
      width: 120px;
      font-weight: 500;
      color: #606266;
      margin-right: 12px;
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
