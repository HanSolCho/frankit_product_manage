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
    private String name;
    private OptionType type;
    private Long price;
    private List<SelectOptionValueSelectResponseDto> selectOptionValueList;

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
