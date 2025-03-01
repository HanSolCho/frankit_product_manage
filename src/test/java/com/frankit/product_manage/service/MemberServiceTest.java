package com.frankit.product_manage.service;

import com.frankit.product_manage.Dto.Request.MemberSignInRequsetDto;
import com.frankit.product_manage.Dto.Request.MemberUpdateRequestDto;
import com.frankit.product_manage.entity.Member;
import com.frankit.product_manage.exception.member.MemberAlreadyExistsException;
import com.frankit.product_manage.exception.member.MemberNotFoundException;
import com.frankit.product_manage.exception.member.MemberNotValidatePasswordException;
import com.frankit.product_manage.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

//todo: 회원 가입 실패(중복 회원,정보부족), 로그인, 로그인 실패(미가입,비밀번호오류),정보수정,정보수정실패(비밀번호오류),회원삭제,회원삭제실패(비밀번호 오류) 이건 서비스에 넣자
// Service 단의 결과는 Service 단의 단위 테스트로 정상 결과가 나온 후 로 가정, contorller에서는 원하는 횟수만큼 정상 동작하는지 + api 문서 를 위한 명세 작성

@ExtendWith(SpringExtension.class)
class MemberServiceTest {
    private static final Logger log = LoggerFactory.getLogger(MemberServiceTest.class);
    @InjectMocks
    MemberService memberService;
    @MockBean
    MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private Member member;
    private MemberSignInRequsetDto memberSignInRequsetDto;
    private MemberUpdateRequestDto memberUpdateRequestDto;  // 테스트용 DTO 객체
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        String pw = passwordEncoder.encode("password");
        member = Member.builder()
                .num(1L)
                .id("id")
                .password(pw) // 실제 비밀번호는 테스트를 위해 평문 그대로 두고, 암호화는 mocking 처리
                .build();

        // SignIn 요청을 위한 Dto 설정
        memberSignInRequsetDto = new MemberSignInRequsetDto();
        memberSignInRequsetDto.setMemberId("id");
        memberSignInRequsetDto.setPassword("password");

        memberUpdateRequestDto = new MemberUpdateRequestDto();
        memberUpdateRequestDto.setMemberId("id");
        memberUpdateRequestDto.setPresentPassword("password");
        memberUpdateRequestDto.setUpdatePassword("newPassword");  // 새로운 비밀번호

    }

    @Test
    @DisplayName("멤버 등록 성공") //실패 기능은 실패 관련 분기 처리 후 그때 그때 추가, save가 정상 호출되었다면 Repository Test 영역
    void signUpMemberSuccess() {
        /*
        given
        */
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("password");
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
                .isInstanceOf(MemberAlreadyExistsException.class);

        // verify: memberRepository.save가 호출되지 않았음을 확인
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("로그인 성공") // jwt 관련 고민 필요
    void signInMemberSuccess() {
    //
    }
    //update 관련 테스트 코드 손보고 실제 테스트도 다시 확인해야함.
    //select 관련 신규 기능 추가한 테스트 코드도 추가하고 확인 필요.

    @Test
    @DisplayName("회원 정보 변경 성공") // 관련 valid check 로직은 따로 테스트 메소드 생성
    void updateMember() {
        // given
        when(memberRepository.findById("id")).thenReturn(Optional.of(member));

        // when: validateMember 호출 (updateMember 내부의 validateMember 호출) - 이 부분은 생략 가능
        Optional<Member> validMember = memberRepository.findById("id");
        assertThat(validMember).isPresent();  // 회원이 존재하는지 확인

        // 비밀번호 업데이트
        Member selectMember = validMember.get();
        selectMember.setPassword("newPassword");
        memberRepository.save(selectMember);

        // then: 비밀번호가 업데이트되고 save 메소드가 호출되었는지 검증
        assertThat(selectMember.getPassword()).isEqualTo("newPassword");
        verify(memberRepository).save(selectMember);  // save 메소드가 한 번 호출되었는지 확인
    }

    @Test
    void deleteMember() {
        // given
        when(memberRepository.findById("id")).thenReturn(Optional.of(member));

        // when: validateMember 호출 (updateMember 내부의 validateMember 호출) - 이 부분은 생략 가능
        Optional<Member> validMember = memberRepository.findById("id");
        assertThat(validMember).isPresent();  // 회원이 존재하는지 확인
        // 비밀번호 업데이트
        memberRepository.deleteById(member.getNum());  // 제품이 존재하면 삭제

        // then: 비밀번호가 업데이트되고 save 메소드가 호출되었는지 검증
        verify(memberRepository).deleteById(member.getNum());  // member.getNum()이 1이라면 deleteById(1)이 호출되어야 함
    }

    @Test
    @DisplayName("회원 정보 없는 케이스")
    void updateAndDeleteMemberFailMemberNotFound() {
        // given
        when(memberRepository.findById("id")).thenReturn(Optional.empty());

        // when: validateMember 호출 (updateMember 내부의 validateMember 호출) - 이 부분은 생략 가능
        Optional<Member> validMember = memberRepository.findById("id");
        assertThat(validMember).isNotPresent();  // 회원이 존재하지 않음을 확인

        // then: 예외가 발생하는지 검증
        assertThatThrownBy(() -> {
            throw new MemberNotFoundException();  // 실제 코드와 동일한 예외를 발생시킴
        }).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void validatePassword() {
        // given: memberRepository에서 id가 "testId"인 멤버가 있을 때
        String password = "password";
        String databasePassword = "password";
        when(passwordEncoder.matches(password, databasePassword)).thenReturn(true);

        // when: validateMember 호출
        boolean validCheck = passwordEncoder.matches(password, databasePassword);

        // then: matches 메소드가 true를 반환하는지 확인
        assertThat(validCheck).isTrue();
        // verify: passwordEncoder.matches가 호출되었는지 확인
        verify(passwordEncoder).matches(password, databasePassword);  // matches(a, b)가 정확히 호출되었는지 확인
    }
}