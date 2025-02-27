package com.frankit.product_manage.Dto.Request;

import com.frankit.product_manage.entity.OptionType;
import lombok.Data;

@Data
public class ProductOptionRequestDto {
    Long id;
    String name;
    Long price;
    OptionType type;
    Long productId;
}
