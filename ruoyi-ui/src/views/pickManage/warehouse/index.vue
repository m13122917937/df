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
import StatsTabs from './components/StatsTabs/StatsTabs'

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
      if (routePath.includes('/pick-ing')) {
        this.activeTab = 'pick-ing'
      } else if (routePath.includes('/pick-end')) {
        this.activeTab = 'pick-end'
      }
    },
    handleTabChange(tabKey) {
      // 根据标签键值跳转到对应的子路由
      const routeMap = {
        'pick-ing': '/pickManage/warehouse/pick-ing',
        'pick-end': '/pickManage/warehouse/pick-end',
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
