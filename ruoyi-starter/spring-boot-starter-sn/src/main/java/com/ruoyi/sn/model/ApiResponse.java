package com.ruoyi.sn.model;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code;
    private T data;
}