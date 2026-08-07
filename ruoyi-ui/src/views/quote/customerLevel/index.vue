<template>
  <div class="app-container quote-customer-level-page">
    <el-card shadow="never" class="quote-customer-level-card">
      <div slot="header" class="quote-customer-level-card-header">
        <span>客户层级设置</span>
        <span class="quote-customer-level-tip">未设置的客户默认为零售层级</span>
      </div>

      <el-table v-loading="loading" :data="levelList" border stripe size="medium">
        <el-table-column prop="companyId" label="客户ID" min-width="100" align="center" />
        <el-table-column prop="companyName" label="客户名称" min-width="240" show-overflow-tooltip />
        <el-table-column label="客户层级" min-width="220" align="center">
          <template slot-scope="scope">
            <el-select v-model="scope.row.level" size="small" style="width: 140px">
              <el-option label="零售" :value="0" />
              <el-option label="批发1" :value="1" />
              <el-option label="批发2" :value="2" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-check" :loading="saving" @click="handleSave(scope.row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script>
import { getQuoteCustomerLevelPage, saveQuoteCustomerLevel } from '@/api/quote'

export default {
  name: 'QuoteCustomerLevel',
  data() {
    return {
      loading: false,
      saving: false,
      total: 0,
      levelList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 20
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getQuoteCustomerLevelPage().then((response) => {
        this.levelList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleSave(row) {
      this.saving = true
      saveQuoteCustomerLevel({
        companyId: row.companyId,
        level: row.level
      }).then(() => {
        this.$message.success(`客户“${row.companyName}”层级已保存`)
        this.saving = false
      }).catch(() => {
        this.saving = false
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.quote-customer-level-page {
  .quote-customer-level-card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .quote-customer-level-tip {
    color: #909399;
    font-size: 12px;
  }
}
</style>
