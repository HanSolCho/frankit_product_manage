package com.frankit.product_manage.exception;

import lombok.Data;

@Data
public class ErrorResponse {

    private int errorCode;
    private String errorMessage;

    public ErrorResponse(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}