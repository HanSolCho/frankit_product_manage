package com.frankit.product_manage.Dto.Response;

import lombok.Data;

@Data
public class MemberSelectResponseDto {
    private String id;
    private String role;

    public MemberSelectResponseDto(String id, String role) {
        this.id = id;
        this.role = role;
    }
}
