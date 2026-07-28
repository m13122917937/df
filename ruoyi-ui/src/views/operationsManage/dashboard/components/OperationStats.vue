<template>
  <div class="operation-stats-page" v-loading="loading">
    <section class="stats-filter-panel">
      <div class="filter-toolbar">
        <el-select v-model="query.subjectName" clearable filterable placeholder="主体选择" @change="loadDashboard">
          <el-option v-for="item in dimensionOptions.subjectName" :key="item" :label="item" :value="item" />
        </el-select>
        <el-select v-model="query.orderType" clearable placeholder="全部类型订单" @change="loadDashboard">
          <el-option v-for="item in dimensionOptions.orderType" :key="item" :label="item" :value="item" />
        </el-select>
        <div class="toolbar-spacer" />
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="yyyy-MM-dd"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="loadDashboard"
        />
        <div class="quick-range-group">
          <button v-for="day in [7, 15, 30]" :key="day" class="quick-range" :class="{ active: quickDays === day }" @click="setQuickRange(day)">{{ day }}天</button>
        </div>
      </div>

      <div v-if="activeFilters.length" class="active-filter-bar">
        <span class="active-filter-label">当前查询项</span>
        <el-tag v-for="item in activeFilters" :key="item.key" closable size="small" @close="clearFilter(item.key)">
          {{ item.label }}：{{ item.value }}
        </el-tag>
      </div>

      <div v-show="!filterCollapsed" class="dimension-filter-list">
        <div v-for="dimension in dimensions" :key="dimension.key" class="dimension-filter-row" :class="{ expanded: expandedDimensions[dimension.key] || dimension.key === 'brand' }">
          <div class="dimension-label"><i :class="dimension.icon" />{{ dimension.label }}</div>
          <button class="all-option" :class="{ selected: !query[dimension.key] }" @click="selectDimension(dimension.key, '')">全选</button>
          <el-input v-if="dimension.searchable" v-model="dimensionKeyword[dimension.key]" class="dimension-search" size="mini" placeholder="搜索/筛选" clearable />
          <div class="dimension-options">
            <button
              v-for="item in visibleDimensionOptions(dimension.key)"
              :key="item"
              class="dimension-option"
              :class="{ selected: query[dimension.key] === item }"
              @click="selectDimension(dimension.key, item)"
            >{{ item }}</button>
          </div>
          <button v-if="dimension.key !== 'brand' && filteredDimensionOptions(dimension.key).length > optionLimit" class="more-option" @click="toggleMore(dimension.key)">
            {{ expandedDimensions[dimension.key] ? '收起' : '更多' }} <i :class="expandedDimensions[dimension.key] ? 'el-icon-caret-top' : 'el-icon-caret-bottom'" />
          </button>
        </div>
      </div>
      <button class="collapse-button" @click="filterCollapsed = !filterCollapsed">{{ filterCollapsed ? '展开筛选' : '收起' }} <i :class="filterCollapsed ? 'el-icon-caret-bottom' : 'el-icon-caret-top'" /></button>
    </section>

    <section class="stats-section sales-comparison-section">
      <div class="stats-section-header"><h2>销售对比</h2><button @click="comparisonCollapsed = !comparisonCollapsed">{{ comparisonCollapsed ? '展开' : '收起' }} <i :class="comparisonCollapsed ? 'el-icon-caret-bottom' : 'el-icon-caret-top'" /></button></div>
      <div v-show="!comparisonCollapsed" class="comparison-grid">
        <article v-for="item in comparisonCards" :key="item.key" class="comparison-card">
          <h3>{{ item.title }} <span>{{ item.range }}</span></h3>
          <div class="comparison-values"><div><span>{{ item.currentLabel }}</span><strong>{{ money(item.currentValue) }}</strong></div><div><span>{{ item.previousLabel }}</span><strong class="previous-value">{{ money(item.previousValue) }}</strong></div></div>
        </article>
      </div>
    </section>

    <section class="stats-section core-metrics-section">
      <div class="stats-section-header"><h2>核心监控指标</h2><button @click="metricsCollapsed = !metricsCollapsed">{{ metricsCollapsed ? '展开' : '收起' }} <i :class="metricsCollapsed ? 'el-icon-caret-bottom' : 'el-icon-caret-top'" /></button></div>
      <div v-show="!metricsCollapsed" class="core-metrics-content">
        <article class="trend-card">
          <div class="trend-caption"><span>销售收入趋势</span><small>{{ rangeLabel }}</small></div>
          <svg class="trend-chart" viewBox="0 0 500 184" preserveAspectRatio="none" role="img" aria-label="销售收入趋势图">
            <line x1="0" y1="168" x2="500" y2="168" class="chart-axis" />
            <rect v-for="bar in chartBars" :key="`bar-${bar.key}`" :x="bar.x" :y="bar.y" :width="bar.width" :height="bar.height" class="bar-column" rx="3" />
            <polyline :points="linePoints" class="trend-line" />
            <circle v-for="point in chartPoints" :key="`point-${point.key}`" :cx="point.x" :cy="point.y" r="3.8" class="trend-point" />
          </svg>
          <div v-if="!chartBars.length" class="chart-empty">暂无趋势数据</div>
        </article>
        <div class="metric-card-grid">
          <article v-for="item in metricCards" :key="item.key" class="core-metric-card" :class="{ emphasized: item.key === 'salesRevenue', pending: item.pending }">
            <span>{{ item.label }}</span><strong>{{ item.value }}</strong><small>{{ item.tip }}</small>
          </article>
        </div>
      </div>
    </section>

    <section class="stats-section metric-detail-section">
      <div class="stats-section-header"><h2>经营指标明细</h2><div class="detail-actions"><el-button size="small" type="primary" plain icon="el-icon-download" @click="notifyExport">导出毛利视图</el-button><div class="period-switch"><button v-for="item in ['日', '月', '年']" :key="item" :class="{ active: period === item }" @click="selectPeriod(item)">{{ item }}</button></div></div></div>
      <div class="metric-table-wrap">
        <el-table :data="metricDetailRows" class="metric-detail-table" row-key="key" border :tree-props="{ children: 'children' }" default-expand-all>
          <el-table-column label="指标" min-width="210" fixed="left"><template slot-scope="scope"><span :class="{ 'metric-level-parent': scope.row.children && scope.row.children.length }">{{ scope.row.name }}</span></template></el-table-column>
          <el-table-column v-for="date in detailDates" :key="date" :label="date" min-width="160" align="right"><template slot-scope="scope">{{ metricDateValue(scope.row, date) }}</template></el-table-column>
          <el-table-column label="汇总" min-width="180" align="right" fixed="right"><template slot-scope="scope"><span class="total-value">{{ money(scope.row.value) }}</span></template></el-table-column>
        </el-table>
        <div v-if="!metricDetailRows.length" class="detail-empty">暂无经营指标数据</div>
      </div>
    </section>
  </div>
</template>

<script>
import { getAnalysisDashboard, getAnalysisDashboardFilterOptions } from '@/api/analysis'

const METRIC_DEFINITIONS = [
  ['salesRevenue', '销售收入'], ['goodsIncentive', '商品激励'], ['goodsGrossProfit', '商品毛利'], ['fulfillmentGrossProfit', '履约毛利'], ['departmentProfit', '部门利润'], ['operatingProfit', '经营利润'], ['goodsCost', '商品成本'], ['platformFee', '平台费用'], ['logisticsFee', '物流费用'], ['marketingFee', '营销费用'], ['penaltyFee', '扣罚费用'], ['capitalCost', '资金成本']
]

export default {
  name: 'OperationStats',
  data() {
    return { loading: false, dateRange: [], quickDays: 30, filterCollapsed: false, comparisonCollapsed: false, metricsCollapsed: false, period: '日', optionLimit: 10, expandedDimensions: {}, dimensionKeyword: { platform: '', shopName: '', brand: '', category: '' }, query: this.createQuery(), filterOptions: { platforms: [], stores: [], brands: [], categories: [] }, summary: {}, rows: [], metricTree: [], comparison: {} }
  },
  computed: {
    dimensions() { return [{ key: 'category', label: '品类', icon: 'el-icon-caret-top', searchable: false }, { key: 'brand', label: '品牌', icon: 'el-icon-caret-bottom', searchable: false }, { key: 'platform', label: '平台', icon: 'el-icon-caret-bottom', searchable: false }, { key: 'shopName', label: '店铺', icon: 'el-icon-caret-bottom', searchable: true }] },
    dimensionOptions() { return { subjectName: this.distinctRowOptions('subjectName'), orderType: this.distinctRowOptions('orderType'), platform: this.filterOptions.platforms, shopName: this.storeOptions, brand: this.filterOptions.brands, category: this.filterOptions.categories } },
    storeOptions() { return this.filterOptions.stores.filter(item => !this.query.platform || item.platformName === this.query.platform).map(item => item.shopName) },
    activeFilters() { return [{ key: 'subjectName', label: '主体', value: this.query.subjectName }, { key: 'orderType', label: '订单类型', value: this.query.orderType }, { key: 'platform', label: '平台', value: this.query.platform }, { key: 'shopName', label: '店铺', value: this.query.shopName }, { key: 'brand', label: '品牌', value: this.query.brand }, { key: 'category', label: '品类', value: this.query.category }].filter(item => item.value) },
    rangeLabel() { return this.dateRange && this.dateRange.length ? `${this.dateRange[0]} 至 ${this.dateRange[1]}` : '' },
    comparisonCards() { return ['week', 'month', 'year'].map(key => { const item = this.comparison[key] || {}; return { key, title: item.title || '', range: item.range || '', currentLabel: item.currentLabel || '', previousLabel: item.previousLabel || '', currentValue: item.currentValue, previousValue: item.previousValue } }) },
    metricCards() { return METRIC_DEFINITIONS.map(([key, label]) => ({ key, label, value: this.money(this.summary[key]), pending: this.summary[key] == null, tip: this.summary[key] == null ? '待核算' : '当前筛选区间' })) },
    chartSeries() { const values = {}; this.rows.forEach(row => { const date = row.metricDate; if (!date) return; values[date] = (values[date] || 0) + Number(row.salesRevenue || 0) }); return Object.keys(values).sort().map(key => ({ key, value: values[key] })) },
    chartBars() { const max = Math.max(...this.chartSeries.map(item => item.value), 0); return this.chartSeries.map((item, index) => { const height = max ? Math.max(8, item.value / max * 138) : 8; const width = Math.max(8, 420 / Math.max(this.chartSeries.length, 1) - 7); return { key: item.key, x: 28 + index * (420 / Math.max(this.chartSeries.length, 1)), y: 168 - height, width, height } }) },
    chartPoints() { const max = Math.max(...this.chartSeries.map(item => item.value), 0); return this.chartSeries.map((item, index) => ({ key: item.key, x: 28 + index * (420 / Math.max(this.chartSeries.length, 1)) + Math.max(8, 420 / Math.max(this.chartSeries.length, 1) - 7) / 2, y: 168 - (max ? item.value / max * 138 : 0) })) },
    linePoints() { return this.chartPoints.map(point => `${point.x},${point.y}`).join(' ') },
    detailDates() { return [...new Set(this.rows.map(row => row.metricDate).filter(Boolean))].sort().slice(-8) },
    dailyMetricMap() { return this.rows.reduce((result, row) => { const date = row.metricDate; if (!date) return result; if (!result[date]) result[date] = {}; METRIC_DEFINITIONS.forEach(([key]) => { result[date][key] = (result[date][key] || 0) + Number(row[key] || 0) }); return result }, {}) },
    metricDetailRows() { return this.metricTree.length ? this.metricTree : METRIC_DEFINITIONS.map(([key, name]) => ({ key, name, value: this.summary[key], children: [] })) }
  },
  async created() { await this.loadFilterOptions(); this.setQuickRange(30) },
  methods: {
    createQuery() { return { subjectName: '', orderType: '', platform: '', shopName: '', brand: '', category: '' } },
    async loadDashboard() { if (!this.dateRange || this.dateRange.length !== 2) return; this.loading = true; try { const params = Object.assign({}, this.query, { startDate: this.dateRange[0], endDate: this.dateRange[1], period: this.period }); const response = await getAnalysisDashboard('operationStats', params); const data = response.data || {}; this.summary = data.summary || {}; this.rows = data.rows || []; this.metricTree = data.metricTree || []; await this.loadComparison(); } finally { this.loading = false } },
    async loadComparison() { const ranges = this.comparisonRanges(); const requests = []; Object.keys(ranges).forEach(key => { requests.push(this.loadComparisonSummary(key, ranges[key].current)); requests.push(this.loadComparisonSummary(`${key}Previous`, ranges[key].previous)) }); const results = await Promise.all(requests); ['week', 'month', 'year'].forEach((key, index) => { const range = ranges[key]; this.$set(this.comparison, key, { title: range.title, range: `${range.current[0]} 至 ${range.current[1]}`, currentLabel: range.currentLabel, previousLabel: range.previousLabel, currentValue: results[index * 2], previousValue: results[index * 2 + 1] }) }) },
    async loadComparisonSummary(key, range) { const params = Object.assign({}, this.query, { startDate: range[0], endDate: range[1], period: this.period }); const response = await getAnalysisDashboard('operationStats', params); return (response.data && response.data.summary && response.data.summary.salesRevenue) || 0 },
    comparisonRanges() { const end = this.parseDate(this.dateRange[1]); return { week: this.buildWeekRange(end), month: this.buildMonthRange(end), year: this.buildYearRange(end) } },
    buildWeekRange(end) { const offset = (end.getDay() + 6) % 7; const start = this.addDays(end, -offset); const previousEnd = this.addDays(start, -1); return { title: '本周销售', current: [this.formatDate(start), this.formatDate(end)], previous: [this.formatDate(this.addDays(previousEnd, -6)), this.formatDate(previousEnd)], currentLabel: '本周已完成', previousLabel: '上周已完成' } },
    buildMonthRange(end) { const start = new Date(end.getFullYear(), end.getMonth(), 1); const previousEnd = new Date(start.getFullYear(), start.getMonth(), 0); return { title: '本月销售', current: [this.formatDate(start), this.formatDate(end)], previous: [this.formatDate(new Date(previousEnd.getFullYear(), previousEnd.getMonth(), 1)), this.formatDate(previousEnd)], currentLabel: '本月已完成', previousLabel: '上月已完成' } },
    buildYearRange(end) { const start = new Date(end.getFullYear(), 0, 1); const previousEnd = new Date(end.getFullYear() - 1, 11, 31); return { title: '本年销售', current: [this.formatDate(start), this.formatDate(end)], previous: [this.formatDate(new Date(end.getFullYear() - 1, 0, 1)), this.formatDate(previousEnd)], currentLabel: '本年已完成', previousLabel: '上年已完成' } },
    setQuickRange(days) { this.quickDays = days; const end = new Date(); this.dateRange = [this.formatDate(this.addDays(end, -(days - 1))), this.formatDate(end)]; this.loadDashboard() },
    async loadFilterOptions() { const response = await getAnalysisDashboardFilterOptions(); this.filterOptions = response.data || { platforms: [], stores: [], brands: [], categories: [] } },
    distinctRowOptions(key) { return [...new Set(this.rows.map(row => row[key]).filter(Boolean))] },
    selectDimension(key, value) { if (key === 'platform' && this.query.platform !== value) this.query.shopName = ''; this.query[key] = value; this.quickDays = 0; this.loadDashboard() },
    clearFilter(key) { if (key === 'platform') this.query.shopName = ''; this.query[key] = ''; this.quickDays = 0; this.loadDashboard() },
    filteredDimensionOptions(key) { const keyword = (this.dimensionKeyword[key] || '').trim().toLowerCase(); const options = this.dimensionOptions[key] || []; return keyword ? options.filter(item => item.toLowerCase().includes(keyword)) : options },
    visibleDimensionOptions(key) { const options = this.filteredDimensionOptions(key); return key === 'brand' || this.expandedDimensions[key] ? options : options.slice(0, this.optionLimit) },
    toggleMore(key) { this.$set(this.expandedDimensions, key, !this.expandedDimensions[key]) },
    selectPeriod(period) { this.period = period; this.loadDashboard() },
    metricDateValue(row, date) { const value = this.dailyMetricMap[date] && this.dailyMetricMap[date][row.key]; return value == null ? '--' : this.money(value) },
    money(value) { return value == null ? '待核算' : Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) },
    notifyExport() { this.$message.info('导出接口接入后可使用毛利视图导出。') },
    addDays(date, days) { const copy = new Date(date); copy.setDate(copy.getDate() + days); return copy },
    parseDate(value) { const [year, month, day] = value.split('-').map(Number); return new Date(year, month - 1, day) },
    formatDate(date) { return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}` }
  }
}
</script>

<style lang="scss" scoped>
.operation-stats-page {
  --panel: var(--bg-card);
  --panel-deep: var(--bg-hover);
  --line: var(--border-tags);
  --title: var(--nl-color-title);
  --text: var(--nl-color);
  --muted: var(--nl-color-weak);
  --accent: var(--primary-color);
  display: flex;
  flex-direction: column;
  gap: 22px;
  color: var(--text);
}

.stats-filter-panel,
.stats-section {
  border: 1px solid var(--line);
  border-radius: 14px;
  background: var(--panel);
  box-shadow: var(--shadow-card);
}

.stats-filter-panel { padding: 18px 22px 16px; }

.filter-toolbar { display: flex; align-items: center; gap: 16px; min-height: 56px; }
.filter-toolbar :deep(.el-select) { width: 280px; }
.filter-toolbar :deep(.el-date-editor) { width: 480px; }
.filter-toolbar :deep(.el-input__inner) { height: 56px; border-color: var(--line); background: var(--panel-deep); color: var(--text); font-size: 18px; }
.toolbar-spacer { flex: 1; min-width: 32px; }

.quick-range-group { display: flex; align-self: stretch; border: 1px solid var(--line); border-radius: 8px; overflow: hidden; }
.quick-range,
.dimension-option,
.all-option,
.more-option,
.collapse-button,
.stats-section-header button { border: 0; background: transparent; color: var(--text); cursor: pointer; }
.quick-range { min-width: 78px; padding: 0 16px; border-right: 1px solid var(--line); font-size: 17px; font-weight: 600; }
.quick-range:last-child { border-right: 0; }
.quick-range.active { background: var(--accent); color: var(--module-nav-active-text); }

.active-filter-bar { display: flex; align-items: center; flex-wrap: wrap; gap: 8px; margin-top: 12px; padding: 10px 12px; border-radius: 8px; background: var(--panel-deep); }
.active-filter-label { margin-right: 4px; color: var(--muted); font-size: 14px; white-space: nowrap; }
.active-filter-bar :deep(.el-tag) { border-color: var(--primary-border); background: var(--primary-light-bg); color: var(--accent); }

.dimension-filter-list { margin-top: 16px; border: 1px solid var(--line); border-radius: 10px; overflow: hidden; }
.dimension-filter-row { display: flex; align-items: center; min-height: 62px; gap: 24px; padding: 0 16px; border-bottom: 1px solid var(--line); }
.dimension-filter-row:last-child { border-bottom: 0; }
.dimension-label { flex: 0 0 86px; color: var(--muted); font-size: 18px; font-weight: 700; white-space: nowrap; }
.dimension-label i { margin-right: 4px; font-size: 13px; }
.all-option { flex: 0 0 auto; min-width: 42px; padding: 4px 0; color: var(--text); font-size: 16px; white-space: nowrap; }
.all-option.selected,
.dimension-option.selected { color: var(--accent); }
.dimension-search { flex: 0 0 178px; width: 178px; }
.dimension-search :deep(.el-input__inner) { height: 36px; border-color: var(--line); background: var(--panel); color: var(--text); }
.dimension-options { display: flex; flex: 1 1 0; min-width: 0; gap: 30px; align-items: center; flex-wrap: nowrap; overflow: hidden; }
.dimension-option { flex: 0 0 auto; padding: 4px 0; font-size: 16px; line-height: 22px; white-space: nowrap; }
.more-option { flex: 0 0 auto; color: var(--accent); white-space: nowrap; font-size: 15px; }
.dimension-filter-row.expanded { align-items: flex-start; padding-top: 12px; padding-bottom: 12px; }
.dimension-filter-row.expanded .dimension-label,
.dimension-filter-row.expanded .all-option,
.dimension-filter-row.expanded .dimension-search,
.dimension-filter-row.expanded .more-option { margin-top: 5px; }
.dimension-filter-row.expanded .dimension-options { flex-wrap: wrap; row-gap: 10px; overflow: visible; }
.collapse-button { display: block; margin: 16px auto 0; color: var(--accent); font-weight: 600; }
.stats-section { padding: 16px 22px; }
.stats-section-header { display: flex; align-items: center; justify-content: space-between; min-height: 48px; border-bottom: 1px solid var(--line); }
.stats-section-header h2 { position: relative; margin: 0; padding-left: 20px; color: var(--title); font-size: 22px; line-height: 1; }
.stats-section-header h2::before { position: absolute; top: -5px; left: 0; width: 5px; height: 28px; border-radius: 2px; background: var(--accent); content: ''; }
.stats-section-header button { color: var(--accent); font-size: 16px; }
.comparison-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 16px; padding-top: 16px; }
.comparison-card { min-height: 168px; border: 1px solid var(--line); border-radius: 16px; padding: 20px; background: var(--panel-deep); }
.comparison-card h3 { margin: 0 0 18px; color: var(--title); font-size: 20px; }
.comparison-card h3 span { margin-left: 10px; color: var(--muted); font-size: 15px; font-weight: 400; }
.comparison-values { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.comparison-values span, .comparison-values strong { display: block; }
.comparison-values span { color: var(--muted); font-size: 15px; }
.comparison-values strong { margin-top: 10px; color: var(--title); font-size: 28px; }
.comparison-values .previous-value { color: var(--text); font-size: 20px; }
.core-metrics-content { display: grid; grid-template-columns: minmax(430px, 1.25fr) minmax(980px, 2.35fr); gap: 16px; padding-top: 16px; }
.trend-card { position: relative; min-height: 272px; border-radius: 12px; background: var(--panel-deep); padding: 16px; overflow: hidden; }
.trend-caption { display: flex; justify-content: space-between; color: var(--title); font-weight: 700; }
.trend-caption small { color: var(--muted); font-weight: 400; }
.trend-chart { width: 100%; height: 224px; margin-top: 14px; }
.chart-axis { stroke: var(--line); stroke-width: 1; }
.bar-column { fill: rgba(var(--primary-rgb), .72); }
.trend-line { fill: none; stroke: var(--red); stroke-width: 4; stroke-linecap: round; stroke-linejoin: round; }
.trend-point { fill: var(--panel); stroke: var(--red); stroke-width: 3; }
.chart-empty { position: absolute; inset: 0; display: grid; place-items: center; color: var(--muted); }
.metric-card-grid { display: grid; grid-template-columns: repeat(6, minmax(0, 1fr)); gap: 10px; }
.core-metric-card { min-height: 128px; padding: 16px; border-radius: 12px; background: var(--panel-deep); display: flex; flex-direction: column; gap: 9px; }
.core-metric-card.emphasized { border: 2px solid var(--primary-color); background: var(--primary-light-bg); }
.core-metric-card span, .core-metric-card small { color: var(--muted); }
.core-metric-card strong { color: var(--title); font-size: 25px; line-height: 1; white-space: nowrap; }
.core-metric-card.pending strong { color: var(--nl-color-tip); font-size: 19px; }
.detail-actions { display: flex; align-items: center; gap: 14px; }.period-switch { display: flex; overflow: hidden; border: 1px solid var(--line); border-radius: var(--small-radius); }.period-switch button { min-width: 42px; height: 32px; padding: 0 12px; border: 0; border-right: 1px solid var(--line); background: var(--panel); color: var(--text); cursor: pointer; }.period-switch button:last-child { border-right: 0; }.period-switch button.active { background: var(--accent); color: var(--module-nav-active-text); }.metric-table-wrap { margin-top: 16px; overflow: auto; }.metric-detail-table { min-width: 960px; }.metric-detail-table :deep(.el-table__header th), .metric-detail-table :deep(.el-table__body td) { background: var(--panel) !important; color: var(--text); border-color: var(--line); }.metric-detail-table :deep(.el-table__header th) { color: var(--accent); font-size: 16px; }.metric-detail-table :deep(.el-table__row--level-0 td) { font-weight: 700; }.metric-level-parent { color: var(--title); font-size: 16px; }.total-value { color: var(--accent); font-weight: 700; }.detail-empty { padding: 48px; color: var(--muted); text-align: center; }
@media (max-width: 1280px) {
  .filter-toolbar { flex-wrap: wrap; }
  .toolbar-spacer { display: none; }
  .dimension-filter-row { align-items: flex-start; flex-wrap: wrap; padding: 12px 14px; }
  .dimension-options { flex: 1 0 calc(100% - 320px); flex-wrap: wrap; overflow: visible; }
  .core-metrics-content { grid-template-columns: 1fr; }
  .metric-card-grid { grid-template-columns: repeat(4, minmax(150px, 1fr)); }
}

@media (max-width: 768px) {
  .stats-filter-panel, .stats-section { padding: 12px; }
  .filter-toolbar :deep(.el-select), .filter-toolbar :deep(.el-date-editor) { width: 100%; }
  .comparison-grid, .metric-card-grid { grid-template-columns: 1fr; }
  .dimension-filter-row { align-items: flex-start; flex-wrap: wrap; padding: 12px; gap: 10px; }
  .dimension-options { flex-basis: 100%; }
  .detail-actions { flex-wrap: wrap; justify-content: flex-end; }
}
</style>
