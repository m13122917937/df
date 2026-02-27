package com.ruoyi.common.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 排序.
 *
 * @date 2021-9-23 10:59:37
 */
@Data
public class SortBy implements Serializable {

    public static final SortBy NON_SORT = new SortBy();

    public static final String SPACE = " ";

    public static final String ASC = "asc";

    public static final String DESC = "desc";

    public static final String COMMA = ",";

    public static final String REDUCE = "-";

    public static final String PLUS = "+";

    private static final long serialVersionUID = 1L;

    private List<Field> orders = new ArrayList<>();

    public boolean isEmpty() {
        return this.orders.isEmpty();
    }

    /**
     * 加入排序字段.
     *
     * @param direction 排序方向
     * @param property  字段
     * @return SortInfo
     */
    public SortBy addField(final Direction direction, final String property) {
        Field field = new Field(direction, property);
        if (!orders.contains(field)) {
            this.orders.add(field);
        }
        return this;
    }

    /**
     * 获取方向字段.
     *
     * @param direction 排序方向
     * @return List
     */
    public List<String> get(final Direction direction) {
        return orders.stream().filter(o -> o.direction == direction).map(Field::getProperty).collect(Collectors.toList());
    }

    /**
     * 转换排序模型.
     *
     * @param orderBy orderBy
     * @return 结果
     */
    public static SortBy of(final String orderBy) {
        SortBy sort = new SortBy();
        // pagehelper写法 eg: update_time,id asc
        if (orderBy.contains(ASC) || orderBy.contains(DESC)) {
            // 排序
            Direction direction = orderBy.contains(ASC) ? Direction.ASC : Direction.DESC;
            int delimiterIndex = orderBy.lastIndexOf(SPACE);
            if (delimiterIndex != -1) {
                // 获取排序字段
                String substring = orderBy.substring(0, delimiterIndex);
                List<String> split = StrUtil.split(substring, StrUtil.COMMA);
                if (CollUtil.isEmpty(split)) {
                    return sort;
                }
                split.stream().map(StrUtil::trim).forEach(s -> sort.addField(direction, s));
            }
        }
        // 后端写法 排序:+为正序[asc], -为反序[desc] eg：-update_time,+id,-order_by
        if (orderBy.contains(REDUCE) || orderBy.contains(PLUS)) {
            String[] sorts = orderBy.split(COMMA);
            Stream.of(sorts).map(StrUtil::trim).forEach(s -> {
                if (s.startsWith(REDUCE)) {
                    sort.addField(Direction.DESC, s.substring(1));
                } else {
                    if (s.startsWith(PLUS)) {
                        s = s.substring(1);
                    }
                    sort.addField(Direction.ASC, s);
                }
            });
        }
        return sort;
    }

    /**
     * 字段定义类.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(of = {"direction", "property"})
    public static class Field implements Serializable {

        private static final long serialVersionUID = 6803755973873209116L;

        private Direction direction;

        private String property;

    }

    /**
     * 排序方向.
     */
    public enum Direction {
        ASC, DESC;
    }


    public static void main(String[] args) {
        SortBy sortBy = SortBy.of("handle_apply ,last_shipping_time desc");
        System.out.println( sortBy );
    }
}
