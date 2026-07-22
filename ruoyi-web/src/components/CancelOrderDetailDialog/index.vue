<template>
  <el-dialog
    :title="title"
    :visible.sync="visibleProxy"
    width="720px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <div v-loading="loading" class="cancel-detail-content">
      <!-- 顶部概要信息 -->
      <div class="summary-line">
        <div class="summary-left">
          <span class="label">公司名称：</span>
          <span class="value">{{ detail.companyName || '-' }}</span>
          <span class="divider">｜</span>
          <span class="label">创建人：</span>
          <span class="value">{{ detail.createBy || '-' }}</span>
          <span class="divider">｜</span>
          <span class="label">创建时间：</span>
          <span class="value">{{ detail.createTime || '-' }}</span>
        </div>
        <div class="badge-group">
          <el-tag v-show="detail.status != null" size="small" effect="dark" :type="statusTagType">{{ statusText }}</el-tag>
        </div>
      </div>

      <!-- 备注信息 -->
      <div class="remark-section">
        <div v-show="detail.remark" class="remark-block">
          <div class="remark-title ">
            <i class="el-icon-warning-outline" /> 申请理由
          </div>
          <div class="remark-content">{{ detail.remark }}</div>
        </div>
      </div>
      <div v-if="detail.refuseRemark" class="remark-section" style="margin-top: 12px;">
        <div class="remark-block">
          <div class="remark-title danger">
            抱歉您的毁单申请被拒绝，理由如下：
          </div>
          <div class="remark-content">{{ detail.refuseRemark || '-' }}</div>
        </div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="close">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { getCancelDetail } from '@/api/order'
export default {
  name: 'CancelOrderDetailDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    orderData: {
      type: Object,
      default: () => ({})
    },
    title: {
      type: String,
      default: '毁单详情'
    }
  },
  data() {
    return {
      loading: false,
      detail: {}
    }
  },
  computed: {
    visibleProxy: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    },
    statusText() {
      const s = Number(this.detail.status)
      if (s === 1) return '申请中'
      if (s === 2) return '已拒绝'
      if (s === 3) return '已审批'
      return this.detail.status || '-'
    },
    statusTagType() {
      const s = Number(this.detail.status)
      if (s === 1) return 'warning'
      if (s === 2) return 'danger'
      if (s === 3) return 'success'
      return 'info'
    },
    typeText() {
      const t = Number(this.detail.type)
      if (t === 1) return '立即毁单'
      if (t === 2) return '免责毁单'
      if (t === 4) return '修改物流单号申请'
      return this.detail.type || '-'
    },
    reasonTagType() {
      const r = Number(this.detail.type)
      if (r === 1) return 'danger'
      if (r === 2) return 'success'
      return 'info'
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.fetchCancelDetail()
      }
    }
  },
  methods: {
    async fetchCancelDetail() {
      const code = this.orderData.orderCode || this.orderData.orderNo
      if (!code) return
      this.loading = true
      try {
        const res = await getCancelDetail(code)
        if (res && res.code === 200) {
          this.detail = res.data || {}
        } else {
          this.detail = {}
        }
      } catch (e) {
        this.detail = {}
      } finally {
        this.loading = false
      }
    },
    close() {
      this.$emit('update:visible', false)
      this.$emit('close')
    },
    handleClose() {
      this.$emit('close')
    }
  }
}
</script>

<style lang="scss" scoped>
.cancel-detail-content {
  .summary-line {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
    .summary-left {
      color: #303133;
      .label { color: #606266; }
      .value { margin-right: 8px; }
      .divider { margin: 0 8px; color: #dcdfe6; }
    }
    .badge-group {
      display: flex;
      gap: 8px;
    }
  }

  .desc-section {
    margin-bottom: 12px;
  }

  .remark-section {
    display: grid;
    grid-template-columns: 1fr;
    gap: 12px;

    .remark-block {
      background: #fafafa;
      border: 1px solid #ebeef5;
      border-radius: 6px;
      padding: 12px;

      .remark-title {
        font-weight: 600;
        color: #606266;
        margin-bottom: 8px;
        display: flex;
        align-items: center;
        gap: 6px;
        &.danger { color: #FF3B30; }
      }

      .remark-content {
        white-space: pre-wrap;
        line-height: 1.6;
        color: #303133;
      }
    }
  }
}
</style>

