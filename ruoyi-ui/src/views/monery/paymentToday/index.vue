<template>
  <div class="wholesale-container">
    <!-- Metric Cards -->
    <div class="metrics-row">
      <div
        v-for="tab in tabs"
        :key="tab.key"
        class="metric-card"
        :class="{ active: activeTab === tab.key }"
        @click="handleTabChange(tab.key)"
      >
        <div class="metric-icon" :style="{ background: tab.bg, color: tab.color }" v-html="tab.icon">
        </div>
        <div class="metric-content">
          <span class="metric-label">{{ tab.label }}</span>
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
  box: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/></svg>',
}

const pageComponents = {
  payDelivery: () => import('./pay-delivery.vue'),
  payPutin: () => import('./pay-putin.vue'),
}

export default {
  name: 'PaymentToday',
  components: {
    payDelivery: pageComponents.payDelivery,
    payPutin: pageComponents.payPutin,
  },
  data() {
    return {
      activeTab: 'pay-delivery',
      tabs: [
        { key: 'pay-delivery', label: '代发', icon: icons.send, bg: 'rgba(91,124,250,0.08)', color: '#5B7CFA' },
        { key: 'pay-putin', label: '入仓', icon: icons.box, bg: 'rgba(52,199,89,0.08)', color: '#34C759' },
      ],
    }
  },
  computed: {
    activeComponent() {
      const map = {
        'pay-delivery': 'payDelivery',
        'pay-putin': 'payPutin',
      }
      return map[this.activeTab] || 'payDelivery'
    },
  },
  methods: {
    handleTabChange(tabKey) {
      this.activeTab = tabKey
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
  padding: 0 20px;
  margin: 8px 0 8px;
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
  padding: 0 20px 20px;
}

/* ==================== Responsive ==================== */

@media (max-width: 1200px) {
  .metrics-row {
    padding: 0 12px;
  }
  .main-content {
    padding: 0 12px 12px;
  }
  .metric-card {
    min-width: 105px;
    padding: 8px 12px;
  }
}
</style>
