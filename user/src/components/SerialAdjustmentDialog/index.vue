<!-- 串码调整弹窗组件 -->
<template>
  <el-dialog
    title="串码调整"
    :visible.sync="dialogVisible"
    width="600px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <div class="serial-adjustment-content">
      <!-- 说明文字 -->
      <div class="instructions">
        请输入串码填反的两个无仓单号,点击确定,即可调整
      </div>

      <!-- 无仓单号1 -->
      <div class="input-section">
        <div class="input-group">
          <div class="input-wrapper">
            无仓单号1:<el-input
              v-model="orderNumber1"
              placeholder="请输入"
              class="order-input"
              @input="handleOrderNumber1Change"
            />
            <el-button
              type="danger"
              size="small"
              class="confirm-btn"
              @click="handleConfirmOrder1"
            >
              确定
            </el-button>
            <span class="serial-display">串码: {{ serialNumber1 || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 交换图标 -->
      <div class="swap-icon">
        <i class="el-icon-sort" />
      </div>

      <!-- 无仓单号2 -->
      <div class="input-section">
        <div class="input-group">
          <div class="input-wrapper">
            无仓单号2:<el-input
              v-model="orderNumber2"
              placeholder="请输入"
              class="order-input"
              @input="handleOrderNumber2Change"
            />
            <el-button
              type="danger"
              size="small"
              class="confirm-btn"
              @click="handleConfirmOrder2"
            >
              确定
            </el-button>
            <span class="serial-display">串码: {{ serialNumber2 || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 警告信息 -->
      <div class="warning-message">
        本月串码填反申请剩余{{ remainingAdjustments }}次,使用完后,每次申请调整串码将扣除50元保证金。
      </div>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="danger" @click="handleConfirm">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'SerialAdjustmentDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      orderNumber1: '',
      orderNumber2: '',
      serialNumber1: '',
      serialNumber2: '',
      remainingAdjustments: 3 // 剩余调整次数
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.resetForm()
      }
    }
  },
  methods: {
    /**
     * 重置表单
     */
    resetForm() {
      this.orderNumber1 = ''
      this.orderNumber2 = ''
      this.serialNumber1 = ''
      this.serialNumber2 = ''
    },

    /**
     * 无仓单号1输入变化
     */
    handleOrderNumber1Change(value) {
      // 可以在这里添加实时验证逻辑
    },

    /**
     * 无仓单号2输入变化
     */
    handleOrderNumber2Change(value) {
      // 可以在这里添加实时验证逻辑
    },

    /**
     * 确认无仓单号1
     */
    handleConfirmOrder1() {
      if (!this.orderNumber1.trim()) {
        this.$message.warning('请输入无仓单号1')
        return
      }

      // 模拟API调用获取串码
      this.serialNumber1 = this.getMockSerialNumber(this.orderNumber1)
      this.$message.success('无仓单号1确认成功')
    },

    /**
     * 确认无仓单号2
     */
    handleConfirmOrder2() {
      if (!this.orderNumber2.trim()) {
        this.$message.warning('请输入无仓单号2')
        return
      }

      // 模拟API调用获取串码
      this.serialNumber2 = this.getMockSerialNumber(this.orderNumber2)
      this.$message.success('无仓单号2确认成功')
    },

    /**
     * 获取模拟串码
     */
    getMockSerialNumber(orderNumber) {
      // 模拟根据订单号生成串码
      return `SN${orderNumber.slice(-6)}${Math.floor(Math.random() * 1000).toString().padStart(3, '0')}`
    },

    /**
     * 取消
     */
    handleCancel() {
      this.$emit('update:visible', false)
      this.$emit('cancel')
    },

    /**
     * 确定调整
     */
    handleConfirm() {
      if (!this.orderNumber1.trim()) {
        this.$message.warning('请输入无仓单号1')
        return
      }
      if (!this.orderNumber2.trim()) {
        this.$message.warning('请输入无仓单号2')
        return
      }
      if (!this.serialNumber1) {
        this.$message.warning('请先确认无仓单号1的串码')
        return
      }
      if (!this.serialNumber2) {
        this.$message.warning('请先确认无仓单号2的串码')
        return
      }

      // 构建调整数据
      const adjustmentData = {
        orderNumber1: this.orderNumber1,
        orderNumber2: this.orderNumber2,
        serialNumber1: this.serialNumber1,
        serialNumber2: this.serialNumber2,
        remainingAdjustments: this.remainingAdjustments - 1
      }

      this.$emit('confirm', adjustmentData)
      this.$emit('update:visible', false)
      this.$message.success('串码调整申请已提交')
    },

    /**
     * 关闭弹窗
     */
    handleClose() {
      this.$emit('update:visible', false)
      this.$emit('close')
    }
  }
}
</script>

<style lang="scss" scoped>
.serial-adjustment-content {
  padding: 20px 0;

  .instructions {
    margin-bottom: 24px;
    color: #606266;
    font-size: 14px;
    line-height: 1.5;
  }

  .input-section {
    margin-bottom: 20px;

    .input-group {
      .input-label {
        display: block;
        margin-bottom: 8px;
        font-weight: 600;
        color: #2c3e50;
        font-size: 14px;
      }

      .input-wrapper {
        display: flex;
        align-items: center;
        gap: 12px;

        .order-input {
          flex: 1;
          max-width: 200px;

          ::v-deep .el-input__inner {
            border-radius: 6px;
            border: 2px solid #e4e7ed;
            transition: all 0.3s ease;

            &:focus {
              border-color: #f56c6c;
              box-shadow: 0 0 0 2px rgba(245, 108, 108, 0.2);
            }
          }
        }

        .confirm-btn {
          border-radius: 6px;
          font-weight: 500;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba(245, 108, 108, 0.3);
          }
        }

        .serial-display {
          color: #606266;
          font-size: 14px;
          font-weight: 500;
          min-width: 100px;
        }
      }
    }
  }

  .swap-icon {
    display: flex;
    justify-content: right;
    padding-right: 60px;
    margin: 20px 0;
    color: #409EFF;
    font-size: 24px;
    opacity: 0.7;
  }

  .warning-message {
    margin-top: 24px;
    padding: 12px 16px;
    background: rgba(245, 108, 108, 0.1);
    border: 1px solid rgba(245, 108, 108, 0.3);
    border-radius: 6px;
    color: #f56c6c;
    font-size: 13px;
    line-height: 1.5;
  }
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;

  .el-button {
    margin-left: 12px;
    border-radius: 6px;
    font-weight: 500;
    transition: all 0.3s ease;

    &:first-child {
      margin-left: 0;
    }

    &.el-button--danger {
      background: #f56c6c;
      border-color: #f56c6c;

      &:hover {
        background: #f78989;
        border-color: #f78989;
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(245, 108, 108, 0.3);
      }
    }
  }
}
</style>
