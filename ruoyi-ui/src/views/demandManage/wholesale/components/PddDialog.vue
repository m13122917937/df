<template>
  <div class="">
  <el-dialog :title="title"
  :visible.sync="showDialog"
     :modal="true"
    :append-to-body="true"
  :close-on-click-modal="false" :destroy-on-close="true"
  custom-class="need-confirmReceipt-pdd-dialog" width="640px" @close="close">
    <div class="content">
      <el-tabs v-model="tab" @tab-click="handleTabClick">
        <el-tab-pane label="导出发货串码" name="1"></el-tab-pane>
        <el-tab-pane label="导入检查结果" name="2"></el-tab-pane>
      </el-tabs>
      <div v-show="tab === '1'">
        <div class="item">
          <div class="label">品牌</div>
          <el-select v-model="form.brand" class="select" popperClass="need-custom-select-dropdown-popper" multiple placeholder="请选择">
            <li :class="['el-select-dropdown__item need-select-item', { 'selected hover': allBrand }]" @click="allBrandChange"><span>全选</span></li>
            <el-option v-for="item in brandList" :key="item.value" class="need-select-item" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </div>
        <div class="item">
          <div class="label">品类</div>
          <el-select v-model="form.category" class="select" popperClass="need-custom-select-dropdown-popper" multiple placeholder="请选择">
            <li :class="['el-select-dropdown__item need-select-item', { 'selected hover': allCategory }]" @click="allCategoryChange"><span>全选</span></li>
            <el-option v-for="item in categoryList" :key="item.value" class="need-select-item" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </div>
      </div>
      <div v-show="tab == 2">
        <div class="uploadWrap">
          <el-upload ref="upload"
            accept=".xlsx"
            :headers="headers"
            :action="actionUrl"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError" class="upload">
            <svg-icon icon-class="icon-upload-cloud"></svg-icon>
            <el-button class="btnUpload">
              <svg-icon icon-class="icon-upload"></svg-icon>
              上传Excel
            </el-button>
            <p slot="tip" class="el-upload__tip">在模板中填写好串码检查结果</p>
          </el-upload>
          <ul class="uploadList">
            <li v-if="uploadExcel" :key="uploadExcel.fileName">
              <svg-icon icon-class="icon-upload-delete" @click="deleteExcel"></svg-icon>
              {{ uploadExcel.originalFilename }}
              <span class="status">上传完成</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <template slot="footer">
      <div class="footer" v-if="tab==1">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" :disabled="disabled" :loading="submitLoading" @click="submit">确定</el-button>
      </div>
    </template>
  </el-dialog>
</div>
</template>
<script>
import { exportError } from "@/api/creatingOrders"
import { formatDate } from "@/utils"
import { saveAs } from "file-saver";
import { blobValidate } from "@/utils/ruoyi";
import { getToken } from "@/utils/auth"

export default {
  name: "ConfirmReceiptPddDialog",
  components: {},
  data() {
    return {
      showDialog: false, // 是否显示
      title: "验证平台销售",
      submitLoading: false, // 提交loading
      tab: "1", // 当前tab
      uploadExcel: "", // 上传的excel文件
      form: {
        brand: [], //品牌列表
        category: [] //分类列表
      },
      brandList: [
        {
          value: "苹果",
          label: "苹果"
        },
        {
          value: "华为",
          label: "华为"
        },
        {
          value: "荣耀",
          label: "荣耀"
        },
        {
          value: "小米",
          label: "小米"
        },
        {
          value: "oppo",
          label: "oppo"
        },
        {
          value: "vivo",
          label: "vivo"
        },
        {
          value: "一加",
          label: "一加"
        },
         {
          value: "IQOO",
          label: "IQOO"
        },
      ],
      categoryList: [
        {
          value: "手机",
          label: "手机"
        },
        {
          value: "平板",
          label: "平板"
        },
        {
          value: "穿戴",
          label: "穿戴"
        },
        {
          value: "耳机",
          label: "耳机"
        }
      ]
    }
  },
  computed: {
    // 选中所有品牌或不选中任何品牌。状态
    allBrand() {
      return this.form.brand.length === this.brandList.length
    },
    // 选中所有分类或不选中任何分类。状态
    allCategory() {
      return this.form.category.length === this.categoryList.length
    },
    // 不能提交
    disabled() {
      if (this.tab === "1") {
        const { brand, category } = this.form
        return !(brand || []).length || !(category || []).length
      }
      if (this.tab === "2") {
        return !this.uploadExcel
      }
      return false
    },
    actionUrl() {
      return (process.env.VUE_APP_BASE_API || "") + "/order/error/import"
    },
    headers() {
      return {
        Authorization: "Bearer " + getToken(),
      }
    }
  },
  mounted() {},
  methods: {
    // 初始化打开弹窗
    init() {
      this.showDialog = true
    },
    // 重置数据
    reset() {
      this.form = {
        brand: [], //品牌列表
        category: [] //分类列表
      }
      this.uploadExcel = ""
      this.tab = "1"
    },
    // 删除上传的文件
    deleteExcel() {
      this.uploadExcel = ""
    },
    // 导出
    async exportExcel() {
      let date = formatDate(new Date(), "yyyyMMdd")
      const { brand: brandSet, category: categorySet } = this.form
      const options = { brandSet, categorySet }
      exportError(options).then((res) => {

        const isBlob = blobValidate(res);
        if (isBlob) {
          const blob = new Blob([res]);
          saveAs(blob, `发货串码${date}.xlsx`);
          this.close()
        }
      })
    },
    // tab 切换
    handleTabClick() {},
    // 全部品牌
    allBrandChange() {
      if (this.allBrand) {
        this.form.brand = []
      } else {
        this.form.brand = this.brandList.map(e => e.value)
      }
    },
    // 全部分类
    allCategoryChange() {
      if (this.allCategory) {
        this.form.category = []
      } else {
        this.form.category = this.categoryList.map(e => e.value)
      }
    },
    // 校验文件 上传文件
    beforeUpload(file) {
      const type = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/msexcel", "application/vnd.ms-excel"]
      const isType = type.includes(file.type)
      // const isLt100 =   qth > 100
      if (!isType) return this.$message.error("文件格式错误!")
      // if (isLt100) return this.$message.error("上传的文件名最长100个字符")
    },
    // 上传成功回调
    handleUploadSuccess(response, file) {
      this.$message.success("导入成功")
      this.uploadExcel = {
        fileName: file.name,
        originalFilename: file.name
      }
      // 关闭弹窗
      this.close()
      // 通知父组件刷新列表
      this.$emit("upload-success")
    },
    // 上传失败回调
    handleUploadError(err, file) {
      this.$message.error("上传失败，请重试")
      console.error("上传失败:", err)
    },
    //  上传单张
    async uploadFile(file) {
      // const form = new FormData()
      // form.append("file", file)
      // let res = await apiErrorImport(form)
      // console.log("res",res)
      // if (res[0]) {
      //   this.uploadExcel = res[1] || ""
      // }
    },
    // 弹窗关闭事件
    close() {
      this.showDialog = false
      this.reset()
    },
    // 提交事件
    submit() {
      if (this.tab === "1") {
        this.exportExcel()
      }

    }
  }
}
</script>
<style lang="scss" scoped>
::v-deep {
  .need-confirmReceipt-pdd-dialog {
    .content {
      .item {
        display: flex;
        align-items: stretch; //改变
        min-height: 36px;
        & .label {
          flex-shrink: 0;
          width: 74px;
          height: inherit;
          background: #f8f8f8;
          border: 1px solid #e6e6e6;
          border-right-width: 0;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 14px;
          color: #333333;
        }
        & .select {
          flex: 1;
          .el-input__inner {
            border-color: #e6e6e6;
            border-radius: 0;
          }
        }
      }
      .item + .item {
        margin-top: 12px;
      }
      .uploadWrap {
        border-radius: 4px;
        border: 1px solid #e6e6e6;
        .upload {
          background: #f7f7f7;
          padding: 36px 0 24px;
          p {
            margin-top: 12px;
            text-align: center;
            font-size: 14px;
          }
        }
        .el-upload {
          display: block;
          svg {
            width: 54px;
            height: 34px;
            color: #eb3935;
            display: block;
            margin: 0 auto;
          }
          .el-icon-upload {
            font-size: 80px;
            display: block;
            color: #eb3935;
          }
          p {
            margin-top: 12px;
          }
        }
        .btnUpload {
          margin-top: 12px;
          padding: 7px 12px;
          box-sizing: border-box;
          font-size: 14px;
          line-height: 20px;
          svg {
            width: 14px;
            height: 14px;
            display: inline-block;
            margin-right: 4px;
            color: #666;
          }
          &:hover,
          &:focus {
            svg {
              color: #eb3935;
            }
          }
        }
        .uploadList {
          margin: 0;
          padding: 0;
          > li {
            margin: 0;
            list-style: none;
            display: flex;
            align-items: center;
            padding: 16px;
            transition: all 0.5s linear;
            svg {
              width: 21px;
              height: 26px;
              cursor: pointer;
              margin-right: 12px;
            }
            .status {
              margin-left: auto;
            }
          }
        }
      }
      .tips {
        font-size: 12px;
        line-height: 1.5em;
        margin-top: 8px;
        color: #eb3935;
      }
      .el-tabs__header.is-top {
        margin-bottom: 24px;
      }
    }
    &.el-dialog .el-dialog__body {
      padding-top: 8px;
    }
  }
}
</style>
<style lang="scss">
.need-custom-select-dropdown-popper {
  .el-select-dropdown__item.hover {
    background-color: #fff;
  }
  &.el-select-dropdown.is-multiple {
    .need-select-item:hover,
    .need-select-item.selected {
      background-color: #f5f7fa;
    }
  }
}
</style>
