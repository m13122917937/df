<template>
  <div class="business-manage-container">
    <!-- 搜索组件 -->
    <div class="search-card">
    <SearchSection
      :form-data="searchForm"
      @search="handleSearch"
      @reset="handleReset"
    >
      <template #table-btn>
        <el-button
          class="add-company-btn"
          type="success"
          icon="el-icon-plus"
          @click="handleChange(true)"
          >新增企业</el-button
        >
      </template>
    </SearchSection>
    </div>

    <!-- 数据表格区域 -->
    <div class="table-section">
      <el-table
        ref="table"
        :header-cell-style="{
          background: '#f7f8fa',
          color: '#606266',
          fontWeight: 600,
        }"
        :cell-style="{ padding: '8px 0' }"
        :data="filteredTableData"
        v-loading="loading"
        border
        style="width: 100%"
        height="100%"
        stripe
        @header-contextmenu="handleHeaderContextMenu"
      >
        <!-- 空数据状态 -->
        <template slot="empty">
          <EmptyState text="暂无企业数据" />
        </template>
        <!-- 列配置触发器（有隐藏列时显示） -->
        <div slot="append" v-if="hasHidden" class="restore-columns-bar" @click="resetColumnDefault">
          有隐藏的列，点击恢复
        </div>
        <!-- ====== 列定义（所有列支持动态筛选，选项从表格数据自动提取） ====== -->
        <el-table-column v-if="colVisible.companyName" prop="companyName" label="企业名称" min-width="180" show-overflow-tooltip>
          <template slot="header">
            <FilterHeader label="企业名称" :value="columnSearch.companyName" :options="colFilterOptions.companyName || []" @update:value="columnSearch.companyName = $event" />
          </template>
        </el-table-column>
        <el-table-column v-if="colVisible.nickName" prop="nickName" label="企业别名" width="150" show-overflow-tooltip>
          <template slot="header">
            <FilterHeader label="企业别名" :value="columnSearch.nickName" :options="colFilterOptions.nickName || []" @update:value="columnSearch.nickName = $event" />
          </template>
        </el-table-column>
        <el-table-column v-if="colVisible.corporateName" prop="corporateName" label="法人" width="150" show-overflow-tooltip>
          <template slot="header">
            <FilterHeader label="法人" :value="columnSearch.corporateName" :options="colFilterOptions.corporateName || []" @update:value="columnSearch.corporateName = $event" />
          </template>
        </el-table-column>
        <el-table-column v-if="colVisible.establishment" prop="establishment" label="成立时间" width="140" show-overflow-tooltip>
          <template slot="header">
            <FilterHeader label="成立时间" :value="columnSearch.establishment" :options="colFilterOptions.establishment || []" @update:value="columnSearch.establishment = $event" />
          </template>
        </el-table-column>
        <el-table-column v-if="colVisible.province" prop="province" label="省" width="120" show-overflow-tooltip>
          <template slot="header">
            <FilterHeader label="省" :value="columnSearch.province" :options="colFilterOptions.province || []" @update:value="columnSearch.province = $event" />
          </template>
        </el-table-column>
        <el-table-column v-if="colVisible.city" prop="city" label="市" width="120" show-overflow-tooltip>
          <template slot="header">
            <FilterHeader label="市" :value="columnSearch.city" :options="colFilterOptions.city || []" @update:value="columnSearch.city = $event" />
          </template>
        </el-table-column>
        <el-table-column v-if="colVisible.stopPurchase" label="停款状态" width="120">
          <template slot="header">
            <FilterHeader label="停款状态" :value="columnSearch.stopPurchase" :options="colFilterOptions.stopPurchase || []" @update:value="columnSearch.stopPurchase = $event" />
          </template>
          <template slot-scope="scope">
            <el-tag :type="scope.row.stopPurchase === 0 ? 'danger' : 'success'" size="small">
              {{ scope.row.stopPurchase === 0 ? '停款' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="colVisible.accountingPeriod" label="账期类型" width="120">
          <template slot="header">
            <FilterHeader label="账期类型" :value="columnSearch.accountingPeriod" :options="colFilterOptions.accountingPeriod || []" @update:value="columnSearch.accountingPeriod = $event" />
          </template>
          <template slot-scope="scope">{{ formatAccountingPeriod(scope.row.accountingPeriod) }}</template>
        </el-table-column>
        <el-table-column v-if="colVisible.grabStatus" label="是否禁抢" width="120">
          <template slot="header">
            <FilterHeader label="是否禁抢" :value="columnSearch.grabStatus" :options="colFilterOptions.grabStatus || []" @update:value="columnSearch.grabStatus = $event" />
          </template>
          <template slot-scope="scope">
            <el-tag :type="scope.row.grabStatus === 1 ? 'danger' : 'success'" size="small">
              {{ scope.row.grabStatus === 1 ? '禁止' : '未禁止' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="colVisible.contractAuthStatus" label="合同认证" width="100">
          <template slot="header">
            <FilterHeader label="合同认证" :value="columnSearch.contractAuthStatus" :options="colFilterOptions.contractAuthStatus || []" @update:value="columnSearch.contractAuthStatus = $event" />
          </template>
          <template slot-scope="scope">
            <el-tag :type="scope.row.contractAuthStatus === 1 ? 'success' : 'warning'" size="small">
              {{ scope.row.contractAuthStatus === 1 ? '已认证' : '未认证' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 操作列（始终显示） -->
        <el-table-column label="操作" fixed="right" width="300">
          <template slot-scope="scope">
            <div class="table-action-btn">
              <el-button size="mini" type="primary" @click="handleChange(false, scope.row)">编辑企业</el-button>
              <el-button size="mini" type="info" @click="handleEdit(scope.row)">子账户信息</el-button>
              <el-button size="mini" @click="handleAccountCard(scope.row)">财务卡号</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[30, 50, 100]"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>

    <!-- 子账户信息弹框 -->
    <SubAccountDialog
      ref="subAccountDialog"
      :visible.sync="subAccountDialogVisible"
      :company-name="currentCompanyName"
      :company-id="currentCompanyId"
      @add-user="handleAddUser"
    />
    <AddUserDialog
      :visible.sync="addUserDialogVisible"
      :form-data="addUserForm"
      :company-id="currentCompanyId"
      @reset-form="handleResetAddUserForm"
      @save="handleSaveUser"
    />
    <AccountCardDialog
      ref="accountCardDialog"
      :visible.sync="accountCardDialogVisible"
      :company-id="currentCompanyId"
      @open-form="handleOpenAccountForm"
    />
    <AccountFormDialog
      :visible.sync="accountFormDialogVisible"
      :edit-data="accountFormData"
      :is-edit="isEditAccount"
      :company-id="currentCompanyId"
      @success="handleAccountFormSuccess"
    />
    <CompanyFormDialog
      :visible.sync="companyFormDialogVisible"
      :edit-data="companyFormData"
      :is-edit="isEditCompany"
      @save="handleAddOrEditCompanySave"
    />
  </div>
</template>

<script>
import FilterHeader from "./components/FilterHeader.vue";
import SearchSection from "./components/searchSection.vue";
import SubAccountDialog from "./components/subAccountDialog.vue";
import AddUserDialog from "./components/addUserDialog.vue";
import AccountCardDialog from "./components/accountCardDialog.vue";
import AccountFormDialog from "./components/accountFormDialog.vue";
import CompanyFormDialog from "./components/companyFormDialog.vue";
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import { getBusinessCompanyListApi } from "@/api/business";

const STORAGE_KEY = 'column_config_business_manage'

// 全部列定义（顺序即默认顺序）
const ALL_COLUMNS = [
  { key: 'companyName', label: '企业名称' },
  { key: 'nickName', label: '企业别名' },
  { key: 'corporateName', label: '法人' },
  { key: 'establishment', label: '成立时间' },
  { key: 'province', label: '省' },
  { key: 'city', label: '市' },
  { key: 'stopPurchase', label: '停款状态' },
  { key: 'accountingPeriod', label: '账期类型' },
  { key: 'grabStatus', label: '是否禁抢' },
  { key: 'contractAuthStatus', label: '合同认证' },
]

const VALUE_LABELS = {
  stopPurchase: { 0: '停款', 1: '正常' },
  grabStatus: { 0: '未禁止', 1: '禁止' },
  contractAuthStatus: { 0: '未认证', 1: '已认证' },
  accountingPeriod: { 0: '现款', 1: 'T+1', 2: 'T+2', 3: 'T+3', 4: 'T+4', 5: 'T+5', 6: 'T+6', 7: 'T+7' },
}

function loadSaved() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch { return null }
}

function saveToLocal(visibleMap) {
  try {
    const data = ALL_COLUMNS.map(c => ({ key: c.key, visible: visibleMap[c.key] !== false }))
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data))
  } catch { /* ignore */ }
}

export default {
  name: "BusinessManage",
  components: {
    FilterHeader, SearchSection, SubAccountDialog, AddUserDialog,
    AccountCardDialog, AccountFormDialog, CompanyFormDialog,
    EmptyState,
  },
  data() {
    return {
      loading: false,
      subAccountDialogVisible: false,
      currentCompanyName: "",
      currentCompanyId: "",
      addUserDialogVisible: false,
      addUserForm: { nickName: "", phone: "", owner: 0 },
      accountCardDialogVisible: false,
      accountFormDialogVisible: false,
      accountFormData: {},
      isEditAccount: false,
      companyFormDialogVisible: false,
      companyFormData: {},
      isEditCompany: false,
      searchForm: { companyNameLike: "", nickNameLike: "", province: "", city: "" },
      colVisible: {},
      columnSearch: { companyName: [], nickName: [], corporateName: [], establishment: [], province: [], city: [], stopPurchase: [], grabStatus: [], contractAuthStatus: [], accountingPeriod: [] },
      tableData: [],
      pagination: { currentPage: 1, pageSize: 30, total: 0 },
    }
  },
  computed: {
    hasHidden() {
      return Object.values(this.colVisible).some(v => v === false)
    },
    // 每列的筛选选项（基于表格数据动态生成，select 列使用显示标签）
    colFilterOptions() {
      const result = {}
      ALL_COLUMNS.forEach(c => {
        const rawValues = [...new Set(this.tableData.map(r => r[c.key]).filter(v => v !== null && v !== undefined && v !== ''))]
        result[c.key] = rawValues.map(v => ({
          text: VALUE_LABELS[c.key] ? (VALUE_LABELS[c.key][v] ?? String(v)) : String(v),
          value: v,
        }))
      })
      return result
    },
    // 根据每列选中的值对表格数据进行过滤（多选 OR 逻辑）
    filteredTableData() {
      let data = this.tableData
      ALL_COLUMNS.forEach(c => {
        const vals = this.columnSearch[c.key]
        if (!vals || vals.length === 0) return
        data = data.filter(row => vals.includes(row[c.key]))
      })
      return data
    },
  },
  created() {
    this.initVisibility()
    this.loadTableData()
  },
  mounted() {
    this.$nextTick(() => this.initColumnDrag())
  },
  methods: {
    // 从 localStorage 恢复列可见性，默认全部可见
    initVisibility() {
      const visible = {}
      ALL_COLUMNS.forEach(c => { visible[c.key] = true })
      const saved = loadSaved()
      if (saved) {
        saved.forEach(s => { visible[s.key] = s.visible !== false })
      }
      this.colVisible = visible
    },
    // 表头拖拽排序
    initColumnDrag() {
      const el = this.$refs.table?.$el
      if (!el) return
      const thead = el.querySelector('.el-table__header-wrapper thead') || el.querySelector('.el-table__header thead')
      if (!thead) return
      const ths = thead.querySelector('tr')?.querySelectorAll('th')
      // 至少需要 2 列（数据列 + 操作列）才能拖拽
      if (!ths || ths.length < 3) return // 至少 2 列 + 操作列

      if (this._colDragCleanup) { this._colDragCleanup(); this._colDragCleanup = null }

      const labelKeyMap = {}
      ALL_COLUMNS.forEach(c => { labelKeyMap[c.label] = c.key })

      let dragIdx = -1
      const cleanups = []

      // 给 th 打上 data-col-key 标记
      ths.forEach((th, i) => {
        // 优先从 FilterHeader 的 label 元素取文本，否则回退到 .cell 文本
        const labelSpan = th.querySelector('.cell .filter-header-label')
        const labelEl = labelSpan || th.querySelector('.cell')
        if (labelEl) {
          const label = labelEl.textContent.trim()
          if (labelKeyMap[label]) {
            th.setAttribute('data-col-key', labelKeyMap[label])
          }
        }
        // 跳过操作列（最后一个 th）— 标记后跳过
        const isAction = !th.hasAttribute('data-col-key')
        if (isAction) return

        th.draggable = true
        th.style.cursor = 'move'
        const onStart = () => { dragIdx = i; th.style.opacity = '0.4'; th.classList.add('col-dragging') }
        const onOver = (e) => { e.preventDefault(); if (i !== dragIdx) th.classList.add('col-drop-target') }
        const onLeave = () => th.classList.remove('col-drop-target')
        const onDrop = () => {
          th.classList.remove('col-drop-target')
          if (dragIdx !== -1 && dragIdx !== i) this.handleReorder(dragIdx, i)
          ths.forEach(t => { t.style.opacity = ''; t.classList.remove('col-dragging', 'col-drop-target') })
          dragIdx = -1
        }
        const onEnd = () => {
          ths.forEach(t => { t.style.opacity = ''; t.classList.remove('col-dragging', 'col-drop-target') })
          dragIdx = -1
        }
        th.addEventListener('dragstart', onStart)
        th.addEventListener('dragover', onOver)
        th.addEventListener('dragleave', onLeave)
        th.addEventListener('drop', onDrop)
        th.addEventListener('dragend', onEnd)
        cleanups.push(() => {
          th.removeEventListener('dragstart', onStart); th.removeEventListener('dragover', onOver)
          th.removeEventListener('dragleave', onLeave); th.removeEventListener('drop', onDrop)
          th.removeEventListener('dragend', onEnd); th.draggable = false
        })
      })
      this._colDragCleanup = () => cleanups.forEach(fn => fn())
    },
    // 拖拽交换时：移动 th 和对应的 td
    handleReorder(from, to) {
      const el = this.$refs.table?.$el
      if (!el) return

      const headerRow = (el.querySelector('.el-table__header-wrapper thead') || el.querySelector('.el-table__header thead'))?.querySelector('tr')
      if (!headerRow) return

      const ths = Array.from(headerRow.children)
      const fromTH = ths[from]
      const toTH = ths[to]
      if (!fromTH || !toTH) return

      // 在 DOM 中移动 th
      if (to > from) {
        toTH.parentNode.insertBefore(fromTH, toTH.nextSibling)
      } else {
        toTH.parentNode.insertBefore(fromTH, toTH)
      }

      // 移动 body 中所有行的对应 td
      const bodyWrappers = el.querySelectorAll('.el-table__body-wrapper tbody')
      bodyWrappers.forEach(tbody => {
        const rows = tbody.querySelectorAll('tr')
        rows.forEach(row => {
          const tds = row.children
          if (tds[from] && tds[to]) {
            if (to > from) {
              tds[from].parentNode.insertBefore(tds[from], tds[to].nextSibling)
            } else {
              tds[from].parentNode.insertBefore(tds[from], tds[to])
            }
          }
        })
      })

      // 同步固定列（如果有）
      const fixedWrappers = el.querySelectorAll('.el-table__fixed-body-wrapper tbody, .el-table__fixed-header-wrapper thead')
      fixedWrappers.forEach(wrapper => {
        const rows = wrapper.querySelectorAll('tr')
        rows.forEach(row => {
          const cells = row.children
          if (cells[from] && cells[to]) {
            if (to > from) {
              cells[from].parentNode.insertBefore(cells[from], cells[to].nextSibling)
            } else {
              cells[from].parentNode.insertBefore(cells[from], cells[to])
            }
          }
        })
      })
    },
    // 右键表头 → 隐藏该列
    handleHeaderContextMenu(column, event) {
      event.preventDefault()
      const labelMap = {}
      ALL_COLUMNS.forEach(c => { labelMap[c.label] = c.key })
      // column.label 在 Element UI 中可通过 column 获取
      const label = column.label
      const key = labelMap[label]
      if (!key || !this.colVisible[key]) return
      this.$confirm(`隐藏「${label}」列？`, '提示', {
        confirmButtonText: '隐藏',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$set(this.colVisible, key, false)
        saveToLocal(this.colVisible)
      }).catch(() => {})
    },
    // 恢复所有隐藏列
    resetColumnDefault() {
      const v = {}
      ALL_COLUMNS.forEach(c => { v[c.key] = true })
      this.colVisible = v
      saveToLocal(this.colVisible)
    },
    handleSearch(searchData) {
      this.searchForm = { ...searchData }
      this.pagination.currentPage = 1
      this.loadTableData()
    },
    handleReset(searchData) {
      this.searchForm = { ...searchData }
      this.pagination.currentPage = 1
      this.loadTableData()
    },
    handleEdit(row) {
      this.currentCompanyName = row.companyName
      this.currentCompanyId = row.id
      this.subAccountDialogVisible = true
    },
    handleAddUser() {
      this.addUserForm = { ...this.addUserForm, companyId: this.currentCompanyId }
      this.addUserDialogVisible = true
    },
    handleResetAddUserForm() {
      this.addUserForm = { nickName: "", phone: "", companyId: "", owner: 0 }
    },
    handleSaveUser() {
      this.$message.success("用户添加成功")
      this.addUserDialogVisible = false
      this.$refs.subAccountDialog && this.$refs.subAccountDialog.getSubAccountList()
    },
    async loadTableData() {
      this.loading = true
      const params = { ...this.searchForm }
      const pageData = { pageNum: this.pagination.currentPage, pageSize: this.pagination.pageSize }
      const res = await getBusinessCompanyListApi(params, pageData)
      if (res.code === 200) { this.tableData = res.rows; this.pagination.total = res.total }
      this.loading = false
    },
    handleChange(isAdd, row) {
      this.companyFormDialogVisible = true
      this.isEditCompany = !isAdd
      this.companyFormData = isAdd ? {} : { ...row }
    },
    handleAddOrEditCompanySave() { this.loadTableData() },
    async handleAccountCard(row) { this.currentCompanyId = row.id; this.accountCardDialogVisible = true },
    handleOpenAccountForm(data, isEdit = false) {
      this.accountFormData = data || {}; this.isEditAccount = isEdit; this.accountFormDialogVisible = true
    },
    handleAccountFormSuccess() {
      this.accountFormDialogVisible = false; this.accountFormData = {}; this.isEditAccount = false
      if (this.$refs.accountCardDialog) this.$refs.accountCardDialog.getAccountList()
    },
    handleSizeChange(val) { this.pagination.pageSize = val; this.pagination.currentPage = 1; this.loadTableData() },
    handleCurrentChange(val) { this.pagination.currentPage = val; this.loadTableData() },
    getDisplayValue(row, key) {
      const rawVal = row[key]
      const labels = VALUE_LABELS[key]
      if (labels && rawVal !== null && rawVal !== undefined && rawVal !== '') return labels[rawVal] ?? rawVal
      return rawVal ?? ''
    },
    formatAccountingPeriod(value) {
      if (value === null || value === undefined || value === '') return '-'
      return ({ 0: '现款', 1: 'T+1', 2: 'T+2', 3: 'T+3', 4: 'T+4', 5: 'T+5', 6: 'T+6', 7: 'T+7' })[value] || '-'
    },
  },
}
</script>

<style scoped lang="scss">
.business-manage-container {
  display: flex; flex-direction: column; height: calc(100vh - 112px);
  background: var(--bg-page); padding: var(--page-padding); box-sizing: border-box; overflow: hidden;
  .search-card {
    flex-shrink: 0; background: var(--adm-card); border-radius: var(--adm-radius-card);
    box-shadow: var(--adm-shadow-card); padding: var(--page-card-padding); margin-bottom: var(--page-section-gap);
  }
  .table-section {
    flex: 1; overflow: hidden; min-height: 0; background: var(--adm-card);
    border-radius: var(--adm-radius-card); box-shadow: var(--adm-shadow-card); padding: 16px 0 0;
    :deep(.el-table) { height: 100% }
    :deep(.col-drop-target) { border-left: 3px solid var(--primary-color, #1677FF) !important }
    :deep(.col-dragging) { opacity: 0.4 }
    :deep(.restore-columns-bar) {
      text-align: center; padding: 6px; font-size: 12px; color: var(--primary-color, #1677FF);
      cursor: pointer; border-bottom: 1px solid var(--border-tags, #EEF2F7);
      &:hover { background: var(--primary-light, #EBF3FF) }
    }
  }
  .table-action-btn {
    display: flex;
    gap: 6px;
    justify-content: space-around;
  }
  .pagination-section {
    flex-shrink: 0; display: flex; justify-content: flex-end; align-items: center;
    padding: var(--page-section-gap) 24px; margin-top: var(--page-section-gap); background: var(--adm-card);
    border-radius: var(--adm-radius-card); box-shadow: var(--adm-shadow-card);
  }
}
.el-input, .el-select, .el-date-editor { .el-input__inner { height: 32px; line-height: 32px } }
.el-button {
  height: 32px;
  padding: 7px 15px;
}
.add-company-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 100px;
  height: 32px;
  padding: 0 16px;
  line-height: 1;
  white-space: nowrap;
  box-sizing: border-box;
}
</style>

<style lang="scss">
.table-filter-popper { z-index: 9999 !important; }
</style>
