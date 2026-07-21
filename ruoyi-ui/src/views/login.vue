<template>
  <div class="login">
    <div class="login-form">
      <template v-if="!passwordLoginVisible">
        <h3 class="title">{{ title }}</h3>
        <p class="login-subtitle">请使用企业微信扫码登录</p>
        <div v-loading="qrLoading" class="wecom-qr-wrap">
          <iframe v-if="wecomAuthorizationUrl" :src="wecomAuthorizationUrl" title="企业微信扫码登录" frameborder="0" />
          <span v-else class="qr-error">{{ qrError || '正在加载扫码登录…' }}</span>
        </div>
        <el-button class="refresh-qr" type="text" icon="el-icon-refresh" @click="loadWeComQr">刷新二维码</el-button>
      </template>

      <template v-else>
        <div class="password-login-title">
          <h3 class="title">账号密码登录</h3>
          <el-button type="text" @click="showWeComLogin">返回扫码登录</el-button>
        </div>
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" type="text" autocomplete="username" placeholder="手机号码">
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" autocomplete="current-password" placeholder="密码" @keyup.enter.native="handleLogin">
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button :loading="loading" size="medium" type="primary" class="password-login-button" @click.native.prevent="handleLogin">
              {{ loading ? '登录中…' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>
      </template>
    </div>
    <div class="el-login-footer"><span>Copyright © 2018-2025 fengyang All Rights Reserved.</span></div>
  </div>
</template>

<script>
import { exchangeWeComLoginTicket, getWeComLoginQr } from "@/api/login"
import { setToken } from "@/utils/auth"

const SECRET_KEYS = ["ArrowLeft", "ArrowRight", "ArrowLeft", "ArrowRight", "ArrowLeft", "ArrowRight"]
const SECRET_TIMEOUT = 2000

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      wecomAuthorizationUrl: "",
      qrLoading: false,
      qrError: "",
      passwordLoginVisible: false,
      secretKeyIndex: 0,
      secretKeyTimer: null,
      loading: false,
      loginForm: { username: "", password: "", code: "", uuid: "" },
      loginRules: {
        username: [{ required: true, trigger: "blur", message: "请输入手机号码" }],
        password: [{ required: true, trigger: "blur", message: "请输入密码" }],
      },
      redirect: "/",
    }
  },
  created() {
    this.loadWeComQr()
    this.exchangeWeComTicket()
  },
  mounted() {
    window.addEventListener("keydown", this.handleSecretKeys)
  },
  beforeDestroy() {
    window.removeEventListener("keydown", this.handleSecretKeys)
    clearTimeout(this.secretKeyTimer)
  },
  methods: {
    loadWeComQr() {
      this.qrLoading = true
      this.qrError = ""
      getWeComLoginQr().then(res => {
        this.wecomAuthorizationUrl = res.authorizationUrl
      }).catch(() => {
        this.qrError = "企业微信扫码登录暂不可用"
      }).finally(() => {
        this.qrLoading = false
      })
    },
    exchangeWeComTicket() {
      const ticket = this.$route.query.wecomTicket
      if (!ticket) {
        return
      }
      this.qrLoading = true
      exchangeWeComLoginTicket(ticket).then(res => {
        setToken(res.token)
        this.$store.commit("SET_TOKEN", res.token)
        this.$router.replace({ path: this.redirect || "/" })
      }).catch(() => {
        this.qrError = "企业微信登录已失效，请重新扫码"
      }).finally(() => {
        this.qrLoading = false
      })
    },
    handleSecretKeys(event) {
      if (event.key !== SECRET_KEYS[this.secretKeyIndex]) {
        this.resetSecretKeys()
        return
      }
      this.secretKeyIndex += 1
      clearTimeout(this.secretKeyTimer)
      if (this.secretKeyIndex === SECRET_KEYS.length) {
        this.passwordLoginVisible = true
        this.resetSecretKeys()
        return
      }
      this.secretKeyTimer = setTimeout(this.resetSecretKeys, SECRET_TIMEOUT)
    },
    resetSecretKeys() {
      this.secretKeyIndex = 0
      clearTimeout(this.secretKeyTimer)
      this.secretKeyTimer = null
    },
    showWeComLogin() {
      this.passwordLoginVisible = false
      this.resetSecretKeys()
      this.loadWeComQr()
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) {
          return
        }
        this.loading = true
        this.$store.dispatch("Login", this.loginForm).then(() => {
          this.$router.push({ path: this.redirect || "/" }).catch(() => {})
        }).catch(() => {
          this.loading = false
        })
      })
    },
  },
}
</script>

<style rel="stylesheet/scss" lang="scss">
.login { display: flex; justify-content: center; align-items: center; height: 100%; background-image: url("../assets/images/login-background.jpg"); background-size: cover; background-position: center; }
.login-form { width: 400px; padding: 30px; border-radius: 8px; background: #fff; text-align: center; z-index: 1; .el-input { height: 40px; input { height: 40px; } } }
.title { margin: 0 0 10px; color: #303133; font-size: 22px; }
.login-subtitle { margin: 0 0 20px; color: #909399; font-size: 14px; }
.wecom-qr-wrap { display: flex; align-items: center; justify-content: center; width: 300px; height: 370px; margin: 0 auto; overflow: hidden; }
.wecom-qr-wrap iframe { width: 300px; height: 370px; }
.qr-error { color: #909399; }
.refresh-qr { margin-top: 8px; }
.password-login-title { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; .title { margin: 0; } }
.password-login-button { width: 100%; }
.input-icon { height: 40px; width: 14px; margin-left: 2px; }
.el-login-footer { position: fixed; bottom: 0; width: 100%; height: 40px; line-height: 40px; color: #fff; text-align: center; font-family: Arial; font-size: 12px; letter-spacing: 1px; }
[data-theme="dark"] .login { background-color: #0f1117; background-image: radial-gradient(circle at 18% 20%, rgba(var(--primary-rgb), 0.24), transparent 34%), radial-gradient(circle at 82% 76%, rgba(91, 124, 250, 0.16), transparent 32%), linear-gradient(135deg, #0f1117 0%, #151927 100%); }
[data-theme="dark"] .login-form { background: rgba(26, 29, 39, .96); border: 1px solid #303644; box-shadow: 0 24px 64px rgba(0,0,0,.42); .title { color: #e2e8f0; } .login-subtitle, .qr-error { color: #94a3b8; } .el-input__inner { background: #11151f; border-color: #343b4c; color: #e2e8f0; } }
</style>
