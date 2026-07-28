<template>
  <div class="app-container analysis-config-page">
    <section class="config-card">
      <div class="title-row">
        <div>
          <h3>平台服务费率</h3>
          <p>按平台、业态、品类三个维度配置不同的平台服务费费率（百分比）。</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" icon="el-icon-plus" @click="openDialog()">新增费率</el-button>
        </div>
      </div>
      <el-form :inline="true" :model="query" size="small">
        <el-form-item label="平台">
          <el-select v-model="query.platform" clearable filterable>
            <el-option v-for="p in platformOptions" :key="p" :label="p" :value="p" />
          </el-select>
        </el-form-item>
        <el-form-item label="业态">
          <el-select v-model="query.businessType" clearable filterable>
            <el-option v-for="(label, value) in businessTypeOptions" :key="value" :label="label" :value="value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="loadData">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="config-card table-card">
      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="platform" label="平台" width="120" />
        <el-table-column prop="businessType" label="业态" width="100">
          <template slot-scope="scope">
            {{ scope.row.businessType != null ? businessTypeOptions[scope.row.businessType] || scope.row.businessType : '全部' }}
          </template>
        </el-table-column>
        <el-table-column prop="category" label="品类" min-width="140">
          <template slot-scope="scope">
            {{ scope.row.category || '全部' }}
          </template>
        </el-table-column>
        <el-table-column prop="feeRate" label="费率(%)" width="120" align="right">
          <template slot-scope="scope">
            {{ scope.row.feeRate != null ? scope.row.feeRate.toFixed(4) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column prop="updatedTime" label="更新时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="openDialog(scope.row)">修改</el-button>
            <el-button size="mini" type="text" style="color:#f56c6c" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="loadData" />
    </section>

    <el-dialog :title="form.id ? '修改费率' : '新增费率'" :visible.sync="dialogVisible" width="500px" @closed="resetForm">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="small">
        <el-form-item label="平台" prop="platform">
          <el-select v-model="form.platform" filterable :disabled="!!form.id" style="width:100%">
            <el-option v-for="p in platformOptions" :key="p" :label="p" :value="p" />
          </el-select>
        </el-form-item>
        <el-form-item label="业态" prop="businessType">
          <el-select v-model="form.businessType" clearable placeholder="全部业态" style="width:100%">
            <el-option v-for="(label, value) in businessTypeOptions" :key="value" :label="label" :value="value" />
          </el-select>
        </el-form-item>
        <el-form-item label="品类" prop="category">
          <el-select v-model="form.category" clearable filterable allow-create placeholder="全部品类（可选填）" style="width:100%">
            <el-option v-for="c in categoryOptions" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="费率(%)" prop="feeRate">
          <el-input-number v-model="form.feeRate" :precision="4" :min="0" :controls="false" style="width:100%" placeholder="如 2.5000 = 2.5%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button size="small" @click="dialogVisible = false">取消</el-button>
        <el-button size="small" type="primary" @click="handleSave">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAnalysisStoreOptions } from '@/api/analysis'
import {
  getPlatformFeeRateList,
  savePlatformFeeRate,
  deletePlatformFeeRate
} from '@/api/analysis'

export default {
  name: 'PlatformFeeRate',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      query: {
        platform: '',
        businessType: null
      },
      queryParams: {
        pageNum: 1,
        pageSize: 20
      },
      dialogVisible: false,
      form: {},
      rules: {
        platform: [{ required: true, message: '请选择平台', trigger: 'change' }],
        feeRate: [{ required: true, message: '请填写费率', trigger: 'blur' }]
      },
      platformOptions: [],
      businessTypeOptions: {},
      categoryOptions: []
    }
  },
  created() {
    this.loadStoreOptions()
    this.loadData()
  },
  methods: {
    async loadStoreOptions() {
      const res = await getAnalysisStoreOptions()
      const stores = res.data || []
      const platforms = [...new Set(stores.map(s => s.platformName).filter(Boolean))]
      this.platformOptions = platforms
    },
    async loadData() {
      this.loading = true
      try {
        const params = { ...this.query, ...this.queryParams }
        if (!params.platform) delete params.platform
        if (params.businessType == null) delete params.businessType
        const res = await getPlatformFeeRateList(params)
        const data = res.data || res
        this.list = Array.isArray(data) ? data : []
        this.total = data.total || this.list.length
      } finally {
        this.loading = false
      }
    },
    resetQuery() {
      this.query = { platform: '', businessType: null }
      this.queryParams.pageNum = 1
      this.loadData()
    },
    openDialog(row) {
      this.form = row ? { ...row } : { platform: '', businessType: null, category: '', feeRate: null, remark: '' }
      this.dialogVisible = true
    },
    resetForm() {
      this.$refs.form && this.$refs.form.resetFields()
      this.form = {}
    },
    async handleSave() {
      await this.$refs.form.validate()
      await savePlatformFeeRate(this.form)
      this.$message.success('保存成功')
      this.dialogVisible = false
      this.loadData()
    },
    async handleDelete(row) {
      await this.$confirm(`确认删除"${row.platform}"的费率配置？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
      await deletePlatformFeeRate(row.id)
      this.$message.success('删除成功')
      this.loadData()
    },
  }
}
</script>

<style scoped>
.analysis-config-page .title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}
.analysis-config-page .title-row h3 {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
}
.analysis-config-page .title-row p {
  margin: 0;
  color: #909399;
  font-size: 13px;
}
.analysis-config-page .header-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.config-card {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 16px;
}
.table-card {
  padding: 8px 20px 20px;
}
</style>
