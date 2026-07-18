<template>
  <div class="set-management">
    <!-- 统计标签栏 -->
    <StatsTabs v-model="activeTab" :value="defaultActiveTab" :stats-items="statsItems" @tab-change="handleTabChange" />

    <!-- 基础配置内容区域 -->
    <div class="set-content">
      <router-view />
    </div>
  </div>
</template>

<script>
import StatsTabs from '@/components/StatsTabs/StatsTabs'

export default {
  name: 'SetManagement',
  components: {
    StatsTabs
  },
  data() {
    return {
      defaultActiveTab: 'user',
      activeTab: 'user',
      statsItems: [
        { key: 'user', title: '用户管理', hideCount: true }
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
      if (routePath.includes('/user')) {
        this.activeTab = 'user'
      }
    },
    handleTabChange(tabKey) {
      // 根据标签键值跳转到对应的子路由
      const routeMap = {
        'user': '/set/user'
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
.set-management {
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  flex: 1;

  .set-content {
    flex: 1;
    overflow: hidden;
  }
}
</style>
