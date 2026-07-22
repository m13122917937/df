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
      activeMenu: 'sign',
      menuList: [
        {
          key: 'sign',
          title: '签署信息',
          icon: 'el-icon-document'
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
      if (routePath.includes('signed')) {
        this.activeMenu = 'signed'
      } else {
        this.activeMenu = 'sign'
      }
    },
    handleMenuSelect(key) {
      this.activeMenu = key
      const routeMap = {
        'sign': '/monery/contract/sign',
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
    border-right: 1px solid #E5E6EB;
    padding: 8px 0;
    overflow-y: auto;

    .menu-item {
      display: flex;
      align-items: center;
      padding: 10px 20px;
      cursor: pointer;
      transition: all 0.2s ease;
      color: #4E5969;
      font-size: 14px;
      border-right: 2px solid transparent;

      .menu-icon {
        margin-right: 8px;
        font-size: 16px;
        color: #86909C;
        transition: color 0.2s ease;
      }

      .menu-title {
        font-size: 14px;
      }

      &:hover {
        background: rgba(22, 119, 255, 0.06);
        color: #1677FF;

        .menu-icon {
          color: #1677FF;
        }
      }

      &.active {
        background: rgba(22, 119, 255, 0.06);
        color: #1677FF;
        border-right: 2px solid #1677FF;
        font-weight: 500;

        .menu-icon {
          color: #1677FF;
        }
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
