<template>
  <div class="monery-management">
    <!-- 统计标签栏 -->
    <StatsTabs v-model="activeTab" :value="defaultActiveTab" :stats-items="statsItems" @tab-change="handleTabChange" />

    <!-- 财务管理内容区域 -->
    <div class="monery-content">
      <router-view />
    </div>
  </div>
</template>

<script>
import StatsTabs from '@/components/StatsTabs/StatsTabs'

export default {
  name: 'MoneryManagement',
  components: {
    StatsTabs
  },
  data() {
    return {
      defaultActiveTab: 'earnest',
      activeTab: 'earnest',
      statsItems: [
        { key: 'earnest', title: '保证金', hideCount: true },
        { key: 'collection', title: '收款信息', hideCount: true },
        { key: 'contract', title: '合同', hideCount: true }
      ]
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
      if (routePath.includes('/earnest')) {
        this.activeTab = 'earnest'
      } else if (routePath.includes('/collection')) {
        this.activeTab = 'collection'
      } else if (routePath.includes('/contract')) {
        this.activeTab = 'contract'
      }
    },
    handleTabChange(tabKey) {
      // 根据标签键值跳转到对应的子路由
      const routeMap = {
        'earnest': '/monery/earnest',
        'collection': '/monery/collection/payment',
        'contract': '/monery/contract/sign'
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
.monery-management {
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  flex: 1;

  .monery-content {
    flex: 1;
    overflow: hidden;
  }
}
</style>
