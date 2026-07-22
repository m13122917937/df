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
        { key: 'wait-send', title: '待发货' },
        { key: 'today-send', title: '当日发货' },
        { key: 'transit', title: '在途' },
        { key: 'exception', title: '异常订单' },
        { key: 'return', title: '退货追单' },
        { key: 'confirm', title: '确认收货' },
        { key: 'sale-after', title: '售后' },
        { key: 'all-orders', title: '全部订单' }
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
    border-bottom: 1px solid #E5E6EB;
    padding: 0 24px;
    display: flex;
    align-items: center;
    height: 48px;

    .stats-items {
      display: flex;
      height: 100%;

      .stats-item {
        position: relative;
        cursor: pointer;
        padding: 0 20px;
        display: flex;
        align-items: center;
        height: 100%;
        color: #4E5969;
        font-size: 16px;
        transition: color 0.2s ease, background 0.2s ease;

        &:hover {
          background: rgba(22, 119, 255, 0.06);
        }

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 20px;
          right: 20px;
          height: 2px;
          background: #1677FF;
          border-radius: 1px 1px 0 0;
          transform: scaleX(0);
          transition: transform 0.2s ease;
        }

        &.active {
          color: #1677FF;

          .stats-title {
            color: #1677FF;
          }

          &::after {
            transform: scaleX(1);
          }
        }

        .stats-title {
          font-size: 16px;
          color: inherit;
          white-space: nowrap;
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
