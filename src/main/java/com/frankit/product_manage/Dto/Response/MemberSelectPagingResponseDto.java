package com.frankit.product_manage.Dto.Response;

import lombok.Data;

import java.util.List;

@Data
public class MemberSelectPagingResponseDto {
    private List<MemberSelectResponseDto> members;
    private int number; // 현재 페이지 번호
    private int size;   // 페이지 크기

    public MemberSelectPagingResponseDto(List<MemberSelectResponseDto> members, int number, int size) {
        this.members = members;
        this.number = number;
        this.size = size;
    }
}
