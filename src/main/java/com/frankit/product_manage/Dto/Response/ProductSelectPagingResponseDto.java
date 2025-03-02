package com.frankit.product_manage.Dto.Response;

import lombok.Data;

import java.util.List;
@Data
public class ProductSelectPagingResponseDto {
    private List<ProductSelectResponseDto> products;
    private int number;
    private int size;

    public ProductSelectPagingResponseDto(List<ProductSelectResponseDto> products, int number, int size) {
        this.products = products;
        this.number = number;
        this.size = size;
    }

}
