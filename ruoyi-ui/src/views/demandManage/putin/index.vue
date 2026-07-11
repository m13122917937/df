<template>
  <div class="order-management">
    <!-- 统计标签栏 -->
    <StatsTabs v-model="activeTab" @tab-change="handleTabChange" />
    <!-- 订单内容区域 -->
    <div class="order-content">
      <component :is="activeComponent" />
    </div>
  </div>
</template>

<script>
import StatsTabs from './components/StatsTabs/StatsTabs'

const pageComponents = {
  todayShipped: () => import('./today-shipped.vue'),
  transit: () => import('./transit.vue'),
  collected: () => import('./collected.vue'),
  putinReturn: () => import('./return.vue'),
}

export default {
  name: 'OrderManagement',
  components: {
    StatsTabs,
    todayShipped: pageComponents.todayShipped,
    transit: pageComponents.transit,
    collected: pageComponents.collected,
    putinReturn: pageComponents.putinReturn,
  },
  data() {
    return {
      activeTab: 'today-send'
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
    handleTabChange(tabKey) {
      this.activeTab = tabKey
    }
  }
}
</script>

<style lang="scss" scoped>
.order-management {
  height: calc(100vh - 112px);
  display: flex;
  flex-direction: column;
  flex: 1;

  .order-content {
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;
  }
}
</style>
