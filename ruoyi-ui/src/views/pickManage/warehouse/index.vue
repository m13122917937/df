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
import PickIng from './pick-ing'
import PickEnd from './pick-end'

export default {
  name: 'PickWarehouse',
  components: {
    StatsTabs,
    PickIng,
    PickEnd,
  },
  data() {
    return {
      activeTab: 'pick-ing'
    }
  },
  computed: {
    activeComponent() {
      const map = {
        'pick-ing': 'PickIng',
        'pick-end': 'PickEnd',
      }
      return map[this.activeTab] || 'PickIng'
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
