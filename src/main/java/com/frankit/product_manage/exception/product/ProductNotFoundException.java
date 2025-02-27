package com.frankit.product_manage.exception.product;

import com.frankit.product_manage.exception.ErrorCode;
import com.frankit.product_manage.exception.ProductManageException;

public class ProductNotFoundException extends ProductManageException {
    public ProductNotFoundException() {
        super(ErrorCode.PRODUCT_NOT_FOUND);
    }
}