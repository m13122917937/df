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
        v-for="item in tabs"
        :key="item.key"
        class="metric-card"
        :class="{ active: activeTab === item.key }"
        @click="handleTabChange(item.key)"
      >
        <div class="metric-icon" :style="{ background: item.bg, color: item.color }" v-html="item.icon">
        </div>
        <div class="metric-content">
          <span class="metric-label">{{ item.title }}</span>
        </div>
      </div>
    </div>

    <!-- Dynamic Sub-page Content -->
    <div class="main-content">
      <component :is="activeComponent" />
    </div>
  </div>
</template>

<script>
const icons = {
  send: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>',
  truck: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="3" width="15" height="13" rx="2"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>',
  check: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>',
  close: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>'
}

const pageComponents = {
  todayShipped: () => import('./today-shipped.vue'),
  transit: () => import('./transit.vue'),
  collected: () => import('./collected.vue'),
  putinReturn: () => import('./return.vue'),
}

export default {
  name: 'PutinIndex',
  components: {
    todayShipped: pageComponents.todayShipped,
    transit: pageComponents.transit,
    collected: pageComponents.collected,
    putinReturn: pageComponents.putinReturn,
  },
  data() {
    return {
      activeTab: 'today-send',
      isDragging: false,
      dragStartX: 0,
      dragScrollLeft: 0,
      dragMoved: false,
      tabs: [
        { key: 'today-send', title: '当日发货', icon: icons.send, bg: 'rgba(52,199,89,0.08)', color: '#34C759' },
        { key: 'transit', title: '在途', icon: icons.truck, bg: 'rgba(91,124,250,0.08)', color: '#5B7CFA' },
        { key: 'confirm', title: '确认收货', icon: icons.check, bg: 'rgba(52,199,89,0.08)', color: '#34C759' },
        { key: 'return', title: '撤销订单', icon: icons.close, bg: 'rgba(148,163,184,0.08)', color: '#94A3B8' },
      ]
    }
  },
  computed: {
    activeComponent() {
      const map = {
        'today-send': 'todayShipped',
        'transit': 'transit',
        'confirm': 'collected',
        'return': 'putinReturn',
      }
      return map[this.activeTab] || 'todayShipped'
    }
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
    handleTabChange(tabKey) {
      if (this.dragMoved) return
      this.activeTab = tabKey
    }
  }
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
