<template>
  <div class="sidebar-container" :class="{ 'has-logo': showLogo }">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <div class="sidebar-panels">
      <module-nav
        ref="moduleNav"
        @select="onModuleSelect"
      />
      <business-menu
        v-if="hasBizMenu"
        :module="activeModule"
      />
    </div>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex';
import Logo from './Logo';
import ModuleNav from './ModuleNav';
import BusinessMenu from './BusinessMenu';

export default {
  components: { Logo, ModuleNav, BusinessMenu },
  data() {
    return {
      activeModule: null
    };
  },
  computed: {
    ...mapState(['settings']),
    ...mapGetters(['sidebarRouters', 'sidebar']),
    showLogo() {
      return this.$store.state.settings.sidebarLogo;
    },
    isCollapse() {
      return !this.sidebar.opened;
    },
    hasBizMenu() {
      if (!this.activeModule) return false;
      return this.activeModule.children && this.activeModule.children.length > 0;
    }
  },
  watch: {
    '$route.path': {
      immediate: true,
      handler() {
        if (!this.activeModule) {
          this.activeModule = this.findActiveModule();
        } else {
          const current = this.findActiveModule();
          if (current && current.path !== this.activeModule.path) {
            this.activeModule = current;
          }
        }
      }
    },
    hasBizMenu: {
      immediate: true,
      handler(val) {
        const w = val ? '300px' : '80px';
        document.documentElement.style.setProperty('--sidebar-expanded-width', w);
      }
    }
  },
  mounted() {
    this.activeModule = this.findActiveModule();
  },
  methods: {
    onModuleSelect(module) {
      this.activeModule = module;
    },
    getModules() {
      return (this.sidebarRouters || []).filter(
        r => !r.hidden && r.meta && r.meta.title && r.children && r.children.length > 0
      );
    },
    findActiveModule() {
      const path = this.$route.path;
      const mods = this.getModules();
      for (const mod of mods) {
        if (this.containsPath(mod, path)) return mod;
      }
      return mods[0] || null;
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
    }
  }
};
</script>
