package com.frankit.product_manage.Dto.Response;

import com.frankit.product_manage.entity.SelectOptionValue;
import lombok.Data;

@Data
public class SelectOptionValueSelectResponseDto {
    private Long id;
    private String name;

    public SelectOptionValueSelectResponseDto(SelectOptionValue value) {
        this.id = value.getId();
        this.name = value.getName();
    }
}
