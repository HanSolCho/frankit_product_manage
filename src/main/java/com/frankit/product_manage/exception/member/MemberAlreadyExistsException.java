package com.frankit.product_manage.exception.member;

import com.frankit.product_manage.exception.ErrorCode;
import com.frankit.product_manage.exception.ProductManageException;

public class MemberAlreadyExistsException extends ProductManageException {
    public MemberAlreadyExistsException() {
        super(ErrorCode.MEMBER_ALREADY_EXISTS);
    }
}