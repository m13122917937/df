<template>
  <div class="module-nav" :class="{ collapsed: isCollapse }">
    <div class="module-nav-list">
      <div
        v-for="mod in modules"
        :key="mod.path"
        class="module-nav-item"
        :class="{ active: isActive(mod) }"
        @click="handleSelect(mod)"
      >
        <svg-icon :icon-class="mod.meta.icon" class="module-icon" />
        <span v-if="!isCollapse" class="module-text">{{ mod.meta.title }}</span>
      </div>
    </div>
    <div class="module-collapse-toggle" @click="toggleCollapse">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
        <template v-if="isCollapse">
          <polyline points="13 17 18 12 13 7"/>
          <polyline points="6 17 11 12 6 7"/>
        </template>
        <template v-else>
          <polyline points="11 17 6 12 11 7"/>
          <polyline points="18 17 13 12 18 7"/>
        </template>
      </svg>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  name: 'ModuleNav',
  data() {
    return {
      selected: null
    };
  },
  computed: {
    ...mapGetters(['sidebarRouters', 'sidebar']),
    modules() {
      return (this.sidebarRouters || []).filter(
        r => !r.hidden && r.meta && r.meta.title
      );
    },
    isCollapse() {
      return !this.sidebar.opened;
    }
  },
  watch: {
    '$route.path': {
      immediate: true,
      handler() {
        this.selected = this.findActiveModule();
      }
    }
  },
  methods: {
    isActive(mod) {
      return this.selected && this.selected.path === mod.path;
    },
    findActiveModule() {
      const path = this.$route.path;
      for (const mod of this.modules) {
        if (this.containsPath(mod, path)) return mod;
      }
      return this.modules[0] || null;
    },
    containsPath(route, targetPath) {
      if (!route.children) return false;
      for (const child of route.children) {
        if (child.hidden) continue;
        const fullPath = this.resolvePath(child, route.path);
        if (fullPath === targetPath || targetPath.startsWith(fullPath + '/')) return true;
        if (this.containsPath(child, targetPath)) return true;
      }
      return false;
    },
    resolvePath(route, parentPath) {
      const p = route.path || '';
      if (p.startsWith('/')) return p;
      if (!parentPath) return p;
      return (parentPath.endsWith('/') ? parentPath : parentPath + '/') + p;
    },
    handleSelect(mod) {
      this.selected = mod;
      if (!mod.children || mod.children.length === 0) {
        this.$message.info('系统完善中');
        return;
      }
      this.$emit('select', mod);
    },
    toggleCollapse() {
      this.$store.dispatch('app/toggleSideBar');
    }
  }
};
</script>

<style scoped lang="scss">
.module-nav {
  width: var(--module-nav-width, 80px);
  background: var(--module-nav-bg);
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  border-right: 1px solid var(--border-sidebar);
  transition: width 0.28s;
}

.module-nav-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 8px 0;
  flex: 1;
  width: 100%;
}

.module-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 56px;
  padding: 0 16px;
  border-radius: 8px;
  cursor: pointer;
  color: var(--menu-text);
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);
  gap: 4px;

  &:hover {
    background: var(--module-nav-hover-bg);
    color: var(--menu-hover-text);
  }

  &.active {
    background: var(--module-nav-active-bg);
    color: var(--module-nav-active-text);

    .module-icon {
      color: var(--module-nav-active-text);
    }
  }
}

.module-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.module-text {
  font-size: 14px;
  font-weight: 500;
  line-height: 1.2;
  white-space: nowrap;
  text-align: center;
  max-width: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.module-collapse-toggle {
  width: 100%;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--menu-text);
  border-top: 1px solid var(--border-sidebar);
  transition: all 180ms;
  flex-shrink: 0;

  &:hover {
    background: var(--module-nav-hover-bg);
    color: #5B7CFA;
  }
}

.module-nav.collapsed {
  width: 80px;

  .module-text {
    display: none;
  }

  .module-nav-item {
    height: 56px;
    padding: 0 16px;
  }

  .module-icon {
    width: 22px;
    height: 22px;
  }
}
</style>
