package com.frankit.product_manage.Dto.Request;

import lombok.Data;

import java.util.List;

@Data
public class SelectOptionValueAddRequestDto {
    List<String> name;
    Long productOptionId;

}
