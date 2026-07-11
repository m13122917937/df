<template>
  <div class="navbar">
    <div class="navbar-left">
      <div class="breadcrumb-bar">
        <template v-for="(crumb, idx) in breadcrumbs">
          <span v-if="idx > 0" class="breadcrumb-sep">/</span>
          <span class="breadcrumb-item" :class="{ current: idx === breadcrumbs.length - 1 }">{{ crumb }}</span>
        </template>
      </div>
      <top-nav v-if="topNav" id="topmenu-container" class="topmenu-container" />
    </div>

    <!-- Right: Actions -->
    <div class="navbar-right">
      <button class="icon-btn" title="通知">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
          <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
        </svg>
        <span class="dot-indicator"></span>
      </button>
      <button class="icon-btn" title="帮助">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"/>
          <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/>
          <line x1="12" y1="17" x2="12.01" y2="17"/>
        </svg>
      </button>
      <button class="icon-btn" title="快捷入口">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="3" width="7" height="7" rx="1"/>
          <rect x="14" y="3" width="7" height="7" rx="1"/>
          <rect x="3" y="14" width="7" height="7" rx="1"/>
          <rect x="14" y="14" width="7" height="7" rx="1"/>
        </svg>
      </button>

      <screenfull class="screenfull-btn" />

      <button class="icon-btn theme-toggle" :title="isDark ? '切换白天模式' : '切换暗黑模式'" @click="toggleThemeMode">
        <svg v-if="!isDark" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="5"/>
          <line x1="12" y1="1" x2="12" y2="3"/>
          <line x1="12" y1="21" x2="12" y2="23"/>
          <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
          <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
          <line x1="1" y1="12" x2="3" y2="12"/>
          <line x1="21" y1="12" x2="23" y2="12"/>
          <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
          <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
        </svg>
        <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
        </svg>
      </button>

      <el-dropdown class="avatar-container" trigger="hover">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar">
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>
              <span>{{ nickName }}</span>
              <span class="dropdown-sub">个人中心</span>
            </el-dropdown-item>
          </router-link>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import TopNav from '@/components/TopNav'
import Screenfull from '@/components/Screenfull'

export default {
  emits: ['setLayout'],
  components: {
    TopNav,
    Screenfull,
  },
  computed: {
    ...mapGetters([
      'avatar',
      'device',
      'nickName',
      'sidebarRouters'
    ]),
    topNav: {
      get() {
        return this.$store.state.settings.topNav
      }
    },
    isDark() {
      return this.$store.state.settings.themeMode === 'dark'
    },
    breadcrumbs() {
      const crumbs = [];
      const path = this.$route.path;
      const routers = this.sidebarRouters || [];
      // Find module
      for (const module of routers) {
        if (module.hidden) continue;
        const group = this.findInModule(module, path);
        if (group) {
          crumbs.push(module.meta && module.meta.title || module.name);
          if (group !== module) {
            crumbs.push(group.meta && group.meta.title || group.name);
          }
          // Find the leaf page title
          const pageTitle = this.$route.meta && this.$route.meta.title;
          if (pageTitle) {
            crumbs.push(pageTitle);
          }
          break;
        }
      }
      return crumbs;
    }
  },
  methods: {
    findInModule(module, targetPath) {
      if (!module.children) return null;
      for (const child of module.children) {
        if (child.path === targetPath) return module;
        if (child.path && targetPath.startsWith(child.path + '/')) return module;
        if (child.children) {
          for (const grandchild of child.children) {
            if (grandchild.path === targetPath) return child;
            if (grandchild.path && targetPath.startsWith(grandchild.path + '/')) return child;
          }
        }
      }
      return null;
    },
    setLayout(event) {
      this.$emit('setLayout')
    },
    toggleThemeMode() {
      const newMode = this.isDark ? 'light' : 'dark'
      this.$store.dispatch('settings/changeSetting', {
        key: 'themeMode',
        value: newMode
      })
    },
    logout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          location.href = '/index'
        })
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  display: flex;
  align-items: center;
  height: 56px;
  padding: 0 20px;
  background: var(--bg-navbar);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'HarmonyOS Sans', 'PingFang SC', system-ui, sans-serif;
  position: relative;
  z-index: 10;
}

.navbar-left {
  display: inline-flex;
  align-items: center;
  gap: 16px;
}

.breadcrumb-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--adm-text-secondary);
  white-space: nowrap;
}

.breadcrumb-item {
  color: var(--adm-text-tertiary);
  font-weight: 400;

  &.current {
    color: var(--adm-text-primary);
    font-weight: 600;
  }
}

.breadcrumb-sep {
  color: var(--adm-text-disabled);
  font-size: 11px;
  margin: 0 2px;
}

.navbar-right {
  flex: 0 0 auto;
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 4px;
}

.icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: none;
  background: transparent;
  color: var(--menu-text);
  cursor: pointer;
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);
  position: relative;

  &:hover {
    background: var(--menu-hover-bg);
    color: var(--menu-hover-text);
  }
}

.dot-indicator {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #FF5E57;
  border: 2px solid var(--bg-navbar);
}

.avatar-container {
  margin-left: 4px;
  cursor: pointer;

  .avatar-wrapper {
    display: flex;
    align-items: center;

    .user-avatar {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      object-fit: cover;
    }
  }
}

.topmenu-container {
  position: static;
  display: inline-flex;
  width: auto;
  border-bottom: none !important;
}

.screenfull-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  cursor: pointer;
  color: var(--menu-text);
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:hover {
    background: var(--menu-hover-bg);
    color: var(--menu-hover-text);
  }
}

::v-deep .el-dropdown-menu {
  border-radius: 12px;
  border: 1px solid var(--adm-border);
  box-shadow: var(--shadow-popup);
  padding: 4px;
  background: var(--contextmenu-bg);

  .el-dropdown-menu__item {
    border-radius: 8px;
    padding: 8px 12px;
    font-size: 13px;
    color: var(--adm-text-primary);

    &:hover {
      background: var(--contextmenu-hover);
      color: #5B7CFA;
    }
  }
}

.dropdown-sub {
  display: block;
  font-size: 11px;
  color: var(--adm-text-tertiary);
  margin-top: 1px;
}
</style>
