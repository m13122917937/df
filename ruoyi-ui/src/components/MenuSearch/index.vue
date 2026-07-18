<template>
  <div class="menu-search">
    <button class="search-trigger" type="button" @click="openSearch">
      <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="11" cy="11" r="8" />
        <line x1="21" y1="21" x2="16.65" y2="16.65" />
      </svg>
      <span>搜索菜单或功能</span>
      <kbd>Ctrl+K</kbd>
    </button>

    <el-dialog
      :visible.sync="dialogVisible"
      width="560px"
      append-to-body
      :show-close="false"
      custom-class="menu-search-dialog"
      @opened="focusSearchInput"
    >
      <div slot="title" class="search-dialog-title">
        <span>搜索菜单或功能</span>
        <span class="search-dialog-hint">Esc 关闭</span>
      </div>
      <el-input
        ref="searchInput"
        v-model="inputText"
        class="search-dialog-input"
        prefix-icon="el-icon-search"
        placeholder="输入菜单名称、功能或路径"
        clearable
        @keydown.native="onKeydown"
      />

      <div class="search-dialog-results">
        <template v-if="inputText">
          <div
            v-for="(item, idx) in filteredResults"
            :key="item.fullPath"
            class="result-item"
            :class="{ highlighted: idx === highlightIdx }"
            @click="selectItem(item)"
            @mouseenter="highlightIdx = idx"
          >
            <div class="result-icon"><i class="el-icon-right" /></div>
            <div class="result-content">
              <div class="result-title">{{ item.title }}</div>
              <div class="result-path">{{ item.parents.join(' / ') || '菜单' }}</div>
            </div>
          </div>
          <div v-if="!filteredResults.length" class="search-empty">未找到相关菜单</div>
        </template>
        <div v-else class="search-empty">输入关键词，查询你有权限访问的菜单和功能</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'MenuSearch',
  data() {
    return {
      dialogVisible: false,
      inputText: '',
      highlightIdx: 0,
    }
  },
  computed: {
    ...mapGetters(['sidebarRouters']),
    flatMenus() {
      return this.flattenMenu(this.sidebarRouters || [])
    },
    filteredResults() {
      const keyword = this.inputText.trim().toLowerCase()
      if (!keyword) return []
      return this.flatMenus.filter(item =>
        item.title.toLowerCase().includes(keyword) ||
        item.fullPath.toLowerCase().includes(keyword) ||
        item.parents.some(parent => parent.toLowerCase().includes(keyword))
      ).slice(0, 8)
    },
  },
  watch: {
    inputText() {
      this.highlightIdx = 0
    },
    '$route'() {
      this.closeSearch()
    },
  },
  mounted() {
    document.addEventListener('keydown', this.handleGlobalKeydown)
  },
  beforeDestroy() {
    document.removeEventListener('keydown', this.handleGlobalKeydown)
  },
  methods: {
    flattenMenu(routes, basePath = '', parentChain = []) {
      let result = []
      routes.forEach(route => {
        if (route.hidden) return
        const routePath = route.path || ''
        const fullPath = routePath.startsWith('/')
          ? routePath
          : basePath ? `${basePath.replace(/\/$/, '')}/${routePath}` : routePath
        const title = route.meta && route.meta.title
        if (route.children && route.children.length) {
          result = result.concat(this.flattenMenu(route.children, fullPath, title ? [...parentChain, title] : parentChain))
        } else if (title) {
          result.push({ title, fullPath, parents: parentChain })
        }
      })
      return result
    },
    openSearch() {
      this.dialogVisible = true
    },
    closeSearch() {
      this.dialogVisible = false
      this.inputText = ''
      this.highlightIdx = 0
    },
    focusSearchInput() {
      this.$nextTick(() => this.$refs.searchInput && this.$refs.searchInput.focus())
    },
    handleGlobalKeydown(event) {
      if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'k') {
        event.preventDefault()
        this.openSearch()
      }
    },
    onKeydown(event) {
      const len = this.filteredResults.length
      if (event.key === 'ArrowDown' && len) {
        event.preventDefault()
        this.highlightIdx = this.highlightIdx < len - 1 ? this.highlightIdx + 1 : 0
      } else if (event.key === 'ArrowUp' && len) {
        event.preventDefault()
        this.highlightIdx = this.highlightIdx > 0 ? this.highlightIdx - 1 : len - 1
      } else if (event.key === 'Enter' && this.filteredResults[this.highlightIdx]) {
        event.preventDefault()
        this.selectItem(this.filteredResults[this.highlightIdx])
      } else if (event.key === 'Escape') {
        this.closeSearch()
      }
    },
    selectItem(item) {
      this.$router.push(item.fullPath).catch(() => {})
      this.closeSearch()
    },
  },
}
</script>

<style scoped lang="scss">
.search-trigger {
  display: flex;
  align-items: center;
  width: 320px;
  height: 36px;
  padding: 0 10px;
  border: 1.5px solid transparent;
  border-radius: 8px;
  background: var(--search-bg, #F1F4F9);
  color: var(--adm-text-tertiary, #94A3B8);
  cursor: pointer;
  font: inherit;
  font-size: 13px;
  text-align: left;
  transition: all 200ms ease;

  &:hover {
    background: var(--search-hover-bg, #E9EDF4);
    border-color: var(--menu-active-text, #1677FF);
  }

  span { flex: 1; }
  kbd {
    padding: 2px 6px;
    border: 1px solid var(--adm-border, #E2E8F0);
    border-radius: 5px;
    background: var(--contextmenu-bg, #FFF);
    color: inherit;
    font-size: 11px;
  }
}

.search-icon { margin-right: 8px; }
</style>

<style lang="scss">
.menu-search-dialog {
  border-radius: 12px;
  overflow: hidden;

  .el-dialog__header {
    padding: 18px 20px 12px;
  }
  .el-dialog__body { padding: 0 20px 20px; }
}

.search-dialog-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--adm-text-primary, #1E293B);
  font-size: 16px;
  font-weight: 600;
}

.search-dialog-hint {
  color: var(--adm-text-tertiary, #94A3B8);
  font-size: 12px;
  font-weight: 400;
}

.search-dialog-input .el-input__inner { height: 40px; }

.search-dialog-results {
  min-height: 160px;
  max-height: 360px;
  margin-top: 12px;
  overflow-y: auto;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;

  &:hover, &.highlighted { background: var(--contextmenu-hover, #F1F5F9); }
}

.result-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 6px;
  background: var(--primary-light, #EBF3FF);
  color: var(--primary-color, #1677FF);
}

.result-content { min-width: 0; }
.result-title { color: var(--adm-text-primary, #1E293B); font-size: 14px; font-weight: 500; }
.result-path { margin-top: 2px; color: var(--adm-text-tertiary, #94A3B8); font-size: 12px; }
.search-empty { padding: 58px 16px; color: var(--adm-text-tertiary, #94A3B8); font-size: 13px; text-align: center; }
</style>
