<!-- 串码弹窗组件 -->
<template>
  <el-dialog
    :title="dialogTitle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    width="600px"
    :append-to-body="true"
    :z-index="99999"
    @close="handleClose"
  >
    <!-- 商品信息展示 -->
    <div class="imei-info-section" v-loading="loading">
      <div class="imei-info-item">
        <span class="label">商品名称：</span>
        <span class="value">{{ imeiInfo.productName || '-' }} / {{ imeiInfo.skuName || '-' }}</span>
      </div>
      <div class="imei-info-item">
        <span class="label">商品编号：</span>
        <span class="value">{{ imeiInfo.sku || '-' }}</span>
      </div>

      <!-- 串码明细列表（含状态 + 人工放行操作） -->
      <div class="imei-row" v-for="(item, idx) in imeiInfo.codeList" :key="idx">
        <div class="imei-row-codes">
          <div class="imei-info-item">
            <span class="label">SN码：</span>
            <span class="value">{{ item.sn || '-' }}</span>
            <i v-if="item.sn" class="el-icon-copy-document copy-icon" title="复制" @click="handleCopyText(item.sn)" />
          </div>
          <div class="imei-info-item">
            <span class="label">86码：</span>
            <span class="value">{{ item.imel || '-' }}</span>
            <i v-if="item.imel" class="el-icon-copy-document copy-icon" @click="handleCopyText(item.imel)" title="复制"></i>
          </div>
          <div class="imei-info-item">
            <span class="label">激活状态：</span>
            <el-tag :type="activatedTagType(item.activated)" size="small">{{ activatedLabel(item.activated) }}</el-tag>
          </div>
        </div>
        <div class="imei-row-action">
          <el-button
            v-if="item.activated === 5"
            type="warning"
            size="mini"
            :loading="!!passing[idx]"
            @click="handleManualPass(item, idx)"
          >人工放行</el-button>
        </div>
      </div>
    </div>

    <!-- 按钮区域 -->
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { copyText } from '@/utils/wholesaleUtils';
import { apiGetImei, manualPassActivatedApi } from '@/api/creatingOrders';

const ACTIVATED_LABEL = {
  0: '待查询',
  1: '已激活',
  2: '未激活',
  3: '型号不一致',
  4: '查询通过',
  5: '验证失败',
  6: '串码已存在',
  7: '已撤销'
};

const ACTIVATED_TAG_TYPE = {
  0: 'info',
  1: 'success',
  2: 'warning',
  3: 'danger',
  4: 'success',
  5: 'danger',
  6: 'danger',
  7: 'info'
};

export default {
  name: 'ImeiDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    currentOrder: {
      type: Object,
      default: () => ({})
    },
    title: {
      type: String,
      default: '串码信息'
    }
  },
  data() {
    return {
      loading: false,
      passing: {},
      imeiInfo: {
        productName: '',
        skuName: '',
        sku: '',
        codeList: []
      }
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      }
    },
    dialogTitle() {
      return this.title;
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.initImeiInfo();
        this.loadImeiData();
      }
    }
  },
  methods: {
    // 初始化串码信息
    initImeiInfo() {
      if (this.currentOrder) {
        this.imeiInfo.productName = this.currentOrder.productName || '';
        this.imeiInfo.skuName = this.currentOrder.skuName || '';
        this.imeiInfo.sku = this.currentOrder.skuCode || '';
      }
      this.imeiInfo.codeList = [];
      this.passing = {};
    },

    // 加载串码数据
    async loadImeiData() {
      if (!this.currentOrder || !this.currentOrder.orderCode) {
        return;
      }
      this.loading = true;
      try {
        const res = await apiGetImei(this.currentOrder.orderCode);
        if (res.code === 200 && res.data) {
          this.imeiInfo.codeList = res.data;
        }
      } catch (error) {
        console.error('获取串码数据失败:', error);
        this.$message.error('获取串码数据失败');
      } finally {
        this.loading = false;
      }
    },

    activatedLabel(code) {
      return ACTIVATED_LABEL[code] || '-';
    },
    activatedTagType(code) {
      return ACTIVATED_TAG_TYPE[code] || 'info';
    },

    // 人工放行（仅 06api 验证失败 NOT_EXITS=5 的串码可见）
    handleManualPass(item, idx) {
      const codeDesc = item.sn ? `SN:${item.sn}` : `IMEI:${item.imel}`;
      this.$confirm(
        `确认放行该串码？操作将记录审计日志。<br/>${codeDesc}`,
        '人工放行串码',
        { type: 'warning', dangerouslyUseHTMLString: true }
      )
        .then(async () => {
          this.$set(this.passing, idx, true);
          try {
            const res = await manualPassActivatedApi({
              orderCode: this.currentOrder.orderCode,
              sn: item.sn || null,
              imei: item.imel || null
            });
            if (res.code === 200) {
              this.$message.success('放行成功');
              // 刷新串码列表 + 通知父页面刷新订单
              await this.loadImeiData();
              this.$emit('refresh');
            }
          } catch (e) {
            console.error('人工放行失败:', e);
          } finally {
            this.$set(this.passing, idx, false);
          }
        })
        .catch(() => {});
    },

    // 复制文本
    handleCopyText(text) {
      copyText(text, this);
    },

    // 关闭弹框
    handleClose() {
      this.$emit('update:visible', false);
    }
  }
}
</script>

<style scoped lang="scss">
.imei-info-section {
  margin-bottom: 20px;
  padding: 16px;
  background-color: var(--bg-table-header);
  border-radius: 4px;
}

.imei-info-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;

  &:last-child {
    margin-bottom: 0;
  }
}

.imei-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-top: 1px dashed var(--border-tags);

  &:first-of-type {
    border-top: none;
  }
}

.imei-row-codes {
  flex: 1;
}

.imei-row-action {
  margin-left: 12px;
  flex-shrink: 0;
}

.label {
  width: 80px;
  color: var(--nl-color-weak);
  font-weight: 500;
  margin-right: 8px;
  flex-shrink: 0;
}

.value {
  flex: 1;
  color: var(--nl-color);
  margin-right: 8px;
}

.copy-icon {
  cursor: pointer;
  color: var(--nl-color-tip);
  transition: color 0.3s;
  font-size: 14px;

  &:hover {
    color: #409eff;
  }
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid var(--border-tags);
}

.dialog-footer .el-button {
  margin-left: 10px;
}
</style>
