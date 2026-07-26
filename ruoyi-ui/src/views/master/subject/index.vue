<template>
  <div class="app-container master-subject-page">
    <section class="subject-card subject-filter-card">
      <el-form :inline="true" :model="queryParams" size="small" @submit.native.prevent>
        <el-form-item label="主体编码">
          <el-input v-model="queryParams.subjectCodeLike" clearable placeholder="请输入主体编码" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="主体名称">
          <el-input v-model="queryParams.subjectNameLike" clearable placeholder="请输入主体名称" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" clearable placeholder="全部状态">
            <el-option label="正常" value="NORMAL" />
            <el-option label="已删除" value="DELETED" />
          </el-select>
        </el-form-item>
        <el-form-item class="subject-filter-actions">
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="subject-card subject-table-card">
      <div class="subject-card-title">
        <div>
          <h3>主体配置</h3>
          <p>数据由吉客云定时同步维护，供供应链和经营分析统一使用。</p>
        </div>
      </div>
      <div class="subject-table-wrap">
        <el-table v-loading="loading" :data="subjectList" height="100%" border stripe>
          <el-table-column prop="subjectCode" label="主体编码" min-width="130" show-overflow-tooltip />
          <el-table-column prop="subjectName" label="主体名称" min-width="220" show-overflow-tooltip />
          <el-table-column prop="subjectShortName" label="主体简称" min-width="160" show-overflow-tooltip />
          <el-table-column label="状态" width="110" align="center">
            <template slot-scope="scope">
              <el-tag :type="scope.row.isDelete === 1 ? 'danger' : 'success'" size="mini">
                {{ scope.row.isDelete === 1 ? '已删除' : '正常' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="lastSyncTime" label="最后同步时间" min-width="180" />
          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button
                type="text"
                icon="el-icon-bank-card"
                @click="handleBankManage(scope.row)"
                v-hasPermi="['master:subject:bank:list']"
              >银行卡</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </section>

    <section v-show="total > 0" class="subject-card subject-pagination-card">
      <pagination
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        :auto-scroll="false"
        @pagination="getList"
      />
    </section>

    <subject-bank-dialog
      :visible.sync="bankDialogVisible"
      :subject-id="currentSubject.id"
      :subject-code="currentSubject.subjectCode"
      @changed="getList"
    />
  </div>
</template>

<script>
import { getMasterSubjectList } from '@/api/master'
import SubjectBankDialog from './components/SubjectBankDialog'

export default {
  name: 'MasterSubject',
  components: { SubjectBankDialog },
  data() {
    return {
      loading: false,
      total: 0,
      subjectList: [],
      bankDialogVisible: false,
      currentSubject: {},
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        subjectCodeLike: '',
        subjectNameLike: '',
        status: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const params = this.buildQueryParams()
      getMasterSubjectList(params).then(response => {
        this.subjectList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    buildQueryParams() {
      const params = { ...this.queryParams }
      if (params.status === 'DELETED') {
        params.isDelete = 1
      } else {
        params.isDelete = 0
      }
      delete params.status
      return params
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 20,
        subjectCodeLike: '',
        subjectNameLike: '',
        status: undefined
      }
      this.getList()
    },
    handleBankManage(row) {
      this.currentSubject = row
      this.bankDialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.master-subject-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: calc(100vh - 112px);
  min-height: 0;
  overflow: hidden;
  box-sizing: border-box;
  color: var(--nl-color);
}

.subject-card {
  padding: 20px;
  border-radius: 8px;
  background: var(--bg-card);
}

.subject-filter-card :deep(.el-form-item) {
  margin-bottom: 0;
}

.subject-filter-card {
  flex: 0 0 auto;
}

.subject-table-card {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.subject-table-wrap {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.subject-table-wrap :deep(.el-table) {
  height: 100%;
}

.subject-pagination-card {
  display: flex;
  flex: 0 0 auto;
  justify-content: flex-end;
  padding: 12px 24px;
}

.subject-pagination-card :deep(.pagination-container) {
  margin-top: 0;
}

.subject-filter-actions {
  margin-right: 0;
}

.subject-card-title {
  margin-bottom: 16px;
}

.subject-card-title h3,
.subject-card-title p {
  margin: 0;
}

.subject-card-title h3 {
  font-size: 16px;
}

.subject-card-title p {
  margin-top: 6px;
  color: var(--nl-color-weak);
  font-size: 13px;
}

@media (max-width: 768px) {
  .subject-card {
    padding: 16px;
  }
}
</style>
