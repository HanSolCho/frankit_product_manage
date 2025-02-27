package com.frankit.product_manage.Dto.Response;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.frankit.product_manage.entity.OptionType;
import com.frankit.product_manage.entity.Product;
import com.frankit.product_manage.entity.ProductOption;
import com.frankit.product_manage.entity.SelectOptionValue;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductOptionSelectResponseDto {
    private Long id;
    private String name;  // 옵션 이름 (예: 색상, 사이즈)
    private OptionType type;  // 옵션 타입 (입력 타입 / 선택 타입) input,select
    private Long price;  // 옵션 추가 금액
    private List<SelectOptionValueSelectResponseDto> selectOptionValueList;  // 선택 타입일 경우, 선택 가능한 값 목록

    // 생성자
    public ProductOptionSelectResponseDto(ProductOption option) {
        this.id = option.getId();
        this.name = option.getName();
        this.type = option.getType();
        this.price = option.getPrice();
        this.selectOptionValueList = option.getSelectOptionValueList().stream()
                .map(SelectOptionValueSelectResponseDto::new)
                .collect(Collectors.toList());
    }
}
