package com.frankit.product_manage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frankit.product_manage.Dto.Request.MemberSignInRequsetDto;
import com.frankit.product_manage.Dto.Request.MemberUpdateRequestDto;
import com.frankit.product_manage.config.TestSecurityConfig;
import com.frankit.product_manage.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs // rest docs 자동 설정
@Import(TestSecurityConfig.class)
public class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private MemberService memberService;


    @Test
    @DisplayName("회원가입 Controller 테스트") // 정상적으로 호출 여부 확인, 비즈니스 로직은 Service 단에서
    public void testSignUp() throws Exception {
        //given
        MemberSignInRequsetDto memberSignInRequsetDto = new MemberSignInRequsetDto();
        memberSignInRequsetDto.setMemberId("id");
        memberSignInRequsetDto.setPassword("password");

        //when & then: 예상되는 결과 검증
        mockMvc.perform(post("/frankit/product-manage/member/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(memberSignInRequsetDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("member/signUp", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("memberId").description("매장 멤버쉽 ID"),
                                        fieldWithPath("password").description("비밀번호")
                                )
                        )
                );

        verify(memberService, times(1)).signUP(memberSignInRequsetDto);
    }

    @Test
    @DisplayName("로그인 Controller 테스트")
    public void testSignIn() throws Exception {
        //given
        MemberSignInRequsetDto memberSignInRequsetDto = new MemberSignInRequsetDto();
        memberSignInRequsetDto.setMemberId("id");
        memberSignInRequsetDto.setPassword("password");

        //when & then: 예상되는 결과 검증
        mockMvc.perform(post("/frankit/product-manage/member/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberSignInRequsetDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("member/signIn", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("memberId").description("매장 멤버쉽 ID"),
                                        fieldWithPath("password").description("비밀번호")
                                )
                        )
                );

        verify(memberService, times(1)).signIn(memberSignInRequsetDto);
    }

    @Test
    @DisplayName("회원정보 업데이트 Controller 테스트")
    public void testUpdateMember() throws Exception {
        //given
        MemberUpdateRequestDto memberUpdateRequestDto = new MemberUpdateRequestDto();
        memberUpdateRequestDto.setMemberId("id");
        memberUpdateRequestDto.setPresentPassword("password");
        memberUpdateRequestDto.setUpdatePassword("updatepassword");

        //when & then: 예상되는 결과 검증
        mockMvc.perform(put("/frankit/product-manage/member/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberUpdateRequestDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("member/update", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("memberId").description("매장 멤버쉽 ID"),
                                        fieldWithPath("presentPassword").description("현재 비밀번호"),
                                        fieldWithPath("updatePassword").description("새로운 비밀번호")
                                )
                        )
                );

        verify(memberService, times(1)).updateMember(memberUpdateRequestDto);
    }

    @Test
    @DisplayName("회원정보 삭제 Controller 테스트")
    public void testDeleteMember() throws Exception {
        //given
        MemberSignInRequsetDto memberSignInRequsetDto = new MemberSignInRequsetDto();
        memberSignInRequsetDto.setMemberId("id");
        memberSignInRequsetDto.setPassword("password");

        //when & then: 예상되는 결과 검증
        mockMvc.perform(delete("/frankit/product-manage/member/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberSignInRequsetDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("member/delete", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("memberId").description("매장 멤버쉽 ID"),
                                        fieldWithPath("password").description("비밀번호")
                                )
                        )
                );

        verify(memberService, times(1)).deleteMember(memberSignInRequsetDto);
    }
}
