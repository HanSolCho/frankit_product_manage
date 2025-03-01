package com.frankit.product_manage.Dto.Request;

import lombok.Data;

@Data
public class MemberUpdateRoleRequestDto {
    private String memberId;
    private String role;
}
