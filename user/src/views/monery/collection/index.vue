<template>
  <div class="collection-container">
    <!-- 左侧菜单 -->
    <div class="collection-sidebar">
      <div
        v-for="item in menuList"
        :key="item.key"
        :class="['menu-item', { active: activeMenu === item.key }]"
        @click="handleMenuSelect(item.key)"
      >
        <i :class="item.icon" class="menu-icon"></i>
        <span class="menu-title">{{ item.title }}</span>
      </div>
    </div>

    <!-- 右侧内容 -->
    <div class="collection-content">
      <router-view />
    </div>
  </div>
</template>

<script>
export default {
  name: 'CollectionManagement',
  data() {
    return {
      activeMenu: 'payment-details',
      menuList: [
        {
          key: 'payment-details',
          title: '收款详情',
          icon: 'el-icon-document'
        },
        {
          key: 'addAccount',
          title: '收款账户',
          icon: 'el-icon-wallet'
        }
      ]
    }
  },
  watch: {
    '$route': {
      handler() {
        this.setActiveMenuFromRoute()
      },
      immediate: true
    }
  },
  mounted() {
    this.setActiveMenuFromRoute()
  },
  methods: {
    setActiveMenuFromRoute() {
      const routePath = this.$route.path
      if (routePath.includes('addAccount')) {
        this.activeMenu = 'addAccount'
      } else {
        this.activeMenu = 'payment-details'
      }
    },
    handleMenuSelect(key) {
      this.activeMenu = key
      const routeMap = {
        'payment-details': '/monery/collection/payment-details',
        'addAccount': '/monery/collection/addAccount'
      }
      const targetRoute = routeMap[key]
      if (targetRoute && targetRoute !== this.$route.path) {
        this.$router.push(targetRoute)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.collection-container {
  height: 100%;
  display: flex;
  background: #f5f7fa;

  .collection-sidebar {
    width: 200px;
    background: #fff;
    border-right: 1px solid #e4e7ed;
    padding: 16px 0;
    overflow-y: auto;

    .menu-item {
      display: flex;
      align-items: center;
      padding: 12px 20px;
      cursor: pointer;
      transition: all 0.3s ease;
      color: #606266;

      .menu-icon {
        margin-right: 8px;
        font-size: 16px;
      }

      .menu-title {
        font-size: 14px;
      }

      &:hover {
        background: #f5f7fa;
        color: #409eff;
      }

      &.active {
        background: #ecf5ff;
        color: #409eff;
        border-right: 3px solid #409eff;
        font-weight: 500;
      }
    }
  }

  .collection-content {
    flex: 1;
    padding: 16px;
    overflow-y: auto;
  }
}
</style>
