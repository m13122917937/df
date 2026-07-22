<template>
  <div :class="classObj" class="app-wrapper">
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
      <navbar />
    </div>

    <!-- 主体容器 -->
    <div class="main-container">
      <div class="main-content">
        <app-main />
      </div>
    </div>
  </div>
</template>

<script>
import { AppMain, Navbar } from './components'
import { mapState } from 'vuex'

export default {
  name: 'Layout',
  components: {
    AppMain,
    Navbar
  },
  computed: {
    ...mapState({
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
      showSettings: state => state.settings.showSettings,
      needTagsView: state => state.settings.tagsView,
      fixedHeader: state => state.settings.fixedHeader
    }),
    classObj() {
      return {
        hideSidebar: !this.sidebar.opened,
        openSidebar: this.sidebar.opened,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    },

    isHideSidebar() {
      // 需要隐藏侧边栏的路由列表
      const hideSidebarRoutes = [
        '/order/all'
      ]

      // 检查当前路由是否在隐藏列表中
      return hideSidebarRoutes.includes(this.$route.path)
    }
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
    },
    handleRegionChange(regionCode) {
      console.log('Selected region:', regionCode)
      // 处理地区选择变化
      this.$store.dispatch('app/setSelectedRegion', regionCode)
    }
  }
}
</script>

<style lang="scss" scoped>
  @import "~@/styles/mixin.scss";
  @import "~@/styles/variables.scss";

  .app-wrapper {
    height: 100vh;
    overflow: hidden;
    position: relative;
    height: 100vh;
    width: 100%;
    display: flex;
    flex-direction: column;

    .top-navbar {
      height: 64px;
      width: 100%;
      box-shadow: 0 1px 0 #E5E6EB;
    }

    .main-container {
      width: 100%;
      height: calc(100vh - 64px);
      background: var(--bg-page, #F2F2F7);
    }
  }

  .drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
  }

  .fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$sideBarWidth});
    transition: width 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  }

  .hideSidebar .fixed-header {
    width: calc(100% - 54px)
  }

  .mobile .fixed-header {
    width: 100%;
  }
</style>
