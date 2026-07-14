<template>
  <div class="wholesale-container">
    <!-- Metric Cards -->
    <div
      ref="metricsRow"
      class="metrics-row"
      @mousedown="onDragStart"
      @mousemove="onDragging"
      @mouseup="onDragEnd"
      @mouseleave="onDragEnd"
    >
      <div
        v-for="(config, statusKey) in statusConfig"
        :key="statusKey"
        class="metric-card"
        :class="{ active: currentStatus === statusKey }"
        @click="handleStatusChange(statusKey)"
      >
        <div class="metric-icon" :style="{ background: config.bg, color: config.color }" v-html="config.icon">
        </div>
        <div class="metric-content">
          <span class="metric-label">{{ config.label }}</span>
          <span class="metric-count">{{ getStatusCount(statusKey) }}</span>
        </div>
      </div>
    </div>

    <!-- Sub-page Content -->
    <div class="main-content">
      <component :is="currentPageComponent" />
    </div>
  </div>
</template>

<script>
import { getCountHeaderApi } from '@/api/wholesale'

const icons = {
  edit: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>',
  clock: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>',
  dollar: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>',
  truck: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="3" width="15" height="13" rx="2"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>',
  send: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>',
  mapPin: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>',
  refreshCw: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>',
  alertTriangle: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>',
  checkCircle: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>',
  xCircle: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>',
}

export default {
  name: 'WholesaleIndex',
  data() {
    return {
      currentStatus: 'creatingOrders',
      countHeader: [],
      isDragging: false,
      dragStartX: 0,
      dragScrollLeft: 0,
      dragMoved: false,
      statusConfig: {
        creatingOrders: { status: '1', label: '新建中', icon: icons.edit, bg: 'rgba(91,124,250,0.08)', color: '#5B7CFA' },
        pendingOrders:  { status: '2', label: '待发布', icon: icons.clock, bg: 'rgba(255,179,64,0.08)', color: '#FFB340' },
        quotingOrders:  { status: '3', label: '报价中', icon: icons.dollar, bg: 'rgba(52,199,89,0.08)', color: '#34C759' },
        shippingOrders: { status: '4', label: '发货中', icon: icons.truck, bg: 'rgba(91,124,250,0.08)', color: '#5B7CFA' },
        shippedOrders:  { status: '5', label: '当日发货', icon: icons.send, bg: 'rgba(52,199,89,0.08)', color: '#34C759' },
        transitOrders:  { status: '6', label: '在途', icon: icons.mapPin, bg: 'rgba(91,124,250,0.08)', color: '#5B7CFA' },
        returnOrders:   { status: '7', label: '追单', icon: icons.refreshCw, bg: 'rgba(255,179,64,0.08)', color: '#FFB340' },
        abnormalOrders: { status: '8', label: '异常订单', icon: icons.alertTriangle, bg: 'rgba(255,77,77,0.08)', color: '#FF4D4D' },
        confirmedOrders:{ status: '10', label: '确认收货', icon: icons.checkCircle, bg: 'rgba(52,199,89,0.08)', color: '#34C759' },
        cancelledOrders:{ status: '11', label: '撤销', icon: icons.xCircle, bg: 'rgba(148,163,184,0.08)', color: '#94A3B8' },
      },
    }
  },
  computed: {
    currentPageComponent() {
      const pageMap = {
        'creatingOrders': () => import('./pages/creatingOrders/index'),
        'pendingOrders': () => import('./pages/pendingOrders/index'),
        'quotingOrders': () => import('./pages/quotingOrders/index'),
        'shippingOrders': () => import('./pages/shippingOrders/index'),
        'shippedOrders': () => import('./pages/shippedOrders/index'),
        'transitOrders': () => import('./pages/transitOrders/index'),
        'returnOrders': () => import('./pages/returnOrders/index'),
        'abnormalOrders': () => import('./pages/abnormalOrders/index'),
        'confirmedOrders': () => import('./pages/confirmedOrders/index'),
        'cancelledOrders': () => import('./pages/cancelledOrders/index'),
        'ruleList': () => import('./pages/ruleList/index'),
      }
      return pageMap[this.currentStatus] || null
    },
    getStatusCount() {
      return (statusKey) => {
        const statusInfo = this.statusConfig[statusKey]
        if (!statusInfo || !this.countHeader || !Array.isArray(this.countHeader)) return 0
        const statusData = this.countHeader.find(item => item.status === statusInfo.status)
        return statusData ? statusData.count : 0
      }
    },
  },
  watch: {
    '$route'(to) {
      this.updateStatusFromRoute(to)
    },
  },
  created() {
    this.getCountHeader()
  },
  mounted() {
    this.updateStatusFromRoute(this.$route)
  },
  methods: {
    onDragStart(e) {
      this.isDragging = true
      this.dragStartX = e.pageX
      this.dragScrollLeft = this.$refs.metricsRow.scrollLeft
      this.dragMoved = false
    },
    onDragging(e) {
      if (!this.isDragging) return
      const dx = e.pageX - this.dragStartX
      if (Math.abs(dx) > 5) this.dragMoved = true
      this.$refs.metricsRow.scrollLeft = this.dragScrollLeft - dx
    },
    onDragEnd() {
      this.isDragging = false
    },
    async getCountHeader() {
      try {
        const res = await getCountHeaderApi()
        if (res && res.code === 200) {
          this.countHeader = Array.isArray(res.data) ? res.data : []
        }
      } catch (error) {
        console.error('获取状态统计数据失败:', error)
        this.countHeader = []
      }
    },
    handleStatusChange(status) {
      if (this.dragMoved) return
      this.currentStatus = status
      this.$router.push({
        path: '/demandManage/wholesale',
        query: { status },
      })
    },
    updateStatusFromRoute(route) {
      const status = route.query && route.query.status
      if (status && this.statusConfig[status]) {
        this.currentStatus = status
      }
    },
  },
}
</script>

<style scoped lang="scss">
.wholesale-container {
  padding: 0;
  background: var(--bg-page);
  height: calc(100vh - 112px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'HarmonyOS Sans', 'PingFang SC', system-ui, sans-serif;
  -webkit-font-smoothing: antialiased;
}

/* ==================== Metrics Row ==================== */

.metrics-row {
  display: flex;
  gap: 8px;
  padding: 0 32px;
  margin: 12px 0 12px;
  overflow-x: auto;
  flex-shrink: 0;
  scrollbar-width: none;
  &::-webkit-scrollbar { display: none; }
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: var(--bg-card);
  border-radius: 12px;
  cursor: pointer;
  flex-shrink: 0;
  min-width: 120px;
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: var(--shadow-card);
  border: 1px solid transparent;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(0,0,0,0.06);
  }

  &.active {
    border-color: #5B7CFA;
    box-shadow: 0 4px 16px rgba(91,124,250,0.12);
  }
}

.metric-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.metric-icon :deep(svg) {
  width: 14px;
  height: 14px;
}

.metric-content {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.metric-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--adm-text-primary);
  white-space: nowrap;
}

.metric-count {
  font-size: 16px;
  font-weight: 600;
  color: var(--adm-text-primary);
}

/* ==================== Content Area ==================== */

.main-content {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  padding: 0 32px 32px;
}

/* ==================== Responsive ==================== */

@media (max-width: 1200px) {
  .metrics-row {
    padding: 0 16px;
  }
  .main-content {
    padding: 0 16px 16px;
  }
  .metric-card {
    min-width: 105px;
    padding: 8px 12px;
  }
}
</style>
