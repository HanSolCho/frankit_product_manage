package com.frankit.product_manage.Dto.Response;

import com.frankit.product_manage.entity.Product;
import com.frankit.product_manage.entity.ProductOption;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductSelectResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long price;
    private Long deliveryFee;
    private Date date;
    private List<ProductOptionSelectResponseDto> productOptionSelectList;

    public ProductSelectResponseDto(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.deliveryFee = product.getDeliveryFee();
        this.date = product.getDate();
        this.productOptionSelectList = product.getOptions().stream()
                .map(ProductOptionSelectResponseDto::new)
                .collect(Collectors.toList());
    }

}
