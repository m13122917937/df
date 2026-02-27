<template>
  <div v-if="remainingTime > 0" class="countdown-text" :class="countdownClass">
    <span class="countdown-label">剩余</span>
    <span class="countdown-number">{{ formattedTime }}</span>
    <span class="countdown-label">秒</span>
  </div>
  <div v-else-if="remainingTime === 0" class="countdown-expired">
    已到期
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
    // 格式化显示时间
    formattedTime() {
      const minutes = Math.floor(this.remainingTime / 60);
      const seconds = this.remainingTime % 60;

      if (minutes > 0) {
        return `${minutes}:${seconds.toString().padStart(2, '0')}`;
      }
      return seconds.toString();
    },

    // 根据剩余时间动态设置样式类
    countdownClass() {
      if (this.remainingTime <= 60) {
        return 'countdown-urgent';
      } else if (this.remainingTime <= 300) {
        return 'countdown-warning';
      }
      return 'countdown-normal';
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
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.countdown-normal {
  color: #409eff;
  background: #ecf5ff;
  border: 1px solid #b3d8ff;
}

.countdown-warning {
  color: #e6a23c;
  background: #fdf6ec;
  border: 1px solid #f5dab1;
}

.countdown-urgent {
  color: #f56c6c;
  background: #fef0f0;
  border: 1px solid #fbc4c4;
  animation: pulse 1s infinite;
}

.countdown-expired {
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  color: #909399;
  background: #f4f4f5;
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid #d3d4d6;
}

.countdown-label {
  color: inherit;
  margin: 0 2px;
  font-size: 11px;
}

.countdown-number {
  color: inherit;
  font-weight: 600;
  margin: 0 2px;
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
  100% {
    opacity: 1;
  }
}
</style>
