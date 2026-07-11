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
import StatsTabs from './components/StatsTabs'

const pageComponents = {
  payDelivery: () => import('./pay-delivery.vue'),
  payPutin: () => import('./pay-putin.vue'),
}

export default {
  name: 'PaymentToday',
  components: {
    StatsTabs,
    payDelivery: pageComponents.payDelivery,
    payPutin: pageComponents.payPutin,
  },
  data() {
    return {
      activeTab: 'pay-delivery'
    }
  },
  computed: {
    activeComponent() {
      const map = {
        'pay-delivery': 'payDelivery',
        'pay-putin': 'payPutin',
      }
      return map[this.activeTab] || 'payDelivery'
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
  height: 100%;
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
