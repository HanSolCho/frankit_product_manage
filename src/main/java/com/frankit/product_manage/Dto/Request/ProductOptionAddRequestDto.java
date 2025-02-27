package com.frankit.product_manage.Dto.Request;

import com.frankit.product_manage.entity.OptionType;
import lombok.Data;

@Data
public class ProductOptionAddRequestDto {
    String name;
    Long price;
    OptionType type;
    Long productId;
    SelectOptionValueAddRequestDto selectOptionValue;
}
