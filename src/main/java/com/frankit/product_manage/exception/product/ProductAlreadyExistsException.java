package com.frankit.product_manage.exception.product;

import com.frankit.product_manage.exception.ErrorCode;
import com.frankit.product_manage.exception.ProductManageException;

public class ProductAlreadyExistsException extends ProductManageException {
    public ProductAlreadyExistsException() {
        super(ErrorCode.PRODUCT_ALREADY_EXISTS);
    }
}