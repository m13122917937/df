/**
 * 主题色工具 - 从基础色计算各状态色值
 */

export function hexToRgb(hex) {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
  return result
    ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
      }
    : null
}

export function rgbToHex(r, g, b) {
  return (
    '#' +
    [r, g, b]
      .map((x) => {
        const hex = Math.max(0, Math.min(255, Math.round(x))).toString(16)
        return hex.length === 1 ? '0' + hex : hex
      })
      .join('')
  )
}

export function blendWithWhite(hex, percent) {
  const rgb = hexToRgb(hex)
  if (!rgb) return hex
  return rgbToHex(
    rgb.r + (255 - rgb.r) * percent,
    rgb.g + (255 - rgb.g) * percent,
    rgb.b + (255 - rgb.b) * percent
  )
}

export function darken(hex, percent) {
  const rgb = hexToRgb(hex)
  if (!rgb) return hex
  return rgbToHex(rgb.r * (1 - percent), rgb.g * (1 - percent), rgb.b * (1 - percent))
}

/**
 * 根据主色生成 CSS 变量值
 */
export function getPrimaryVariants(hex) {
  const color = hex.startsWith('#') ? hex : '#' + hex
  const rgb = hexToRgb(color)
  const rgbStr = rgb ? `${rgb.r}, ${rgb.g}, ${rgb.b}` : '22, 119, 255'
  return {
    '--primary-color': color,
    '--primary-rgb': rgbStr,
    '--primary-hover': blendWithWhite(color, 0.15),
    '--primary-active': darken(color, 0.12),
    '--primary-light': blendWithWhite(color, 0.88),
    '--primary-light-bg': blendWithWhite(color, 0.78),
    '--primary-border': blendWithWhite(color, 0.58)
  }
}

/**
 * 将 CSS 变量对象应用到 documentElement
 */
/**
 * 浅色 ↔ 深色模式默认主色配对表
 * 深色模式下使用更亮、轻微降低饱和度的色值，减少视觉刺激
 */
const THEME_COLOR_PAIRS = {
  light: {
    '#007AFF': '#0A84FF', // 蓝 → 暗蓝
    '#34C759': '#30D158', // 绿 → 暗绿
    '#FF9500': '#FF9F0A', // 橙 → 暗橙
    '#FF3B30': '#FF453A', // 红 → 暗红
    '#AF52DE': '#BF5AF2', // 紫 → 暗紫
    '#5AC8FA': '#64D2FF', // 青 → 暗青
    '#FF2D55': '#FF375F', // 粉 → 暗粉
    '#8E8E93': '#98989D', // 灰 → 暗灰
  },
  dark: {
    '#0A84FF': '#007AFF',
    '#30D158': '#34C759',
    '#FF9F0A': '#FF9500',
    '#FF453A': '#FF3B30',
    '#BF5AF2': '#AF52DE',
    '#64D2FF': '#5AC8FA',
    '#FF375F': '#FF2D55',
    '#98989D': '#8E8E93',
  }
}

/**
 * 获取主题模式对应的配对色
 * @param {string} currentColor - 当前主色
 * @param {'light'|'dark'} fromMode - 当前模式
 * @param {'light'|'dark'} toMode - 目标模式
 * @returns {string} 配对色
 */
export function getThemePairedColor(currentColor, fromMode, toMode) {
  const color = currentColor && currentColor.startsWith('#') ? currentColor.toUpperCase() : '#007AFF'
  const upperColor = color.toUpperCase()
  // 1. 精确匹配预设表
  const pair = THEME_COLOR_PAIRS[fromMode]?.[upperColor]
  if (pair) return pair

  // 2. 自定义色 — 自动按比例调整亮度
  if (toMode === 'dark') {
    // 浅→深：提亮 25%，使其在暗色背景上舒适
    return blendWithWhite(color, 0.25)
  } else {
    // 深→浅：加深 12%，还原正常色感
    return darken(color, 0.12)
  }
}

export function applyPrimaryColor(hex) {
  const variants = getPrimaryVariants(hex)
  const root = document.documentElement
  Object.entries(variants).forEach(([key, value]) => {
    root.style.setProperty(key, value)
  })
}

/**
 * 浅色/深色模式切换，带平滑圆形揭幕动画
 * @param {'light'|'dark'} newMode - 目标模式
 * @param {Function} doSwitch - 执行实际切换的回调 (同步)
 */
export function toggleThemeWithAnimation(newMode, doSwitch) {
  // 1. 用户开启「减少动态效果」— 直接切换，无动画
  if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
    doSwitch()
    return
  }

  const root = document.documentElement

  // 2. View Transitions API — 圆形扩散揭幕动画
  if (document.startViewTransition) {
    const transition = document.startViewTransition(doSwitch)
    // 等待浏览器捕获新旧状态
    transition.ready.then(() => {
      try {
        const isDark = newMode === 'dark'
        // 计算按钮在视口中的位置作为动画圆心
        const trigger = document.querySelector('.theme-trigger')
        const rect = trigger ? trigger.getBoundingClientRect() : null
        const x = rect ? rect.left + rect.width / 2 : window.innerWidth / 2
        const y = rect ? rect.top + rect.height / 2 : window.innerHeight / 2
        const maxRadius = Math.hypot(
          Math.max(x, window.innerWidth - x),
          Math.max(y, window.innerHeight - y)
        )
        // 使用 WAAPI 控制伪元素的 clip-path 实现圆形扩散
        root.animate(
          {
            clipPath: [
              `circle(${isDark ? 0 : maxRadius}px at ${x}px ${y}px)`,
              `circle(${isDark ? maxRadius : 0}px at ${x}px ${y}px)`
            ]
          },
          {
            duration: 500,
            easing: 'cubic-bezier(0.4, 0, 0.2, 1)',
            pseudoElement: isDark
              ? '::view-transition-new(root)'
              : '::view-transition-old(root)'
          }
        )
      } catch (e) {
        // WAAPI pseudoElement 不受支持时静默降级为默认 crossfade
      }
    })
    return
  }

  // 3. CSS 过渡降级 — 仅在 themed 属性上增加过渡
  root.classList.add('theme-transitioning')
  doSwitch()
  setTimeout(() => {
    root.classList.remove('theme-transitioning')
  }, 500)
}
