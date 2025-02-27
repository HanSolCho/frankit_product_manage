package com.frankit.product_manage.exception;

public enum ErrorCode {
    // 공통 오류 코드
    INTERNAL_SERVER_ERROR(500, "서버 오류"),

    // 회원 관련 오류 코드
    MEMBER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    MEMBER_FAIL_VALIDATE(403, "로그인 정보가 일치하지 않습니다."),
    MEMBER_ALREADY_EXISTS(409, "이미 존재하는 회원 아이디입니다."),

    // 로그인 관련 오류 코드
    INVALID_LOGIN_INFO(401, "잘못된 아이디 또는 비밀번호입니다."),

    // 상품 관련 오류 코드
    // 제품 관련 오류 코드
    PRODUCT_NOT_FOUND(404, "존재하지 않는 제품입니다."),
    PRODUCT_ALREADY_EXISTS(409, "이미 존재하는 제품입니다."),

    // 제품 옵션 관련 오류 코드
    PRODUCT_OPTION_NOT_FOUND(404, "존재하지 않는 제품 옵션입니다."),
    SELECT_OPTION_VALUE_NOT_FOUND(404, "존재하지 않는 선택 옵션 값입니다."),
    INVALID_OPTION_TYPE(400, "옵션 타입이 잘못되었습니다."),
    TOO_MANY_OPTIONS(400, "제품에 더 이상 옵션을 추가할 수 없습니다."),

    // 범용 에러
    BAD_REQUEST(400, "잘못된 요청입니다.");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
