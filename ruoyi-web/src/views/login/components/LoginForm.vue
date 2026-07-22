<template>
  <div class="login-card">
    <!-- Header -->
    <div class="card-header">
      <div class="card-logo">
        <svg viewBox="0 0 32 32" width="32" height="32">
          <rect width="32" height="32" rx="7" fill="#1677FF"/>
          <path d="M9 16 L13 20 L23 10" stroke="white" stroke-width="2.8" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
        </svg>
      </div>
      <h3 class="card-title">無界电商</h3>
    </div>

    <!-- Welcome -->
    <div class="card-welcome">
      <h2>欢迎回来</h2>
      <p>登录您的企业账号</p>
    </div>

    <!-- QR Login -->
    <div v-if="!isLogin" class="qr-section">
      <div class="qr-wrapper">
        <div v-if="!qrCodeUrl" class="qr-placeholder">
          <i class="el-icon-loading qr-spinner" />
          <p>正在生成二维码...</p>
        </div>
        <div v-else class="qr-display">
          <img :src="qrCodeUrl" alt="微信登录二维码">
          <div v-if="showRefreshOverlay" class="qr-overlay">
            <div class="overlay-body">
              <i class="el-icon-refresh" />
              <p>二维码已过期</p>
              <el-button type="primary" size="small" round @click="$emit('refresh-qr-code')">刷新二维码</el-button>
            </div>
          </div>
        </div>
      </div>
      <div class="qr-tip">
        <i class="el-icon-scan" />
        <span>请使用微信扫码登录</span>
      </div>
    </div>

    <!-- Company Selection -->
    <div v-else class="company-section">
      <div class="company-header">
        <h3>选择企业</h3>
        <p>请选择您要登录的企业账号</p>
      </div>

      <div v-if="companyVOList.length > 0 && !newUser" ref="companyList" class="company-list">
        <div v-if="companyVOList.length > 3 && showScrollHint" class="scroll-hint">
          <i class="el-icon-arrow-down" />
          <span>滚动查看更多企业</span>
        </div>
        <div
          v-for="company in companyVOList"
          :key="company.id"
          class="company-item"
          :class="{ loading: loadingCompanyId === company.id, disabled: company.grabStatus === 1 }"
          @click="$emit('select-company', company)"
        >
          <div class="company-icon"><i class="el-icon-office-building" /></div>
          <div class="company-info">
            <h4 class="company-name">{{ company.companyName }}</h4>
            <span class="company-desc">点击进入企业工作台</span>
          </div>
          <div class="company-arrow"><i class="el-icon-arrow-right" /></div>
          <div v-if="loadingCompanyId === company.id" class="company-loading"><i class="el-icon-loading" /></div>
        </div>
      </div>

      <div v-else class="company-empty">
        <i class="el-icon-warning" />
        <p>暂无企业请联系管理员，注册账号</p>
        <div class="admin-code"><img src="@/assets/wxadmincode.png" alt=""></div>
      </div>
    </div>

    <!-- Footer -->
    <div class="card-footer">
      <a href="javascript:;">隐私政策</a>
      <span class="divider">|</span>
      <a href="javascript:;">服务协议</a>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginForm',
  props: {
    isLogin: { type: Boolean, default: false },
    qrCodeUrl: { type: String, default: '' },
    showRefreshOverlay: { type: Boolean, default: false },
    companyVOList: { type: Array, default: () => [] },
    loadingCompanyId: { type: [Number, null], default: null },
    newUser: { type: Boolean, default: false }
  },
  emits: ['refresh-qr-code', 'select-company'],
  data() {
    return { showScrollHint: true }
  },
  watch: {
    companyVOList() { this.$nextTick(() => this.addScrollListener()) }
  },
  beforeDestroy() { this.removeScrollListener() },
  methods: {
    addScrollListener() { const el = this.$refs.companyList; if (el) el.addEventListener('scroll', this.handleScroll) },
    removeScrollListener() { const el = this.$refs.companyList; if (el) el.removeEventListener('scroll', this.handleScroll) },
    handleScroll() { if (this.showScrollHint) this.showScrollHint = false }
  }
}
</script>

<style lang="scss" scoped>
$ease: cubic-bezier(0.16, 1, 0.3, 1);

.login-card {
  width: 420px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04), 0 8px 32px rgba(0,0,0,0.03);
  padding: 40px;
  animation: card-enter 0.7s $ease 0.1s both;
}

// ---------- Header ----------
.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 28px;

  .card-logo {
    flex-shrink: 0;
    margin-right: 12px;
    svg { display: block; }
  }

  .card-title {
    font-size: 18px;
    font-weight: 600;
    color: var(--text-primary, #1D1D1F);
    margin: 0;
  }
}

// ---------- Welcome ----------
.card-welcome {
  margin-bottom: 32px;

  h2 {
    font-size: 22px;
    font-weight: 600;
    color: var(--text-primary, #1D1D1F);
    margin: 0 0 6px 0;
  }

  p {
    font-size: 14px;
    color: var(--text-secondary, #6E6E73);
    margin: 0;
  }
}

// ---------- QR Section ----------
.qr-section {
  text-align: center;

  .qr-wrapper {
    width: 200px;
    height: 200px;
    margin: 0 auto 24px;
    position: relative;
    background: #FAFBFC;
    border-radius: 16px;
    border: 1px solid #EBEDF0;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: border-color 250ms $ease;

    &:hover { border-color: #D9DDE3; }
  }

  .qr-placeholder {
    text-align: center;
    .qr-spinner { font-size: 32px; color: #1677FF; display: block; margin-bottom: 10px; animation: spin 1s linear infinite; }
    p { font-size: 14px; color: var(--text-tertiary, #8E8E93); margin: 0; }
  }

  .qr-display {
    width: 100%;
    height: 100%;
    position: relative;
    border-radius: 15px;
    overflow: hidden;
    img { width: 100%; height: 100%; object-fit: cover; display: block; }
  }

  .qr-overlay {
    position: absolute;
    inset: 0;
    background: rgba(255,255,255,0.95);
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 15px;
    .overlay-body { text-align: center; i { font-size: 28px; color: #FF9500; display: block; margin-bottom: 8px; } p { font-size: 14px; color: var(--text-secondary, #6E6E73); margin: 0 0 14px 0; } }
  }

  .qr-tip {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: var(--text-tertiary, #8E8E93);
    padding: 7px 18px;
    background: #F5F6FA;
    border-radius: 100px;

    i { font-size: 15px; color: #1677FF; }
  }
}

// ---------- Company Section ----------
.company-section {
  .company-header {
    margin-bottom: 20px;
    text-align: center;
    h3 { font-size: 18px; font-weight: 600; color: var(--text-primary, #1D1D1F); margin: 0 0 4px 0; }
    p { font-size: 14px; color: var(--text-secondary, #6E6E73); margin: 0; }
  }

  .company-list {
    max-height: 340px;
    overflow-y: auto;
    padding-right: 4px;
    &::-webkit-scrollbar { width: 4px; }
    &::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.1); border-radius: 2px; }
  }

  .scroll-hint {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 10px;
    margin-bottom: 12px;
    background: rgba(37,99,255,0.05);
    border-radius: 8px;
    font-size: 13px;
    color: var(--text-secondary, #6E6E73);
    border: 1px dashed rgba(37,99,255,0.25);
    animation: bounce 2s infinite;
    i { font-size: 14px; color: #1677FF; }
  }

  .company-item {
    position: relative;
    display: flex;
    align-items: center;
    padding: 16px 18px;
    margin-bottom: 10px;
    background: #F5F6FA;
    border-radius: 12px;
    cursor: pointer;
    transition: all 200ms $ease;
    border: 2px solid transparent;

    &:hover {
      background: #EBF3FF;
      border-color: #1677FF;
      .company-arrow i { color: #1677FF; transform: translateX(3px); }
    }

    &:active { transform: scale(0.99); }
    &.loading { pointer-events: none; opacity: 0.7; }

    &.disabled {
      background: #FFF5F5; border-color: #FF3B30; cursor: not-allowed; opacity: 0.8;
      .company-icon { background: #FF3B30; }
      .company-name { color: #FF3B30; }
      .company-desc { color: #FF3B30; }
      .company-arrow i { color: #FF3B30; }
      &:hover { transform: none; border-color: #FF3B30; }
    }

    .company-icon {
      width: 42px; height: 42px; background: #1677FF; border-radius: 12px;
      display: flex; align-items: center; justify-content: center; margin-right: 14px; flex-shrink: 0;
      i { font-size: 20px; color: #fff; }
    }

    .company-info {
      flex: 1; min-width: 0;
      .company-name { font-size: 15px; font-weight: 600; color: var(--text-primary, #1D1D1F); margin: 0 0 3px 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
      .company-desc { font-size: 13px; color: var(--text-secondary, #6E6E73); }
    }

    .company-arrow { margin-left: 12px; flex-shrink: 0; i { font-size: 15px; color: #C7C7CC; transition: all 200ms $ease; } }

    .company-loading {
      position: absolute; inset: 0; background: rgba(255,255,255,0.85);
      display: flex; align-items: center; justify-content: center; border-radius: 10px;
      i { font-size: 22px; color: #1677FF; animation: spin 1s linear infinite; }
    }
  }

  .company-empty {
    text-align: center; padding: 20px 0;
    i { font-size: 40px; color: #FF9500; margin-bottom: 12px; display: block; }
    p { font-size: 15px; color: var(--text-secondary, #6E6E73); margin: 0 0 16px 0; }
    .admin-code { width: 240px; margin: 0 auto; border-radius: 10px; overflow: hidden; img { width: 100%; display: block; } }
  }
}

// ---------- Card Footer ----------
.card-footer {
  margin-top: 32px;
  padding-top: 20px;
  border-top: 1px solid #EBEDF0;
  text-align: center;
  font-size: 12px;
  color: var(--text-tertiary, #8E8E93);

  a {
    color: var(--text-tertiary, #8E8E93);
    text-decoration: none;
    transition: color 200ms $ease;
    &:hover { color: #1677FF; }
  }

  .divider { margin: 0 10px; color: #D9DDE3; }
}

@keyframes card-enter {
  from { opacity: 0; transform: translateY(24px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-4px); }
  60% { transform: translateY(-2px); }
}

@media (prefers-reduced-motion: reduce) {
  .login-card { animation: none; }
  .company-item { transition: none; }
  .company-arrow i { transition: none; }
  .scroll-hint { animation: none; }
  .qr-wrapper { transition: none; }
}
</style>
