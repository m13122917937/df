<template>
  <div class="empty-data">
    <div class="empty-icon">
      <i class="el-icon-document-remove"></i>
    </div>
    <div class="empty-text">{{ text }}</div>
  </div>
</template>

<script>
export default {
  name: 'EmptyState',
  props: {
    text: {
      type: String,
      default: '暂无数据'
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.el-table__empty-block) {
  position: absolute !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  min-height: 300px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  z-index: 10 !important;
}

:deep(.el-table__empty-text) {
  display: none !important;
}

.empty-data {
  display: flex !important;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  max-width: 400px;
  margin: 0 auto;
}

.empty-icon {
  font-size: 64px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: #606266;
  margin-bottom: 8px;
  font-weight: 500;
}

.empty-tip {
  font-size: 14px;
  color: #909399;
  line-height: 1.5;
}
</style>
