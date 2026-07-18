<template>
  <el-tooltip content="刷新当前页面" placement="bottom">
    <button
      class="page-refresh-btn"
      :class="{ refreshing: isRefreshing }"
      :disabled="isRefreshing"
      @click="handleRefresh"
    >
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
        <polyline points="23 4 23 10 17 10" />
        <polyline points="1 20 1 14 7 14" />
        <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15" />
      </svg>
    </button>
  </el-tooltip>
</template>

<script>
export default {
  name: 'PageRefresh',
  data() {
    return {
      isRefreshing: false,
      refreshPath: null,
    }
  },
  watch: {
    $route: {
      handler(to) {
        if (!this.isRefreshing || !this.refreshPath) return
        if (to.path === this.refreshPath) {
          this.isRefreshing = false
          this.refreshPath = null
        }
      },
    },
  },
  methods: {
    handleRefresh() {
      if (this.isRefreshing) return
      this.isRefreshing = true
      this.refreshPath = this.$route.path
      this.$tab.refreshPage()
    },
  },
}
</script>

<style scoped lang="scss">
.page-refresh-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: transparent;
  color: var(--menu-text);
  cursor: pointer;
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);
  flex-shrink: 0;

  &:hover {
    background: var(--menu-hover-bg);
    color: var(--menu-hover-text);
  }

  &:disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }

  svg {
    width: 18px;
    height: 18px;
  }

  &.refreshing svg {
    animation: page-refresh-spin 0.7s linear infinite;
  }
}

@keyframes page-refresh-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
