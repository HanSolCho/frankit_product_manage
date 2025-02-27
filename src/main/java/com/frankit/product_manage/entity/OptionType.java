package com.frankit.product_manage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

public enum OptionType {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    INPUT, // 사용자 입력 타입
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    SELECT // 선택 타입
}
