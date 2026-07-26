<template>
  <div class="app-container sales-channel-page">
    <section class="sales-channel-card sales-channel-filter-card">
      <el-form :inline="true" :model="queryParams" size="small" @submit.native.prevent>
        <el-form-item label="渠道编码">
          <el-input v-model="queryParams.channelCodeLike" clearable placeholder="请输入渠道编码" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="渠道名称">
          <el-input v-model="queryParams.channelNameLike" clearable placeholder="请输入渠道名称" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="平台名称">
          <el-input v-model="queryParams.platformNameLike" clearable placeholder="请输入平台名称" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="经营主体">
          <el-input v-model="queryParams.subjectNameLike" clearable placeholder="请输入经营主体" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item class="sales-channel-filter-actions">
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="sales-channel-card sales-channel-table-card">
      <div class="sales-channel-card-title">
        <div>
          <h3>销售渠道</h3>
          <p>数据由吉客云每日定时同步维护，仅供业务查询使用。</p>
        </div>
      </div>
      <div class="sales-channel-table-wrap">
        <el-table ref="channelTable" v-loading="loading" :data="filteredChannelList" height="100%" border stripe>
          <el-table-column
            v-for="column in visibleColumns"
            :key="column.key"
            :prop="column.key"
            :label="column.label"
            :min-width="column.minWidth"
            :show-overflow-tooltip="column.showOverflowTooltip"
          >
            <template #header>
              <div class="sales-channel-column-header">
                <FilterHeader
                  :label="column.label"
                  :value="columnSearch[column.key]"
                  :options="colFilterOptions[column.key] || []"
                  @update:value="updateColumnFilter(column.key, $event)"
                />
              </div>
            </template>
            <template slot-scope="scope">
              <span v-if="column.key === 'lastSyncTime'">{{ formatSyncTime(scope.row.lastSyncTime) }}</span>
              <span v-else>{{ scope.row[column.key] }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right" align="center">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="openDepositDialog(scope.row)">修改保证金</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </section>

    <section v-show="total > 0" class="sales-channel-card sales-channel-pagination-card">
      <pagination
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        :auto-scroll="false"
        @pagination="getList"
      />
    </section>

    <el-dialog title="修改保证金" :visible.sync="depositDialogVisible" width="420px" append-to-body>
      <el-form ref="depositForm" :model="depositForm" :rules="depositRules" label-width="96px">
        <el-form-item label="销售渠道">
          <span>{{ depositChannelName }}</span>
        </el-form-item>
        <el-form-item label="保证金金额" prop="depositAmount">
          <el-input-number
            v-model="depositForm.depositAmount"
            :min="0"
            :precision="2"
            :step="100"
            controls-position="right"
            class="sales-channel-deposit-input"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="depositDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="depositSubmitting" @click="submitDeposit">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Sortable from 'sortablejs'
import { getMasterSalesChannelList, updateMasterSalesChannelDeposit } from '@/api/master'
import { parseTime } from '@/utils/ruoyi'
import FilterHeader from '@/views/business/manage/components/FilterHeader.vue'

const SALES_CHANNEL_COLUMNS = [
  { key: 'channelCode', label: '渠道编码', minWidth: 130, showOverflowTooltip: true },
  { key: 'channelName', label: '渠道名称', minWidth: 180, showOverflowTooltip: true },
  { key: 'platformName', label: '平台', minWidth: 130, showOverflowTooltip: true },
  { key: 'subjectName', label: '经营主体', minWidth: 200, showOverflowTooltip: true },
  { key: 'depositAmount', label: '保证金', minWidth: 130, showOverflowTooltip: false },
  { key: 'lastSyncTime', label: '最后同步时间', minWidth: 180, showOverflowTooltip: false }
]

function createColumnSearch() {
  return SALES_CHANNEL_COLUMNS.reduce((search, column) => {
    search[column.key] = []
    return search
  }, {})
}

export default {
  name: 'MasterSalesChannel',
  components: { FilterHeader },
  data() {
    return {
      loading: false,
      total: 0,
      channelList: [],
      depositDialogVisible: false,
      depositSubmitting: false,
      depositChannelName: '',
      depositForm: {
        id: undefined,
        depositAmount: 0
      },
      depositRules: {
        depositAmount: [
          { required: true, message: '请输入保证金金额', trigger: 'blur' },
          { type: 'number', min: 0, message: '保证金金额不能小于0', trigger: 'blur' }
        ]
      },
      columnOrder: SALES_CHANNEL_COLUMNS.map(column => column.key),
      columnSearch: createColumnSearch(),
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        channelCodeLike: '',
        channelNameLike: '',
        platformNameLike: '',
        subjectNameLike: ''
      }
    }
  },
  created() {
    this.getList()
  },
  mounted() {
    this.$nextTick(this.initColumnDrag)
  },
  beforeDestroy() {
    this.destroyColumnDrag()
  },
  computed: {
    visibleColumns() {
      return this.columnOrder.map(key => SALES_CHANNEL_COLUMNS.find(column => column.key === key))
    },
    colFilterOptions() {
      return SALES_CHANNEL_COLUMNS.reduce((options, column) => {
        const values = this.channelList
          .map(row => row[column.key])
          .filter(value => value !== null && value !== undefined && value !== '')
        options[column.key] = [...new Set(values)].map(value => ({ text: String(value), value }))
        return options
      }, {})
    },
    filteredChannelList() {
      return this.channelList.filter(row => this.matchesColumnFilters(row))
    }
  },
  methods: {
    getList() {
      this.loading = true
      getMasterSalesChannelList(this.queryParams).then(response => {
        this.channelList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 20,
        channelCodeLike: '',
        channelNameLike: '',
        platformNameLike: '',
        subjectNameLike: ''
      }
      this.getList()
    },
    openDepositDialog(row) {
      this.depositChannelName = row.channelName
      this.depositForm = {
        id: row.id,
        depositAmount: Number(row.depositAmount || 0)
      }
      this.depositDialogVisible = true
      this.$nextTick(() => this.$refs.depositForm.clearValidate())
    },
    submitDeposit() {
      this.$refs.depositForm.validate(valid => {
        if (!valid) {
          return
        }
        this.depositSubmitting = true
        updateMasterSalesChannelDeposit(this.depositForm).then(() => {
          this.$modal.msgSuccess('保证金已更新')
          this.depositDialogVisible = false
          this.getList()
        }).finally(() => {
          this.depositSubmitting = false
        })
      })
    },
    updateColumnFilter(key, values) {
      this.$set(this.columnSearch, key, values)
    },
    formatSyncTime(value) {
      return value ? parseTime(value) : '-'
    },
    matchesColumnFilters(row) {
      return SALES_CHANNEL_COLUMNS.every(column => {
        const values = this.columnSearch[column.key]
        return !values.length || values.includes(row[column.key])
      })
    },
    initColumnDrag() {
      const table = this.$refs.channelTable
      const headerRow = table && table.$el.querySelector('.el-table__header-wrapper tr')
      if (!headerRow) {
        return
      }
      this.destroyColumnDrag()
      this.columnSortable = Sortable.create(headerRow, {
        animation: 150,
        fallbackTolerance: 6,
        onEnd: ({ oldIndex, newIndex }) => this.moveColumn(oldIndex, newIndex)
      })
    },
    destroyColumnDrag() {
      if (this.columnSortable) {
        this.columnSortable.destroy()
        this.columnSortable = null
      }
    },
    moveColumn(oldIndex, newIndex) {
      if (oldIndex === newIndex || oldIndex === undefined || newIndex === undefined) {
        return
      }
      const movedKey = this.columnOrder.splice(oldIndex, 1)[0]
      this.columnOrder.splice(newIndex, 0, movedKey)
      this.destroyColumnDrag()
      this.$nextTick(() => {
        this.$refs.channelTable.doLayout()
        this.initColumnDrag()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.sales-channel-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: calc(100vh - 112px);
  min-height: 0;
  overflow: hidden;
  box-sizing: border-box;
  color: var(--nl-color);
}

.sales-channel-card {
  padding: 20px;
  border-radius: 8px;
  background: var(--bg-card);
}

.sales-channel-filter-card :deep(.el-form-item) {
  margin-bottom: 0;
}

.sales-channel-filter-card {
  flex: 0 0 auto;
}

.sales-channel-table-card {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.sales-channel-table-wrap {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.sales-channel-table-wrap :deep(.el-table) {
  height: 100%;
}

.sales-channel-pagination-card {
  display: flex;
  flex: 0 0 auto;
  justify-content: flex-end;
  padding: 12px 24px;
}

.sales-channel-pagination-card :deep(.pagination-container) {
  margin-top: 0;
}

.sales-channel-filter-actions {
  margin-right: 0;
}

.sales-channel-card-title {
  margin-bottom: 16px;
}

.sales-channel-card-title h3,
.sales-channel-card-title p {
  margin: 0;
}

.sales-channel-deposit-input {
  width: 100%;
}

.sales-channel-column-header {
  display: flex;
  align-items: center;
  min-width: 0;
}

.sales-channel-column-header :deep(.filter-header-trigger) {
  min-width: 0;
  flex: 1;
}

.sales-channel-card-title h3 {
  font-size: 16px;
}

.sales-channel-card-title p {
  margin-top: 6px;
  color: var(--nl-color-weak);
  font-size: 13px;
}

@media (max-width: 768px) {
  .sales-channel-card {
    padding: 16px;
  }
}
</style>
