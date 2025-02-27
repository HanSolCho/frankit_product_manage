package com.frankit.product_manage.exception.member;

import com.frankit.product_manage.exception.ErrorCode;
import com.frankit.product_manage.exception.ProductManageException;

public class MemberNotFoundException extends ProductManageException {
    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}