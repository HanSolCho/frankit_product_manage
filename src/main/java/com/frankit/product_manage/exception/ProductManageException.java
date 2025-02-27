package com.frankit.product_manage.exception;

import lombok.Data;

@Data
public class ProductManageException extends RuntimeException {
    private final ErrorCode errorCode;
    public ProductManageException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
