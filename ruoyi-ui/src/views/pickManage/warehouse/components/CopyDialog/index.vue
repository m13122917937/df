<template>
  <el-dialog
    title="商品信息"
    :visible.sync="dialogVisible"
    width="500px"
    center
    @close="handleClose"
  >
    <div class="copy-dialog-content">
      <div class="info-item">
        <label>供应商：</label>
        <span>{{ data.tradeCompanyName || '-' }}</span>
        <i
          v-if="data.tradeCompanyName"
          class="el-icon-copy-document copy-icon"
          @click="copyText(data.tradeCompanyName)"
        ></i>
      </div>
      <div class="info-item">
        <label>商品信息：</label>
        <span>{{ data.productName || '-' }} {{ data.skuName ? '(' + data.skuName + ')' : '' }}</span>
        <i
          v-if="data.productName || data.skuName"
          class="el-icon-copy-document copy-icon"
          @click="copyText((data.productName || '') + (data.skuName ? ' (' + data.skuName + ')' : ''))"
        ></i>
      </div>
      <div class="info-item">
        <label>送货码：</label>
        <span>{{ data.deliveryCode || '-' }}</span>
        <i
          v-if="data.deliveryCode"
          class="el-icon-copy-document copy-icon"
          @click="copyText(data.deliveryCode)"
        ></i>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关闭</el-button>
      <el-button type="primary" @click="copyAllInfo">复制全部信息</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'CopyDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    data: {
      type: Object,
      default: () => ({})
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
  methods: {
    handleClose() {
      this.dialogVisible = false
    },

    // 复制文本到剪贴板
    copyText(text) {
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(text).then(() => {
          this.$message.success('复制成功')
        }).catch(() => {
          this.fallbackCopyText(text)
        })
      } else {
        this.fallbackCopyText(text)
      }
    },

    // 降级复制方法
    fallbackCopyText(text) {
      const textArea = document.createElement('textarea')
      textArea.value = text
      textArea.style.position = 'fixed'
      textArea.style.left = '-999999px'
      textArea.style.top = '-999999px'
      document.body.appendChild(textArea)
      textArea.focus()
      textArea.select()
      try {
        document.execCommand('copy')
        this.$message.success('复制成功')
      } catch (err) {
        this.$message.error('复制失败')
      }
      document.body.removeChild(textArea)
    },

    // 复制全部信息
    copyAllInfo() {
      const data = this.data
      const allInfo = `供应商: ${data.tradeCompanyName || '-'}\n商品信息: ${data.productName || '-'} ${data.skuName ? '(' + data.skuName + ')' : ''}\n送货码: ${data.deliveryCode || '-'}`
      this.copyText(allInfo)
    }
  }
}
</script>

<style lang="scss" scoped>
/* 一键复制弹窗样式 */
.copy-dialog-content {
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  padding: 16px;

  .info-item {
    display: flex;
    align-items: center;
    margin-bottom: 6px;
    padding: 12px;


    &:last-child {
      margin-bottom: 0;
    }

    label {
      font-weight: 600;
      color: #303133;
      min-width: 80px;
      margin-right: 12px;
      font-size: 14px;
    }

    span {
      flex: 1;
      color: #606266;
      font-size: 14px;
      word-break: break-word;
    }

    .copy-icon {
      color: var(--primary-color);
      cursor: pointer;
      font-size: 16px;
      margin-left: 8px;
      transition: color 0.3s;

      &:hover {
        color: var(--primary-hover);
      }
    }
  }
}
</style>
