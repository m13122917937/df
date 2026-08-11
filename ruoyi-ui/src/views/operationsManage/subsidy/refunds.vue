<template>
  <div class="app-container">
    <el-card shadow="never">
      <el-form :inline="true"><el-form-item label="退款状态"><el-select v-model="query.refundStatus" clearable><el-option label="待审核" value="APPLYING" /><el-option label="退款中" value="REFUNDING" /><el-option label="已退款" value="REFUNDED" /><el-option label="退款失败" value="FAILED" /></el-select></el-form-item><el-button type="primary" @click="load">查询</el-button></el-form>
      <el-table v-loading="loading" :data="rows" border>
        <el-table-column prop="refundNo" label="退款单号" min-width="210" />
        <el-table-column prop="orderId" label="订单 ID" width="100" />
        <el-table-column prop="amount" label="退款金额" width="110" />
        <el-table-column prop="reason" label="退款原因" min-width="180" show-overflow-tooltip />
        <el-table-column prop="refundStatus" label="状态" width="120" />
        <el-table-column label="操作" width="110"><template slot-scope="scope"><el-button v-if="scope.row.refundStatus === 'APPLYING'" type="text" @click="approve(scope.row)">审核退款</el-button></template></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { approveSubsidyRefund, getSubsidyRefunds } from '@/api/subsidy'

export default {
  name: 'SubsidyRefunds',
  data() { return { loading: false, rows: [], query: { refundStatus: '' } } },
  created() { this.load() },
  methods: {
    load() { this.loading = true; getSubsidyRefunds(this.query).then(response => { this.rows = response.data || [] }).finally(() => { this.loading = false }) },
    approve(row) { this.$confirm('确认发起微信原路退款？', '退款审核', { type: 'warning' }).then(() => approveSubsidyRefund(row.refundNo)).then(() => { this.$message.success('退款请求已发起'); this.load() }).catch(() => {}) }
  }
}
</script>
