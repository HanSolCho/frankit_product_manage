package com.frankit.product_manage.exception.product.option;

import com.frankit.product_manage.exception.ErrorCode;
import com.frankit.product_manage.exception.ProductManageException;

public class ProductOptionException extends ProductManageException {
    public ProductOptionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
