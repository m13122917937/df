<template>
  <el-dialog
    title="添加扣罚订单"
    :visible.sync="dialogVisible"
    width="600px"
    :before-close="handleClose"
  >
    <el-form
      :model="form"
      :rules="formRules"
      ref="form"
      label-width="120px"
    >
      <el-form-item label="订单编号" prop="orderCode">
        <el-input
          v-model="form.orderCode"
          placeholder="请输入订单编号"
          clearable
          @blur="handleOrderCodeBlur"
        />
      </el-form-item>
      <!-- 订单信息展示区域 -->
      <el-form-item v-if="orderInfo.shopName" label="订单信息">
        <div class="order-info-card">
          <div class="info-item">
            <span class="info-label">店铺名称：</span>
            <span class="info-value">{{ orderInfo.shopName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">产品名称：</span>
            <span class="info-value">{{ orderInfo.productName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">SKU名称：</span>
            <span class="info-value">{{ orderInfo.skuName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">公司名称：</span>
            <span class="info-value">{{ orderInfo.companyName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">发货时间：</span>
            <span class="info-value">{{ orderInfo.sendTime }}</span>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="扣罚金额" prop="amount">
        <el-input-number
          v-model="form.amount"
          :min="0"
          style="width: 100%"
          placeholder="请输入扣罚金额"
        />
      </el-form-item>
      <el-form-item label="扣罚原因" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入扣罚原因"
          clearable
        />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getDeductionApi } from "@/api/monery";

export default {
  name: 'AddPenaltyOrderDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      dialogVisible: false,
      form: {
        orderCode: '',
        amount: null,
        remark: ''
      },
      orderInfo: {
        shopName: '',
        productName: '',
        skuName: '',
        companyName: '',
        sendTime: ''
      },
      formRules: {
        orderCode: [
          { required: true, message: '请输入订单编号', trigger: 'blur' }
        ],
        amount: [
          { required: true, message: '请输入扣罚金额', trigger: 'blur' },
          { type: 'number', min: 0, message: '扣罚金额必须大于0', trigger: 'blur' }
        ],
        remark: [
          { required: true, message: '请输入扣罚原因', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.resetForm()
      }
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.resetForm()
    },
    resetForm() {
      this.form = {
        orderCode: '',
        amount: null,
        remark: ''
      }
      this.orderInfo = {
        shopName: '',
        productName: '',
        skuName: '',
        companyName: '',
        sendTime: ''
      }
      if (this.$refs.form) {
        this.$refs.form.clearValidate()
      }
    },
    async handleOrderCodeBlur() {
      if (!this.form.orderCode || !this.form.orderCode.trim()) {
        this.orderInfo = {
          shopName: '',
          productName: '',
          skuName: '',
          companyName: '',
          sendTime: ''
        }
        return
      }
      try {
        const res = await getDeductionApi(this.form.orderCode)
        if (res && res.data) {
          this.orderInfo = {
            shopName: res.data.shopName || '',
            productName: res.data.productName || '',
            skuName: res.data.skuName || '',
            companyName: res.data.companyName || '',
            sendTime: res.data.sendTime || ''
          }
        }
      } catch (error) {
        console.error('获取扣罚信息失败:', error)
        this.$message.error('获取订单信息失败，请检查订单编号是否正确')
        this.orderInfo = {
          shopName: '',
          productName: '',
          skuName: '',
          companyName: '',
          sendTime: ''
        }
      }
    },
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.$emit('submit', this.form)
        } else {
          this.$message.error('请检查表单信息')
          return false
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.dialog-tip {
  margin-top: 20px;
  padding: 12px 16px;
  background-color: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 4px;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;

  p {
    margin: 4px 0;
    &:first-child {
      margin-top: 0;
    }
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.order-info-card {
  width: 100%;
  padding: 16px;
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;

  .info-item {
    display: flex;
    margin-bottom: 12px;
    line-height: 1.6;

    &:last-child {
      margin-bottom: 0;
    }

    .info-label {
      min-width: 80px;
      color: #909399;
      font-size: 14px;
    }

    .info-value {
      flex: 1;
      color: #303133;
      font-size: 14px;
      font-weight: 500;
    }
  }
}
</style>

