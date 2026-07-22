<template>
  <div class="stats-tabs">
    <div class="stats-items">
      <div
        v-for="item in statsItems"
        :key="item.key"
        :class="['stats-item', { active: activeTab === item.key }]"
        @click="selectTab(item.key)"
      >
        <div class="stats-title">{{ item.title }}</div>
        <div v-if="!item.hideCount" class="stats-count">{{ item.count }}</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StatsTabs',
  props: {
    value: {
      type: String,
      default: 'wait-send'
    },
    statsItems: {
      type: Array,
      default: () => [
        { key: 'wait-send', title: '待发货', count: 0 },
        { key: 'today-send', title: '当日发货', count: 28 },
        { key: 'transit', title: '在途', count: 28 },
        { key: 'exception', title: '异常订单', count: 0 },
        { key: 'return', title: '退货追单', count: 0 },
        { key: 'confirm', title: '确认收货', count: 54 },
        { key: 'sale-after', title: '售后', count: 2 },
        { key: 'all-orders', title: '全部订单', count: 0 }
      ]
    }
  },
  data() {
    return {
    }
  },
  computed: {
    activeTab: {
      get() {
        return this.value
      },
      set(val) {
        this.$emit('input', val)
      }
    }
  },
  methods: {
    selectTab(key) {
      this.activeTab = key
      this.$emit('tab-change', key)
    }
  }
}
</script>

<style lang="scss" scoped>
.stats-tabs {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 4px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .stats-items {
    display: flex;
    gap: 20px;
    flex-wrap: wrap;

    .stats-item {
      cursor: pointer;
      text-align: center;
      padding: 10px 15px;
      border-radius: 4px;
      min-width: 80px;
      border-bottom: 6px solid transparent;

      &:hover {
        background: #f5f5f5;
      }

      &.active {
        background: #e3f2fd;
        color: #1677FF;
        border-bottom: 6px solid #1677FF;

        .stats-title {
          color: #1677FF;
          font-weight: bold;
        }

        .stats-count {
          color: #1677FF;
          font-weight: bold;
        }
      }

      .stats-title {
        font-size: 14px;
        color: #333;
        margin-bottom: 5px;
      }

      .stats-count {
        font-size: 18px;
        font-weight: bold;
        color: #666;
      }
    }
  }
}
</style>
