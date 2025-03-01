package com.frankit.product_manage.Dto.Request;

import lombok.Data;

@Data
public class MemberUpdateRequestDto {
    private String memberId;
    private String presentPassword;
    private String updatePassword;
}
