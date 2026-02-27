package com.ruoyi.common.model.page;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@Accessors(chain = true)
public class PageBO<T> implements Serializable {
    private List<T> data;

    private Long total;

    public PageBO() {
    }

    public PageBO(final List<T> data, final Long total) {
        this.data = data;
        this.total = total;
    }

    public static <T> PageBO<T> empty() {
        return new PageBO<>(Collections.emptyList(), 0L);
    }

    public boolean isEmpty() {
        return CollUtil.isEmpty(data);
    }
    
}
