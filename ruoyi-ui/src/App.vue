<template>
  <div id="app">
    <router-view />
    <theme-picker />
  </div>
</template>

<script>
import ThemePicker from "@/components/ThemePicker"

export default {
  name: "App",
  components: { ThemePicker },
  created() {
    // 尽早设置 data-theme，防止白屏
    const mode = this.$store.state.settings.themeMode || 'light'
    document.documentElement.setAttribute('data-theme', mode)
  },
  computed: {
    themeMode() {
      return this.$store.state.settings.themeMode
    }
  },
  watch: {
    themeMode(val) {
      document.documentElement.setAttribute('data-theme', val || 'light')
    }
  }
}
</script>
<style scoped>
#app .theme-picker {
  display: none;
}
</style>
