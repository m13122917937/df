import Vue from 'vue'
import { fetchGlobalNotifications } from '@/api/globalNotification'

// 去重持久化：使用 localStorage 持久化已展示通知 ID，刷新后也不会重复弹出
const STORAGE_KEY = 'global_notification_shown_ids_v1'
const loadShownIds = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return new Set()
    const arr = JSON.parse(raw)
    if (!Array.isArray(arr)) return new Set()
    return new Set(arr)
  } catch (e) {
    // eslint-disable-next-line no-console
    console.warn('loadShownIds parse error', e)
    return new Set()
  }
}
const saveShownIds = (set) => {
  try {
    const arr = Array.from(set)
    localStorage.setItem(STORAGE_KEY, JSON.stringify(arr))
  } catch (e) {
    // eslint-disable-next-line no-console
    console.warn('saveShownIds error', e)
  }
}

const shownIds = loadShownIds()
// 如果存在正在展示的 pending mapping 通知实例，保留引用以便更新
let pendingMappingNotificationInstance = null
// 防抖：避免短时间内频繁调用后端检查
const MIN_CHECK_INTERVAL_MS = 1000
let lastCheckTs = 0

async function checkOnce(params = {}) {
  try {
    const res = await fetchGlobalNotifications(params)
    if (!res || res.code !== 200) return

    const payload = res.data
    if (payload === null || payload === undefined) return

    // 后端有时直接返回数字 N（表示待审核数量），仅当 N > 0 时显示提示
    if (typeof payload === 'number') {
      const n = payload
      if (n > 0) {
        const key = `pending_mapping_count`
        const message = `您有${n}条待审核商品映射。`
        if (Vue && Vue.prototype && Vue.prototype.$notifyCustom) {
          const api = Vue.prototype.$notifyCustom
          // 如果已持久化且当前没有正在展示的实例，说明用户之前已关闭过，不再重复弹出
          if (shownIds.has(key) && !pendingMappingNotificationInstance) {
            return
          }
          // 若已有正在展示的实例，先关闭以便更新内容
          if (pendingMappingNotificationInstance && typeof pendingMappingNotificationInstance.close === 'function') {
            try {
              pendingMappingNotificationInstance.close()
            } catch (e) {
              // ignore
            }
            pendingMappingNotificationInstance = null
          }
          // 标记为已展示（持久化）
          if (!shownIds.has(key)) {
            shownIds.add(key)
            saveShownIds(shownIds)
          }
          // 显示并保存实例引用；duration=0 表示不自动关闭
          pendingMappingNotificationInstance = api.notify({
            title: '',
            message,
            type: 'info',
            duration: 0,
            onClose() {
              try {
                shownIds.delete(key)
                saveShownIds(shownIds)
              } catch (e) {
                // ignore
              }
              pendingMappingNotificationInstance = null
            }
          })
        } else {
          // fallback: persist if notify unavailable
          if (!shownIds.has(key)) {
            shownIds.add(key)
            saveShownIds(shownIds)
          }
        }
      }
      return
    }

    const list = Array.isArray(payload) ? payload : [payload]

    list.forEach(item => {
      // 预期 item = { id, type, title, message, duration }
      if (!item || !item.message) return
      if (item.id && shownIds.has(item.id)) return

      const type = item.type || 'info'
      const title = item.title || ''
      const message = item.message || ''
      const duration = typeof item.duration === 'number' ? item.duration : undefined

      // 调用全局通知（使用 notify 支持 onClose 与 duration）
      if (Vue && Vue.prototype && Vue.prototype.$notifyCustom) {
        const api = Vue.prototype.$notifyCustom
        const opts = {
          title,
          message,
          type,
          duration: duration !== undefined ? duration : 0
        }
        if (item.id) {
          // persist then show; onClose will remove persistence
          shownIds.add(item.id)
          saveShownIds(shownIds)
          opts.onClose = () => {
            try {
              shownIds.delete(item.id)
              saveShownIds(shownIds)
            } catch (e) {
              // ignore
            }
          }
        }
        api.notify(opts)
      } else {
        if (item.id) {
          shownIds.add(item.id)
          saveShownIds(shownIds)
        }
      }
    })
  } catch (err) {
    // 静默失败，不阻塞页面
    // eslint-disable-next-line no-console
    console.warn('globalNotificationChecker checkOnce error', err)
  }
}

export function initGlobalNotificationChecker({ router, intervalMs = 0, params = {} } = {}) {
  // 页面首次加载时检查一次（不受防抖限制）
  lastCheckTs = Date.now()
  checkOnce(params)

  // 每次路由切换后再次触发（如果需要）
  if (router && typeof router.afterEach === 'function') {
    router.afterEach(() => {
      const now = Date.now()
      if (now - lastCheckTs >= MIN_CHECK_INTERVAL_MS) {
        lastCheckTs = now
        checkOnce(params)
      } else {
        // skip due to debounce
      }
    })
  }

  // 可选轮询
  let timer = null
  if (intervalMs && intervalMs > 0) {
    timer = setInterval(() => {
      const now = Date.now()
      if (now - lastCheckTs >= MIN_CHECK_INTERVAL_MS) {
        lastCheckTs = now
        checkOnce(params)
      }
    }, intervalMs)
  }

  return {
    stop() {
      if (timer) clearInterval(timer)
    },
    checkNow: (forceParams = params) => {
      lastCheckTs = Date.now()
      return checkOnce(forceParams)
    }
  }
}

export default {
  initGlobalNotificationChecker
}

// 清除已展示通知（用于用户登出时清理，防止不同用户冲突）
export function clearShownNotifications() {
  try {
    localStorage.removeItem(STORAGE_KEY)
  } catch (e) {
    // eslint-disable-next-line no-console
    console.warn('clearShownNotifications error', e)
  }
  // 清空内存集合
  try {
    shownIds.clear()
  } catch (e) {
    // ignore
  }
}


