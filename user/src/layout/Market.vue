<template>
  <div :class="classObj" class="app-wrapper">
    <!-- 主体容器 -->
    <div class="main-container">
      <!-- 左侧地区选择边栏 -->
      <!-- 主内容区域 -->
      <div class="content-area">
        <!-- 主要内容 -->
        <div class="main-content">
          <app-main />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { AppMain } from './components'
import { mapState } from 'vuex'

export default {
  name: 'Layout',
  components: {
    AppMain
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
      @include clearfix;
      position: relative;
      height: 100vh;
      width: 100%;
      display: flex;
      flex-direction: column;

      .top-navbar {
        height: 60px;
        width: 100%;
        box-shadow: 0 1px 4px rgba(0,21,41,.08);
      }

      .main-container {
        flex: 1;
        display: flex;
        width: 100%;

        .region-sidebar-container {
          flex-shrink: 0;
          background: #fff;
          border-right: 1px solid #e6e6e6;
        }

        .content-area {
          flex: 1;
          display: flex;
          flex-direction: column;
          background: #f5f5f5;
          overflow: auto;
          .main-content {
            flex: 1;
            overflow-y: auto;
          }
        }
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
      transition: width 0.28s;
    }

    .hideSidebar .fixed-header {
      width: calc(100% - 54px)
    }

    .mobile .fixed-header {
      width: 100%;
    }
  </style>
