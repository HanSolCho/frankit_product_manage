package com.frankit.product_manage.Dto.Response;

import lombok.Data;

import java.util.List;
@Data
public class ProductSelectPagingResponseDto {
    private List<ProductSelectResponseDto> products;
    private int number; // 현재 페이지 번호
    private int size;   // 페이지 크기

    public ProductSelectPagingResponseDto(List<ProductSelectResponseDto> products, int number, int size) {
        this.products = products;
        this.number = number;
        this.size = size;
    }

}
