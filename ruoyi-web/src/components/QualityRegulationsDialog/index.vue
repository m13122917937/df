<template>
  <el-dialog
    :visible.sync="visible"
    title=""
    width="1200px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :modal-append-to-body="false"
    class="quality-regulations-dialog"
    @close="handleClose"
  >
    <!-- 标题区域 -->
    <div class="dialog-header">
      <div class="header-icon">
        <i class="el-icon-warning-outline" />
      </div>
      <div class="header-title">平台商品质量管理条例(新版)</div>
    </div>

    <!-- 内容区域 -->
    <div class="dialog-content">
      <!-- 开头说明 -->
      <div class="intro-text">
        <p>尊敬的用户:</p>
        <p>为规范平台商品质量管理，特制定本条例。您的接单行为将被视为您接受本条例，请仔细阅读后再接单。</p>
      </div>

      <!-- 条例内容 -->
      <div class="regulations-content">
        <!-- 第一条：错发、漏发、夹带包裹卡 -->
        <div class="regulation-item">
          <div class="regulation-title">一、错发、漏发、夹带包裹卡:</div>
          <div class="regulation-desc">
            提供与订单要求不符的产品(品类、品牌、型号、数量等)或在包裹中夹带未经授权的材料(宣传页、好评卡、第三方平台信息等)的行为。
          </div>
          <div class="penalty-table">
            <table>
              <thead>
                <tr>
                  <th>累计违规次数</th>
                  <th>首次违规</th>
                  <th>第二次违规</th>
                  <th>第三次违规</th>
                  <th>第四次及之后违规</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>须承担违约金</td>
                  <td>200元</td>
                  <td>300元</td>
                  <td>400元</td>
                  <td>500元</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="additional-penalty">同时暂停一周接单权限。</div>
        </div>

        <!-- 第二条：定制机 -->
        <div class="regulation-item">
          <div class="regulation-title">二、定制机:</div>
          <div class="regulation-desc">
            如提供的手机被认定为定制机，则构成违规。
          </div>
          <div class="penalty-table">
            <table>
              <thead>
                <tr>
                  <th>违规类型</th>
                  <th>须承担违约金</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>定制机</td>
                  <td>2000元每台/或向消费者等第三方承担的赔偿或违约处罚(二者取高值)</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="additional-penalty">同时暂停两周接单权限,并冻结货款直至平台处罚评估期结束。</div>
        </div>

        <!-- 第三条：非新机、瑕疵机 -->
        <div class="regulation-item">
          <div class="regulation-title">三、非新机、瑕疵机:</div>
          <div class="regulation-desc">
            手机未处于原装密封包装状态、未激活、未绑定/锁定、非租赁、团购、演示、合约、SIM卡锁定或翻新机，或存在质量缺陷影响正常销售的。
          </div>
          <div class="penalty-table">
            <table>
              <thead>
                <tr>
                  <th>累计违规次数</th>
                  <th>首次违规</th>
                  <th>第二次违规</th>
                  <th>第三次违规</th>
                  <th>第四次违规</th>
                  <th>第五次及之后违规</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>须承担违约金</td>
                  <td>1万元</td>
                  <td>2万元</td>
                  <td>3万元</td>
                  <td>4万元</td>
                  <td>5万元</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="additional-penalty">同时暂停一个月接单权限,并冻结货款直至平台处罚评估期结束。</div>
        </div>

        <!-- 第四条：到付件、上传串码与实际发货串码不符 -->
        <div class="regulation-item">
          <div class="regulation-title">四、到付件、上传串码与实际发货串码不符:</div>
          <div class="regulation-desc">
            未确保包邮，或上传的串码与实际发货产品的串码不符的行为。
          </div>
          <div class="penalty-table">
            <table>
              <thead>
                <tr>
                  <th>累计违规次数</th>
                  <th>首次违规</th>
                  <th>第二次违规</th>
                  <th>第三次违规</th>
                  <th>第四次及之后违规</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>须承担违约金</td>
                  <td>200元</td>
                  <td>300元</td>
                  <td>400元</td>
                  <td>500元</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="additional-penalty">同时暂停一周接单权限。</div>
        </div>

        <!-- 第五条 -->
        <div class="regulation-item">
          <div class="regulation-title">五、</div>
          <div class="regulation-desc">
            如存在违规行为，买方有权选择适用《平台采购框架协议》或《平台商品质量管理条例(新版)》追究责任。
          </div>
        </div>

        <!-- 第六条 -->
        <div class="regulation-item">
          <div class="regulation-title">六、</div>
          <div class="regulation-desc">
            如违规导致买方承担对消费者/第三方的责任或赔偿，或承担高于本条例规定违约金的行政处罚，买方有权相应提高违约金。
          </div>
          <div class="general-message">
            请各位用户密切关注新规定,并严格遵守相关要求。如有任何疑问需要进一步了解,请随时与我们联系。谢谢您的支持和配合!
          </div>
        </div>

        <!-- 第七条：附则 -->
        <div class="regulation-item">
          <div class="regulation-title">七、附则:</div>
          <div class="regulation-desc">
            本条例于2023年8月17日首次生效,于2024年12月02日最新修订生效。
          </div>
        </div>
      </div>
    </div>

    <!-- 底部区域 -->
    <div class="dialog-footer">
      <div class="footer-left">
        <el-button type="danger" class="close-btn" @click="handleClose">关闭</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'QualityRegulationsDialog',
  data() {
    return {
      visible: false
    }
  },
  methods: {
    open() {
      this.visible = true
    },
    handleClose() {
      this.visible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.quality-regulations-dialog {
  ::v-deep .el-dialog {
    border-radius: 8px;
    overflow: hidden;
  }

  ::v-deep .el-dialog__header {
    display: none;
  }

  ::v-deep .el-dialog__body {
    padding: 0;
    background: #fff;
  }
}

.dialog-header {
  text-align: center;
  padding: 20px 24px 16px;
  border-bottom: 1px solid #e9ecef;

  .header-icon {
    margin-bottom: 12px;
    i {
      font-size: 24px;
      color: #ff6b35;
    }
  }

  .header-title {
    font-size: 20px;
    font-weight: 600;
    color: #212529;
  }
}

.dialog-content {
  padding: 20px 24px;
  max-height: 60vh;
  overflow-y: auto;

  .intro-text {
    margin-bottom: 24px;
    font-size: 14px;
    line-height: 1.6;
    color: #495057;

    p {
      margin-bottom: 8px;
    }
  }

  .regulations-content {
    .regulation-item {
      margin-bottom: 24px;

      .regulation-title {
        font-size: 16px;
        font-weight: 600;
        color: #212529;
        margin-bottom: 8px;
      }

      .regulation-desc {
        font-size: 14px;
        line-height: 1.6;
        color: #495057;
        margin-bottom: 12px;
      }

      .penalty-table {
        margin: 12px 0;

        table {
          width: 100%;
          border-collapse: collapse;
          border: 1px solid #dee2e6;

          th, td {
            border: 1px solid #dee2e6;
            padding: 8px 12px;
            text-align: center;
            font-size: 13px;
          }

          th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #495057;
          }

          td {
            color: #212529;
          }
        }
      }

      .additional-penalty {
        font-size: 14px;
        color: #dc3545;
        font-weight: 500;
        margin-top: 8px;
      }

      .general-message {
        font-size: 14px;
        line-height: 1.6;
        color: #495057;
        margin-top: 12px;
        padding: 12px;
        background-color: #f8f9fa;
        border-radius: 4px;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-top: 1px solid #e9ecef;
  background-color: #f8f9fa;

  .footer-left {
    .close-btn {
      width: 80px;
      height: 36px;
      font-size: 14px;
      font-weight: 500;
    }
  }

  .footer-right {
    text-align: right;

    .company-name {
      font-size: 14px;
      font-weight: 600;
      color: #212529;
      margin-bottom: 4px;
    }

    .date {
      font-size: 12px;
      color: #6c757d;
    }
  }
}
</style>
