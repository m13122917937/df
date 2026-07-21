<template>
  <div v-if="remainingTime > 0" class="countdown-text">
    <span class="countdown-label">剩余</span>
    <span class="countdown-number">{{ minutes }}</span>
    <span class="countdown-label">分</span>
    <span class="countdown-number">{{ seconds }}</span>
    <span class="countdown-label">秒</span>
  </div>
</template>

<script>
// 全局倒计时管理器
class CountdownManager {
  constructor() {
    this.timer = null
    this.components = new Set()
    this.isRunning = false
  }

  // 添加组件到管理器
  addComponent(component) {
    this.components.add(component)
    if (!this.isRunning) {
      this.start()
    }
  }

  // 移除组件
  removeComponent(component) {
    this.components.delete(component)
    if (this.components.size === 0) {
      this.stop()
    }
  }

  // 开始全局定时器
  start() {
    if (this.isRunning) return

    this.isRunning = true
    this.timer = setInterval(() => {
      this.updateAllComponents()
    }, 1000)
  }

  // 停止全局定时器
  stop() {
    if (this.timer) {
      clearInterval(this.timer)
      this.timer = null
    }
    this.isRunning = false
  }

  // 更新所有组件
  updateAllComponents() {
    const componentsToRemove = []

    this.components.forEach(component => {
      if (component.calculateRemainingTime) {
        component.calculateRemainingTime()
        if (component.remainingTime <= 0) {
          componentsToRemove.push(component)
        }
      }
    })

    // 移除已结束的组件
    componentsToRemove.forEach(component => {
      this.removeComponent(component)
      if (component.onCountdownEnd) {
        component.onCountdownEnd()
      }
    })
  }
}

// 创建全局实例
const countdownManager = new CountdownManager()

export default {
  name: 'OptimizedCountdownText',
  props: {
    data: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      remainingTime: 0
    }
  },
  computed: {
    minutes() {
      return Math.floor(this.remainingTime / 60)
    },
    seconds() {
      return this.remainingTime % 60
    }
  },
  mounted() {
    this.calculateRemainingTime()
    if (this.remainingTime > 0) {
      countdownManager.addComponent(this)
    }
  },
  beforeDestroy() {
    countdownManager.removeComponent(this)
  },
  methods: {
    // 计算剩余时间
    calculateRemainingTime() {
      if (!this.data.lastCompeteTime) {
        this.remainingTime = 0
        return
      }

      const now = new Date()
      const lastTime = new Date(this.data.lastCompeteTime)
      const diffMs = now.getTime() - lastTime.getTime()
      const quotationInterval = this.data.quotationInterval

      let size = 0
      if (this.data.priceHighestStatus === 1) {
        size = 3
      } else if (this.data.priceHignStatus === 1) {
        size = 2
      } else if (this.data.priceLowStatus === 1) {
        size = 1
      } else if (this.data.priceLowestStatus === 1) {
        size = 0
      }

      const quotationTime = 1000 * 60 * quotationInterval * size
      const remainingMs = quotationTime - diffMs

      if (remainingMs <= 0) {
        this.remainingTime = 0
      } else {
        this.remainingTime = Math.ceil(remainingMs / 1000)
      }
    },

    // 倒计时结束回调
    onCountdownEnd() {
      this.$emit('countdown-end')
    }
  }
}
</script>

<style scoped>
.countdown-text {
  display: inline-flex;
  align-items: center;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  font-size: 14px;
  color: #ffc107;
  background: #fff3cd;
  padding: 0 5px;
}

.countdown-label {
  color: #999;
  margin: 0 1px;
}

.countdown-number {
  color: #ff4757;
  font-weight: 500;
  margin: 0 1px;
}
</style>
