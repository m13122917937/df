<template>
  <div class="login-page">
    <div class="login-inner">
      <div class="content-container">
      <!-- Left: Brand copy -->
      <div class="brand-side">
        <div class="brand-content">
          <div class="brand-header">
            <svg viewBox="0 0 36 36" width="36" height="36">
              <rect width="36" height="36" rx="8" fill="#1677FF"/>
              <path d="M10 18 L15 23 L26 12" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
            </svg>
            <span class="brand-name">無界电商</span>
          </div>

          <div class="brand-tagline">
            <h1>智能连接电商业务<br/>让管理更高效</h1>
          </div>

          <p class="brand-desc">
            为企业提供订单、商品、库存、数据分析<br/>与协同管理的一体化平台
          </p>

          <div class="capabilities">
            <div class="cap-item">
              <span class="cap-check">✓</span>
              <span>数据智能分析</span>
            </div>
            <div class="cap-item">
              <span class="cap-check">✓</span>
              <span>企业流程自动化</span>
            </div>
            <div class="cap-item">
              <span class="cap-check">✓</span>
              <span>多端协同管理</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Right: Login card -->
      <div class="login-side">
        <div class="login-card">
          <!-- Card header -->
          <div class="card-header">
            <svg viewBox="0 0 32 32" width="32" height="32">
              <rect width="32" height="32" rx="7" fill="#1677FF"/>
              <path d="M9 16 L13 20 L23 10" stroke="white" stroke-width="2.8" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
            </svg>
            <h3 class="card-title">無界电商</h3>
          </div>

          <div class="card-divider" />

          <!-- QR Login -->
          <div v-if="!isLogin" class="qr-section">
            <h2 class="section-title">微信扫码登录</h2>
            <p class="section-desc">请使用微信扫描下方二维码登录您的企业账号</p>
            <div class="qr-wrapper">
              <div v-if="!qrCodeUrl" class="qr-placeholder">
                <i class="el-icon-loading qr-spinner" />
                <p>正在生成二维码...</p>
              </div>
              <div v-else class="qr-display">
                <img :src="qrCodeUrl" alt="微信登录二维码">
                <div v-if="showRefreshOverlay" class="qr-overlay">
                  <i class="el-icon-refresh" />
                  <p>二维码已过期</p>
                  <el-button type="primary" size="small" round @click="refreshQRCode">刷新二维码</el-button>
                </div>
              </div>
            </div>
            <div class="qr-tip">
              <i class="el-icon-scan" /> 打开微信扫一扫登录
            </div>
          </div>

          <!-- Company Selection -->
          <div v-else class="company-section">
            <h2 class="section-title">选择企业</h2>
            <p class="section-desc">请选择您要登录的企业账号</p>
            <div v-if="companyVOList.length > 0 && !newUser" ref="companyList" class="company-list">
              <div v-if="companyVOList.length > 3 && showScrollHint" class="scroll-hint">
                <i class="el-icon-arrow-down" /> 滚动查看更多企业
              </div>
              <div
                v-for="company in companyVOList"
                :key="company.id"
                class="company-item"
                :class="{ loading: loadingCompanyId === company.id, disabled: company.grabStatus === 1 }"
                @click="handleCompanyClick(company)"
              >
                <div class="company-icon"><i class="el-icon-office-building" /></div>
                <div class="company-info">
                  <h3 class="company-name">{{ company.companyName }}</h3>
                  <span class="company-desc">点击进入企业工作台</span>
                </div>
                <div class="company-arrow"><i class="el-icon-arrow-right" /></div>
                <div v-if="loadingCompanyId === company.id" class="company-loading"><i class="el-icon-loading" /></div>
              </div>
            </div>
            <div v-else class="company-empty">
              <i class="el-icon-warning" />
              <p>暂无企业请联系管理员</p>
              <div class="admin-code"><img src="@/assets/wxadmincode.png" alt=""></div>
            </div>
          </div>

          <div class="card-footer">
            <a href="javascript:;">隐私政策</a>
            <span class="dot">·</span>
            <a href="javascript:;">服务协议</a>
          </div>
        </div>
      </div>
    </div>
    </div>

    <footer class="page-footer">
      &copy; 2024 無界电商. All rights reserved.
    </footer>
  </div>
</template>

<script>
import { checkLoginQrCode, getLoginToken, getLoginQrCode } from '@/api/login'

export default {
  name: 'LoginPage',
  data() {
    return {
      redirect: undefined,
      companyVOList: [],
      qrCodeUrl: '',
      uuid: '',
      isLogin: false,
      qrTimer: null,
      checkTimer: null,
      refreshTimer: null,
      qrStartTime: null,
      showRefreshOverlay: false,
      newUser: false,
      loadingCompanyId: null,
      showScrollHint: true
    }
  },
  created() { this.generateQRCode() },
  mounted() { this.$nextTick(() => this.addScrollListener()) },
  beforeDestroy() { this.clearTimers(); this.removeScrollListener() },
  methods: {
    generateQRCode() {
      this.showRefreshOverlay = false; this.qrCodeUrl = ''; this.uuid = ''; this.clearTimers()
      getLoginQrCode().then(res => {
        const { data } = res || {}
        this.qrCodeUrl = data.url; this.uuid = data.uuid; this.qrStartTime = Date.now()
        this.startQRStatusCheck(); this.startRefreshTimer()
      })
    },
    startQRStatusCheck() {
      this.checkTimer = setInterval(() => {
        checkLoginQrCode(this.uuid).then(res => {
          const { data } = res || {}
          const { isLogin = true, companyVOList = [], newUser = false } = data || {}
          this.newUser = newUser; this.isLogin = isLogin
          if (isLogin) { this.companyVOList = companyVOList; this.clearTimers() }
        })
      }, 2000)
      this.qrTimer = setTimeout(() => { this.clearTimers() }, 300000)
    },
    startRefreshTimer() {
      if (this.refreshTimer) clearTimeout(this.refreshTimer)
      this.refreshTimer = setTimeout(() => { if (!this.isLogin && this.qrCodeUrl) this.showRefreshOverlay = true }, 60000)
    },
    refreshQRCode() { this.showRefreshOverlay = false; this.generateQRCode() },
    handleCompanyClick(company) {
      if (company.grabStatus === 1) { this.$message.warning('该供应商已被禁枪'); return }
      this.getLoginToken(company)
    },
    getLoginToken(company) {
      const { id, owner } = company
      this.$store.commit('user/SET_OWNER', owner); this.loadingCompanyId = id
      getLoginToken(this.uuid, id).then(res => {
        const { data = '' } = res || {}
        this.$store.dispatch('user/setAccessToken', data)
        this.$store.dispatch('user/getInfo').then(() => { this.$router.push({ path: '/df' }) })
          .catch(() => { this.$message.error('获取用户信息失败，请重试') })
      }).catch(() => { this.$message.error('登录失败，请重试') })
        .finally(() => { this.loadingCompanyId = null })
    },
    clearTimers() {
      if (this.qrTimer) { clearTimeout(this.qrTimer); this.qrTimer = null }
      if (this.checkTimer) { clearInterval(this.checkTimer); this.checkTimer = null }
      if (this.refreshTimer) { clearTimeout(this.refreshTimer); this.refreshTimer = null }
    },
    addScrollListener() { const el = this.$refs.companyList; if (el) el.addEventListener('scroll', this.handleScroll) },
    removeScrollListener() { const el = this.$refs.companyList; if (el) el.removeEventListener('scroll', this.handleScroll) },
    handleScroll() { if (this.showScrollHint) this.showScrollHint = false }
  }
}
</script>

<style lang="scss" scoped>
$ease: cubic-bezier(0.16, 1, 0.3, 1);

.login-page {
  position: relative;
  width: 100%;
  height: 100vh;
  background:
    linear-gradient(rgba(0,0,0,0.55), rgba(0,0,0,0.55)),
    url('~@/assets/login-background.jpg') center center / cover no-repeat;
  display: flex;
  flex-direction: column;
}

.login-inner {
  display: flex;
  flex: 1;
  min-height: 0;
  width: 100%;
  justify-content: center;
}

.content-container {
  width: calc(100% - 96px);
  max-width: 1260px;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 80px;
}

// ===== Left Brand Side =====
.brand-side {
  flex: 0 0 52%;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 60px 0;
}

.brand-content {
  max-width: 520px;
  animation: fade-in 0.8s $ease both;

  .brand-header {
    display: flex;
    align-items: center;
    margin-bottom: 42px;

    svg { flex-shrink: 0; filter: drop-shadow(0 2px 10px rgba(37,99,255,0.45)); }

    .brand-name {
      margin-left: 14px;
      font-size: 22px;
      font-weight: 600;
      color: #fff;
      letter-spacing: 1px;
    }
  }

  .brand-tagline {
    margin-bottom: 20px;
    h1 {
      font-size: 42px;
      font-weight: 800;
      color: #fff;
      line-height: 1.35;
      margin: 0;
      letter-spacing: 0.3px;
    }
  }

  .brand-desc {
    font-size: 17px;
    line-height: 1.8;
    color: rgba(255, 255, 255, 0.55);
    margin: 0 0 32px 0;
  }

  .capabilities {
    display: flex;
    flex-direction: column;
    gap: 18px;
  }

  .cap-item {
    display: flex;
    align-items: center;
    font-size: 16px;
    color: rgba(255, 255, 255, 0.78);
  }

  .cap-check {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: rgba(52,199,89,0.18);
    color: #34C759;
    font-size: 12px;
    font-weight: 700;
    margin-right: 12px;
    flex-shrink: 0;
  }
}

// ===== Right Login Side =====
.login-side {
  flex: 0 0 48%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
}

.login-card {
  width: 420px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.15);
  padding: 36px 36px 28px;
  animation: card-enter 0.6s $ease 0.15s both;

  .card-header {
    display: flex;
    align-items: center;
    gap: 10px;
    svg { flex-shrink: 0; }
    .card-title { font-size: 18px; font-weight: 600; color: #1D1D1F; margin: 0; letter-spacing: 0.5px; }
  }

  .card-divider {
    height: 1px;
    background: #EBEDF0;
    margin: 22px 0;
  }
}

// QR Section
.qr-section {
  text-align: center;

  .section-title { font-size: 18px; font-weight: 600; color: #1D1D1F; margin: 0 0 6px 0; text-align: center; }
  .section-desc { font-size: 14px; color: #6E6E73; margin: 0 0 28px 0; text-align: center; line-height: 1.5; }

  .qr-wrapper {
    width: 200px; height: 200px; margin: 0 auto 24px; position: relative;
    background: #FAFBFC; border-radius: 16px; border: 1px solid #EBEDF0;
    display: flex; align-items: center; justify-content: center;
    transition: border-color 250ms $ease;
    &:hover { border-color: #D9DDE3; }
  }

  .qr-placeholder { text-align: center; .qr-spinner { font-size: 32px; color: #1677FF; display: block; margin-bottom: 10px; } p { font-size: 14px; color: #8E8E93; margin: 0; } }
  .qr-display { width: 100%; height: 100%; position: relative; border-radius: 15px; overflow: hidden; img { width: 100%; height: 100%; object-fit: cover; display: block; } }

  .qr-overlay {
    position: absolute; inset: 0; background: rgba(255,255,255,0.95);
    display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px; border-radius: 15px;
    i { font-size: 28px; color: #FF9500; } p { font-size: 14px; color: #6E6E73; margin: 0; }
  }

  .qr-tip {
    display: inline-flex; align-items: center; gap: 6px;
    font-size: 13px; color: #8E8E93; padding: 7px 18px; background: #F5F6FA; border-radius: 100px;
    i { font-size: 15px; color: #1677FF; }
  }
}

// Company Section
.company-section {
  .section-title { font-size: 18px; font-weight: 600; color: #1D1D1F; margin: 0 0 6px 0; text-align: center; }
  .section-desc { font-size: 14px; color: #6E6E73; margin: 0 0 24px 0; text-align: center; }

  .company-list { max-height: 340px; overflow-y: auto; padding-right: 4px;
    &::-webkit-scrollbar { width: 4px; } &::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.1); border-radius: 2px; }
  }

  .scroll-hint {
    display: flex; align-items: center; justify-content: center; gap: 6px; padding: 10px; margin-bottom: 12px;
    background: rgba(37,99,255,0.05); border-radius: 8px; font-size: 13px; color: #6E6E73;
    border: 1px dashed rgba(37,99,255,0.25); animation: bounce 2s infinite;
    i { font-size: 14px; color: #1677FF; }
  }

  .company-item {
    position: relative; display: flex; align-items: center; padding: 16px 18px; margin-bottom: 10px;
    background: #F5F6FA; border-radius: 12px; cursor: pointer; transition: all 200ms $ease; border: 2px solid transparent;
    &:hover { background: #EBF3FF; border-color: #1677FF; .company-arrow i { color: #1677FF; transform: translateX(3px); } }
    &:active { transform: scale(0.99); }
    &.loading { pointer-events: none; opacity: 0.7; }
    &.disabled {
      background: #FFF5F5; border-color: #FF3B30; cursor: not-allowed; opacity: 0.8;
      .company-icon { background: #FF3B30; } .company-name, .company-desc { color: #FF3B30; }
      .company-arrow i { color: #FF3B30; } &:hover { transform: none; border-color: #FF3B30; }
    }
    .company-icon { width: 42px; height: 42px; background: #1677FF; border-radius: 12px; display: flex; align-items: center; justify-content: center; margin-right: 14px; flex-shrink: 0; i { font-size: 20px; color: #fff; } }
    .company-info { flex: 1; min-width: 0; .company-name { font-size: 15px; font-weight: 600; color: #1D1D1F; margin: 0 0 3px 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; } .company-desc { font-size: 13px; color: #6E6E73; } }
    .company-arrow { margin-left: 12px; flex-shrink: 0; i { font-size: 15px; color: #C7C7CC; transition: all 200ms $ease; } }
    .company-loading { position: absolute; inset: 0; background: rgba(255,255,255,0.85); display: flex; align-items: center; justify-content: center; border-radius: 10px; i { font-size: 22px; color: #1677FF; } }
  }

  .company-empty { text-align: center; padding: 20px 0;
    i { font-size: 40px; color: #FF9500; margin-bottom: 12px; display: block; }
    p { font-size: 15px; color: #6E6E73; margin: 0 0 16px 0; }
    .admin-code { width: 240px; margin: 0 auto; border-radius: 10px; overflow: hidden; img { width: 100%; display: block; } }
  }
}

// Card Footer
.card-footer { margin-top: 24px; padding-top: 16px; text-align: center; font-size: 12px; color: #8E8E93;
  a { color: #8E8E93; text-decoration: none; transition: color 200ms $ease; &:hover { color: #1677FF; } }
  .dot { margin: 0 10px; color: #D9DDE3; }
}

// Page Footer
.page-footer {
  flex-shrink: 0;
  text-align: center;
  padding: 16px 24px;
  font-size: 12px;
  color: rgba(255,255,255,0.35);
}

@keyframes card-enter {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-4px); }
  60% { transform: translateY(-2px); }
}

@media (prefers-reduced-motion: reduce) {
  .login-card, .brand-content { animation: none; }
  .company-item, .company-arrow i, .qr-wrapper { transition: none; }
  .scroll-hint { animation: none; }
}

@media (max-width: 1024px) {
  .content-container { gap: 40px; }
  .brand-content { max-width: 100%; .brand-tagline h1 { font-size: 34px; } }
}

@media (max-width: 900px) {
  .brand-side { display: none; }
  .content-container { justify-content: center; width: calc(100% - 48px); }
  .login-side { flex: 0 0 100%; justify-content: center; padding: 40px 0; }
  .login-card { width: 100%; max-width: 420px; }
}
</style>
