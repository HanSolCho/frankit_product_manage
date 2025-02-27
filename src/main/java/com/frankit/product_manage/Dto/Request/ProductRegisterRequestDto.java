package com.frankit.product_manage.Dto.Request;

import lombok.Data;

@Data
public class ProductRegisterRequestDto {
    String name;
    String description;
    Long price;
    Long deliveryFee;
}
