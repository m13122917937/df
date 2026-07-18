<!-- 价格芯片组件 -->
<template>
  <div class="price-chips">
    <el-tag
      v-for="opt in processedOptions"
      :key="opt.key"
      :type="opt.highlight ? 'danger' : 'info'"
      effect="dark"
      size="small"
    >
      <span :class="{ 'is-striked': opt.striked }">
        {{ currencySymbol }}{{ formatNumber(opt.value) }}
      </span>
    </el-tag>
  </div>
</template>

<script>
export default {
  name: 'PriceChips',
  props: {
    // 数据行对象
    row: {
      type: Object,
      required: true
    },
    // 成交价格字段名
    tradePriceField: {
      type: String,
      default: 'tradePrice'
    },
    // 价格字段映射配置
    priceFields: {
      type: Array,
      default: () => [
        { key: 'priceHighest', value: 'priceHighest' },
        { key: 'priceHign', value: 'priceHign' },
        { key: 'priceLow', value: 'priceLow' },
        { key: 'priceLowest', value: 'priceLowest' }
      ]
    },
    // 货币符号
    currencySymbol: {
      type: String,
      default: '￥'
    },
    // 是否显示删除线（当价格低于成交价时）
    showStriked: {
      type: Boolean,
      default: true
    },
    // 是否高亮成交价
    highlightTradePrice: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    // 处理后的选项数据
    processedOptions() {
      const mapping = this.priceFields.map(field => ({
        key: field.key,
        value: this.row[field.value]
      }))

      const tradePrice = Number(this.row[this.tradePriceField])
      const idx = mapping.findIndex(m => Number(m.value) === tradePrice)

      return mapping.map((m, i) => ({
        ...m,
        highlight: this.highlightTradePrice && i === idx,
        striked: this.showStriked && idx !== -1 && i < idx
      }))
    }
  },
  methods: {
    // 格式化数字
    formatNumber(val) {
      const n = Number(val)
      if (!isFinite(n)) return '-'
      return n.toLocaleString('zh-CN', { maximumFractionDigits: 0 })
    }
  }
}
</script>

<style scoped lang="scss">
.price-chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.is-striked {
  text-decoration: line-through;
  opacity: 0.7;
}
</style>
