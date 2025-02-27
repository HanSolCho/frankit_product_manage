package com.frankit.product_manage.Dto.Request;

import lombok.Data;

@Data
public class MemberSignInRequsetDto {
    private String memberId;
    private String password;
}
