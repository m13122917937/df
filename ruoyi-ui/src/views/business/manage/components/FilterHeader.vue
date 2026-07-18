<template>
  <el-popover
    placement="bottom-start"
    width="240"
    trigger="click"
    popper-class="table-filter-popper"
    :visible-arrow="false"
    @show="onShow"
  >
    <div class="filter-popover-body">
      <el-input
        v-model="searchText"
        placeholder="搜索选项"
        clearable
        size="mini"
        prefix-icon="el-icon-search"
      />
      <div class="filter-checkbox-list">
        <el-checkbox-group v-model="selectedValues" @change="onChange">
          <div
            v-for="opt in filteredOptions"
            :key="opt.value"
            class="filter-checkbox-item"
          >
            <el-checkbox :label="opt.value">{{ opt.text }}</el-checkbox>
          </div>
        </el-checkbox-group>
        <div v-if="filteredOptions.length === 0" class="filter-no-match">
          {{ searchText ? '无匹配选项' : '暂无数据' }}
        </div>
      </div>
      <div class="filter-popover-footer">
        <el-button size="mini" :disabled="!selectedValues.length" @click="clearAll">重置</el-button>
      </div>
    </div>
    <div slot="reference" class="filter-header-trigger">
      <span class="filter-header-label">{{ label }}</span>
      <svg class="filter-icon" :class="{ active: hasValue }" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 3H2l8 9.46V19l4 2v-8.54L22 3z"/></svg>
    </div>
  </el-popover>
</template>

<script>
export default {
  name: 'FilterHeader',
  props: {
    label: { type: String, required: true },
    value: { type: Array, default: () => [] },
    options: { type: Array, default: () => [] },
  },
  data() {
    return { searchText: '', selectedValues: [...this.value] }
  },
  computed: {
    hasValue() {
      return this.selectedValues.length > 0
    },
    filteredOptions() {
      if (!this.searchText) return this.options
      const q = this.searchText.toLowerCase()
      return this.options.filter(o => String(o.text).toLowerCase().includes(q))
    },
  },
  watch: {
    value(val) { this.selectedValues = [...val] },
  },
  methods: {
    onShow() {
      this.searchText = ''
      this.selectedValues = [...this.value]
      this.$nextTick(() => {
        const input = this.$el.querySelector('.el-input__inner')
        if (input) input.focus()
      })
    },
    onChange() {
      this.$emit('update:value', [...this.selectedValues])
    },
    clearAll() {
      this.selectedValues = []
      this.onChange()
    },
  },
}
</script>

<style scoped>
.filter-header-trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  cursor: pointer;
  user-select: none;
}
.filter-header-label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  min-width: 0;
  line-height: 1;
}
.filter-icon {
  flex-shrink: 0;
  margin-left: 4px;
  width: 13px;
  height: 13px;
  color: #8c8c8c;
  transition: color 150ms ease;
}
.filter-icon.active {
  color: var(--primary-color, #1677FF);
}
.filter-popover-body { padding: 4px; }
.filter-checkbox-list { max-height: 240px; overflow-y: auto; margin-top: 8px; }
.filter-checkbox-item { padding: 4px 0; }
.filter-no-match { text-align: center; color: #999; padding: 16px 0; font-size: 12px; }
.filter-popover-footer { border-top: 1px solid #eee; padding-top: 8px; margin-top: 4px; display: flex; justify-content: flex-end; }
</style>
