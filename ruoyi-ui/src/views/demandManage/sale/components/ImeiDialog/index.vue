<!-- 串码弹窗组件 -->
<template>
  <el-dialog
    :title="dialogTitle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    width="600px"
    :append-to-body="true"
    :z-index="99999"
    @close="handleClose"
  >
    <!-- 商品信息展示 -->
    <div v-loading="loading" class="imei-info-section">
      <div class="imei-info-item">
        <span class="label">商品名称：</span>
        <span class="value">{{ imeiInfo.productName || '-' }} / {{ imeiInfo.skuName || '-' }}</span>
      </div>
      <div v-if="imeiInfo.sku" class="imei-info-item">
        <span class="label">商品编号：</span>
        <span class="value">{{ imeiInfo.sku || '-' }}</span>
      </div>
       <div  class="imei-info-item" v-for="item in imeiInfo.codeList" :key="item.sn">
        <span class="label">SN码：</span>
        <span class="value">{{ item.sn || '-' }}</span>
        <i v-if="item.sn" class="el-icon-copy-document copy-icon" title="复制" @click="handleCopyText(item.sn )" />
      </div>
      <div class="imei-info-item" v-for="item in imeiInfo.codeList" :key="item.imel">
        <span class="label">86码：</span>
        <span class="value">{{ item.imel || '-' }}</span>
        <i v-if="item.imel" class="el-icon-copy-document copy-icon" @click="handleCopyText(item.imel)" title="复制"></i>
      </div>
    </div>

    <!-- 按钮区域 -->
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { copyText } from '@/utils/copy.js'
import { apiGetImei } from '@/api/creatingOrders';

export default {
  name: 'ImeiDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    currentOrder: {
      type: Object,
      default: () => ({})
    },
    title: {
      type: String,
      default: '串码信息'
    }
  },
  data() {
    return {
      loading: false,
      imeiInfo: {
        productName: '',
        skuName: '',
        sku: '',
        codeList: ''
      }
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
    },
    dialogTitle() {
      return this.title
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        console.log('Initializing ImeiDialog...')
        this.initImeiInfo()
        this.loadImeiData()
      }
    }
  },
  methods: {
    // 初始化串码信息
    initImeiInfo() {
      if (this.currentOrder) {
        this.imeiInfo.productName = this.currentOrder.productName || ''
        this.imeiInfo.skuName = this.currentOrder.skuName || ''
        this.imeiInfo.sku = this.currentOrder.skuCode || ''
      }
    },

    // 加载串码数据
    async loadImeiData() {

      if (!this.currentOrder) {
        return
      }

      this.loading = true

      try {
        const res = await apiGetImei(this.currentOrder.orderCode)
        console.log('apiGetImei response:', res)

        if (res.code === 200 && res.data) {
          // 如果接口返回了串码数据，更新串码信息
          this.imeiInfo.codeList = res.data
        }
      } catch (error) {
        console.error('获取串码数据失败:', error)
        this.$message.error('获取串码数据失败')
      } finally {
        this.loading = false
      }
    },

    // 复制文本
    handleCopyText(text) {
      copyText(text, this)
    },

    // 关闭弹框
    handleClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style scoped lang="scss">
.imei-info-section {
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.imei-info-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
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
  color: #409eff;

  &:hover {
    color: #0a76e2;
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
