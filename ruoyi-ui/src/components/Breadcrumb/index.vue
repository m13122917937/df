<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <span v-if="item.redirect === 'noRedirect' || index == levelList.length - 1" class="no-redirect">{{ item.meta.title }}</span>
        <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script>
export default {
  data() {
    return {
      levelList: null
    }
  },
  watch: {
    $route(route) {
      // if you go to the redirect page, do not update the breadcrumbs
      if (route.path.startsWith('/redirect/')) {
        return
      }
      this.getBreadcrumb()
    }
  },
  created() {
    this.getBreadcrumb()
  },
  methods: {
    getBreadcrumb() {
      let matched = []
      const currentPath = this.$route.path
      const routes = this.$store.getters.defaultRoutes || []
      this.walkRoutes(currentPath, routes, matched, '')
      if (matched.length === 0) {
        matched = this.$route.matched.filter(item => item.meta && item.meta.title)
      }
      if (!this.isDashboard(matched[0])) {
        matched = [{ path: '/index', meta: { title: '首页' } }].concat(matched)
      }
      this.levelList = matched.filter(item => item.meta && item.meta.title && item.meta.breadcrumb !== false)
    },
    walkRoutes(currentPath, routeList, matched, parentPath) {
      for (const route of routeList) {
        const routePath = route.path || ''
        if (!routePath) {
          if (currentPath === parentPath || currentPath === parentPath + '/') {
            if (route.meta && route.meta.title) matched.push(route)
            if (route.children) this.walkRoutes(currentPath, route.children, matched, parentPath)
          }
          continue
        }
        const fullPath = routePath.startsWith('/') ? routePath : (parentPath ? parentPath + '/' + routePath : routePath)
        if (currentPath === fullPath || currentPath.startsWith(fullPath + '/')) {
          if (route.meta && route.meta.title) matched.push(route)
          if (route.children && route.children.length > 0) {
            this.walkRoutes(currentPath, route.children, matched, fullPath)
          }
          return
        }
      }
    },
    isDashboard(route) {
      const name = route && route.name
      if (!name) {
        return false
      }
      return name.trim() === 'Index'
    },
    handleLink(item) {
      const { redirect, path } = item
      if (redirect) {
        this.$router.push(redirect)
        return
      }
      this.$router.push(path)
    }
  }
}
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  margin-left: 8px;
  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}
</style>
