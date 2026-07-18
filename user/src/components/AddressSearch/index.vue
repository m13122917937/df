<template>
  <div class="address-search">
    <el-row :gutter="20">
      <el-col :span="span">
        <el-input
          v-model.trim="addressValue"
          placeholder="收货地址"
          size="small"
          clearable
          @input="handleInput"
          @clear="handleClear"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'AddressSearch',
  props: {
    value: {
      type: String,
      default: ''
    },
    span: {
      type: Number,
      default: 4
    }
  },
  data() {
    return {
      addressValue: this.value
    }
  },
  watch: {
    value(newVal) {
      this.addressValue = newVal
    },
    addressValue(newVal) {
      this.$emit('input', newVal)
    }
  },
  methods: {
    handleInput(value) {
      this.$emit('input', value)
      this.$emit('change', value)
    },
    handleClear() {
      this.$emit('input', '')
      this.$emit('change', '')
      this.$emit('clear')
    }
  }
}
</script>

<style lang="scss" scoped>
.address-search {
  // 可以添加额外的样式如果需要
}
</style>
