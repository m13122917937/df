<template>
  <el-dialog
    :title="title"
    width="500px"
    :visible.sync="dialogVisible"
    :append-to-body="true"
    :z-index="99999"
    :close-on-click-modal="false"
    @close="handleCancel"
  >
    <el-input
      type="textarea"
      :rows="8"
      maxlength="2000"
      show-word-limit
      :placeholder="placeholder"
      v-model="localValue"
    />
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取 消</el-button>
      <el-button type="primary" @click="handleConfirm">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'BatchQueryDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    inputValue: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: '批量查询'
    },
    placeholder: {
      type: String,
      default: '每行输入一条记录，最多可输入200条'
    }
  },
  data() {
    return {
      localValue: this.inputValue
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    inputValue(val) {
      this.localValue = val
    }
  },
  methods: {
    handleCancel() {
      this.$emit('update:visible', false)
      this.$emit('cancel')
    },
    handleConfirm() {
      this.$emit('update:inputValue', this.localValue)
      this.$emit('confirm', this.localValue)
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style scoped>
</style>

