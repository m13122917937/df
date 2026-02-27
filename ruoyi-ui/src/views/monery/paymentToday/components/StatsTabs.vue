<template>
  <div class="stats-tabs">
    <div class="stats-items">
      <div
        v-for="item in statsItems"
        :key="item.key"
        :class="['stats-item', { active: activeTab === item.key }]"
        @click="selectTab(item.key)"
      >
        <div class="status-content">
          <div class="status-icon" v-if="item.icon">
            <i :class="item.icon"></i>
          </div>
          <span class="status-label">{{ item.title }}</span>
        </div>
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
      default: 'pay-delivery'
    },
    statsItems: {
      type: Array,
      default: () => [
        { key: 'pay-delivery', title: '代发' },
        { key: 'pay-putin', title: '入仓' },
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
    background: rgba(255,255,255,0.95);
    border-radius: 10px;
    padding: 8px 12px;
    display: flex;
    align-items: center;
    box-shadow: 0 4px 12px rgba(0,0,0,0.06);
    border: 1px solid rgba(255,255,255,0.2);

    .stats-items {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
      width: 100%;

      .stats-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 8px 12px;
        cursor: pointer;
        border-radius: 8px;
        min-width: 100px;
        transition: all 0.25s ease;
        position: relative;

        &:hover {
          background: linear-gradient(135deg, #f0f9ff 0%, #e8f5ff 100%);
          transform: translateY(-2px);
        }

        &.active {
          background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
          color: #fff;
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(64,158,255,0.2);
        }

        .status-content {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 4px;
        }

        .status-icon {
          font-size: 16px;
          opacity: 0.9;
          display: flex;
          align-items: center;
        }

        .status-label {
          font-size: 13px;
          font-weight: 600;
          white-space: nowrap;
        }

        .status-count {
          font-size: 16px;
          font-weight: 700;
          color: inherit;
        }
      }
    }
  }
  </style>
