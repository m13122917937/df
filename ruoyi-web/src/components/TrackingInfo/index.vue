<template>
  <div class="tracking-info" :class="{ 'tracking-info-clickable': clickable }" @click="handleClick">
    <span class="company">{{ company || '-' }}</span>
    <span class="divider">｜</span>
    <span class="number">{{ number || '-' }}</span>
  </div>
</template>

<script>
export default {
  name: 'TrackingInfo',
  props: {
    company: {
      type: String,
      default: ''
    },
    number: {
      type: String,
      default: ''
    },
    clickable: {
      type: Boolean,
      default: true
    },
    // 用于传递完整的数据对象，方便父组件使用
    data: {
      type: Object,
      default: null
    }
  },
  methods: {
    handleClick() {
      if (this.clickable) {
        this.$emit('click', this.data)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.tracking-info {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #1f2d3d;

  &.tracking-info-clickable {
    cursor: pointer;
  }

  .company {
    font-weight: 600;
    color: #303133;
  }

  .divider {
    color: #dcdfe6;
  }

  .number {
    font-family: SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
    color: #1677FF;
  }

  &:hover.tracking-info-clickable {
    transform: translateY(-1px);
    transition: all 0.2s ease;
  }
}
</style>
