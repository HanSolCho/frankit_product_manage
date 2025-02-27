package com.frankit.product_manage.Dto.Request;

import lombok.Data;

import java.util.HashMap;

@Data
public class SelectOptionValueUpdateRequestDto {
    HashMap<Long,String> selectOptionValueMap;
}
