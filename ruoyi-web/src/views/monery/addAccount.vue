<template>
  <div class="add-account">
    <!-- 页面头部 -->
    <div class="page-header-card">
      <div class="header-left">
        <div class="header-icon"><i class="el-icon-bank-card" /></div>
        <div>
          <div class="header-title">收款账户</div>
          <div class="header-desc">查看和管理所有收款账户信息</div>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" icon="el-icon-plus" @click="handleAddAccount">新增银行账户</el-button>
      </div>
    </div>

    <!-- 账户列表 -->
    <div class="account-list-section">
      <div v-loading="loading" class="account-list">
        <div
          v-for="account in accountList"
          :key="account.id"
          class="account-item"
        >
          <div class="account-header">
            <div class="account-name">
              <span class="name-text">{{ account.accountBankName || account.companyName || account.nickName || '-' }}</span>
              <el-tag
                v-if="account.valid === 1"
                type="success"
                size="small"
                class="account-tag"
              >启用</el-tag>
              <el-tag
                v-else
                type="info"
                size="small"
                class="account-tag"
              >禁用</el-tag>
              <el-tag
                v-if="account.defaulted === 1"
                type="warning"
                size="small"
                class="account-tag"
              >默认</el-tag>
            </div>
          </div>
          <div class="account-content">
            <div class="account-info-row">
              <div class="info-item">
                <span class="info-label">开户银行:</span>
                <span class="info-value">{{ account.accountBank || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">银行账号:</span>
                <span class="info-value">{{ account.bankAccount || '-' }}</span>
              </div>
            </div>
            <div class="account-info-row">
              <div class="info-item">
                <span class="info-label">企业别称:</span>
                <span class="info-value">{{ account.nickName || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">企业主体:</span>
                <span class="info-value">{{ account.companyName || '-' }}</span>
              </div>
            </div>
            <div class="account-info-row">
              <div class="info-item">
                <span class="info-label">注册地:</span>
                <span class="info-value">{{ (account.provinceName || '') + (account.cityName ? ' ' + account.cityName : '') || '-' }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-if="!loading && accountList.length === 0" class="empty-state">
          <i class="el-icon-document" />
          <p>暂无账户信息</p>
        </div>
      </div>
    </div>

    <!-- 新增银行账户对话框 -->
    <el-dialog
      title="新增银行账户"
      :visible.sync="dialogVisible"
      width="600px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="accountForm"
        :model="accountForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="开户银行" prop="accountBank">
          <el-input
            v-model="accountForm.accountBank"
            placeholder="请输入开户银行"
            clearable
          />
        </el-form-item>
        <el-form-item label="银行账号" prop="bankAccount">
          <el-input
            v-model="accountForm.bankAccount"
            placeholder="请输入银行账号"
            clearable
          />
        </el-form-item>
        <el-form-item label="注册省" prop="province">
          <el-select
            v-model="accountForm.province"
            placeholder="请选择注册省"
            style="width: 100%"
            clearable
            @change="handleProvinceChange"
          >
            <el-option
              v-for="item in provinceList"
              :key="item.id"
              :label="item.district"
              :value="item.districtId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="注册市" prop="city">
          <el-select
            v-model="accountForm.city"
            placeholder="请选择注册市"
            style="width: 100%"
            clearable
            :disabled="!accountForm.province"
          >
            <el-option
              v-for="item in cityList"
              :key="item.districtId"
              :label="item.district"
              :value="item.districtId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleDialogClose">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getBankAccountList, addBankAccount, getProvinceList, getCityListByProvince } from '@/api/monery'

export default {
  name: 'AddAccount',
  data() {
    return {
      loading: false,
      accountList: [],
      dialogVisible: false,
      saving: false,
      provinceList: [],
      cityList: [],
      accountForm: {
        accountBank: '',
        bankAccount: '',
        province: null,
        city: null
      },
      formRules: {
        accountBank: [
          { required: true, message: '请输入开户银行', trigger: 'blur' }
        ],
        bankAccount: [
          { required: true, message: '请输入银行账号', trigger: 'blur' }
        ],
        province: [
          { required: true, message: '请选择注册省', trigger: 'change' }
        ],
        city: [
          { required: true, message: '请选择注册市', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.loadAccountList()
    this.loadProvinceList()
  },
  methods: {
    async loadAccountList() {
      this.loading = true
      try {
        const res = await getBankAccountList()
        if (res.code === 200) {
          this.accountList = res.data || res.rows || []
        } else {
          this.$message.error(res.msg || '获取账户列表失败')
        }
      } catch (error) {
        console.error('获取账户列表失败:', error)
        this.$message.error('获取账户列表失败，请稍后重试')
        // 如果接口不存在，使用模拟数据
        this.accountList = this.getMockData()
      } finally {
        this.loading = false
      }
    },
    // 模拟数据（当接口不存在时使用）
    getMockData() {
      return [
        {
          id: 1,
          accountBank: '招商银行',
          accountBankName: '上海知子电子商务有限公司',
          bankAccount: '121937328410501',
          companyName: '上海知子电子商务有限公司',
          nickName: '知子电商',
          province: 310000,
          provinceName: '上海市',
          city: 310100,
          cityName: '上海市',
          defaulted: 1,
          valid: 1
        },
        {
          id: 2,
          accountBank: '招商银行',
          accountBankName: '上海尊实电子商务有限公司',
          bankAccount: '121936683110701',
          companyName: '上海尊实电子商务有限公司',
          nickName: '尊实电商',
          province: 310000,
          provinceName: '上海市',
          city: 310100,
          cityName: '上海市',
          defaulted: 0,
          valid: 1
        },
        {
          id: 3,
          accountBank: '招商银行',
          accountBankName: '上海能良电子科技有限公司',
          bankAccount: '121934221610901',
          companyName: '上海能良电子科技有限公司',
          nickName: '能良科技',
          province: 310000,
          provinceName: '上海市',
          city: 310100,
          cityName: '上海市',
          defaulted: 0,
          valid: 1
        }
      ]
    },
    // 打开新增对话框
    handleAddAccount() {
      this.dialogVisible = true
      this.resetForm()
    },
    // 关闭对话框
    handleDialogClose() {
      this.dialogVisible = false
      this.resetForm()
    },
    // 重置表单
    resetForm() {
      this.accountForm = {
        accountBank: '',
        bankAccount: '',
        province: null,
        city: null
      }
      this.cityList = []
      this.$nextTick(() => {
        if (this.$refs.accountForm) {
          this.$refs.accountForm.clearValidate()
        }
      })
    },
    // 加载省份列表
    async loadProvinceList() {
      try {
        const res = await getProvinceList()
        if (res.code === 200) {
          console.log('省份列表', res.data)
          this.provinceList = res.data || res.rows || []
        }
      } catch (error) {
        console.error('获取省份列表失败:', error)
      }
    },
    // 省份改变时加载城市列表
    async handleProvinceChange(districtId) {
      this.accountForm.city = null
      this.cityList = []
      if (!districtId) {
        return
      }
      try {
        const res = await getCityListByProvince(districtId)
        if (res.code === 200) {
          this.cityList = res.data || []
          console.log('城市列表', this.cityList)
        }
      } catch (error) {
        console.error('获取城市列表失败:', error)
        this.$message.warning('获取城市列表失败，请稍后重试')
      }
    },
    // 保存账户
    handleSave() {
      this.$refs.accountForm.validate(async(valid) => {
        if (!valid) {
          return false
        }
        this.saving = true
        try {
          // 构建符合API要求的数据结构
          const requestData = {
            accountBank: this.accountForm.accountBank,
            bankAccount: this.accountForm.bankAccount,
            province: this.accountForm.province,
            city: this.accountForm.city
          }
          const res = await addBankAccount(requestData)
          if (res.code === 200) {
            this.$message.success('新增银行账户成功')
            this.handleDialogClose()
            this.loadAccountList()
          } else {
            this.$message.error(res.msg || '新增银行账户失败')
          }
        } catch (error) {
          console.error('新增银行账户失败:', error)
          this.$message.error('新增银行账户失败，请稍后重试')
        } finally {
          this.saving = false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.add-account {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  background: #f5f5f5;

  .page-header-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 4px 16px 0 rgba(74, 127, 255, 0.07),
      0 1.5px 8px rgba(0, 0, 0, 0.03);
    padding: 20px;
    margin-bottom: 12px;
    flex-shrink: 0;

      .header-left {
      display: flex;
      align-items: center;

      .header-icon {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        background: linear-gradient(130deg, #63a1fd 0%, #5edfff 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        box-shadow: 0 2px 16px #abbfff33, 0 1px 1.5px #99e2ef22;
        flex-shrink: 0;

        i {
          font-size: 24px;
          color: #fff;
        }
      }

      .header-title {
        font-size: 20px;
        font-weight: bold;
        color: #2c365a;
        letter-spacing: 0.5px;
        margin-bottom: 4px;
      }

      .header-desc {
        color: #8c99b5;
        font-size: 12px;
        letter-spacing: 0.5px;
      }
    }

    .header-right {
      flex-shrink: 0;
    }
  }

  .account-list-section {
    flex: 1;
    background: white;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 16px;
    overflow-y: auto;
    min-height: 0;

    .account-list {
        .account-item {
        background: #fff;
        border: 1px solid #ebeef5;
        border-radius: 8px;
        padding: 16px;
        margin-bottom: 12px;
        transition: all 0.3s;

        &:hover {
          box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
          border-color: #c0c4cc;
        }

        .account-header {
          margin-bottom: 16px;
          padding-bottom: 12px;
          border-bottom: 1px solid #f0f0f0;

          .account-name {
            display: flex;
            align-items: center;
            gap: 8px;

            .name-text {
              font-size: 16px;
              font-weight: 600;
              color: #303133;
            }

            .account-tag {
              margin-left: 4px;
            }
          }
        }

          .account-content {
          .account-info-row {
            display: flex;
            flex-direction: column;
            margin-bottom: 8px;

            &:last-child {
              margin-bottom: 0;
            }

            .info-item {
              flex: 1;
              display: flex;
              align-items: center;
              flex-direction: row;
              margin-bottom: 8px;

              &:last-child {
                margin-bottom: 0;
              }

              .info-label {
                font-size: 12px;
                color: #909399;
                margin-right: 8px;
                flex-shrink: 0;
              }

              .info-value {
                font-size: 13px;
                color: #606266;
                font-weight: 500;
                word-break: break-all;
              }
            }
          }
        }
      }

      .empty-state {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 60px 20px;
        color: #909399;

        i {
          font-size: 48px;
          margin-bottom: 16px;
          color: #c0c4cc;
        }

        p {
          font-size: 14px;
          margin: 0;
        }
      }
    }
  }
}
</style>

