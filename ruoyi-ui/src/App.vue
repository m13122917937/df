<template>
  <div id="app">
    <router-view />
    <theme-picker ref="themePicker" />
  </div>
</template>

<script>
import ThemePicker from "@/components/ThemePicker"
import { applyPrimaryColor } from "@/utils/theme-color"

export default {
  name: "App",
  components: { ThemePicker },
  created() {
    // 尽早设置 data-theme，防止白屏
    const mode = this.$store.state.settings.themeMode || 'light'
    document.documentElement.setAttribute('data-theme', mode)
    // 应用保存的主题色
    const savedColor = this.$store.state.settings.primaryColor || '#1677FF'
    applyPrimaryColor(savedColor)
  },
  computed: {
    themeMode() {
      return this.$store.state.settings.themeMode
    },
    primaryColor() {
      return this.$store.state.settings.primaryColor
    }
  },
  watch: {
    themeMode(val) {
      document.documentElement.setAttribute('data-theme', val || 'light')
    },
    primaryColor(val) {
      if (val) {
        applyPrimaryColor(val)
      }
    }
  }
}
</script>
<style scoped>
#app .theme-picker {
  display: none;
}
</style>
