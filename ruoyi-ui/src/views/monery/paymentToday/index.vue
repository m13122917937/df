<template>
  <div class="order-management">
    <!-- 统计标签栏 -->
    <StatsTabs v-model="activeTab" @tab-change="handleTabChange" />
    <!-- 订单内容区域 -->
    <div class="order-content">
      <router-view />
    </div>
  </div>
</template>

<script>
import StatsTabs from './components/StatsTabs'

export default {
  name: 'PaymentToday',
  components: {
    StatsTabs
  },
  data() {
    return {
      activeTab: ''
    }
  },
  watch: {
    '$route'() {
      this.setActiveTabFromRoute()
    }
  },
  mounted() {
    // 根据当前路由设置活跃标签
    this.setActiveTabFromRoute()
  },
  methods: {
    setActiveTabFromRoute() {
      const routePath = this.$route.path
      if (routePath.includes('/pay-delivery')) {
        this.activeTab = 'pay-delivery'
      } else if (routePath.includes('/pay-putin')) {
        this.activeTab = 'pay-putin'
      }
    },
    handleTabChange(tabKey) {
      // 根据标签键值跳转到对应的子路由
      const routeMap = {
        'pay-delivery': '/monery/paymentToday/pay-delivery',
        'pay-putin': '/monery/paymentToday/pay-putin'
      }

      const targetRoute = routeMap[tabKey]
      if (targetRoute && targetRoute !== this.$route.path) {
        this.$router.push(targetRoute)
      }
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
