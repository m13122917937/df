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
        <el-table v-loading="loading" :data="filteredSubjectList" height="100%" border stripe>
          <el-table-column
            v-for="column in tableColumns"
            :key="column.key"
            :prop="column.isAction ? undefined : column.key"
            :label="column.label"
            :min-width="column.minWidth"
            :show-overflow-tooltip="column.showOverflowTooltip"
          >
            <template #header>
              <div class="subject-column-header">
                <template v-if="column.isAction">{{ column.label }}</template>
                <FilterHeader
                  v-else
                  :label="column.label"
                  :value="columnSearch[column.key]"
                  :options="colFilterOptions[column.key] || []"
                  @update:value="updateColumnFilter(column.key, $event)"
                />
              </div>
            </template>
            <template slot-scope="scope">
              <template v-if="column.key === '_actions'">
                <el-button
                  type="text"
                  icon="el-icon-bank-card"
                  @click="handleBankManage(scope.row)"
                  v-hasPermi="['master:subject:bank:list']"
                >银行卡</el-button>
              </template>
              <template v-else-if="column.key === 'isDelete'">
                <el-tag :type="scope.row.isDelete === 1 ? 'danger' : 'success'" size="mini">
                  {{ getColumnValue(scope.row, column.key) }}
                </el-tag>
              </template>
              <template v-else>
                {{ getColumnValue(scope.row, column.key) }}
              </template>
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
import FilterHeader from '@/views/business/manage/components/FilterHeader.vue'

const ALL_COLUMNS = [
  { key: 'subjectCode', label: '主体编码', minWidth: 130, showOverflowTooltip: true },
  { key: 'subjectName', label: '主体名称', minWidth: 220, showOverflowTooltip: true },
  { key: 'subjectShortName', label: '主体简称', minWidth: 160, showOverflowTooltip: true },
  { key: 'isDelete', label: '状态', minWidth: 110, showOverflowTooltip: false },
  { key: 'lastSyncTime', label: '最后同步时间', minWidth: 180, showOverflowTooltip: false },
  { key: '_actions', label: '操作', minWidth: 120, showOverflowTooltip: false, isAction: true }
]

function createColumnSearch() {
  return ALL_COLUMNS.reduce((search, column) => {
    search[column.key] = []
    return search
  }, {})
}

export default {
  name: 'MasterSubject',
  components: { SubjectBankDialog, FilterHeader },
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
      },
      columnSearch: createColumnSearch()
    }
  },
  computed: {
    tableColumns() { return ALL_COLUMNS },
    filteredSubjectList() {
      return this.subjectList.filter(row => this.matchesColumnFilters(row))
    },
    colFilterOptions() {
      return ALL_COLUMNS.reduce((options, column) => {
        if (column.isAction) return options
        const values = this.subjectList
          .map(row => this.getColumnValue(row, column.key))
          .filter(value => value !== '-')
        options[column.key] = [...new Set(values)].map(value => ({ text: value, value }))
        return options
      }, {})
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
    },
    getColumnValue(row, key) {
      if (key === 'isDelete') {
        return row.isDelete === 1 ? '已删除' : '正常'
      }
      return row[key] || '-'
    },
    updateColumnFilter(key, values) {
      this.$set(this.columnSearch, key, values)
    },
    matchesColumnFilters(row) {
      return ALL_COLUMNS.every(column => {
        if (column.isAction) return true
        const values = this.columnSearch[column.key]
        return !values.length || values.includes(this.getColumnValue(row, column.key))
      })
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

.subject-column-header {
  display: flex;
  align-items: center;
  min-width: 0;
}

.subject-column-header :deep(.filter-header-trigger) {
  flex: 1;
  min-width: 0;
}

@media (max-width: 768px) {
  .subject-card {
    padding: 16px;
  }
}
</style>
