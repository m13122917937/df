<!-- table表格header筛选弹窗 -->
<template>
  <el-popover
    v-model="visible"
    placement="bottom-start"
    width="300"
    trigger="click"
  >
    <div class="filter-panel" role="dialog" :aria-label="ariaLabel">
      <!-- 搜索框 -->
      <el-input
        v-model.trim="searchKeyword"
        placeholder="搜索"
        size="small"
        style="margin-bottom: 10px;"
        @input="handleSearchInput"
      >
        <i slot="prefix" class="el-input__icon el-icon-search" />
      </el-input>

      <!-- 筛选选项 -->
      <div class="filter-options">
        <!-- 全选复选框（可选） -->
        <el-checkbox
          v-if="showSelectAll"
          v-model="checkAll"
          :indeterminate="isIndeterminate"
          @change="handleCheckAllChange"
        >
          全选
        </el-checkbox>
        <div v-if="showSelectAll" style="margin: 15px 0;" />

        <!-- 选项列表 -->
        <el-checkbox-group v-model="selectedValues" @change="handleCheckedChange">
          <el-checkbox
            v-for="option in filteredOptions"
            :key="option.value"
            :label="option.value"
            style="display: block; margin-top: 8px;"
          >
            {{ option.label }}
          </el-checkbox>
        </el-checkbox-group>
      </div>

      <!-- 操作按钮 -->
      <div class="filter-actions">
        <el-button size="small" @click="handleCancel">取消</el-button>
        <el-button type="primary" size="small" @click="handleConfirm">确定</el-button>
      </div>
    </div>

    <span slot="reference" class="filter-header-btn">
      <span class="filter-text">{{ title }}</span>
      <i class="el-icon-s-operation filter-icon" />
    </span>
  </el-popover>
</template>

<script>
export default {
  name: 'FilterPopover',
  props: {
    // 弹框标题
    title: {
      type: String,
      required: true
    },
    // 选项数据
    options: {
      type: Array,
      required: true,
      default: () => []
    },
    // 选中的值
    value: {
      type: Array,
      default: () => []
    },
    // 是否显示全选
    showSelectAll: {
      type: Boolean,
      default: true
    },
    // 无障碍标签
    ariaLabel: {
      type: String,
      default: '筛选面板'
    }
  },
  data() {
    return {
      visible: false,
      searchKeyword: '',
      selectedValues: [...this.value],
      checkAll: false,
      isIndeterminate: false
    }
  },
  computed: {
    filteredOptions() {
      if (!this.searchKeyword) {
        return this.options
      }
      return this.options.filter(option =>
        option.label.toLowerCase().includes(this.searchKeyword.toLowerCase())
      )
    }
  },
  watch: {
    value: {
      handler(newVal) {
        this.selectedValues = [...newVal]
        this.updateCheckAllState()
      },
      immediate: true
    },
    selectedValues: {
      handler(newVal) {
        this.updateCheckAllState()
      },
      deep: true
    }
  },
  methods: {
    handleSearchInput() {
      // 搜索时更新全选状态
      this.updateCheckAllState()
    },
    handleCheckAllChange(val) {
      this.$set(this, 'selectedValues', val ? this.filteredOptions.map(option => option.value) : [])
      this.$set(this, 'isIndeterminate', false)
    },
    handleCheckedChange(value) {
      const checkedCount = value.length
      this.$set(this, 'checkAll', checkedCount === this.filteredOptions.length)
      this.$set(this, 'isIndeterminate', checkedCount > 0 && checkedCount < this.filteredOptions.length)
    },
    updateCheckAllState() {
      if (!this.showSelectAll) return

      const checkedCount = this.selectedValues.length
      this.$set(this, 'checkAll', checkedCount === this.filteredOptions.length)
      this.$set(this, 'isIndeterminate', checkedCount > 0 && checkedCount < this.filteredOptions.length)
    },
    handleCancel() {
      // 重置筛选选项
      this.searchKeyword = ''
      this.selectedValues = [...this.value]
      this.updateCheckAllState()
      // 关闭popover
      this.visible = false
      this.$emit('cancel')
    },
    handleConfirm() {
      // 获取选中的选项对象
      const selectedObjects = this.options.filter(option =>
        this.selectedValues.includes(option.value)
      )

      // 关闭popover
      this.visible = false

      // 触发确认事件
      this.$emit('confirm', selectedObjects)
      this.$emit('input', this.selectedValues)
    }
  }
}
</script>

<style lang="scss" scoped>
.filter-panel {
  .filter-options {
    max-height: 200px;
    overflow-y: auto;
    margin-bottom: 15px;
  }

  .filter-actions {
    text-align: right;

    .el-button {
      margin-left: 8px;
    }
  }
}

.filter-header-btn {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  color: #606266;
  padding: 0 4px;

  .filter-text {
    flex: 1;
  }

  .filter-icon {
    font-size: 12px;
    margin-left: 4px;
    opacity: 0.7;
    transition: all 0.3s ease;
  }

  &:hover {
    color: #409EFF;

    .filter-icon {
      opacity: 1;
      transform: scale(1.1);
    }
  }
}
</style>
