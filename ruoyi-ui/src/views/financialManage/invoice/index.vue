<!-- 发票管理 -->
<template>
  <div class="app-container invoice-page">
    <!-- 筛选区 -->
    <div class="search-card">
      <el-form
        :model="queryForm"
        ref="queryForm"
        :inline="true"
        @submit.native.prevent
        class="query-form"
      >
        <el-form-item label="发票号码">
          <el-input
            v-model="queryForm.invoiceNo"
            placeholder="请输入"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input
            v-model="queryForm.vendorName"
            placeholder="请输入"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryForm.status"
            placeholder="全部"
            clearable
            style="width: 140px"
          >
            <el-option label="待开票" :value="0" />
            <el-option label="已开票" :value="1" />
            <el-option label="已抵扣" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <div class="table-wrapper">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
        height="100%"
      >
        <el-table-column label="发票号码" prop="invoiceNo" min-width="180" />
        <el-table-column label="发票类型" prop="invoiceType" min-width="100" />
        <el-table-column label="金额" prop="amount" min-width="140" align="right">
          <template slot-scope="scope">
            {{ scope.row.amount != null ? '¥' + Number(scope.row.amount).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="税额" prop="taxAmount" min-width="120" align="right">
          <template slot-scope="scope">
            {{ scope.row.taxAmount != null ? '¥' + Number(scope.row.taxAmount).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="供应商" prop="vendorName" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="statusTag(scope.row.status)" size="small">
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="开票日期" prop="invoiceDate" width="120" />
        <el-table-column label="创建时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="handleDetail(scope.row)">详情</el-button>
            <el-button type="text" size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="mini" class="danger-text" @click="handleRemove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-wrapper">
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryForm.pageNum"
        :limit.sync="queryForm.pageSize"
        :page-sizes="[30, 50, 100]"
        @pagination="loadData"
      />
    </div>
  </div>
</template>

<script>
import { pageInvoiceApi, getInvoiceApi, removeInvoiceApi } from '@/api/invoice'

export default {
  name: 'InvoiceIndex',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      queryForm: {
        pageNum: 1,
        pageSize: 30,
        invoiceNo: '',
        vendorName: '',
        status: null,
      },
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    statusText(s) {
      const map = { 0: '待开票', 1: '已开票', 2: '已抵扣' }
      return map[s] ?? '-'
    },
    statusTag(s) {
      const map = { 0: 'info', 1: 'warning', 2: 'success' }
      return map[s] ?? 'info'
    },
    buildPayload() {
      const p = { ...this.queryForm }
      Object.keys(p).forEach(k => { if (p[k] === '' || p[k] === null) delete p[k] })
      return p
    },
    async loadData() {
      this.loading = true
      try {
        const res = await pageInvoiceApi(this.buildPayload())
        this.tableData = res.rows || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleQuery() {
      this.queryForm.pageNum = 1
      this.loadData()
    },
    handleReset() {
      this.queryForm = { pageNum: 1, pageSize: 30, invoiceNo: '', vendorName: '', status: null }
      this.handleQuery()
    },
    async handleDetail(row) {
      const res = await getInvoiceApi(row.id)
      const d = res.data || res
      this.$alert(this.formatDetail(d), '发票详情', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '关闭',
      })
    },
    formatDetail(d) {
      return `
        <div style="line-height:1.8">
          <div>发票号码：${d.invoiceNo || '-'}</div>
          <div>发票类型：${d.invoiceType || '-'}</div>
          <div>金额：${d.amount != null ? '¥' + Number(d.amount).toLocaleString() : '-'}</div>
          <div>税额：${d.taxAmount != null ? '¥' + Number(d.taxAmount).toLocaleString() : '-'}</div>
          <div>供应商：${d.vendorName || '-'}</div>
          <div>状态：${this.statusText(d.status)}</div>
          <div>开票日期：${d.invoiceDate || '-'}</div>
          <div>创建人：${d.createName || '-'}</div>
          <div>创建时间：${d.createTime || '-'}</div>
          <div>备注：${d.remark || '-'}</div>
        </div>`
    },
    async handleEdit(row) {
      const res = await getInvoiceApi(row.id)
      this.$message.info('编辑功能待对接')
    },
    async handleRemove(row) {
      await this.$confirm('确认删除该发票记录？', '提示', { type: 'warning' })
      await removeInvoiceApi(row.id)
      this.$message.success('已删除')
      this.loadData()
    },
  },
}
</script>

<style lang="scss" scoped>
.invoice-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 112px);
  background: var(--bg-page);
  padding: 12px;
  box-sizing: border-box;
  overflow: hidden;

  .search-card {
    flex-shrink: 0;
    background: var(--adm-card);
    border-radius: var(--adm-radius-card);
    box-shadow: var(--adm-shadow-card);
    padding: 16px;
    margin-bottom: 12px;
    .query-form .el-form-item { margin-bottom: 0; }
  }
  .table-wrapper {
    flex: 1;
    overflow: hidden;
    min-height: 0;
    background: var(--adm-card);
    border-radius: var(--adm-radius-card);
    box-shadow: var(--adm-shadow-card);
  }
  .danger-text { color: #f56c6c; }
  .pagination-wrapper {
    flex-shrink: 0;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 12px 24px;
    margin-top: 12px;
    background: var(--adm-card);
    border-radius: var(--adm-radius-card);
    box-shadow: var(--adm-shadow-card);
  }
}
</style>
