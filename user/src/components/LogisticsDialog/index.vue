<!-- 物流信息弹框组件 -->
<template>
  <el-dialog
    title="物流信息"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    width="720px"
    top="3vh"
    :append-to-body="true"
    :z-index="99999"
    class="logistics-dialog"
    :modal="true"
    :lock-scroll="true"
    @close="handleClose"
  >
    <!-- 物流信息展示 -->
    <div class="logistics-info-section">
      <div class="info-header">
        <div class="express-type">
          <i class="el-icon-truck" />
          <span>{{
            (currentOrder && currentOrder.trackingCompany) || "快递发货"
          }}</span>
        </div>
        <div v-if="logisticsInfo.logisticsNo" class="tracking-number">
          <span class="label">快递单号：</span>
          <span class="value">{{ logisticsInfo.logisticsNo }}</span>
          <i
            class="el-icon-copy-document copy-icon"
            title="复制"
            @click="handleCopyText(logisticsInfo.logisticsNo)"
          />
        </div>
      </div>

      <div class="shipping-info">
        <div class="shipping-location">
          <i class="el-icon-location-outline" />
          <span class="label">发货地点：</span>
          <span class="value">{{
            (logisticsInfo.routeInfoVO &&
              logisticsInfo.routeInfoVO.from &&
              logisticsInfo.routeInfoVO.from.name) ||
              "-"
          }}</span>
        </div>
      </div>
    </div>

    <!-- 物流状态时间线 -->
    <div
      v-if="
        logisticsInfo.detailInfoVOList &&
          logisticsInfo.detailInfoVOList.length > 0
      "
      class="timeline-section"
    >
      <div class="timeline-header">
        <i class="el-icon-time" />
        <span class="timeline-title">物流轨迹</span>
      </div>
      <div
        v-loading="loading"
        class="timeline-container"
        element-loading-text="正在获取物流信息..."
      >
        <div
          v-for="(item, index) in logisticsInfo.detailInfoVOList"
          :key="index"
          class="timeline-item"
          :class="{ 'is-current': index === 0 }"
        >
          <div class="timeline-icon">
            <i
              class="el-icon-check"
              :class="{
                'is-current': index === 0,
                'is-completed': index !== 0,
              }"
            />
          </div>
          <div class="timeline-content">
            <div class="timeline-time">{{ item.time || "-" }}</div>
            <div class="timeline-status">{{ item.context || "-" }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 无数据状态 -->
    <div v-else-if="!loading" class="no-data">
      <i class="el-icon-truck" />
      <p>暂无物流信息</p>
    </div>

    <!-- 按钮区域 -->
    <div slot="footer" class="dialog-footer">
      <el-button class="close-btn" @click="handleClose">
        <i class="el-icon-close" />
        关闭
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { copyText } from '@/utils/copy.js'
import { getExpressCompanyInfo } from '@/api/order'

export default {
  name: 'LogisticsDialog',
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
      loading: false,
      logisticsInfo: {
        detailInfoVOList: [],
        routeInfoVO: {
          from: {
            name: ''
          }
        },
        logisticsNo: ''
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
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.getCompanyInfo()
      }
    }
  },
  methods: {
    // 查询快递
    async getCompanyInfo() {
      console.log(this.currentOrder)
      if (!this.currentOrder || !this.currentOrder.trackingNumber) {
        return
      }

      this.loading = true
      try {
        const res = await getExpressCompanyInfo({
          logisticsNo: this.currentOrder.trackingNumber
        })
        this.logisticsInfo = res
        if (res) {
          this.logisticsInfo = res
        } else {
          this.$message.warning('暂无物流信息')
        }
      } catch (error) {
        console.error('获取物流信息失败:', error)
        this.$message.error('获取物流信息失败，请稍后重试')
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
// 弹框整体样式
:deep(.logistics-dialog) {
  .el-dialog {
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
    max-height: 60vh;
    display: flex;
    flex-direction: column;
  }

  .el-dialog__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 12px 12px 0 0;
    padding: 10px 24px;
    flex-shrink: 0;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__close {
      color: white;
      font-size: 20px;

      &:hover {
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }

  .el-dialog__body {
    padding: 10px;
    background: #fafbfc;
    flex: 1;
    overflow-y: auto;
    max-height: calc(60vh - 120px);

    // 自定义滚动条样式
    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: #c1c1c1;
      border-radius: 3px;

      &:hover {
        background: #a8a8a8;
      }
    }
  }

  .el-dialog__footer {
    background: white;
    border-radius: 0 0 12px 12px;
    padding: 16px 24px;
    border-top: 1px solid #e4e7ed;
    flex-shrink: 0;
  }
}

// 物流信息区域
.logistics-info-section {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 12px;
  padding: 10px;
  margin-bottom: 14px;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e9ecef;
}

.express-type {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;

  i {
    margin-right: 8px;
    color: #409eff;
    font-size: 18px;
  }
}

.tracking-number {
  display: flex;
  align-items: center;
  background: #f0f9ff;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #b3d8ff;

  .label {
    color: #606266;
    font-size: 14px;
    margin-right: 8px;
    font-weight: 500;
  }

  .value {
    color: #409eff;
    font-weight: 600;
    margin-right: 8px;
    font-family: "Monaco", "Menlo", "Ubuntu Mono", monospace;
  }

  .copy-icon {
    cursor: pointer;
    color: #409eff;
    transition: all 0.3s;
    font-size: 16px;
    padding: 4px;
    border-radius: 4px;

    &:hover {
      background: #409eff;
      color: white;
      transform: scale(1.1);
    }
  }
}

.shipping-info {
  .shipping-location {
    display: flex;
    align-items: center;
    font-size: 14px;

    i {
      margin-right: 8px;
      color: #67c23a;
      font-size: 16px;
    }

    .label {
      color: #606266;
      font-weight: 500;
      margin-right: 8px;
    }

    .value {
      color: #2c3e50;
      font-weight: 500;
    }
  }
}

// 时间线区域
.timeline-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.timeline-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f2f5;

  i {
    margin-right: 8px;
    color: #409eff;
    font-size: 18px;
  }

  .timeline-title {
    font-size: 16px;
    font-weight: 600;
    color: #2c3e50;
  }
}

.timeline-container {
  position: relative;
  padding-left: 24px;
  max-height: 400px;
  overflow-y: auto;

  // 自定义滚动条样式
  &::-webkit-scrollbar {
    width: 4px;
  }

  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 2px;
  }

  &::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 2px;

    &:hover {
      background: #a8a8a8;
    }
  }

  &::before {
    content: "";
    position: absolute;
    left: 11px;
    top: 0;
    bottom: 0;
    width: 2px;
    background: linear-gradient(180deg, #409eff 0%, #e4e7ed 100%);
    border-radius: 1px;
  }
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  position: relative;
  transition: all 0.3s ease;

  &:last-child {
    margin-bottom: 0;
  }

  &.is-current {
    .timeline-content {
      background: linear-gradient(135deg, #f0f9ff 0%, #e1f5fe 100%);
      border-color: #409eff;
    }
  }
}

.timeline-icon {
  margin-right: 16px;
  margin-top: 4px;
  flex-shrink: 0;
  z-index: 2;
  position: relative;

  .el-icon-check {
    display: inline-block;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background-color: #e4e7ed;
    color: #fff;
    text-align: center;
    line-height: 20px;
    font-size: 12px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;

    &.is-current {
      background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
      animation: pulse 2s infinite;
    }

    &.is-completed {
      background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
      box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
    }
  }
}

@keyframes pulse {
  0% {
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }
  50% {
    box-shadow: 0 4px 20px rgba(64, 158, 255, 0.5);
  }
  100% {
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }
}

.timeline-content {
  flex: 1;
  background: #fafbfc;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }
}

.timeline-time {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
  line-height: 1.4;
  font-weight: 500;
}

.timeline-status {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
  line-height: 1.6;
  word-break: break-word;
}

// 无数据状态
.no-data {
  text-align: center;
  padding: 40px 20px;
  color: #909399;

  i {
    font-size: 48px;
    margin-bottom: 16px;
    display: block;
    color: #c0c4cc;
  }

  p {
    font-size: 14px;
    margin: 0;
  }
}

// 按钮区域
.dialog-footer {
  text-align: right;

  .close-btn {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    color: white;
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);

    i {
      margin-right: 6px;
    }

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
    }

    &:active {
      transform: translateY(0);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  :deep(.logistics-dialog) {
    .el-dialog {
      width: 95% !important;
      margin: 3vh auto !important;
      max-height: 60vh;
    }

    .el-dialog__body {
      max-height: calc(60vh - 120px);
    }
  }

  .info-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .tracking-number {
    width: 100%;
    justify-content: space-between;
  }

  .timeline-container {
    padding-left: 20px;
    max-height: 300px;
  }

  .timeline-item {
    margin-bottom: 16px;
  }

  .timeline-content {
    padding: 12px;
  }
}
</style>
