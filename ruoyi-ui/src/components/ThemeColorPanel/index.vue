<template>
  <div class="theme-color-panel" ref="panel">
    <!-- Trigger Button -->
    <button
      class="icon-btn theme-trigger"
      :title="isDark ? '切换浅色模式' : '切换深色模式'"
      @click="togglePanel"
    >
      <svg v-if="!isDark" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="5"/>
        <line x1="12" y1="1" x2="12" y2="3"/>
        <line x1="12" y1="21" x2="12" y2="23"/>
        <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
        <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
        <line x1="1" y1="12" x2="3" y2="12"/>
        <line x1="21" y1="12" x2="23" y2="12"/>
        <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
        <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
      </svg>
      <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
      </svg>
      <span class="color-dot" :style="{ background: primaryColor }"></span>
    </button>

    <!-- Dropdown Panel -->
    <transition name="panel-fade">
      <div v-if="visible" class="theme-dropdown">
        <!-- Preset Colors -->
        <div class="section">
          <div class="section-title">预设主题色</div>
          <div class="color-grid">
            <div
              v-for="color in presetColors"
              :key="color"
              class="color-swatch"
              :class="{ active: color === primaryColor }"
              :style="{ background: color }"
              @click="selectColor(color)"
            >
              <svg v-if="color === primaryColor" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="20 6 9 17 4 12"/>
              </svg>
            </div>
          </div>
        </div>

        <!-- Custom Color Picker -->
        <div class="section">
          <div class="section-title">自定义颜色</div>
          <div class="custom-color-row">
            <div class="color-picker-wrap">
              <input
                type="color"
                :value="primaryColor"
                @input="selectColor($event.target.value)"
                class="color-input-native"
              />
              <span class="color-preview-fake" :style="{ background: primaryColor }"></span>
            </div>
            <span class="color-hex">{{ primaryColor }}</span>
          </div>
        </div>

        <!-- Actions -->
        <div class="section-actions">
          <button class="reset-btn" @click="resetDefault">恢复默认</button>
          <button class="mode-btn" @click="toggleThemeMode">
            <svg v-if="!isDark" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
            </svg>
            <svg v-else width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="5"/>
              <line x1="12" y1="1" x2="12" y2="3"/>
              <line x1="12" y1="21" x2="12" y2="23"/>
              <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
              <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
              <line x1="1" y1="12" x2="3" y2="12"/>
              <line x1="21" y1="12" x2="23" y2="12"/>
              <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
              <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
            </svg>
            <span>{{ isDark ? '浅色模式' : '深色模式' }}</span>
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { applyPrimaryColor, toggleThemeWithAnimation, getThemePairedColor } from '@/utils/theme-color'

const DEFAULT_COLOR = '#007AFF'
const PRESET_COLORS = [
  '#007AFF',
  '#34C759',
  '#FF9500',
  '#FF3B30',
  '#AF52DE',
  '#5AC8FA',
  '#FF2D55',
  '#8E8E93'
]

export default {
  name: 'ThemeColorPanel',
  data() {
    return {
      visible: false,
      presetColors: PRESET_COLORS
    }
  },
  computed: {
    isDark() {
      return this.$store.state.settings.themeMode === 'dark'
    },
    primaryColor: {
      get() {
        return this.$store.state.settings.primaryColor || DEFAULT_COLOR
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'primaryColor',
          value: val
        })
      }
    }
  },
  watch: {
    visible(val) {
      if (val) {
        document.addEventListener('click', this.handleOutsideClick)
      } else {
        document.removeEventListener('click', this.handleOutsideClick)
      }
    }
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleOutsideClick)
  },
  methods: {
    togglePanel() {
      this.visible = !this.visible
    },
    handleOutsideClick(e) {
      const el = this.$refs.panel
      if (el && !el.contains(e.target)) {
        this.visible = false
      }
    },
    selectColor(color) {
      if (color === this.primaryColor) return
      this.primaryColor = color
      // 同步更新 Element UI 主题（触发 ThemePicker）
      this.$store.dispatch('settings/changeSetting', {
        key: 'theme',
        value: color
      })
      applyPrimaryColor(color)
      this.saveToLocal()
      this.$nextTick(() => {
        this.$emit('theme-change', color)
      })
    },
    resetDefault() {
      if (this.primaryColor === DEFAULT_COLOR) return
      this.selectColor(DEFAULT_COLOR)
    },
    toggleThemeMode() {
      if (this._togglingTheme) return
      this._togglingTheme = true
      const fromMode = this.isDark ? 'dark' : 'light'
      const newMode = this.isDark ? 'light' : 'dark'
      const currentColor = this.primaryColor
      const pairedColor = getThemePairedColor(currentColor, fromMode, newMode)
      const colorChanged = pairedColor !== currentColor

      const doSwitch = () => {
        // 直接设置 data-theme（同步、在 transition 捕获内完成）
        document.documentElement.setAttribute('data-theme', newMode)
        // 如果主色有配对色变化，同步切换
        if (colorChanged) {
          document.documentElement.style.setProperty('--primary-color', pairedColor)
          applyPrimaryColor(pairedColor)
        }
        // 更新 Vuex 状态
        this.$store.dispatch('settings/changeSetting', {
          key: 'themeMode',
          value: newMode
        })
        if (colorChanged) {
          this.$store.dispatch('settings/changeSetting', {
            key: 'primaryColor',
            value: pairedColor
          })
          this.$store.dispatch('settings/changeSetting', {
            key: 'theme',
            value: pairedColor
          })
        }
        // 持久化到 localStorage
        try {
          const setting = JSON.parse(localStorage.getItem('layout-setting')) || {}
          setting.themeMode = newMode
          if (colorChanged) setting.primaryColor = pairedColor
          localStorage.setItem('layout-setting', JSON.stringify(setting))
        } catch (e) { /* ignore */ }
        this._togglingTheme = false
      }

      toggleThemeWithAnimation(newMode, doSwitch)
    },
    saveToLocal() {
      try {
        const setting = JSON.parse(localStorage.getItem('layout-setting')) || {}
        setting.primaryColor = this.primaryColor
        localStorage.setItem('layout-setting', JSON.stringify(setting))
      } catch (e) {
        // ignore
      }
    }
  }
}
</script>

<style scoped lang="scss">
.theme-color-panel {
  position: relative;
}

.theme-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: none;
  background: transparent;
  color: var(--menu-text);
  cursor: pointer;
  transition: all 180ms var(--ease-apple);
  position: relative;

  &:hover {
    background: var(--menu-hover-bg);
    color: var(--menu-hover-text);
  }
}

.color-dot {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  border: 1.5px solid var(--bg-navbar);
  pointer-events: none;
}

.theme-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 240px;
  background: var(--contextmenu-bg);
  backdrop-filter: var(--blur-popup);
  -webkit-backdrop-filter: var(--blur-popup);
  border: 1px solid var(--adm-border);
  border-radius: 12px;
  box-shadow: var(--shadow-popup);
  padding: 10px 12px;
  z-index: 3000;
}

.section {
  margin-bottom: 10px;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--adm-text-tertiary);
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.color-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.color-swatch {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 150ms var(--ease-apple), box-shadow 150ms var(--ease-apple);
  position: relative;
  border: 2px solid transparent;

  &:hover {
    transform: scale(1.15);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  }

  &.active {
    border-color: var(--adm-text-primary);
    box-shadow: 0 0 0 1px var(--contextmenu-bg), 0 0 0 2px var(--adm-text-primary);
  }
}

.custom-color-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.color-picker-wrap {
  position: relative;
  width: 34px;
  height: 34px;
  overflow: hidden;
  border-radius: 8px;
  border: 1px solid var(--adm-border);
  flex-shrink: 0;
}

.color-input-native {
  position: absolute;
  top: -6px;
  left: -6px;
  width: calc(100% + 12px);
  height: calc(100% + 12px);
  border: none;
  padding: 0;
  cursor: pointer;
  background: none;
}

.color-hex {
  font-size: 13px;
  color: var(--adm-text-primary);
  font-family: 'SF Mono', 'Fira Code', monospace;
  font-weight: 500;
}

.section-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 8px;
  border-top: 1px solid var(--adm-border);
  gap: 8px;
}

.reset-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 30px;
  padding: 0 12px;
  border: 1px solid var(--adm-border);
  border-radius: 7px;
  background: transparent;
  color: var(--adm-text-secondary);
  font-size: 12px;
  cursor: pointer;
  transition: all 150ms;
  font-family: inherit;

  &:hover {
    background: var(--contextmenu-hover);
    color: var(--adm-text-primary);
    border-color: var(--adm-text-tertiary);
  }
}

.mode-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 30px;
  padding: 0 10px;
  border: none;
  border-radius: 7px;
  background: var(--contextmenu-hover);
  color: var(--adm-text-secondary);
  font-size: 12px;
  cursor: pointer;
  transition: all 150ms;
  font-family: inherit;

  &:hover {
    background: var(--menu-hover-bg);
    color: var(--adm-text-primary);
  }
}

.panel-fade-enter-active,
.panel-fade-leave-active {
  transition: opacity 150ms ease, transform 150ms ease;
}
.panel-fade-enter,
.panel-fade-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.96);
  transform-origin: top right;
}
</style>
