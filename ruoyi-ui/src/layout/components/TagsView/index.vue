<template>
  <div id="tags-view-container" class="tags-view-container">
    <scroll-pane ref="scrollPane" class="tags-view-wrapper" @scroll="handleScroll">
      <router-link
        v-for="tag in visitedViews"
        ref="tag"
        :key="tag.path"
        :class="{ 'active': isActive(tag) }"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
        tag="span"
        class="tags-view-item"
        @click.middle.native="!isAffix(tag)?closeSelectedTag(tag):''"
        @contextmenu.prevent.native="openMenu(tag,$event)"
      >
        <span class="tab-star" :class="{ 'is-favorite': isFavorite(tag) }" @click.prevent.stop="toggleFavoriteTag(tag)">
          <svg v-if="isFavorite(tag)" width="14" height="14" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="1" class="star-icon-filled" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
          <svg v-else width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
        </span>
        <span class="tab-label">{{ getTabTitle(tag) }}</span>
        <span class="tab-close" @click.prevent.stop="closeSelectedTag(tag)">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </span>
      </router-link>
    </scroll-pane>
    <ul v-show="visible" :style="{left:left+'px',top:top+'px'}" class="contextmenu">
      <li @click="refreshSelectedTag(selectedTag)"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/></svg> 刷新页面</li>
      <li @click="toggleFavoriteTag(selectedTag)"><svg v-if="isFavorite(selectedTag)" width="14" height="14" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="1" class="star-icon-filled" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg><svg v-else width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg> {{ isFavorite(selectedTag) ? '取消收藏' : '收藏此页' }}</li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg> 关闭当前</li>
      <li @click="closeOthersTags"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="8" y1="12" x2="16" y2="12"/></svg> 关闭其他</li>
      <li v-if="!isFirstView()" @click="closeLeftTags"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg> 关闭左侧</li>
      <li v-if="!isLastView()" @click="closeRightTags"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg> 关闭右侧</li>
      <li @click="closeAllTags(selectedTag)"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="8" y1="12" x2="16" y2="12"/></svg> 全部关闭</li>
    </ul>
  </div>
</template>

<script>
import ScrollPane from './ScrollPane'
import path from 'path'

export default {
  components: { ScrollPane },
  data() {
    return {
      visible: false,
      top: 0,
      left: 0,
      selectedTag: {},
      affixTags: []
    }
  },
  computed: {
    visitedViews() {
      return this.$store.state.tagsView.visitedViews
    },
    routes() {
      return this.$store.state.permission.routes
    },
    theme() {
      return this.$store.state.settings.theme
    }
  },
  watch: {
    $route() {
      this.addTags()
      this.moveToCurrentTag()
    },
    visible(value) {
      if (value) {
        document.body.addEventListener('click', this.closeMenu)
      } else {
        document.body.removeEventListener('click', this.closeMenu)
      }
    }
  },
  mounted() {
    this.initTags()
    this.$nextTick(() => {
      this.restoreFavoriteTabs()
    })
    this.addTags()
  },
  methods: {
    getTabTitle(tag) {
      const title = tag.title || ''
      const path = tag.path || ''
      // 主路由页面直接返回原始标题，不添加模块前缀
      const mainRoutes = ['/demandManage/wholesale', '/demandManage/putin', '/demandManage/sale', '/monery/paymentToday', '/pickManage/warehouse']
      if (mainRoutes.includes(path)) {
        return title
      }
      const moduleMap = [
        { pattern: '/demandManage/wholesale/', prefix: '代发-', keyword: '代发' },
        { pattern: '/wholesale', prefix: '代发-', keyword: '代发' },
        { pattern: '/demandManage/putin/', prefix: '入仓-', keyword: '入仓' },
        { pattern: '/putin', prefix: '入仓-', keyword: '入仓' },
        { pattern: '/demandManage/sale/', prefix: '销售-', keyword: '销售' },
        { pattern: '/sale', prefix: '销售-', keyword: '销售' },
      ]
      for (const { pattern, prefix, keyword } of moduleMap) {
        if (path.includes(pattern) && !title.includes(keyword)) {
          return prefix + title
        }
      }
      return title
    },
    isActive(route) {
      return route.path === this.$route.path
    },
    activeStyle(tag) {
      if (!this.isActive(tag)) return {}
      return {
        "background-color": this.theme,
        "border-color": this.theme
      }
    },
    isAffix(tag) {
      return tag.meta && tag.meta.affix
    },
    isFirstView() {
      try {
        return this.selectedTag.fullPath === '/index' || this.selectedTag.fullPath === this.visitedViews[1].fullPath
      } catch (err) {
        return false
      }
    },
    isLastView() {
      try {
        return this.selectedTag.fullPath === this.visitedViews[this.visitedViews.length - 1].fullPath
      } catch (err) {
        return false
      }
    },
    filterAffixTags(routes, basePath = '/') {
      let tags = []
      routes.forEach(route => {
        if (route.meta && route.meta.affix) {
          const tagPath = path.resolve(basePath, route.path)
          tags.push({
            fullPath: tagPath,
            path: tagPath,
            name: route.name,
            meta: { ...route.meta }
          })
        }
        if (route.children) {
          const tempTags = this.filterAffixTags(route.children, route.path)
          if (tempTags.length >= 1) {
            tags = [...tags, ...tempTags]
          }
        }
      })
      return tags
    },
    initTags() {
      const affixTags = this.affixTags = this.filterAffixTags(this.routes)
      for (const tag of affixTags) {
        if (tag.name) {
          this.$store.dispatch('tagsView/addVisitedView', tag)
        }
      }
    },
    restoreFavoriteTabs() {
      const favorites = this.$store.state.tagsView.favoriteTabs
      if (!favorites || favorites.length === 0) return
      const visitedPaths = this.$store.state.tagsView.visitedViews.map(v => v.path)
      const findAndAdd = (routes, basePath = '/') => {
        for (const route of routes) {
          const routePath = path.resolve(basePath, route.path)
          if (favorites.includes(routePath) && !visitedPaths.includes(routePath) && route.name) {
            this.$store.dispatch('tagsView/addView', {
              fullPath: routePath,
              path: routePath,
              name: route.name,
              meta: { ...route.meta, title: route.meta?.title || route.name }
            })
          }
          if (route.children) {
            findAndAdd(route.children, routePath)
          }
        }
      }
      findAndAdd(this.routes)
    },
    addTags() {
      const { name } = this.$route
      if (name) {
        this.$store.dispatch('tagsView/addView', this.$route)
      }
    },
    moveToCurrentTag() {
      const tags = this.$refs.tag
      this.$nextTick(() => {
        for (const tag of tags) {
          if (tag.to.path === this.$route.path) {
            this.$refs.scrollPane.moveToTarget(tag)
            if (tag.to.fullPath !== this.$route.fullPath) {
              this.$store.dispatch('tagsView/updateVisitedView', this.$route)
            }
            break
          }
        }
      })
    },
    refreshSelectedTag(view) {
      this.$tab.refreshPage(view)
      if (this.$route.meta.link) {
        this.$store.dispatch('tagsView/delIframeView', this.$route)
      }
    },
    isFavorite(tag) {
      return this.$store.state.tagsView.favoriteTabs.includes(tag.path)
    },
    toggleFavoriteTag(tag) {
      this.$store.dispatch('tagsView/toggleFavorite', tag.path)
    },
    closeSelectedTag(view) {
      if (this.isFavorite(view)) {
        this.$confirm('该标签页已收藏，确定要关闭吗？', '提示', {
          confirmButtonText: '确定关闭',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.doCloseSelectedTag(view)
        }).catch(() => {})
      } else {
        this.doCloseSelectedTag(view)
      }
    },
    doCloseSelectedTag(view) {
      this.$tab.closePage(view).then(({ visitedViews }) => {
        if (this.isActive(view)) {
          this.toLastView(visitedViews, view)
        }
      })
    },
    closeRightTags() {
      this.$tab.closeRightPage(this.selectedTag).then(visitedViews => {
        if (!visitedViews.find(i => i.fullPath === this.$route.fullPath)) {
          this.toLastView(visitedViews)
        }
      })
    },
    closeLeftTags() {
      this.$tab.closeLeftPage(this.selectedTag).then(visitedViews => {
        if (!visitedViews.find(i => i.fullPath === this.$route.fullPath)) {
          this.toLastView(visitedViews)
        }
      })
    },
    closeOthersTags() {
      this.$router.push(this.selectedTag.fullPath).catch(()=>{})
      this.$tab.closeOtherPage(this.selectedTag).then(() => {
        this.moveToCurrentTag()
      })
    },
    closeAllTags(view) {
      this.$tab.closeAllPage().then(({ visitedViews }) => {
        if (this.affixTags.some(tag => tag.path === this.$route.path)) {
          return
        }
        this.toLastView(visitedViews, view)
      })
    },
    toLastView(visitedViews, view) {
      const latestView = visitedViews.slice(-1)[0]
      if (latestView) {
        this.$router.push(latestView.fullPath)
      } else {
        const firstRoute = this.getFirstRoute()
        if (firstRoute) {
          this.$router.push(firstRoute)
        }
      }
    },
    getFirstRoute() {
      const sidebarRoutes = this.$store.state.permission.sidebarRouters || []
      for (const route of sidebarRoutes) {
        if (route.hidden) continue
        if (route.redirect && route.redirect !== 'noredirect') {
          return route.redirect
        }
        if (route.children && route.children.length > 0) {
          for (const child of route.children) {
            if (child.hidden) continue
            if (child.redirect && child.redirect !== 'noredirect') return child.redirect
            if (child.path) {
              return (route.path ? route.path + '/' + child.path : child.path).replace(/\/+/g, '/')
            }
          }
        }
      }
      return '/'
    },
    openMenu(tag, e) {
      const menuMinWidth = 105
      const offsetLeft = this.$el.getBoundingClientRect().left
      const offsetWidth = this.$el.offsetWidth
      const maxLeft = offsetWidth - menuMinWidth
      const left = e.clientX - offsetLeft + 15

      if (left > maxLeft) {
        this.left = maxLeft
      } else {
        this.left = left
      }

      this.top = e.clientY
      this.visible = true
      this.selectedTag = tag
    },
    closeMenu() {
      this.visible = false
    },
    handleScroll() {
      this.closeMenu()
    }
  }
}
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 56px;
  width: 100%;
  background: var(--bg-navbar);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'HarmonyOS Sans', 'PingFang SC', system-ui, sans-serif;
  display: flex;
  align-items: center;
  padding: 0 24px;
  position: relative;
  z-index: 5;

  .tags-view-wrapper {
    display: flex;
    align-items: center;
    height: 100%;
    flex: 1;
    overflow: hidden;

    .tags-view-item {
      position: relative;
      display: inline-flex;
      align-items: center;
      gap: 6px;
      height: 40px;
      min-width: 110px;
      padding: 0 18px;
      border: 1.5px solid var(--border-tags);
      border-radius: 12px;
      margin-right: 12px;
      cursor: pointer;
      color: var(--menu-text);
      font-size: 14px;
      font-weight: 500;
      background: transparent;
      text-decoration: none;
      user-select: none;
      transition: color 150ms ease;
      flex-shrink: 0;
      white-space: nowrap;

      &:hover {
        color: var(--tag-hover-text);
      }

      &.active {
        color: var(--tag-active-text);
        font-weight: 600;
        border: 1.5px solid var(--tag-active-text);
      }

      .tab-star {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 18px;
        height: 18px;
        flex-shrink: 0;
        opacity: 0;
        transition: opacity 150ms ease;

        &.is-favorite {
          opacity: 1;
          color: var(--star-fav-color);
        }

        svg {
          width: 14px;
          height: 14px;
        }
      }

      &:hover .tab-star:not(.is-favorite) {
        opacity: 0.6;
      }

      .tab-label {
        line-height: 1;
      }

      .tab-close {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 18px;
        height: 18px;
        border-radius: 4px;
        opacity: 1;
        pointer-events: auto;
        transition: opacity 150ms ease, background 150ms ease;
        color: var(--adm-text-tertiary);
        flex-shrink: 0;
        color: var(--star-fav-color);
        margin-left: -2px;

        &:hover {
          background: var(--tag-hover-bg);
          color: var(--adm-text-secondary);
        }

        svg {
          width: 14px;
          height: 14px;
        }
      }
    }
  }

  .contextmenu {
    margin: 0;
    background: var(--contextmenu-bg);
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 4px;
    border-radius: 12px;
    font-size: 13px;
    font-weight: 500;
    color: var(--adm-text-primary);
    border: 1px solid var(--adm-border);
    box-shadow: var(--shadow-popup);
    min-width: 140px;

    li {
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 0;
      padding: 7px 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: background 150ms ease, color 200ms ease-out;
      color: var(--menu-text);
      font-size: 13px;

      &:hover {
        background: var(--contextmenu-hover);
        color: var(--menu-active-text);
      }

      svg {
        flex-shrink: 0;
      }
      svg.star-icon-filled {
        color: var(--star-fav-color);
      }
    }
  }
}
</style>
