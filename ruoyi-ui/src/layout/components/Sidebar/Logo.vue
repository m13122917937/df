<template>
  <div class="sidebar-logo" :class="{ collapse: collapse }">
    <div class="logo-nav-area">
      <span v-if="!collapse" class="logo-title">無界电商</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  }
};
</script>

<style scoped lang="scss">
.sidebar-logo {
  display: flex;
  align-items: center;
  height: 56px;
  flex-shrink: 0;
  border-bottom: 1px solid var(--border-sidebar);
  backdrop-filter: var(--blur-sidebar);
  -webkit-backdrop-filter: var(--blur-sidebar);
}

.logo-nav-area {
  width: var(--module-nav-width, 80px);
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  flex-shrink: 0;
}

.logo-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--adm-text-primary);
  white-space: nowrap;
  letter-spacing: -0.01em;
}

.sidebar-logo.collapse {
  .logo-title {
    display: none;
  }
}
</style>
