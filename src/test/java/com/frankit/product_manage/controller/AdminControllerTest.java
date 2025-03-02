package com.frankit.product_manage.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frankit.product_manage.Dto.Request.MemberSignInRequsetDto;
import com.frankit.product_manage.Dto.Request.MemberUpdateRequestDto;
import com.frankit.product_manage.Dto.Request.MemberUpdateRoleRequestDto;
import com.frankit.product_manage.Dto.Request.ProductRequestDto;
import com.frankit.product_manage.Dto.Response.MemberSelectPagingResponseDto;
import com.frankit.product_manage.Dto.Response.MemberSelectResponseDto;
import com.frankit.product_manage.Dto.Response.ProductSelectPagingResponseDto;
import com.frankit.product_manage.Dto.Response.ProductSelectResponseDto;
import com.frankit.product_manage.config.TestSecurityConfig;
import com.frankit.product_manage.entity.*;
import com.frankit.product_manage.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs // rest docs 자동 설정
@Import(TestSecurityConfig.class)
class AdminControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    void selectAllMember() throws Exception {
        int page = 0;
        int size = 1;
        Member mockMember = Member.builder()
                .num(1L)
                .id("id")
                .role("ADMIN")
                .build();

        List<Member> content = Collections.singletonList(mockMember);
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> memberPage = new PageImpl<>(content, pageable, 10);


        when(memberService.selectAllMember(page, size)).thenReturn(new MemberSelectPagingResponseDto(
                content.stream()
                        .map(member -> new MemberSelectResponseDto(member.getId(), member.getRole()))
                        .collect(Collectors.toList()),
                memberPage.getNumber(),
                memberPage.getSize())
        );

        //when & then: 예상되는 결과 검증
        mockMvc.perform(get("/frankit/product-manage/admin/member/all")
                        .param("pageIndex",String.valueOf(page))
                        .param("pageSize",String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andDo(
                        document("admin/all", //여기이름이 스니펫 폴더 이름
                                queryParameters(  // 실제 요청 본문 필드 설명
                                        parameterWithName("pageIndex")
                                                .description("전체 상품 페이지 중 몇번째 페이지인지"),
                                        parameterWithName("pageSize")
                                                .description("페이지 별 상품 개수")
                                ),
                                responseFields(  // 응답 본문 필드 설명
                                        fieldWithPath("members[].id").description("ID"),
                                        fieldWithPath("members[].role").description("권한"),

                                        fieldWithPath("number").description("현재 페이지 번호"),
                                        fieldWithPath("size").description("페이지 크기")
                                )
                        )
                );

        verify(memberService, times(1)).selectAllMember(page,size);
    }

    @Test
    void selectMemberById() throws Exception {
        // mock 응답 데이터 준비
        String id = "id";

        Member mockMember = Member.builder()
                .num(1L)
                .id("id")
                .role("ADMIN")
                .build();

        MemberSelectResponseDto memberSelectResponseDto = new MemberSelectResponseDto(mockMember.getId(), mockMember.getRole());
        when(memberService.selectMemberById(id)).thenReturn(Optional.of(memberSelectResponseDto));

        //when & then: 예상되는 결과 검증
        mockMvc.perform(get("/frankit/product-manage/admin/member/id")
                        .param("id",id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andDo(
                        document("admin/id", //여기이름이 스니펫 폴더 이름
                                queryParameters(  // 실제 요청 본문 필드 설명
                                        parameterWithName("id")
                                                .description("ID")
                                ),
                                responseFields(  // 응답 본문 필드 설명
                                        fieldWithPath("id").description("매장 멤버쉽 ID"),
                                        fieldWithPath("role").description("권한")
                                )
                        )
                );

        verify(memberService, times(1)).selectMemberById(id);
    }

    @Test
    void updateRoleMember() throws Exception {
        //given
        MemberUpdateRoleRequestDto memberUpdateRoleRequestDto = new MemberUpdateRoleRequestDto();
        memberUpdateRoleRequestDto.setMemberId("id");
        memberUpdateRoleRequestDto.setRole("USER");

        //when & then: 예상되는 결과 검증
        mockMvc.perform(put("/frankit/product-manage/admin/update-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberUpdateRoleRequestDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("admin/update", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("memberId").description("매장 멤버쉽 ID"),
                                        fieldWithPath("role").description("권한")
                                )
                        )
                );

        verify(memberService, times(1)).updateRoleMember(memberUpdateRoleRequestDto);
    }

    @Test
    void deleteMember() throws Exception {
        //given
        MemberUpdateRoleRequestDto memberUpdateRoleRequestDto = new MemberUpdateRoleRequestDto();
        memberUpdateRoleRequestDto.setMemberId("id");
        memberUpdateRoleRequestDto.setRole("USER");

        //when & then: 예상되는 결과 검증
        mockMvc.perform(delete("/frankit/product-manage/admin/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberUpdateRoleRequestDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("admin/delete", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("memberId").description("매장 멤버쉽 ID"),
                                        fieldWithPath("role").description("권한")
                                )
                        )
                );

        verify(memberService, times(1)).deleteMemberFromAdmin(memberUpdateRoleRequestDto.getMemberId());
    }
}