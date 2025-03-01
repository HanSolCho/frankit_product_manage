package com.frankit.product_manage;

import com.frankit.product_manage.exception.ErrorCode;
import com.frankit.product_manage.exception.ErrorResponse;
import com.frankit.product_manage.exception.ProductManageException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductManageException.class)
    public ResponseEntity<ErrorResponse> handleProductManageException(ProductManageException productManageException) {
        log.debug("Handling ProductManageException: {}", productManageException.getMessage());
        ErrorCode errorCode = productManageException.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getCode()));
    }
}