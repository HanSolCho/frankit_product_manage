package com.frankit.product_manage.service;

import com.frankit.product_manage.Dto.Request.MemberSignInRequsetDto;
import com.frankit.product_manage.Dto.Request.MemberUpdateRequestDto;
import com.frankit.product_manage.Dto.Response.MemberSelectPagingResponseDto;
import com.frankit.product_manage.Dto.Response.MemberSelectResponseDto;
import com.frankit.product_manage.Dto.Response.ProductSelectPagingResponseDto;
import com.frankit.product_manage.entity.Member;
import com.frankit.product_manage.entity.Product;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
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
    private MemberUpdateRequestDto memberUpdateRequestDto;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        String pw = passwordEncoder.encode("password");
        member = Member.builder()
                .num(1L)
                .id("id")
                .password(pw)
                .role("USER")
                .build();


        memberSignInRequsetDto = new MemberSignInRequsetDto();
        memberSignInRequsetDto.setMemberId("id");
        memberSignInRequsetDto.setPassword("password");

        memberUpdateRequestDto = new MemberUpdateRequestDto();
        memberUpdateRequestDto.setMemberId("id");
        memberUpdateRequestDto.setPresentPassword("password");
        memberUpdateRequestDto.setUpdatePassword("newPassword");

    }

    @Test
    @DisplayName("멤버 등록 성공")
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

    @Test
    void selectAllMember() {
        // given: 페이지 정보를 설정
        int page = 0;
        int size = 1;

        Pageable pageable = PageRequest.of(page, size);

        Page<Member> memberPage = new PageImpl<>(List.of(member));

        when(memberRepository.findAll(pageable)).thenReturn(memberPage);

        // when: service 메서드 호출
        MemberSelectPagingResponseDto result = memberService.selectAllMember(page, size);

        // then: 반환된 결과 검증
        assertThat(result.getMembers()).hasSize(1);
        assertThat(result.getMembers().getFirst().getId()).isEqualTo("id");
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(1);

        verify(memberRepository, times(1)).findAll(pageable);
    }

    @Test
    void selectMemberById() {
        // given:
        String id = "id";
        when(memberRepository.findById(id)).thenReturn(Optional.ofNullable(member));

        // when:
        Optional<MemberSelectResponseDto> result = memberService.selectMemberById(id);

        // then:
        assertThat(result.get().getId()).isEqualTo("id");

        verify(memberRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("회원 정보 변경 성공")
    void updateMember() {
        // given
        when(memberRepository.findById("id")).thenReturn(Optional.of(member));

        // when:
        Optional<Member> validMember = memberRepository.findById("id");
        assertThat(validMember).isPresent();

        Member selectMember = validMember.get();
        selectMember.setPassword("newPassword");
        memberRepository.save(selectMember);

        // then:
        assertThat(selectMember.getPassword()).isEqualTo("newPassword");
        verify(memberRepository).save(selectMember);
    }

    @Test
    @DisplayName("회원 권한 변경 성공")
    void updateRoleMember() {
        // given
        when(memberRepository.findById("id")).thenReturn(Optional.of(member));

        // when:
        Optional<Member> validMember = memberRepository.findById("id");
        assertThat(validMember).isPresent();

        Member selectMember = validMember.get();
        selectMember.setRole("ADMIN");
        memberRepository.save(selectMember);

        // then:
        assertThat(selectMember.getRole()).isEqualTo("ADMIN");
        verify(memberRepository).save(selectMember);
    }

    @Test
    void deleteMember() {
        // given
        when(memberRepository.findById("id")).thenReturn(Optional.of(member));

        // when:
        Optional<Member> validMember = memberRepository.findById("id");
        assertThat(validMember).isPresent();

        memberRepository.deleteById(member.getNum());

        // then:
        verify(memberRepository).deleteById(member.getNum());
    }

    @Test
    @DisplayName("회원 정보 없는 케이스")
    void updateAndDeleteMemberFailMemberNotFound() {
        // given
        when(memberRepository.findById("id")).thenReturn(Optional.empty());

        // when:
        Optional<Member> validMember = memberRepository.findById("id");
        assertThat(validMember).isNotPresent();

        // then:
        assertThatThrownBy(() -> {
            throw new MemberNotFoundException();
        }).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void validatePassword() {
        // given:
        String password = "password";
        String databasePassword = "password";
        when(passwordEncoder.matches(password, databasePassword)).thenReturn(true);

        // when:
        boolean validCheck = passwordEncoder.matches(password, databasePassword);

        // then:
        assertThat(validCheck).isTrue();
        // verify:
        verify(passwordEncoder).matches(password, databasePassword);
    }
}