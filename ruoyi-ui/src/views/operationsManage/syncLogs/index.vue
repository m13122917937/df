<template>
  <div class="app-container sync-log-page">
    <section class="log-card">
      <div class="header-row">
        <div><h3>同步任务日志</h3><p>记录吉客云经营数据同步批次、读取量及失败原因。</p></div>
        <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="loadData">刷新</el-button>
      </div>
      <el-table v-loading="loading" :data="rows" border stripe>
        <el-table-column prop="windowStart" label="数据窗口开始" min-width="165" />
        <el-table-column prop="windowEnd" label="数据窗口结束" min-width="165" />
        <el-table-column prop="syncType" label="类型" width="110" />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope"><el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="readCount" label="读取" width="90" align="right" />
        <el-table-column prop="insertCount" label="新增" width="90" align="right" />
        <el-table-column prop="updateCount" label="更新" width="90" align="right" />
        <el-table-column prop="skipCount" label="跳过" width="90" align="right" />
        <el-table-column prop="startedTime" label="开始时间" min-width="165" />
        <el-table-column prop="finishedTime" label="结束时间" min-width="165" />
        <el-table-column prop="errorMessage" label="错误原因" min-width="240" show-overflow-tooltip />
      </el-table>
    </section>
  </div>
</template>

<script>
import { getAnalysisSyncLogs } from '@/api/analysis'

export default {
  name: 'AnalysisSyncLogs',
  data() { return { loading: false, rows: [] } },
  created() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const response = await getAnalysisSyncLogs()
        this.rows = response.data || []
      } finally { this.loading = false }
    },
    statusType(status) { return status === 'COMPLETE' ? 'success' : status === 'FAILED' ? 'danger' : 'warning' },
    statusText(status) { return { COMPLETE: '成功', FAILED: '失败', RUNNING: '执行中' }[status] || status }
  }
}
</script>

<style lang="scss" scoped>
.sync-log-page { min-height: 100%; background: var(--bg-page); color: var(--nl-color); }
.log-card { background: var(--bg-card); border: 1px solid var(--border-tags); border-radius: var(--radius); padding: var(--page-card-padding); box-shadow: var(--shadow-card); }
.header-row { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.header-row h3 { margin: 0 0 7px; color: var(--nl-color-title); }
.header-row p { margin: 0; color: var(--nl-color-tip); }
</style>
