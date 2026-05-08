package com.ruoyi.common.utils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.page.PageParam;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sql.SqlUtil;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 分页工具类
 *
 * @author ruoyi
 */
public class PageUtils extends PageHelper {
    /**
     * 设置请求分页数据
     */
    public static void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }


    public static void startPage(PageParamV2 pageParam) {
        if (Objects.nonNull(pageParam)) {
            String orderBy = pageParam.getOrderBy();
            if (StringUtils.isNotEmpty(orderBy)) {
                orderBy = SqlUtil.escapeOrderBySql(orderBy);
                PageHelper.startPage(pageParam.getPage(), pageParam.getSize(), orderBy).setReasonable(pageParam.getReasonable());
            } else {
                PageHelper.startPage(pageParam.getPage(), pageParam.getSize()).setReasonable(pageParam.getReasonable());
            }
        }
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage() {
        PageHelper.clearPage();
    }


    public static <T, R> PageBO<R> fromList(List<T> page, Function<List<T>, List<R>> function) {

        PageInfo<T> result = new PageInfo(page);
        PageBO pageBO = new PageBO((List)function.apply(result.getList()), result.getTotal());
        clearPage();
        return pageBO;
    }


    public static <T> PageBO<T> fromList(List<T> page) {

        PageInfo<T> result = new PageInfo(page);
        PageBO pageBO = new PageBO(result.getList(), result.getTotal());
        clearPage();
        return pageBO;
    }


}
