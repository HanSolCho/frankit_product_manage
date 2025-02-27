package com.frankit.product_manage.service;

import com.frankit.product_manage.Dto.Request.MemberSignInRequsetDto;
import com.frankit.product_manage.entity.Member;
import com.frankit.product_manage.jwt.JwtToken;
import com.frankit.product_manage.jwt.JwtTokenProvider;
import com.frankit.product_manage.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

//todo: 회원 가입 실패(중복 회원,정보부족), 로그인, 로그인 실패(미가입,비밀번호오류),정보수정,정보수정실패(비밀번호오류),회원삭제,회원삭제실패(비밀번호 오류) 이건 서비스에 넣자
// Service 단의 결과는 Service 단의 단위 테스트로 정상 결과가 나온 후 로 가정, contorller에서는 원하는 횟수만큼 정상 동작하는지 + api 문서 를 위한 명세 작성

@ExtendWith(SpringExtension.class)
class MemberServiceTest {
    @InjectMocks
    MemberService memberService;
    @MockBean
    MemberRepository memberRepository;
    private Member member;
    private MemberSignInRequsetDto memberSignInRequsetDto;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        member = Member.builder()
                .id("id")
                .password("password") // 실제 비밀번호는 테스트를 위해 평문 그대로 두고, 암호화는 mocking 처리
                .build();

        // SignIn 요청을 위한 Dto 설정
        memberSignInRequsetDto = new MemberSignInRequsetDto();
        memberSignInRequsetDto.setMemberId("id");
        memberSignInRequsetDto.setPassword("password");

    }

    @Test
    @DisplayName("멤버 등록 성공") //실패 기능은 실패 관련 분기 처리 후 그때 그때 추가, save가 정상 호출되었다면 Repository Test 영역
    void signUpMemberSuccess() {
        /*
        given
        */
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        /*
        when
         */
       memberService.signUP(memberSignInRequsetDto);
        /*
        then
         */
        verify(memberRepository, times(1)).save(any(Member.class)); // save 메소드가 한 번 호출되었는지 확인
    }

    @Test
    @DisplayName("회원 가입 실패 (중복 아이디)")
    void signUpMemberFailDuplicateId() {

        // given: 이미 존재하는 ID일 경우
        when(memberRepository.findById("id")).thenReturn(Optional.of(member));

        // when & then: 예외가 발생하는지 확인
        assertThatThrownBy(() -> memberService.signUP(memberSignInRequsetDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 아이디입니다.");

        // verify: memberRepository.save가 호출되지 않았음을 확인
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("로그인 성공") // jwt 관련 고민 필요
    void signInMemberSuccess() {
    //
    }

    @Test
    void updateMember() {
        // given: memberRepository에서 member를 찾을 수 있을 때
        MemberSignInRequsetDto updatetDto = new MemberSignInRequsetDto();
        updatetDto.setMemberId("id");
        updatetDto.setPassword("newPassword");  // 새로운 비밀번호

        when(memberRepository.findById("id")).thenReturn(Optional.of(member));  // member 존재
        when(memberRepository.save(any(Member.class))).thenReturn(member);  // save 메소드 Mock 설정

        // when: 업데이트 메소드 호출
        memberService.updateMember(updatetDto);

        // then: member의 비밀번호가 업데이트 되고 save 메소드가 호출되는지 검증
        assertThat(member.getPassword()).isEqualTo("newPassword");
        verify(memberRepository).save(member);  // save 메소드가 한 번 호출되었는지 확인
    }

    @Test
    @DisplayName("회원 정보 업데이트 실패 (회원 없음)")
    void updateMemberFailMemberNotFound() {
        // given: memberRepository에서 member를 찾을 수 없을 때
        when(memberRepository.findById("id")).thenReturn(Optional.empty());  // member 없음

        // when: 업데이트 메소드 호출
        memberService.updateMember(memberSignInRequsetDto);

        // then: memberRepository의 save 메소드가 호출되지 않았음을 확인
        verify(memberRepository, never()).save(any(Member.class));  // save 메소드가 호출되지 않았음을 확인
    }

    @Test
    void deleteMember() {
        // given: 존재하는 회원을 mock
        member.setNum(1L);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        // when: 회원 삭제 메소드 호출
        memberService.deleteMember(memberSignInRequsetDto);

        // then: deleteById가 호출되었는지 검증
        verify(memberRepository).deleteById(1L);  // 실제 삭제 메소드 호출 여부 확인
    }

    @Test
    @DisplayName("회원 삭제 실패 (존재하지 않는 회원)")
    void deleteMemberFailNotFound() {
        // given: 존재하지 않는 회원을 mock
        when(memberRepository.findById("nonExistentID")).thenReturn(Optional.empty());

        // when & then: 예외가 발생하는지 확인
        assertThatThrownBy(() -> memberService.deleteMember(memberSignInRequsetDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }
}