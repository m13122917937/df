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
export default {
  name: 'GrabCountdownText',
  props: {
    timedata: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      remainingTime: 0,
      timer: null
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
    this.startCountdown()
  },
  beforeDestroy() {
    this.stopCountdown()
  },
  methods: {
    // 开始倒计时
    startCountdown() {
      this.calculateRemainingTime()
      if (this.remainingTime > 0) {
        this.timer = setInterval(() => {
          this.calculateRemainingTime()
          if (this.remainingTime <= 0) {
            this.stopCountdown()
            this.$emit('countdown-end')
          }
        }, 1000)
      }
    },

    // 停止倒计时
    stopCountdown() {
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
    },

    // 计算剩余时间
    calculateRemainingTime() {
      if (!this.timedata.lastCompeteTime) {
        this.remainingTime = 0
        return
      }

      const now = new Date()
      const lastTime = new Date(this.timedata.lastCompeteTime)
      const diffMs = now.getTime() - lastTime.getTime()
      const quotationInterval = this.timedata.quotationInterval

      let size = 0
      if (this.timedata.priceHighestStatus === 1) {
        size = 3
      } else if (this.timedata.priceHignStatus === 1) {
        size = 2
      } else if (this.timedata.priceLowStatus === 1) {
        size = 1
      } else if (this.timedata.priceLowestStatus === 1) {
        size = 0
      }

      const quotationTime = 1000 * 60 * quotationInterval * size
      const remainingMs = quotationTime - diffMs

      if (remainingMs <= 0) {
        this.remainingTime = 0
      } else {
        this.remainingTime = Math.ceil(remainingMs / 1000)
      }
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
