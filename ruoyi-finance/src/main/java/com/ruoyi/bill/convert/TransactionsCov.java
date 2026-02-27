package com.ruoyi.bill.convert;


import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.bill.domain.Transactions;
import com.ruoyi.bill.model.bo.TransactionsBO;
import com.ruoyi.bill.model.query.TransactionsQuery;
import com.ruoyi.bill.model.param.TransactionsParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TransactionsCov {

    TransactionsCov INSTANCE = Mappers.getMapper(TransactionsCov.class);


    List<TransactionsBO>  listToBO(List<Transactions>  list );

    TransactionsBO   toBO(Transactions  list );

    Transactions  queryToDomain(TransactionsQuery query);

    Transactions  paramToDomain(TransactionsParam param);

    Transactions toEntity(TransactionsParam param);

}

