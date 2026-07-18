<!-- 发货规则弹窗组件 -->
<template>
  <el-dialog
    title="发货规则"
    :visible.sync="dialogVisible"
    width="800px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="shipping-rules-content">
      <!-- 警告信息 -->
      <div class="warning-message">
        <i class="el-icon-warning" />
        <span>请仔细阅读以下发货规则，违规操作将面临相应处罚</span>
      </div>

      <!-- 规则内容 -->
      <div class="rules-section">
        <h3>一、关于物流</h3>
        <p>订单成交后，尽快安排快递发货。当天成交当天必须发货，否则超时未发货，取消订单，并扣除50元保证金/单</p>
        <p>发货时注意物流单号不要上传错误，保证填写的准确性。填写错误则查询不到物流信息，可能会产生虚假物流扣罚</p>
        <p>发货时，收件人手机号需要与订单收件人手机号一致，否则查询不到物流信息</p>
        <p>若修改了收件人手机号，则需要在运单号后填写寄件人手机号后4位</p>
        <p><strong>注意：</strong>预约快递单号时，收件人手机号请填写11位手机号码，虚拟号码的分机号【XXXX】不用填写</p>
        <p>发货后，若物流无流转信息，转入到【异常订单】物流异常-物流无流转信息</p>
        <p>若物流单号填写错误, 22小时内可修改，超过22小时不可修改</p>
        <p>若24小时内无揽收信息，则判定为虚假物流, 扣除200元/单 ，并扣除此单50元保证金，自动退货追单</p>
        <p>每家供应商每月：发货后次日无责修改物流单号次数有限，若使用完之后，修改物流单号时会扣除50元保证金</p>
        <p>若物流轨迹，客户签收城市与订单收货信息城市不符，可能会被判定为虚假轨迹，扣除200元/单</p>
        <p>请每天关注异常订单列表，每日及时处理异常订单，避免超时处理产生不必要的扣罚</p>
        <p>发货时快递外包装禁止使用带有品牌或平台LOGO的材料,违者扣除200元/单</p>
        <p>发货时请确保快递方式必须为包邮方式，严禁使用到付的快递方式，如出现违规需按《平台商品质量管理条例(新版) 》承当违约责任</p>
      </div>

      <div class="rules-section">
        <h3>二、关于串码</h3>
        <p>串码和物流单号允许分开上传</p>
        <p>苹果新订单须在成交后2小时内上传串码，荣耀/小米等品牌新订单须在成交当日19点前上传串码</p>
        <p>系统会自动检测所传串码是否已激活，对提示已激活的请及时更换机器</p>
        <p><strong>注：</strong> 串码查验出结果之后，实物才发货，避免串码不通过发货后造成不必要的损失</p>
        <p>上传至系统的串码须与顾客收到的串码一致，串码不一致的订单，扣除200元/单</p>
      </div>

      <div class="rules-section">
        <h3>三、恶意虚假发货</h3>
        <h4>判定逻辑</h4>
        <p>当天发货后无流转信息，次日修改物流单号，揽收时间为次日揽收</p>
        <p><strong>举例：</strong>5月1日发货，到5月2日一直无物流信息，5月2日修改物流单号，新物流单号揽收时间为5月2日</p>
      </div>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button type="danger" @click="handleClose">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'ShippingRulesDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
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
  methods: {
    handleClose() {
      this.$emit('update:visible', false)
      this.$emit('close')
    }
  }
}
</script>

<style lang="scss" scoped>
.shipping-rules-content {
  max-height: 500px;
  overflow-y: auto;

  .warning-message {
    background: #fef0f0;
    border: 1px solid #fbc4c4;
    border-radius: 4px;
    padding: 12px 16px;
    margin-bottom: 20px;
    color: #f56c6c;
    display: flex;
    align-items: center;
    font-weight: 500;

    i {
      margin-right: 8px;
      font-size: 16px;
    }
  }

  .rules-section {
    margin-bottom: 24px;

    h3 {
      color: #2c3e50;
      font-size: 16px;
      font-weight: 600;
      margin: 0 0 12px 0;
      padding-bottom: 8px;
      border-bottom: 2px solid #e4e7ed;
    }

    h4 {
      color: #34495e;
      font-size: 14px;
      font-weight: 600;
      margin: 16px 0 8px 0;
    }

    p {
      margin: 8px 0;
      line-height: 1.6;
      color: #606266;
      font-size: 14px;

      &:first-child {
        margin-top: 0;
      }

      &:last-child {
        margin-bottom: 0;
      }

      strong {
        color: #2c3e50;
        font-weight: 600;
      }
    }
  }
}

.dialog-footer {
  text-align: center;
  padding: 20px 0 0 0;
  border-top: 1px solid #e4e7ed;
}
</style>
