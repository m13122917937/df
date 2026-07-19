package com.ruoyi.web.controller.bill;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.bill.model.param.TransactionsParam;
import com.ruoyi.biz.bill.TransactionsBizService;
import com.ruoyi.bill.model.bo.TransactionsBO;
import com.ruoyi.bill.model.query.TransactionsQuery;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import com.ruoyi.mapper.bill.TransactionsConvert;
import com.ruoyi.web.form.bill.TransactionsForm;
import com.ruoyi.web.form.bill.TransactionsQForm;
import com.ruoyi.web.vo.bill.TransactionsVO;
import com.ruoyi.web.vo.order.OrderListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController

@RequestMapping("/bill/transaction")
public class TransactionsController extends BaseController {

    @Autowired
    private TransactionsBizService transactionsBizService;




    @PostMapping("/page/list")
    public TableDataInfo pageList(@RequestBody @Validated TransactionsQForm transactionsQForm) {

        TransactionsQuery query = new TransactionsQuery().setAccountId(transactionsQForm.getAccountId()).setCounterpartyLike(transactionsQForm.getCounterpartyLike())
                .setTransactionDateStart(transactionsQForm.getTransactionDateStart()).setTransactionDateEnd(transactionsQForm.getTransactionDateEnd());

        PageBO<TransactionsBO> transactionsBOPageBO = transactionsBizService.listPage(
                query, startParamV2("transaction_date desc"));
        List<TransactionsVO> transactionsVOList = TransactionsConvert.INSTANCE.toVOList(transactionsBOPageBO.getData());

        return getDataTable(transactionsVOList, transactionsBOPageBO.getTotal());
    }





    @PostMapping("/page/list/export")
    public void export(@RequestBody @Validated TransactionsQForm transactionsQForm) throws IOException {
        HttpServletResponse response = ServletUtils.getResponse();
        ServletUtils.renderExcel(response, "资金流水");

        TransactionsQuery query = new TransactionsQuery().setAccountId(transactionsQForm.getAccountId()).setCounterpartyLike(transactionsQForm.getCounterpartyLike())
                .setTransactionDateStart(transactionsQForm.getTransactionDateStart()).setTransactionDateEnd(transactionsQForm.getTransactionDateEnd());
        List<TransactionsBO> list = transactionsBizService.list(query, SortBy.of("transaction_date asc"));

        List<TransactionsVO> transactionsVOList = TransactionsConvert.INSTANCE.toVOList(list);
        EasyExcel.write(response.getOutputStream(), TransactionsVO.class).sheet("sheet1").doWrite(transactionsVOList);

    }




    @PostMapping("/save")
    public AjaxResult save(@Validated(value = AddGroup.class) @RequestBody TransactionsForm transactionsForm) {
        TransactionsParam param = TransactionsConvert.INSTANCE.toParam(transactionsForm);
        TransactionsBO savedTransaction = transactionsBizService.addTransactionAndUpdateBalance(param, getUserId());
        TransactionsVO resultVO = TransactionsConvert.INSTANCE.toVO(savedTransaction);
        return AjaxResult.success(resultVO);
    }





    @DeleteMapping("/del/{id}")
    public AjaxResult del(@PathVariable("id") Long id) {
        transactionsBizService.del(id, getUserId());
        return AjaxResult.success();
    }





    @PostMapping("/update")
    public AjaxResult update(@Validated(value = UpdateGroup.class) @RequestBody TransactionsForm transactionsForm) {
        transactionsBizService.update(transactionsForm, getUserId());
        return AjaxResult.success();
    }


}
