<template>
  <div class="business-menu">
    <div class="biz-menu-body">
      <template v-if="isFlatMenu">
        <div
          v-for="item in flatItems"
          :key="item.path"
          class="biz-menu-item"
          :class="{ active: isItemActive(item) }"
          @click="navigate(item)"
        >
          <svg-icon v-if="item.meta && item.meta.icon" :icon-class="item.meta.icon" class="biz-item-icon" />
          <span class="biz-item-indicator" v-else></span>
          <span class="biz-item-title">{{ item.meta ? item.meta.title : '' }}</span>
        </div>
      </template>
      <template v-else>
        <!-- Standalone items (C-type leaf menus directly under module) -->
        <div
          v-for="item in standaloneItems"
          :key="item._key"
          class="biz-menu-item standalone-item"
          :class="{ active: isItemActive(item) }"
          @click="navigate(item)"
        >
          <svg-icon v-if="item.meta && item.meta.icon" :icon-class="item.meta.icon" class="biz-item-icon" />
          <span class="biz-item-indicator" v-else></span>
          <span class="biz-item-title">{{ item.meta ? item.meta.title : '' }}</span>
        </div>
        <!-- Group items (M-type directories with children) -->
        <div
          v-for="group in groups"
          :key="group._key"
          class="biz-menu-group"
          :class="{ expanded: expandedGroups[group._key] }"
        >
          <div class="biz-group-header" @click="toggleGroup(group._key)">
            <svg class="biz-group-arrow" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
            <svg-icon v-if="group.icon" :icon-class="group.icon" class="biz-group-icon" />
            <span class="biz-group-title">{{ group.name }}</span>
          </div>
          <div class="biz-group-items" v-show="expandedGroups[group._key]">
            <div
              v-for="item in group.children"
              :key="item.path"
              class="biz-menu-item"
              :class="{ active: isItemActive(item) }"
              @click="navigate(item)"
            >
              <svg-icon v-if="item.meta && item.meta.icon" :icon-class="item.meta.icon" class="biz-item-icon" />
              <span class="biz-item-indicator" v-else></span>
              <span class="biz-item-title">{{ item.meta ? item.meta.title : '' }}</span>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BusinessMenu',
  props: {
    module: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      expandedGroups: {}
    };
  },
  computed: {
    isFlatMenu() {
      if (!this.module) return true;
      return !this.module.children || this.module.children.length === 0;
    },
    flatItems() {
      if (!this.module || !this.module.children) return [];
      const modPath = this.module.path || '';
      return this.module.children
        .filter(child => !child.hidden && child.meta && child.meta.title)
        .map(child => ({ ...child, fullPath: this.resolveFullPath(modPath, child) }));
    },
    standaloneItems() {
      if (!this.module || !this.module.children) return [];
      const modPath = this.module.path || '';
      return this.module.children
        .filter(child => !child.hidden && child.meta && child.meta.title && !child.children)
        .map(child => ({
          _key: 'sa-' + (child.meta?.title || child.path),
          ...child,
          fullPath: this.resolveFullPath(modPath, child)
        }));
    },
    groups() {
      if (!this.module || !this.module.children) return [];
      const modPath = this.module.path || '';
      const result = [];
      for (const child of this.module.children) {
        if (child.hidden) continue;
        if (child.children && child.children.length > 0) {
          const catFullPath = this.resolveFullPath(modPath, child);
          const pages = (child.children || [])
            .filter(p => !p.hidden && p.meta && p.meta.title)
            .map(p => ({ ...p, fullPath: this.resolveFullPath(catFullPath, p) }));
          if (pages.length > 0) {
            result.push({
              _key: child.meta?.title || child.path,
              name: child.meta?.title || '',
              icon: child.meta?.icon,
              children: pages
            });
          }
        }
      }
      return result;
    }
  },
  watch: {
    module: {
      immediate: true,
      handler() {
        this.expandGroupsForRoute();
      }
    },
    '$route.path'() {
      this.expandGroupsForRoute();
    }
  },
  methods: {
    resolveFullPath(base, route) {
      const path = route.path || '';
      if (path.startsWith('/')) return path;
      if (!base) return path;
      return (base.endsWith('/') ? base : base + '/') + path;
    },
    toggleGroup(key) {
      this.$set(this.expandedGroups, key, !this.expandedGroups[key]);
    },
    expandGroupsForRoute() {
      const currentPath = this.$route.path;
      const grps = this.groups;
      if (grps.length === 0) return;
      const expanded = {};
      let found = false;
      for (const g of grps) {
        if (g.children.some(item => item.fullPath === currentPath || currentPath.startsWith(item.fullPath + '/'))) {
          expanded[g._key] = true;
          found = true;
        }
      }
      if (!found) {
        expanded[grps[0]._key] = true;
      }
      this.expandedGroups = expanded;
    },
    isItemActive(item) {
      const currentPath = this.$route.path;
      if (item.fullPath === currentPath) return true;
      if (currentPath.startsWith(item.fullPath + '/')) return true;
      return false;
    },
    navigate(item) {
      if (!item.fullPath) return;
      this.$router.push(item.fullPath).catch(e => console.warn('[BizMenu] navigation error:', e));
    }
  }
};
</script>

<style scoped lang="scss">
.business-menu {
  width: var(--biz-menu-width, 220px);
  display: flex;
  flex-direction: column;
  background: var(--bg-sidebar);
  flex-shrink: 0;
  overflow: hidden;
}

.biz-menu-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;

  &::-webkit-scrollbar { width: 4px; }
  &::-webkit-scrollbar-track { background: transparent; }
  &::-webkit-scrollbar-thumb { background: var(--adm-text-disabled); border-radius: 2px; }
}

.biz-menu-group {
  margin-bottom: 2px;
}

.biz-group-header {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 11px 16px;
  cursor: pointer;
  color: #475569;
  font-size: 14px;
  font-weight: 500;
  transition: color 180ms;
  user-select: none;

  &:hover {
    color: var(--adm-text-secondary);
  }
}

.biz-group-arrow {
  flex-shrink: 0;
  transition: transform 200ms ease;
  width: 16px;
  height: 16px;

  .biz-menu-group.expanded & {
    transform: rotate(90deg);
  }
}

.biz-group-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.biz-group-items {
  overflow: hidden;
}

.biz-menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 16px 9px 32px;
  margin: 0 12px;
  border-radius: 8px;
  cursor: pointer;
  color: #475569;
  font-size: 14px;
  font-weight: 400;
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:hover {
    background: #F1F5F9;
    color: #1E293B;
  }

  &.active {
    background: #1677FF;
    color: #FFFFFF;
    font-weight: 500;

    .biz-item-indicator {
      background: #FFFFFF;
    }
  }
}

.biz-item-indicator {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--adm-text-disabled);
  flex-shrink: 0;
  transition: background 180ms;
}

.biz-item-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.biz-item-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
