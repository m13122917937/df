<template>
  <section
    v-watermark="watermarkConfig"
    class="app-main"
  >
    <keep-alive :include="cachedViews">
      <router-view :key="key" />
    </keep-alive>
  </section>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'AppMain',
  computed: {
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    key() {
      return this.$route.path
    },
    ...mapGetters([
      'name',
      'currentCompany'
    ]),
    watermarkConfig() {
      // 获取公司名称，优先使用当前选中公司，如果没有则从公司列表中取第一个
      const companyName = this.currentCompany?.companyName ||
                         this.companyVOList?.[0]?.companyName ||
                         ''

      // 拼接水印文本：公司名称 + 用户名（如果有）
      const textParts = []
      if (companyName) {
        textParts.push(companyName)
      }
      if (this.name) {
        textParts.push(this.name)
      }
      // 如果没有公司名称和用户名，使用默认文本
      const text = textParts.length > 0 ? textParts.join(' ') : '水印自定义'

      return {
        text: text,
        zIndex: 3,
        height: 84,
        widthMultiple: 1.8
      }
    },
    companyVOList() {
      return this.$store.getters.companyVOList || []
    }
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  /* 64= navbar  64  */
  min-height: calc(100vh - 64px);
  width: 100%;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.fixed-header+.app-main {
  padding-top: 50px;
}

.hasTagsView {
  .app-main {
    /* 84 = navbar + tags-view = 50 + 34 */
    min-height: calc(100vh - 84px);
  }

  .fixed-header+.app-main {
    padding-top: 84px;
  }
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 15px;
  }
}
</style>
