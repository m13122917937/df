package com.ruoyi.biz.bill;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.bill.facade.IPayerFacade;
import com.ruoyi.bill.facade.ITransactionsFacade;
import com.ruoyi.bill.model.bo.PayerBO;
import com.ruoyi.bill.model.bo.TransactionsBO;
import com.ruoyi.bill.model.param.PayerParam;
import com.ruoyi.bill.model.param.TransactionsParam;
import com.ruoyi.bill.model.query.PayerQuery;
import com.ruoyi.bill.model.query.TransactionsQuery;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.web.form.bill.TransactionsForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 流水记录业务服务
 * 负责处理流水记录的增删改查及余额计算逻辑
 */
@Slf4j
@Component
public class TransactionsBizService {

    @Autowired
    private ITransactionsFacade transactionsFacade;

    @Autowired
    private IPayerFacade payerFacade;

    private static final String ASC_CREATED_AT = "transaction_date desc";
    private static final int CATEGORY_INCOME = 0;
    private static final int CATEGORY_EXPENSE = 1;

    /**
     * 添加流水记录并更新余额
     */
    @Transactional(rollbackFor = Exception.class)
    public TransactionsBO addTransactionAndUpdateBalance(TransactionsParam param, Long loginUser) {
        log.info("添加流水记录，账户ID: {}, 金额: {}, 类别: {}", param.getAccountId(), param.getAmount(), param.getCategory());

        validateTransactionParam(param);
        TransactionsBO transactionsBO = new TransactionsBO().setTransactionDate(param.getTransactionDate()).setAccountId(param.getAccountId());
        BigDecimal currentBalance = getBalanceBeforeTransaction(transactionsBO);
        BigDecimal newBalance = calculateNewBalance(currentBalance, param.getAmount(), param.getCategory());
        checkBalanceForExpense(param, newBalance, currentBalance);

        // 保存流水记录
        param.setBalanceAfter(newBalance).setCreatedAt(DateUtil.date()).setUpdatedAt(DateUtil.date());
        TransactionsBO savedTransaction = transactionsFacade.save(param);

        // 更新后续流水余额
        List<TransactionsBO> subsequentTransactions = getSubsequentTransactions(transactionsBO);
        log.info("后续流水记录：{}", JacksonUtil.toJson(subsequentTransactions));
        newBalance = updateSubsequentTransactionsBalance(subsequentTransactions, newBalance, null);

        // 更新银行卡余额
        updatePayerBalance(param.getAccountId(), newBalance, loginUser);
        log.info("流水记录添加成功，ID: {}, 原余额: {}, 新余额: {}", savedTransaction.getId(), currentBalance, newBalance);
        return savedTransaction;
    }

    /**
     * 更新流水记录
     */
//    @Transactional(rollbackFor = Exception.class)
    public void update(TransactionsForm transactionsForm, Long userId) {
        log.info("更新流水记录，ID: {}, 操作人: {}", transactionsForm.getId(), userId);

        TransactionsBO originalTransaction = getTransactionOrThrow(transactionsForm.getId());
        boolean needRecalculate = isRecalculateNeeded(originalTransaction, transactionsForm);

        // 更新流水记录基本信息
        TransactionsParam updateParam = buildUpdateParam(transactionsForm);
        updateTransaction(updateParam, transactionsForm.getId());

        log.info("流水记录更新成功，ID: {}", transactionsForm.getId());

        // 如需要则重新计算余额
        if (needRecalculate) {
            recalculateBalancesAfterUpdate(originalTransaction, updateParam, userId);
        }
    }

    /**
     * 删除流水记录并重新计算余额
     */
    @Transactional(rollbackFor = Exception.class)
    public void del(Long id, Long userId) {
        log.info("删除流水记录，ID: {}, 操作人: {}", id, userId);

        TransactionsBO deletedTransaction = getTransactionOrThrow(id);

        // 删除流水记录
        transactionsFacade.delete(new TransactionsQuery().setId(id));
        log.info("流水记录删除成功，ID: {}", id);

        // 重新计算后续流水的余额
        recalculateSubsequentTransactionsAfterDelete(deletedTransaction, userId);
    }

    // ==================== 私有方法 ====================

    /**
     * 验证流水记录参数
     */
    private void validateTransactionParam(TransactionsParam param) {
        if (param.getAccountId() == null) {
            throw new ServiceException("账户ID不能为空");
        }
        if (param.getAmount() == null || param.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("交易金额必须大于0");
        }
        if (param.getCategory() == null || (param.getCategory() != CATEGORY_INCOME && param.getCategory() != CATEGORY_EXPENSE)) {
            throw new ServiceException("交易类别必须为0（收入）或1（支出）");
        }
        if (param.getTransactionDate() == null) {
            param.setTransactionDate(DateUtil.date());
        }
    }

    /**
     * 计算交易后余额
     */
    private BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount, Integer category) {
        return CATEGORY_INCOME == category ?
                currentBalance.add(amount) :
                currentBalance.subtract(amount);
    }

    /**
     * 反向计算交易影响（用于删除操作）
     */
    private BigDecimal reverseTransactionImpact(BigDecimal currentBalance, BigDecimal amount, Integer category) {
        return CATEGORY_INCOME == category ?
                currentBalance.subtract(amount) :
                currentBalance.add(amount);
    }

    /**
     * 检查支出时余额是否充足
     */
    private void checkBalanceForExpense(TransactionsParam param, BigDecimal newBalance, BigDecimal currentBalance) {
        if (CATEGORY_EXPENSE == param.getCategory() && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("账户余额不足，当前余额: " + currentBalance + "，支出金额: " + param.getAmount());
        }
    }

    /**
     * 获取账户信息，不存在则抛出异常
     */
    private PayerBO getPayerOrThrow(Long accountId) {
        PayerBO payer = payerFacade.getOne(new PayerQuery().setId(accountId));
        if (payer == null) {
            throw new ServiceException("银行卡账户不存在，ID: " + accountId);
        }
        return payer;
    }

    /**
     * 获取流水记录，不存在则抛出异常
     */
    private TransactionsBO getTransactionOrThrow(Long id) {
        TransactionsBO transaction = transactionsFacade.getOne(new TransactionsQuery().setId(id));
        if (transaction == null) {
            throw new ServiceException("流水记录不存在，ID: " + id);
        }
        return transaction;
    }

    /**
     * 获取安全的余额值（null转0）
     */
    private BigDecimal getSafeBalance(BigDecimal balance) {
        return balance != null ? balance : BigDecimal.ZERO;
    }

    /**
     * 更新银行卡余额
     */
    private void updatePayerBalance(Long accountId, BigDecimal newBalance, Long loginUser) {
        PayerParam updateParam = new PayerParam()
                .setBalance(newBalance)
                .setUpdateTime(DateUtil.date())
                .setUpdateBy(loginUser);

        boolean updated = payerFacade.update(updateParam, new PayerQuery().setId(accountId));
        if (!updated) {
            throw new ServiceException("更新银行卡余额失败，账户ID: " + accountId);
        }
    }

    /**
     * 更新流水记录
     */
    private void updateTransaction(TransactionsParam updateParam, Long id) {
        boolean updated = transactionsFacade.update(updateParam, new TransactionsQuery().setId(id));
        if (!updated) {
            throw new ServiceException("更新流水记录失败，ID: " + id);
        }
    }

    /**
     * 构建更新参数
     */
    private TransactionsParam buildUpdateParam(TransactionsForm form) {
        return TransactionsParam.builder()
                .id(form.getId())
                .transactionDate(form.getTransactionDate())
                .category(form.getCategory())
                .subCategory(form.getSubCategory())
                .amount(form.getAmount())
                .remark(form.getRemark())
                .paymentMethod(form.getPaymentMethod())
                .counterparty(form.getCounterparty())
                .updatedAt(DateUtil.date())
                .build();
    }

    /**
     * 检查是否需要重新计算余额
     */
    private boolean isRecalculateNeeded(TransactionsBO original, TransactionsForm updated) {
        return !original.getAmount().equals(updated.getAmount()) ||
                !original.getCategory().equals(updated.getCategory()) ||
                !original.getAccountId().equals(updated.getAccountId()) ||
                !DateUtil.isSameDay(original.getTransactionDate(), updated.getTransactionDate());
    }

    /**
     * 删除后重新计算后续流水余额
     */
    private void recalculateSubsequentTransactionsAfterDelete(TransactionsBO deletedTransaction, Long userId) {
        List<TransactionsBO> subsequentTransactions = getSubsequentTransactions(deletedTransaction);

        if (!subsequentTransactions.isEmpty()) {
            BigDecimal previousBalance = getBalanceBeforeExcludingDeleted(deletedTransaction);
            BigDecimal finalBalance = updateSubsequentTransactionsBalance(subsequentTransactions, previousBalance, deletedTransaction.getId());
            updatePayerBalance(deletedTransaction.getAccountId(), finalBalance, userId);
            log.info("银行卡余额已更新，账户ID: {}, 新余额: {}", deletedTransaction.getAccountId(), finalBalance);
        } else {
            recalculateAccountBalanceFromTransactions(deletedTransaction.getAccountId(), userId);
        }
    }

    /**
     * 更新后重新计算余额
     */
    private void recalculateBalancesAfterUpdate(TransactionsBO originalTransaction, TransactionsParam updatedParam, Long userId) {
        log.info("重新计算流水余额，从流水ID: {}", originalTransaction.getId());

        TransactionsBO updatedTransaction = getTransactionOrThrow(updatedParam.getId());
        Long accountId = updatedTransaction.getAccountId();
        // 重置 originalTransaction 的值
        Date transactionDate = DateUtil.compare(originalTransaction.getTransactionDate(), updatedParam.getTransactionDate()) > 0 ? updatedParam.getTransactionDate() : originalTransaction.getTransactionDate();
        originalTransaction.setTransactionDate(transactionDate);

        BigDecimal previousBalance = getBalanceBeforeTransaction(originalTransaction);
        BigDecimal currentBalance = calculateNewBalance(previousBalance, updatedTransaction.getAmount(), updatedTransaction.getCategory().intValue());

        // 更新当前流水余额
        updateTransactionBalance(updatedTransaction.getId(), currentBalance);
        log.info("当前流水余额已更新，ID: {}, 新余额: {}", updatedTransaction.getId(), currentBalance);

        // 更新后续流水余额
        List<TransactionsBO> subsequentTransactions = getSubsequentTransactions(originalTransaction);
        log.info("当前流水余额历史记录，update: {}", JacksonUtil.toJson(subsequentTransactions));

        currentBalance = updateSubsequentTransactionsBalance(subsequentTransactions, null, null);

        // 更新银行卡余额
        updatePayerBalance(accountId, currentBalance, userId);
        log.info("余额重新计算完成，账户ID: {}, 最终余额: {}", accountId, currentBalance);
    }

    /**
     * 获取后续流水记录
     */
    private List<TransactionsBO> getSubsequentTransactions(TransactionsBO transaction) {
        return transactionsFacade.list(new TransactionsQuery().setAccountId(transaction.getAccountId()).setGtTransactionDate(transaction.getTransactionDate()), SortBy.of("transaction_date"));
    }

    /**
     * 获取流水记录之前的余额
     */
    private BigDecimal getBalanceBeforeTransaction(TransactionsBO transaction) {
        List<TransactionsBO> previousTransactions = transactionsFacade.list(new TransactionsQuery().setLimit(1).setAccountId(transaction.getAccountId()).setLtTransactionDate(transaction.getTransactionDate()), SortBy.of(ASC_CREATED_AT));
        log.info("获取流水记录之前的余额:{}", JacksonUtil.toJson(previousTransactions));

        if (!previousTransactions.isEmpty()) {
            return previousTransactions.get(0).getBalanceAfter();
        } else {
            PayerBO payer = getPayerOrThrow(transaction.getAccountId());
            return getSafeBalance(payer.getBalance());
        }
    }

    /**
     * 获取删除流水之前的余额（排除被删除流水的影响）
     */
    private BigDecimal getBalanceBeforeExcludingDeleted(TransactionsBO deletedTransaction) {
        List<TransactionsBO> previousTransactions = transactionsFacade.list(new TransactionsQuery().setLimit(1).setAccountId(deletedTransaction.getAccountId()).setLtTransactionDate(deletedTransaction.getTransactionDate()), SortBy.of(ASC_CREATED_AT));

        if (!previousTransactions.isEmpty()) {
            log.info("获取删除流水之前的余额:{}", JacksonUtil.toJson(previousTransactions.get(0)));
            return previousTransactions.get(0).getBalanceAfter();
        }

        // 没有前置流水时，反推初始余额
        return calculateInitialBalance(deletedTransaction.getAccountId());
    }

    /**
     * 计算账户初始余额
     */
    private BigDecimal calculateInitialBalance(Long accountId) {
        PayerBO payer = getPayerOrThrow(accountId);
        BigDecimal currentBalance = getSafeBalance(payer.getBalance());

        List<TransactionsBO> allTransactions = transactionsFacade.list(new TransactionsQuery().setAccountId(accountId), SortBy.of(ASC_CREATED_AT));

        // 反向计算初始余额
        BigDecimal initialBalance = currentBalance;
        for (TransactionsBO transaction : allTransactions) {
            initialBalance = reverseTransactionImpact(initialBalance, transaction.getAmount(), transaction.getCategory().intValue());
        }

        return initialBalance;
    }

    /**
     * 更新后续流水记录的余额
     */
    private BigDecimal updateSubsequentTransactionsBalance(List<TransactionsBO> subsequentTransactions, BigDecimal startBalance, Long excludeId) {
        BigDecimal currentBalance = startBalance;

        for (TransactionsBO transaction : subsequentTransactions) {
            if (excludeId != null && transaction.getId().equals(excludeId)) {
                continue;
            }
            if (Objects.isNull(currentBalance)) {
                currentBalance = getBalanceBeforeTransaction(transaction);
            }

            BigDecimal newBalance = calculateNewBalance(currentBalance, transaction.getAmount(), transaction.getCategory().intValue());
            updateTransactionBalance(transaction.getId(), newBalance);
            currentBalance = newBalance;

            log.debug("流水余额已更新，ID: {},历史记录金额:{}, 新余额: {}", transaction.getId(), transaction.getAmount(), newBalance);
        }

        return currentBalance;
    }

    /**
     * 更新单条流水记录的余额
     */
    private void updateTransactionBalance(Long transactionId, BigDecimal balance) {
        TransactionsParam updateParam = TransactionsParam.builder()
                .balanceAfter(balance)
                .updatedAt(DateUtil.date())
                .build();

        transactionsFacade.update(updateParam, new TransactionsQuery().setId(transactionId));
    }

    /**
     * 基于所有流水记录重新计算账户余额
     */
    private void recalculateAccountBalanceFromTransactions(Long accountId, Long userId) {
        List<TransactionsBO> allTransactions = transactionsFacade.list(
                new TransactionsQuery().setAccountId(accountId),
                SortBy.of(ASC_CREATED_AT)
        );

        BigDecimal calculatedBalance = BigDecimal.ZERO;
        for (TransactionsBO transaction : allTransactions) {
            calculatedBalance = calculateNewBalance(calculatedBalance, transaction.getAmount(), transaction.getCategory().intValue());
        }

        updatePayerBalance(accountId, calculatedBalance, userId);
    }
}
