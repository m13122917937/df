<!-- 价格芯片组件 -->
<template>
  <div class="price-chips" v-if="haveData">
    <div
      v-for="opt in processedOptions" 
      :key="opt.key" 
      :class="['price-chip', { 'is-highlighted': opt.highlight, 'is-striked': opt.striked }]"
    >
      <span class="price-text">
        {{ currencySymbol }}{{ formatNumber(opt.value) }}
      </span>
    </div>
  </div>
  <div v-else>-</div>
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
    },
    haveData() {
      for(let i of this.processedOptions) {
        const n = Number(i.value)
        if (isFinite(n)) return true
      } 
      return false
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
  gap: 6px;
  flex-wrap: wrap;
  // align-items: center;
  // justify-content: center;
}

.price-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 50px;
  height: 32px;
  background-color: #ddd;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  transition: all 0.2s ease;
  cursor: pointer;
  padding: 0 2px;
  
  &:hover {
    background-color: #e8e8e8;
    border-color: #d0d0d0;
  }
  
  &.is-highlighted {
    background-color: #ffebee;
    border-color: #ffcdd2;
    color: #d32f2f;
    
    .price-text {
      color: #d32f2f;
      font-weight: 600;
    }
  }
  
  &.is-striked {
    .price-text {
      text-decoration: line-through;
      // opacity: 0.6;
    }
  }
}

.price-text {
  font-size: 12px;
  color: #333;
  font-weight: 500;
  white-space: nowrap;
}
</style>
