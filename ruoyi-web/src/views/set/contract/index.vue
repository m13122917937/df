<template>
  <div class="contract-container">
    <!-- 左侧菜单 -->
    <div class="contract-sidebar">
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
    <div class="contract-content">
      <router-view />
    </div>
  </div>
</template>

<script>
export default {
  name: 'ContractManagement',
  data() {
    return {
      activeMenu: 'sign-info',
      menuList: [
        {
          key: 'sign-info',
          title: '签署信息',
          icon: 'el-icon-document'
        },
        {
          key: 'pending',
          title: '待签署',
          icon: 'el-icon-time'
        },
        {
          key: 'signed',
          title: '已签署',
          icon: 'el-icon-circle-check'
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
      if (routePath.includes('pending')) {
        this.activeMenu = 'pending'
      } else if (routePath.includes('signed')) {
        this.activeMenu = 'signed'
      } else {
        this.activeMenu = 'sign-info'
      }
    },
    handleMenuSelect(key) {
      this.activeMenu = key
      const routeMap = {
        'sign-info': '/monery/contract/sign-info',
        'pending': '/monery/contract/pending',
        'signed': '/monery/contract/signed'
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
.contract-container {
  height: 100%;
  display: flex;
  background: #f5f7fa;

  .contract-sidebar {
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
        color: #1677FF;
      }

      &.active {
        background: #ecf5ff;
        color: #1677FF;
        border-right: 3px solid #1677FF;
        font-weight: 500;
      }
    }
  }

  .contract-content {
    flex: 1;
    padding: 16px;
    overflow-y: auto;
  }
}
</style>
