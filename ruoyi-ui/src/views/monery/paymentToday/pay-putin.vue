<!-- 入仓  billType = 1 -->
<template>
  <div class="payment-today">
    <div class="search-section">
      <div class="search-content">
        <div class="search-container">
          <div class="search-row">
            <div class="search-item">
              <label class="search-label">付款主体</label>
              <el-select
                v-model="query.payCompanyId"
                placeholder="请选择付款主体"
                clearable
                filterable
                remote
                reserve-keyword
                :remote-method="handleSubjectRemote"
                :loading="subjectsLoading"
                @visible-change="handleSubjectVisible"
                @change="handleQuery"
                @clear="handleQuery"
                class="search-select"
              >
                <el-option v-for="s in subjects" :key="s.value" :label="s.label" :value="s.value" />
              </el-select>
            </div>
            <div class="search-item">
              <label class="search-label">供应商名称</label>
              <el-select
                v-model="query.supplierId"
                placeholder="请输入供应商名称"
                clearable
                filterable
                remote
                reserve-keyword
                :remote-method="handleSupplierRemote"
                :loading="supplierLoading"
                @visible-change="handleSupplierVisible"
                @change="handleSupplierChange"
                @clear="handleSupplierClear"
                class="search-select"
              >
                <el-option v-for="s in supplierOptions" :key="s.value" :label="s.label" :value="s.value" />
              </el-select>
            </div>
          </div>
        </div>
        <div class="search-actions">
          <el-button
            icon="el-icon-refresh"
            @click="handleReset"
            class="reset-btn"
          >
            重置
          </el-button>
          <el-button
            type="primary"
            icon="el-icon-search"
            @click="handleQuery"
            class="search-action-btn"
          >
            搜索
          </el-button>
        </div>
      </div>
      <div class="toolbar-slot">
        <!-- <el-button
          icon="el-icon-download"
          @click="onExportTable"
          type="primary"
        >
          导出表格
        </el-button> -->
        <el-button
          icon="el-icon-download"
          @click="onExportAging"
          type="primary"
          :loading="exportLoading"
        >
          导出付款详情
        </el-button>
      </div>
    </div>
    <div class="table-section">
      <div class="summary-bar">
        合计：{{ formatNumber(todayAmount) }}
      </div>
      <el-table
        ref="tableRef"
        :data="list"
        border
        stripe
        row-key="id"
        height="100%"
        v-loading="tableLoading"
      >
        <el-table-column prop="supplierName" label="供应商" min-width="200" align="left" />
        <el-table-column prop="billingAmount" label="应付金额" min-width="200" align="left" />
        <el-table-column prop="payAmount" label="排款计划" min-width="200" align="left">
          <template slot-scope="scope">
            <div>
              <!-- 总计 -->
              <div style="margin-bottom: 8px; font-weight: 600;">
                总计：{{ formatNumber(getBillPlanTotal(scope.row.billPlanVOS)) }}
              </div>
              <div v-for="item in scope.row.billPlanVOS" :key="item.id">
                <div style="display: flex; align-items: center; justify-content: flex-start; gap: 8px;" >
                  <div v-if="item.status == 1">
                    <span style="margin-right: 8px;">{{ item.payAmount }}</span>
                    <el-popconfirm
                      confirm-button-text='确定'
                      cancel-button-text='取消'
                      icon="el-icon-info"
                      icon-color="red"
                      @confirm="confirmRevoke(item.id)"
                      title="撤销后需重新排款，是否撤销？"
                    >
                      <el-button size="mini" slot="reference" type="text">撤销</el-button>
                    </el-popconfirm>
                  </div>
                </div>
              </div>
              <el-button
                v-if="shouldShowPlanButton(scope.row)"
                size="mini"
                type="text"
                @click="openPlan(scope.row)"
              >排款</el-button>

            </div>
          </template>
        </el-table-column>
        <el-table-column prop="payAmount" label="出纳操作" min-width="200" align="left">
          <template slot-scope="scope">
            <div>
              <!-- 总计 -->
              <div style="margin-bottom: 8px; font-weight: 600;">
                总计：{{ formatNumber(getBillPlanTotal(scope.row.billPlanVOS)) }}
              </div>
              <div v-for="item in scope.row.billPlanVOS" :key="item.id">
                <div style="display: flex; align-items: center; justify-content: flex-start; gap: 8px;" >
                  <el-button
                    size="mini"
                    type="text"
                    @click="openCashier(item, scope.row)"
                  >付款操作</el-button>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 加载更多提示 -->
      <div v-if="loadingMore" class="load-more-tip">
        <i class="el-icon-loading"></i>
        <span>正在加载更多...</span>
      </div>
      <div v-else-if="!hasMore && list.length > 0" class="load-more-tip no-more">
        <span>没有更多数据了</span>
      </div>
    </div>

    <!-- 排款计划弹窗 -->
    <el-dialog :visible.sync="planDialog.visible" width="1080px">
      <template slot="title">
        <span>排款计划</span>
        <span style="margin-left: 16px; color: #909399">供应商：{{ planDialog.supplierName }}</span>
      </template>

      <!-- 标签页 -->
      <el-tabs v-model="planDialog.activeTab" class="plan-tabs">
        <el-tab-pane label="待排款账单" name="pending">
          <!-- 黄色提示横幅 -->
          <!-- <div class="plan-alert">
            <i class="el-icon-warning"></i>
            <span>移动鼠标选择计划付款金额!如订单有异常,可点击「异常」按钮转入异常订单,暂不付款!排款只支持先进先出</span>
          </div> -->

          <!-- 表格 -->
          <div class="plan-table-wrapper">
            <el-table
              ref="planTable"
              :data="planDialog.displayRows"
              border
              height="400px"
              v-loading="planDialog.loadingAll"
              element-loading-text="正在加载全部数据..."
            >
              <el-table-column prop="orderCode" label="内部订单号" min-width="150" show-overflow-tooltip fixed="left" />
              <el-table-column prop="originalOrderId" label="原始订单" min-width="150" show-overflow-tooltip />
              <el-table-column prop="billType" label="账单类型" min-width="120" align="center">
                <template slot-scope="scope">
                  {{ formatBillType(scope.row.billType) }}
                </template>
              </el-table-column>
              <el-table-column prop="reversed" label="账单性质" min-width="120" align="center">
                <template slot-scope="scope">
                  {{ formatReversed(scope.row.reversed) }}
                </template>
              </el-table-column>
              <el-table-column prop="productName" label="商品名" min-width="180" show-overflow-tooltip />
              <el-table-column prop="skuName" label="SKU名称" min-width="180" show-overflow-tooltip />
              <el-table-column prop="brand" label="品牌" min-width="120" show-overflow-tooltip />
              <el-table-column prop="category" label="类别" min-width="120" show-overflow-tooltip />
              <el-table-column prop="tradePrice" label="单价" min-width="100" align="right">
                <template slot-scope="scope">
                  {{ scope.row.tradePrice === '-' || scope.row.tradePrice === null || scope.row.tradePrice === undefined ? '-' : formatNumber(scope.row.tradePrice) }}
                </template>
              </el-table-column>
              <el-table-column prop="quantity" label="数量" min-width="80" align="center" />
              <el-table-column prop="billingAmount" label="账单金额" min-width="140" align="right" fixed="right">
                <template slot-scope="scope">
                  <el-checkbox
                    v-model="scope.row.selected"
                    @change="handleRowAmountChange(scope.row)"
                  >
                    {{ formatNumber(scope.row.billingAmount) }}
                  </el-checkbox>
                </template>
              </el-table-column>
              <el-table-column prop="accounting" label="账期" min-width="100" align="center">
                <template slot-scope="scope">
                  {{ formatAccounting(scope.row.accounting) }}
                </template>
              </el-table-column>
              <el-table-column prop="signedDate" label="签收日期" min-width="160" align="center">
                <template slot-scope="scope">
                  {{ formatDate(scope.row.signedDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="settlementDate" label="结算日期" min-width="160" align="center">
                <template slot-scope="scope">
                  {{ formatDate(scope.row.settlementDate) }}
                </template>
              </el-table-column>
            </el-table>
            <div v-if="planDialog.processing" class="processing-overlay">
              <i class="el-icon-loading" />
              <div class="processing-text">处理中，请稍候...</div>
            </div>
          </div>
          <!-- 加载更多提示 -->
          <div v-if="planDialog.loadingMore" class="load-more-tip">
            <i class="el-icon-loading"></i>
            <span>正在加载更多...</span>
          </div>
          <div v-else-if="!planDialog.hasMore && planDialog.displayRows.length > 0" class="load-more-tip no-more">
            <span>没有更多数据了</span>
          </div>
        </el-tab-pane>
      </el-tabs>

      <div class="plan-footer">
        <div class="plan-amount">计划付款金额: ¥ {{ formatNumber(planDialog.selectedAmount) }}</div>
        <div class="plan-actions">
          <el-button size="small" @click="onPlanReset" :disabled="planDialog.loadingAll || planDialog.processing" :loading="planDialog.processing">重置</el-button>
          <el-button size="small" @click="onPlanSelectAll" :disabled="planDialog.loadingAll || planDialog.processing" :loading="planDialog.processing">全选</el-button>
          <el-button size="small" type="danger" @click="onPlanPay">下一步</el-button>
          <el-button size="small" @click="planDialog.visible = false">关闭</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 付款操作弹窗 -->
    <el-dialog :visible.sync="cashierDialog.visible" width="960px" class="cashier-dialog">
      <template slot="title">
        <div class="cashier-title">
          <div class="cashier-title__main">
            <span>                                                    </span>
            <el-button
              type="text"
              icon="el-icon-document-copy"
              size="mini"
              @click="copyCashierInfo"
              class="cashier-copy-btn"
            >
              复制信息
            </el-button>
          </div>
          <span class="cashier-title__subtitle">请确认付款信息并上传凭证</span>
        </div>
      </template>

      <div class="cashier-content">
        <div class="cashier-info-grid">
          <div class="cashier-card">
            <div class="cashier-card__label">
              <i class="el-icon-s-custom"></i>
              收款账户名
            </div>
            <div class="cashier-card__value">{{ cashierDialog.supplierName || '-' }}</div>
          </div>
          <div class="cashier-card">
            <div class="cashier-card__label">
              <i class="el-icon-office-building"></i>
              收款银行
            </div>
            <div class="cashier-card__value">{{ cashierDialog.bankName || '-' }}</div>
          </div>
          <div class="cashier-card">
            <div class="cashier-card__label">
              <i class="el-icon-bank-card"></i>
              收款账户
            </div>
            <div class="cashier-card__value">{{ cashierDialog.bankAccount || '-' }}</div>
          </div>
          <div class="cashier-card">
            <div class="cashier-card__label">
              <i class="el-icon-office-building"></i>
              付款主体
            </div>
            <div class="cashier-card__value">{{ cashierDialog.subjectName || '-' }}</div>
          </div>
          <div class="cashier-card">
            <div class="cashier-card__label">
              <i class="el-icon-office-building"></i>
              付款银行
            </div>
            <div class="cashier-card__value">{{ cashierDialog.payCompanyBank || '-' }}</div>
          </div>
          <div class="cashier-card">
            <div class="cashier-card__label">
              <i class="el-icon-credit-card"></i>
              付款账户
            </div>
            <div class="cashier-card__value">{{ cashierDialog.payCompanyBankAccount || '-' }}</div>
          </div>
          <div class="cashier-card">
            <div class="cashier-card__label">
              <i class="el-icon-coin"></i>
              付款金额
            </div>
            <div class="cashier-card__value cashier-card__value--amount">
              ¥ {{ formatNumber(cashierDialog.amount) }}
            </div>
          </div>
        </div>

        <div class="cashier-upload-card">
          <div class="cashier-upload-card__header">
            <div>
              <div class="cashier-upload-card__title">付款凭证</div>
              <div class="cashier-upload-card__desc">支持拖拽、粘贴或点击上传，单张 ≤ 5MB</div>
            </div>
          </div>

          <el-upload
            v-if="!cashierDialog.fileList.length"
            ref="cashierUpload"
            drag
            :action="uploadAction"
            :headers="uploadHeaders"
            :auto-upload="true"
            :multiple="false"
            :limit="1"
            :on-exceed="handleUploadExceed"
            :before-upload="beforeVoucherUpload"
            :on-success="onUploadSuccess"
            :on-error="onUploadError"
            :on-remove="onUploadRemove"
            :file-list="cashierDialog.fileList"
            :disabled="cashierDialog.fileList.length >= 1"
            :show-file-list="false"
            accept="image/jpeg,image/png"
            class="cashier-upload"
            style="width: 100%"
          >
            <i class="el-icon-upload" />
            <div class="el-upload__text">
              将凭证拖拽到此处，或<em>点击上传</em>
            </div>
            <div class="el-upload__tip">仅支持 jpg / png，最多 1 张</div>
          </el-upload>

          <div v-else class="cashier-preview">
            <el-image
              class="cashier-preview__img"
              :src="cashierDialog.fileList[0].url"
              :preview-src-list="cashierDialog.fileList.map(item => item.url)"
              fit="cover"
              :lazy="false"
              @error="handleImageError"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
                <div>图片加载失败</div>
              </div>
            </el-image>
            <div class="cashier-preview__info">
              <div class="cashier-preview__name">{{ cashierDialog.fileList[0].name }}</div>
              <div class="cashier-preview__actions">
                <el-button type="text" size="mini" @click="resetCashierUpload">重新上传</el-button>
                <el-button type="text" size="mini" @click="removeCashierVoucher">移除</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="cashierDialog.visible = false">取 消</el-button>
        <el-button size="small" type="primary" @click="submitCashier">提 交</el-button>
      </span>
    </el-dialog>

    <!-- 收付款账户确认弹窗 -->
    <AccountConfirmDialog
      :visible.sync="accountConfirmDialog.visible"
      :supplier-name="accountConfirmDialog.supplierName"
      :supplier-id="accountConfirmDialog.supplierId"
      :bank-name="accountConfirmDialog.bankName"
      :bank-account="accountConfirmDialog.bankAccount"
      :pay-company-list="accountConfirmDialog.payCompanySummaryVOS"
      :total-pay-plan-amount="accountConfirmDialog.totalPayPlanAmount"
      :bill-ids="accountConfirmDialog.billIds"
      :pay-company-options="subjects"
      :pay-company-loading="subjectsLoading"
      :supplier-options="supplierOptions"
      :supplier-loading="supplierLoading"
      @confirm="handleAccountConfirm"
      @cancel="handleAccountCancel"
      @close="handleAccountClose"
      @pay-company-change="handlePayCompanyChange"
      @pay-company-remote="handlePayCompanyRemote"
      @pay-company-visible="handlePayCompanyVisible"
    />

  </div>
</template>

<script>
import {
  getPaymentTodayListApi,
  getPaymentTodaySumApi,
  getPaymentTodayDetailApi,
  getPaymentTodaySplitApi,
  getPaymentTodayRevokeApi,
  getPaymentTodayUpdateApi,
  exportPaymentTodayDetailApi
} from '@/api/paymentToday';
import { getBusinessCompanyListApi } from '@/api/business';
import { getPayerAllListApi, getPaymentTodayPlanApi } from '@/api/monery';
import { getToken } from '@/utils/auth';
import { saveAs } from "file-saver";
import { blobValidate } from "@/utils/ruoyi";
import { createCopyTextMethod } from '@/utils/wholesaleUtils';
import AccountConfirmDialog from './components/AccountConfirmDialog.vue';

export default {
  name: 'PaymentToday',
  components: {
    AccountConfirmDialog
  },
  data() {
    return {
      todayAmount: 0,
      planDialog: {
        visible: false,
        supplierName: '',
        rows: [],
        displayRows: [],
        abnormalRows: [],
        total: 0,
        selectedRows: [],
        selectedAmount: 0,
        supplierId: null,
        payCompanyId: null,
        activeTab: 'pending',
        abnormalCount: 0,
        pageSize: 30,
        pageNum: 1,
        hasMore: true,
        loadingAll: false, // 改为 loadingAll 来表示正在加载所有数据
        processing: false // 标记正在处理全选/重置等操作
      },
      cashierDialog: {
        visible: false,
        supplierName: '',
        bankName: '',
        amount: 0,
        subjectName: '上海华喜电子商务有限公司',
        fileList: [],
        planId: null,
        bankAccount: '',
        payCompanyBank: '',
        payCompanyBankAccount: ''
      },
      accountConfirmDialog: {
        visible: false,
        supplierName: '',
        supplierId: null,
        bankName: '',
        bankAccount: '',
        payCompanySummaryVOS: [],
        totalPayPlanAmount: 0,
        billIds: [] // 保存选中的账单ID
      },
      query: {
        payCompanyId: undefined,
        supplierId: '',
        billType: 1
      },
      pageQuery:{
        pageSize:50,
        pageNum:1
      },
      subjects: [],
      subjectsLoading: false,
      list: [],
      tableLoading: false,
      supplierOptions: [],
      supplierLoading: false,
      selectedSupplierName: '',
      hasMore: true,
      loadingMore: false,
      exportLoading: false
    };
  },
  created() {
    this.fetchSubjects();
    this.loadList();
    this.loadSummary();
    this.handleSupplierRemote();
  },
  mounted() {
    this.initScrollListener();
  },
  beforeDestroy() {
    this.removeScrollListener();
  },
  computed: {
    uploadAction() {
      return `${process.env.VUE_APP_BASE_API || ''}/common/v2/upload`;
    },
    uploadHeaders() {
      return {
        Authorization: `Bearer ${getToken()}`
      };
    }
  },
  watch: {
    'planDialog.visible'(newVal) {
      if (!newVal) {
        // 弹窗关闭时的清理逻辑
        // 滚动监听器已移除，无需清理
      }
    }
  },
  methods: {
    // 复制付款信息
    copyCashierInfo() {
      const supplierName = this.cashierDialog.supplierName || '-';
      const bankName = this.cashierDialog.bankName || '-';
      const bankAccount = this.cashierDialog.bankAccount || '-';
      const subjectName = this.cashierDialog.subjectName || '-';
      const amount = this.formatNumber(this.cashierDialog.amount) || '-';
      const payCompanyBank = this.cashierDialog.payCompanyBank || '-';
      const payCompanyBankAccount = this.cashierDialog.payCompanyBankAccount || '-';

      const copyText = `收款账户名：${supplierName}\n收款银行：${bankName}\n收款账户：${bankAccount}\n付款主体：${subjectName}\n付款银行：${payCompanyBank}\n付款账户：${payCompanyBankAccount}\n付款金额：¥ ${amount}`;
      this.copyText(copyText);
    },
    async fetchSubjects(keyword = '') {
      this.subjectsLoading = true;
      try {
        const params = {};
        if (keyword) {
          params.payName = keyword;
        }
        const { code, data = [] } = await getPayerAllListApi(params);
        if (code === 200) {
          this.subjects = data.map(item => ({
            label: item.payName,
            value: item.id
          }));
        }
      } catch (error) {
        console.error('获取付款主体列表失败', error);
      } finally {
        this.subjectsLoading = false;
      }
    },
    handleSubjectRemote(keyword) {
      const trimmed = (keyword || '').trim();
      this.fetchSubjects(trimmed);
    },
    handleSubjectVisible(visible) {
      if (visible && !this.subjects.length) {
        this.fetchSubjects();
      }
    },
    async handleSupplierRemote(keyword = '') {
      const trimmed = (keyword || '').trim();
      this.supplierLoading = true;
      try {
        const pageData = {
          pageNum: 1,
          pageSize: 30
        };
        const params = {}
        if (trimmed) {
          params.companyNameLike = trimmed;
        }
        const res = await getBusinessCompanyListApi(params,pageData);
        const list = res?.rows || res?.data || [];
        this.supplierOptions = Array.isArray(list) ? list.map(item => ({
          label: item.companyName,
          value: item.id,
          raw: item
        })) : [];
      } catch (error) {
        console.error('获取供应商列表失败', error);
        this.supplierOptions = [];
      } finally {
        this.supplierLoading = false;
      }
    },
    handleSupplierVisible(visible) {
      if (visible && !this.supplierOptions.length) {
        this.handleSupplierRemote();
      }
    },
    handleSupplierChange(val) {
      const option = this.supplierOptions.find(item => item.value === val);
      this.selectedSupplierName = option ? option.label : '';
      this.handleQuery();
    },
    handleSupplierClear() {
      this.selectedSupplierName = '';
      this.query.supplierId = undefined;
      this.handleQuery();
    },
    buildQueryParams() {
      const params = {
        billType: this.query.billType
      };
      if (this.query.payCompanyId) {
        params.payCompanyId = this.query.payCompanyId;
      }
      if (this.query.supplierId) {
        params.supplierId = this.query.supplierId;
      }

      return params;
    },
    handleQuery() {
      // 重置分页
      this.pageQuery.pageNum = 1;
      this.hasMore = true;
      this.list = [];
      this.loadList();
      this.loadSummary();
    },
    // 重置
    handleReset() {
      this.query = {
        payCompanyId: undefined,
        supplierId: '',
        billType: 1
      };
      this.selectedSupplierName = '';
      this.pageQuery.pageNum = 1;
      this.hasMore = true;
      this.list = [];
      this.supplierOptions = [];
      this.supplierLoading = false;
      this.loadList();
      this.loadSummary();
    },
    async loadList(isAppend = false) {
      // 如果是追加模式，使用 loadingMore，否则使用 tableLoading
      if (isAppend) {
        if (this.loadingMore || !this.hasMore) return;
        this.loadingMore = true;
      } else {
        this.tableLoading = true;
      }

      try {
        const params = this.buildQueryParams();
        const listRes = await getPaymentTodayListApi({...params,...this.pageQuery});

        if (listRes && listRes.code === 200) {
          const rowsSource = listRes.rows || [];
          const newList = rowsSource.map(item => {
            const billPlanData = Array.isArray(item.billPlanVOS)
              ? item.billPlanVOS[0]
              : (item.billPlanVOS || {});
            const billingAmount = Number(item.billingAmount ?? billPlanData?.billingAmount ?? 0);
            const payAmount = Number(item.payAmount ?? billPlanData?.payAmount ?? 0);
            return {
              ...billPlanData,
              ...item,
              billingAmount,
              payAmount,
              id: item.id || billPlanData?.id || `${item.supplierId || billPlanData?.supplierId || ''}-${item.payCompanyId || billPlanData?.payCompanyId || ''}`,
              supplierName: item.supplierName || billPlanData?.supplierName || '',
              payCompanyName: item.payCompanyName || billPlanData?.payCompanyName || '',
              payTime: item.payTime || billPlanData?.payTime || ''
            };
          });

          // 判断是否还有更多数据
          if (rowsSource.length < this.pageQuery.pageSize) {
            this.hasMore = false;
          } else {
            this.hasMore = true;
          }

          // 追加或替换数据
          if (isAppend) {
            this.list = [...this.list, ...newList];
          } else {
            this.list = newList;
          }
        } else {
          if (!isAppend) {
            this.list = [];
          }
          this.hasMore = false;
        }
      } catch (error) {
        console.error('获取今日付款列表失败', error);
        if (!isAppend) {
          this.$message.error('获取列表数据失败，请稍后重试');
          this.list = [];
        }
        this.hasMore = false;
      } finally {
        if (isAppend) {
          this.loadingMore = false;
        } else {
          this.tableLoading = false;
        }
      }
    },
    // 初始化滚动监听
    initScrollListener() {
      this.$nextTick(() => {
        const tableEl = this.$refs.tableRef;
        if (!tableEl) return;

        // 获取表格的 body wrapper 元素
        const tableBodyWrapper = tableEl.$el.querySelector('.el-table__body-wrapper');
        if (!tableBodyWrapper) return;

        // 监听滚动事件
        this.scrollHandler = () => {
          this.handleTableScroll(tableBodyWrapper);
        };
        tableBodyWrapper.addEventListener('scroll', this.scrollHandler);
      });
    },
    // 移除滚动监听
    removeScrollListener() {
      if (this.scrollHandler) {
        const tableEl = this.$refs.tableRef;
        if (tableEl) {
          const tableBodyWrapper = tableEl.$el.querySelector('.el-table__body-wrapper');
          if (tableBodyWrapper) {
            tableBodyWrapper.removeEventListener('scroll', this.scrollHandler);
          }
        }
        this.scrollHandler = null;
      }
    },
    // 移除待排款账单表格滚动监听
    removePlanTableScrollListener() {
      if (this.planTableScrollHandler) {
        const tableEl = this.$refs.planTable;
        if (tableEl) {
          const tableBodyWrapper = tableEl.$el.querySelector('.el-table__body-wrapper');
          if (tableBodyWrapper) {
            tableBodyWrapper.removeEventListener('scroll', this.planTableScrollHandler);
          }
        }
        this.planTableScrollHandler = null;
      }
    },
    // 处理表格滚动
    handleTableScroll(el) {
      // 如果正在加载或没有更多数据，不处理
      if (this.loadingMore || !this.hasMore) return;

      // 计算滚动位置
      const scrollTop = el.scrollTop;
      const scrollHeight = el.scrollHeight;
      const clientHeight = el.clientHeight;

      // 当滚动到底部附近（距离底部50px）时，加载下一页
      if (scrollHeight - scrollTop - clientHeight < 50) {
        this.pageQuery.pageNum += 1;
        this.loadList(true);
      }
    },
    async loadSummary() {
      try {
        const params = this.buildQueryParams();
        const sumRes = await getPaymentTodaySumApi(params);
        if (sumRes && sumRes.code === 200) {
          const sumData = sumRes.data || sumRes.rows || {};
          const amount = Number(sumData.todayAmount ?? sumData.payAmount ?? sumData.totalPayAmount ?? sumData.billingAmount ?? 0);
          this.todayAmount = Number.isFinite(amount) ? amount : 0;
        } else {
          this.todayAmount = 0;
        }
      } catch (error) {
        console.error('获取今日付款合计失败', error);
        this.todayAmount = 0;
      }
    },
    formatNumber(val) {
      if (val === null || val === undefined || val === '') return '-';
      return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
    },
    shouldShowPlanButton(row = {}) {
      const billTotal = Number(row.billingAmount) || 0;
      const plannedTotal = this.getBillPlanTotal(row.billPlanVOS || []);
      // 当排款总计小于应付金额时才显示排款按钮
      return plannedTotal < billTotal;
    },
    // 计算排款计划总计
    getBillPlanTotal(billPlanVOS) {
      if (!Array.isArray(billPlanVOS) || billPlanVOS.length === 0) {
        return 0;
      }
      return billPlanVOS.reduce((total, item) => {
        const payAmount = Number(item.payAmount) || 0;
        return total + payAmount;
      }, 0);
    },
    formatBillType(val) {
      if (val === null || val === undefined || val === '') return '-';
      const map = {
        1: '批量采购',
        2: '一件代发',
        3: '接龙订单'
      };
      return map[val] || val;
    },
    formatReversed(val) {
      if (val === null || val === undefined || val === '') return '-';
      const map = {
        0: '货款账单',
        1: '售后账单',
        2: '扣罚账单'
      };
      return map[val] || val;
    },
    formatAccounting(val) {
      if (val === null || val === undefined || val === '') return '-';
      const map = {
        0: 'T+0',
        1: 'T+1',
        2: 'T+2',
        3: 'T+3',
        4: 'T+4',
        5: 'T+5',
        6: 'T+6',
        7: 'T+7',
      };
      return map[val] || val;
    },
    formatDate(val) {
      if (!val) return '-';
      if (typeof val === 'string') {
        // 如果是日期字符串，格式化显示
        const date = new Date(val);
        if (isNaN(date.getTime())) return val;
        // 格式化为 YYYY-MM-DD HH:mm
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}`;
      }
      return val;
    },

    openPlan(row) {
      this.planDialog.supplierName = row.supplierName;
      this.planDialog.supplierId = row.supplierId;
      this.planDialog.payCompanyId = this.query.payCompanyId || '';
      // 重置选中金额和分页
      this.planDialog.selectedAmount = 0;
      this.planDialog.pageNum = 1;
      this.planDialog.hasMore = true;
      this.planDialog.rows = [];
      this.planDialog.displayRows = [];
      this.planDialog.loadingAll = true;  // 开始加载所有数据
      this.loadPlanDetail();
      this.planDialog.visible = true;
    },
    async loadPlanDetail() {
      // 重置数据
      this.planDialog.rows = [];
      this.planDialog.hasMore = true;

      try {
        // 循环加载所有页面数据
        while (this.planDialog.hasMore) {
          const params = {
            billType: 1,
            supplierId: this.planDialog.supplierId,
            payCompanyId: this.planDialog.payCompanyId,
            pageSize: this.planDialog.pageSize,
            pageNum: this.planDialog.pageNum
          };

          const res = await getPaymentTodayDetailApi(params);

          if (res && res.code === 200) {
            // 初始化数据，确保每行都有 selected 属性
            const newRows = (res.rows || []).map(item => ({
              ...item,
              selected: false,
              billingAmount: Number(item.billingAmount) || 0
            }));

            // 追加数据到 rows
            this.planDialog.rows = [...this.planDialog.rows, ...newRows];

            // 判断是否还有更多数据
            if (newRows.length < this.planDialog.pageSize) {
              this.planDialog.hasMore = false;
            } else {
              this.planDialog.pageNum += 1;  // 加载下一页
            }
          } else {
            // API调用失败，停止加载并显示错误
            this.planDialog.hasMore = false;
            this.$message.error('获取账单详情失败，请稍后重试');
            break;  // 立即跳出循环
          }
        }

        // 所有数据加载完成后，更新显示数据
        this.planDialog.total = 0;
        this.updateDisplayRows();
      } catch (error) {
        console.error('获取待排款账单详情失败', error);
        this.$message.error('获取账单详情失败，请稍后重试');
        this.planDialog.rows = [];
        this.planDialog.displayRows = [];
        this.planDialog.hasMore = false;
      } finally {
        this.planDialog.loadingAll = false;  // 加载完成
      }
    },
    updateDisplayRows() {
      // 过滤掉异常订单，生成显示行，并确保 selected 属性存在
      this.planDialog.displayRows = this.planDialog.rows
        .filter(row => !row.isAbnormal && !row.isSummary)
        .map(row => ({
          ...row,
          selected: row.selected !== undefined ? row.selected : false
        }));
      // 重新计算选中金额
      this.calculateSelectedAmount();
    },
    handleRowAmountChange(row) {
      this.calculateSelectedAmount();
    },
    calculateSelectedAmount() {
      // 优化：使用 for 循环代替 forEach，提升性能
      let total = 0;
      const rows = this.planDialog.displayRows;

      for (let i = 0, len = rows.length; i < len; i++) {
        const row = rows[i];
        if (row.selected && !row.isSummary && !row.isAbnormal) {
          // 确保 billingAmount 是数字类型
          const amount = Number(row.billingAmount) || 0;
          total += amount;
        }
      }

      this.planDialog.selectedAmount = total;
    },
    onPlanReset() {
      // 当数据较大时，这里可能耗时：先让 DOM 更新显示覆盖层，再执行同步重置
      this.planDialog.processing = true;
      // allow Vue to render the overlay before heavy synchronous work
      this.$nextTick(() => {
        setTimeout(() => {
          try {
            const rows = this.planDialog.displayRows;
            for (let i = 0, len = rows.length; i < len; i++) {
              if (!rows[i].isSummary) {
                rows[i].selected = false;
              }
            }
            this.planDialog.selectedRows = [];
            this.planDialog.selectedAmount = 0;
          } finally {
            // small delay to ensure user notices the processing state
            this.$nextTick(() => {
              const timer = setTimeout(() => {
                this.planDialog.processing = false;
              }, 100);
              return () => clearTimeout(timer);
            });
          }
        }, 20);
      });
    },
    onPlanSelectAll() {
      // 当数据较大时，这里可能耗时：先让 DOM 更新显示覆盖层，再执行同步全选
      this.planDialog.processing = true;
      this.$nextTick(() => {
        setTimeout(() => {
          try {
            const rows = this.planDialog.displayRows;
            // 选中所有展示行（displayRows 已过滤掉 isAbnormal 与 isSummary）
            for (let i = 0, len = rows.length; i < len; i++) {
              rows[i].selected = true;
            }
            // 更新选中行 id 列表
            this.planDialog.selectedRows = rows
              .filter(r => r.selected && !r.isSummary && !r.isAbnormal)
              .map(r => r.id);
            // 计算金额
            this.calculateSelectedAmount();
          } finally {
             this.$nextTick(() => {
              const timer = setTimeout(() => {
                this.planDialog.processing = false;
              }, 100);
              return () => clearTimeout(timer);
            });
          }
        }, 20);
      });
    },
    onPlanPay() {
      if (this.planDialog.selectedAmount <= 0) {
        this.$message.warning('请选择要付款的订单');
        return;
      }

      // 获取选中的账单ID列表
      const selectedBillIds = this.planDialog.displayRows
        .filter(row => row.selected && !row.isSummary && !row.isAbnormal)
        .map(row => row.id)
        .filter(id => id);

      if (selectedBillIds.length === 0) {
        this.$message.warning('请选择要付款的订单');
        return;
      }

      // 调用API获取拆分数据
      getPaymentTodaySplitApi({
        billIds: selectedBillIds
      }).then(res => {
        if (res && res.code === 200) {
          const data = res.data || {};

          // 设置弹窗数据
          this.accountConfirmDialog.supplierName = data.supplierName || this.planDialog.supplierName;
          this.accountConfirmDialog.supplierId = data.supplierId || null;
          this.accountConfirmDialog.bankName = data.bankName || '';
          this.accountConfirmDialog.bankAccount = data.bankAccount || '';
          this.accountConfirmDialog.payCompanySummaryVOS = Array.isArray(data.payCompanySummaryVOS)
            ? data.payCompanySummaryVOS
            : [];

          // 计算总付款计划金额（所有付款主体的金额之和）
          this.accountConfirmDialog.totalPayPlanAmount = this.accountConfirmDialog.payCompanySummaryVOS.reduce((sum, item) => {
            return sum + (Number(item.totalBillingAmount) || 0);
          }, 0);

          // 保存选中的账单ID
          this.accountConfirmDialog.billIds = selectedBillIds;

          // 显示收付款账户确认弹窗
          this.accountConfirmDialog.visible = true;
          // 关闭排款计划弹窗
          this.planDialog.visible = false;
        } else {
          this.$message.error(res?.msg || '获取账户信息失败');
        }
      }).catch(error => {
        console.error('获取账户拆分信息失败', error);
        this.$message.error('获取账户信息失败，请稍后重试');
      });
    },
    confirmRevoke(id) {
      // 这里调用撤销接口
      // TODO: 调用实际的撤销接口
      getPaymentTodayRevokeApi(id).then(res => {
        if (res && res.code === 200) {
          this.$message.success('撤销成功');
          this.handleQuery();
        } else {
          this.$message.error(res?.msg || '撤销失败');
        }
      }).catch(error => {
        console.error('撤销排款计划失败', error);
        this.$message.error('撤销排款计划失败，请稍后重试');
      });
    },
    onPlanRevoke() {
      this.$confirm('确定要撤销当前排款计划吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('撤销成功');
        this.planDialog.visible = false;
        // 刷新列表
        this.handleQuery();
      }).catch(() => {});
    },
    openCashier(planItem = {}, supplierRow = {}) {
      const supplier = supplierRow || {};
      const amount = Number(planItem.payAmount) || 0;
      this.cashierDialog.supplierName = supplier.supplierName||'';
      this.cashierDialog.amount = amount;
      this.cashierDialog.bankName = supplier.bankName;
      this.cashierDialog.subjectName = planItem.payCompanyName;
      this.cashierDialog.planId = planItem.id || null;
      this.cashierDialog.bankAccount = supplier.bankAccount;
      this.cashierDialog.payCompanyBank = planItem.payCompanyBank;
      this.cashierDialog.payCompanyBankAccount = planItem.payCompanyBankAccount;

      // 处理付款凭证URL - 只从 billPlanVOS 中获取，不从 supplier 中获取
      const payVoucherUrls = planItem.payVoucherUrls || '';
      if (payVoucherUrls) {
        let url = payVoucherUrls;
        // 确保 URL 是完整的，如果是相对路径则拼接基础 URL
        if (url && !url.startsWith('http://') && !url.startsWith('https://') && !url.startsWith('//')) {
          // 如果是相对路径，拼接基础 API 地址
          const baseApi = process.env.VUE_APP_BASE_API || '';
          url = url.startsWith('/') ? `${baseApi}${url}` : `${baseApi}/${url}`;
        }
        // 如果 URL 以 // 开头，补充协议
        if (url && url.startsWith('//')) {
          url = window.location.protocol + url;
        }
        // 从URL中提取文件名
        const fileName = url.split('/').pop() || '付款凭证.jpg';
        this.cashierDialog.fileList = [{
          name: fileName,
          url: url,
          uid: Date.now()
        }];
      } else {
        this.cashierDialog.fileList = [];
      }

      this.cashierDialog.visible = true;
    },

    handleUploadExceed() {
      this.$message.warning('凭证最多上传一张');
    },
    beforeVoucherUpload(file) {
      const isImage = ['image/jpeg', 'image/png'].includes(file.type);
      if (!isImage) {
        this.$message.error('仅支持 JPG/PNG 格式的凭证');
        return false;
      }
      const isLt5M = file.size / 1024 / 1024 <= 5;
      if (!isLt5M) {
        this.$message.error('单张凭证大小不能超过 5MB');
        return false;
      }
      if (this.cashierDialog.fileList.length >= 1) {
        this.$message.warning('凭证最多上传一张');
        return false;
      }
      return true;
    },
    onUploadSuccess(response, file) {
      if (response && response.code === 200) {
        let url = response.msg || '';
        // 确保 URL 是完整的，如果是相对路径则拼接基础 URL
        if (url && !url.startsWith('http://') && !url.startsWith('https://') && !url.startsWith('//')) {
          // 如果是相对路径，拼接基础 API 地址
          const baseApi = process.env.VUE_APP_BASE_API || '';
          url = url.startsWith('/') ? `${baseApi}${url}` : `${baseApi}/${url}`;
        }
        // 如果 URL 以 // 开头，补充协议
        if (url && url.startsWith('//')) {
          url = window.location.protocol + url;
        }
        const name = file.name;
        this.cashierDialog.fileList = [{
          name,
          url,
          uid: file.uid,
          response
        }];
        this.$message.success('上传成功');
        // 验证图片 URL 是否可访问
        this.$nextTick(() => {
          this.validateImageUrl(url);
        });
      } else {
        this.$message.error(response?.msg || '上传失败，请稍后重试');
      }
    },
    handleImageError() {
      console.error('图片加载失败，URL:', this.cashierDialog.fileList[0]?.url);
      this.$message.warning('图片加载失败，请检查图片地址是否正确');
    },
    validateImageUrl(url) {
      if (!url) return;
      const img = new Image();
      img.onload = () => {
        console.log('图片验证成功:', url);
      };
      img.onerror = () => {
        console.error('图片验证失败，URL 不可访问:', url);
        this.$message.warning('图片地址可能无法访问，请检查网络或联系管理员');
      };
      img.src = url;
    },
    onUploadError() {
      this.$message.error('上传失败，请稍后重试');
    },
    onUploadRemove(file, fileList) {
      this.cashierDialog.fileList = Array.isArray(fileList) ? fileList : [];
    },
    resetCashierUpload() {
      if (this.$refs.cashierUpload && this.$refs.cashierUpload.clearFiles) {
        this.$refs.cashierUpload.clearFiles();
      }
      this.cashierDialog.fileList = [];
    },
    removeCashierVoucher() {
      this.resetCashierUpload();
    },
    submitCashier() {
      if (!this.cashierDialog.fileList.length) {
        this.$message.warning('请先上传凭证');
        return;
      }
      getPaymentTodayUpdateApi({
        id: this.cashierDialog.planId,
        payVoucherUrls: this.cashierDialog.fileList[0].url
      }).then(res => {
        if (res && res.code === 200) {
          this.cashierDialog.visible = false;
          this.handleQuery();
          this.$message.success("提交成功");
        }
      });
    },
    // 收付款账户确认弹窗 - 确认事件
    handleAccountConfirm(data) {
      // 调用API确认排款计划
      getPaymentTodayPlanApi({
        splitForms: data.splitForms
      }).then(res => {
        if (res && res.code === 200) {
          this.$message.success(res.msg || '排款计划确认成功');

          // 关闭收付款账户确认弹窗
          this.accountConfirmDialog.visible = false;
          // 刷新列表
          this.handleQuery();
        } else {
          this.$message.error(res?.msg || '排款计划确认失败');
        }
      }).catch(error => {
        console.error('确认排款计划失败', error);
        this.$message.error('确认排款计划失败，请稍后重试');
      });
    },
    // 收付款账户确认弹窗 - 取消事件
    handleAccountCancel() {
      // 可以在这里添加取消时的逻辑
    },
    // 收付款账户确认弹窗 - 关闭事件
    handleAccountClose() {
      // 可以在这里添加关闭时的逻辑
    },
    // 付款主体切换处理
    handlePayCompanyChange({ index, payCompanyId, item }) {
      // 可以在这里处理付款主体切换的逻辑
      // 比如加载对应的银行账户列表等
    },
    // 付款主体远程搜索
    handlePayCompanyRemote(keyword) {
      this.handleSubjectRemote(keyword);
    },
    // 付款主体下拉框显示/隐藏
    handlePayCompanyVisible(visible) {
      if (visible && !this.subjects.length) {
        this.fetchSubjects();
      }
    },
    // 复制文本方法
    copyText: createCopyTextMethod(this),
    onExportTable() {
      this.$message.success('导出表格（示例）');
    },
    async onExportAging() {
      const params = {
        billType: 1
      };

      this.exportLoading = true;
      try {
        const res = await exportPaymentTodayDetailApi(params);
        const isBlob = blobValidate(res);
        if (isBlob) {
          const blob = new Blob([res]);
          const supplierName = this.selectedSupplierName || '入仓付款详情';
          const now = new Date();
          const timestamp =
            now.getFullYear() +
            String(now.getMonth() + 1).padStart(2, '0') +
            String(now.getDate()).padStart(2, '0') +
            '_' +
            String(now.getHours()).padStart(2, '0') +
            String(now.getMinutes()).padStart(2, '0')
          const fileName = `${supplierName}_${timestamp}.xlsx`;
          saveAs(blob, fileName);
          this.$message.success('导出成功');
        } else {
          const reader = new FileReader();
          reader.onload = (e) => {
            try {
              const result = JSON.parse(e.target.result || '{}');
              this.$message.error(result.msg || '导出失败');
            } catch (error) {
              this.$message.error('导出失败');
            }
          };
          reader.readAsText(res);
        }
      } catch (error) {
        console.error('导出付款详情失败', error);
        this.$message.error('导出失败，请稍后重试');
      } finally {
        this.exportLoading = false;
      }
    },

  }
}
</script>

<style scoped lang="scss">
.payment-today {
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 140px);
}

.search-section {
  margin-bottom: 20px;
  padding: 10px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 12px;
  border: 1px solid #e9ecef;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

.search-section::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409eff, #67c23a, #e6a23c);
}

.search-content {
  display: flex;
  align-items: flex-end;
}

.search-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  flex: 1;
  gap: 12px;
}

.search-row {
  display: flex;
  align-items: flex-end;
  width: 100%;
  gap: 12px;
}

.search-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-label {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
  min-width: fit-content;
  flex-shrink: 0;
}

.search-label::before {
  content: "";
  width: 4px;
  height: 4px;
  background: #409eff;
  border-radius: 50%;
}

.search-select {
  width: 220px;
}

.search-select :deep(.el-input__inner) {
  border-radius: 8px;
  font-size: 14px;
  padding: 12px 16px;
  transition: all 0.3s ease;
  border: 2px solid #e4e7ed;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.search-select :deep(.el-input__inner):focus {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
  background: #fafbfc;
}

.search-actions {
  display: flex;
  width: 200px;
  justify-content: space-between;
  align-items: center;
}

.toolbar-slot {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  min-height: 40px;
  gap: 12px;
}

.search-action-btn {
  height: 36px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
  width: 100px;
}

.search-action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.reset-btn {
  height: 36px;
  font-size: 14px;
  border-radius: 8px;
  color: #606266;
  border-color: #dcdfe6;
  transition: all 0.3s ease;
  width: 100px;
}

.reset-btn:hover {
  color: #409eff;
  border-color: #409eff;
  background: #f0f9ff;
}

.export-btn {
  height: 36px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  color: #606266;
  border-color: #dcdfe6;
  transition: all 0.3s ease;
  padding: 0 16px;
}

.export-btn:hover {
  color: #409eff;
  border-color: #409eff;
  background: #f0f9ff;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.empty-block {
  padding: 24px;
  color: #909399;
}

.plan-tabs {
  margin-top: 0;
}

.plan-tabs ::v-deep .el-tabs__header {
  margin-bottom: 12px;
}

.plan-alert {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 12px;
  background-color: #fdf6ec;
  border: 1px solid #faecd8;
  border-radius: 4px;
  color: #e6a23c;
  font-size: 14px;
}

.plan-alert i {
  margin-right: 8px;
  font-size: 16px;
}

.plan-table-wrapper {
  margin-bottom: 12px;
  position: relative;
}

.plan-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.plan-amount {
  color: #f56c6c;
  font-weight: 600;
  font-size: 14px;
}

.plan-actions {
  display: flex;
  gap: 8px;
}
/* 全局处理覆盖层，用于处理大量数据时给出明显反馈 */
.processing-overlay {
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.92);
  z-index: 9999;
  gap: 12px;
  font-size: 16px;
  color: #606266;
}
.processing-overlay .el-icon-loading {
  font-size: 28px;
  animation: rotating 1s linear infinite;
  color: #409eff;
}
.processing-text {
  font-size: 14px;
}

.table-section {
  background: #fff;
  padding: 24px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #f0f0f0;
  flex: 1;
  overflow: auto;
  display: flex;
  flex-direction: column;

  .el-table {
    border-radius: 6px;
    overflow: hidden;
    flex: 1;

    th {
      background-color: #fafafa;
      color: #303133;
      font-weight: 600;
      border-bottom: 1px solid #ebeef5;
    }

    td {
      border-bottom: 1px solid #f0f0f0;
    }

    .el-table__row:hover {
      background-color: #f5f7fa;
    }
  }
}

.summary-bar {
  margin-bottom: 8px;
  font-weight: 600;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.load-more-tip {
  text-align: center;
  padding: 12px 0;
  color: #909399;
  font-size: 14px;
}

.load-more-tip i {
  margin-right: 6px;
  animation: rotating 1s linear infinite;
}

.load-more-tip.no-more {
  color: #c0c4cc;
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}


.cashier-dialog ::v-deep .el-dialog__body {
  padding: 0 24px 24px;
}

.cashier-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cashier-title {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.cashier-title__main {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cashier-title__subtitle {
  font-size: 13px;
  color: #909399;
  font-weight: normal;
}

.cashier-info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.cashier-card {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #fafbff;
}

.cashier-card__label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.cashier-card__value {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
  word-break: break-all;
}

.cashier-card__value--amount {
  font-size: 20px;
  color: #f56c6c;
}

.cashier-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  background-color: #fff;
}

.cashier-meta__label {
  font-size: 13px;
  color: #909399;
}

.cashier-meta__value {
  flex: 1;
  font-weight: 500;
  color: #303133;
}

.cashier-copy-btn {
  color: #409eff;
  padding: 4px 8px;
  font-size: 13px;
}

.cashier-copy-btn:hover {
  color: #66b1ff;
}

.cashier-upload-card {
  border: none;
  border-radius: 8px;
  padding: 0;
  background-color: transparent;
}

.cashier-upload-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.cashier-upload-card__title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.cashier-upload-card__desc {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.cashier-upload {
  width: 100%;
  display: block;
  border: none;
}

.cashier-upload ::v-deep .el-upload-dragger {
  padding: 24px;
  width: 100%;
  border: none;
  background-color: #f9fafc;
  box-sizing: border-box;
}
.cashier-upload ::v-deep .el-upload{
  width: 100%;
  border: 1px dashed #dcdfe6;
}
.cashier-upload ::v-deep .el-upload__text em {
  color: #409EFF;
  font-style: normal;
}

.cashier-count {
  font-size: 12px;
  color: #909399;
}

.cashier-preview {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
  padding: 12px;
  border: 1px dashed #e4e7ed;
  border-radius: 6px;
  background-color: #fff;
}

.cashier-preview__img {
  width: 120px;
  height: 90px;
  border-radius: 4px;
  overflow: hidden;
  object-fit: cover;
}

.image-slot {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 12px;
}

.image-slot i {
  font-size: 24px;
  margin-bottom: 4px;
}

.cashier-preview__info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.cashier-preview__name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.cashier-preview__actions {
  display: flex;
  gap: 8px;
}

@media screen and (max-width: 1200px) {
  .cashier-info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
