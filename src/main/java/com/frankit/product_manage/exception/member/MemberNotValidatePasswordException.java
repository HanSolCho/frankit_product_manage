package com.frankit.product_manage.exception.member;

import com.frankit.product_manage.exception.ErrorCode;
import com.frankit.product_manage.exception.ProductManageException;

public class MemberNotValidatePasswordException extends ProductManageException {
    public MemberNotValidatePasswordException() {
        super(ErrorCode.MEMBER_FAIL_VALIDATE);
    }
}
