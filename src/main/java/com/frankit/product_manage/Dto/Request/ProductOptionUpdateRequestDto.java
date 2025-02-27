package com.frankit.product_manage.Dto.Request;

import com.frankit.product_manage.entity.OptionType;
import lombok.Data;

import java.util.HashMap;

@Data
public class ProductOptionUpdateRequestDto {
    Long id;
    String name;
    Long price;
    OptionType type;
    Long productId;
    HashMap<Long,String> selectOptionValueMap;
}