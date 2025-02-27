package com.frankit.product_manage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frankit.product_manage.Dto.Request.MemberSignInRequsetDto;
import com.frankit.product_manage.config.TestSecurityConfig;
import com.frankit.product_manage.entity.Member;
import com.frankit.product_manage.jwt.JwtToken;
import com.frankit.product_manage.repository.MemberRepository;
import com.frankit.product_manage.service.MemberService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

//todo: 회원 가입 실패(중복 회원,정보부족), 로그인, 로그인 실패(미가입,비밀번호오류),정보수정,정보수정실패(비밀번호오류),회원삭제,회원삭제실패(비밀번호 오류) 이건 서비스에 넣자
// Service 단의 결과는 Service 단의 단위 테스트로 정상 결과가 나온 후 로 가정, contorller에서는 원하는 횟수만큼 정상 동작하는지 + api 문서 를 위한 명세 작성

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
    @DisplayName("로그인 Controller 테스트") // 정상적으로 호출 여부 확인, 비즈니스 로직은 Service 단에서
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
    @DisplayName("회원정보 업데이트 Controller 테스트") // 정상적으로 호출 여부 확인, 비즈니스 로직은 Service 단에서
    public void testUpdateMember() throws Exception {
        //given
        MemberSignInRequsetDto memberSignInRequsetDto = new MemberSignInRequsetDto();
        memberSignInRequsetDto.setMemberId("id");
        memberSignInRequsetDto.setPassword("password");

        //when & then: 예상되는 결과 검증
        mockMvc.perform(put("/frankit/product-manage/member/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberSignInRequsetDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("member/update", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("memberId").description("매장 멤버쉽 ID"),
                                        fieldWithPath("password").description("비밀번호")
                                )
                        )
                );

        verify(memberService, times(1)).updateMember(memberSignInRequsetDto);
    }

    @Test
    @DisplayName("회원정보 삭제 Controller 테스트") // 정상적으로 호출 여부 확인, 비즈니스 로직은 Service 단에서
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
