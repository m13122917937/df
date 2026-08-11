<template>
  <div class="app-container">
    <el-alert title="身份冲突不可自动合并。请由管理员核验会员资料后在线下完成处理。" type="warning" :closable="false" show-icon class="notice" />
    <el-card shadow="never">
      <el-form :inline="true"><el-form-item label="状态"><el-input v-model="query.status" clearable placeholder="例如 PENDING" /></el-form-item><el-button type="primary" @click="load">查询</el-button></el-form>
      <el-table v-loading="loading" :data="rows" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="UnionID" min-width="200"><template slot-scope="scope">{{ maskUnionId(scope.row.unionId) }}</template></el-table-column>
        <el-table-column prop="sourceMemberId" label="来源会员" width="100" />
        <el-table-column prop="targetMemberId" label="目标会员" width="100" />
        <el-table-column prop="conflictStatus" label="冲突状态" width="120" />
        <el-table-column prop="handleRemark" label="处理备注" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getWechatIdentityConflicts } from '@/api/subsidy'

export default {
  name: 'SubsidyIdentityConflicts',
  data() { return { loading: false, rows: [], query: { status: '' } } },
  created() { this.load() },
  methods: {
    load() { this.loading = true; getWechatIdentityConflicts(this.query).then(response => { this.rows = response.data || [] }).finally(() => { this.loading = false }) },
    maskUnionId(value) { if (!value || value.length < 9) return '***'; return value.substring(0, 3) + '***' + value.substring(value.length - 3) }
  }
}
</script>

<style lang="scss" scoped>
.notice { margin-bottom: 16px; }
</style>
