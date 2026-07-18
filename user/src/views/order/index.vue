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
import StatsTabs from '@/components/StatsTabs/StatsTabs'

export default {
  name: 'OrderManagement',
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
      if (routePath.includes('/pending')) {
        this.activeTab = 'wait-send'
      } else if (routePath.includes('/today-shipped')) {
        this.activeTab = 'today-send'
      } else if (routePath.includes('/transit')) {
        this.activeTab = 'transit'
      } else if (routePath.includes('/exception')) {
        this.activeTab = 'exception'
      } else if (routePath.includes('/refund')) {
        this.activeTab = 'return'
      } else if (routePath.includes('/collected')) {
        this.activeTab = 'confirm'
      } else if (routePath.includes('/after-sales')) {
        this.activeTab = 'sale-after'
      } else if (routePath.includes('/all')) {
        this.activeTab = 'all-orders'
      }
    },
    handleTabChange(tabKey) {
      // 根据标签键值跳转到对应的子路由
      const routeMap = {
        'wait-send': '/order/pending',
        'today-send': '/order/today-shipped',
        'transit': '/order/transit',
        'exception': '/order/exception',
        'return': '/order/refund',
        'confirm': '/order/collected',
        'sale-after': '/order/after-sales',
        'all-orders': '/order/all'
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
