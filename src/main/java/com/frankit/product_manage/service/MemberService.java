package com.frankit.product_manage.service;

import com.frankit.product_manage.Dto.Request.MemberSignInRequsetDto;
import com.frankit.product_manage.Dto.Request.MemberUpdateRequestDto;
import com.frankit.product_manage.Dto.Request.MemberUpdateRoleRequestDto;
import com.frankit.product_manage.entity.Member;
import com.frankit.product_manage.exception.member.MemberAlreadyExistsException;
import com.frankit.product_manage.exception.member.MemberNotFoundException;
import com.frankit.product_manage.exception.member.MemberNotValidatePasswordException;
import com.frankit.product_manage.jwt.JwtToken;
import com.frankit.product_manage.jwt.JwtTokenProvider;
import com.frankit.product_manage.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    public MemberRepository memberRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public void signUP(MemberSignInRequsetDto memberSignInRequsetDto) {
        // 이미 존재하는 ID인지 확인
        try {
            Optional<Member> existingMember = memberRepository.findById(memberSignInRequsetDto.getMemberId());
            if (existingMember.isPresent()) {
                log.error("이미 존재하는 아이디: {}", memberSignInRequsetDto.getMemberId());
                throw new MemberAlreadyExistsException();
            }

            Member member = Member.builder()
                    .id(memberSignInRequsetDto.getMemberId())
                    .password(passwordEncoder.encode(memberSignInRequsetDto.getPassword()))
                    .role("USER")
                    .build();

            memberRepository.save(member);
            log.info("회원가입 성공 : {}", member.getId());
        } catch (MemberAlreadyExistsException e) {
            log.error("회원가입 실패: 이미 존재하는 아이디입니다. Member ID: {}", memberSignInRequsetDto.getMemberId());
            throw e;  // 이미 존재하는 아이디 예외를 그대로 던짐
        } catch (Exception e) {
            log.error("회원가입 중 예외 발생: ", e);
            throw new RuntimeException("회원가입 중 예외 발생");
        }
    }

    @Transactional
    public JwtToken signIn(MemberSignInRequsetDto memberSignInRequsetDto) {
        try {
            validateMember(memberSignInRequsetDto.getMemberId(), memberSignInRequsetDto.getPassword());
            // 1. username + password 를 기반으로 Authentication 객체 생성
            // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberSignInRequsetDto.getMemberId(), memberSignInRequsetDto.getPassword());

            // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
            // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
            return jwtToken;

        } catch (Exception e) {
            log.error("로그인 실패: ", e);
            throw new RuntimeException("로그인 실패");
        }
    }
    //todo: 업데이트를 위한 dto 필요 현재 비밀번호 이전 비밀번호 다 필요해
    public void updateMember(MemberUpdateRequestDto memberUpdateRequestDto){

        validateMember(memberUpdateRequestDto.getMemberId(), memberUpdateRequestDto.getPresentPassword());

        Optional<Member> validMember = memberRepository.findById(memberUpdateRequestDto.getMemberId());

        if (validMember.isPresent()) {
            Member member = validMember.get();
            member.setPassword(memberUpdateRequestDto.getUpdatePassword());
            memberRepository.save(member);
            log.info("회원 정보 수정 성공: {}", member.getId());
        } else {
            log.error("회원 정보 수정 실패: 존재하지 않는 회원 ID: {}", memberUpdateRequestDto.getMemberId());
            throw new MemberNotFoundException();
        }
    }
    @Transactional
    public void deleteMember(MemberSignInRequsetDto memberSignInRequsetDto){

        validateMember(memberSignInRequsetDto.getMemberId(), memberSignInRequsetDto.getPassword());

        Optional<Member> deleteMember = memberRepository.findById(memberSignInRequsetDto.getMemberId());

        if (deleteMember.isPresent()) {
            memberRepository.deleteById(deleteMember.get().getNum());  // 제품이 존재하면 삭제
            log.info("회원 삭제 성공: {}", memberSignInRequsetDto.getMemberId());
        }else {
            log.error("회원 삭제 실패: 존재하지 않는 회원 ID: {}", memberSignInRequsetDto.getMemberId());
            throw new MemberNotFoundException();
        }
    }

    public void validateMember(String id, String password) {
        // 1. ID 유효성 체크
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        // 2. 비밀번호 유효성 체크
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new MemberNotValidatePasswordException();
        }
    }
}
