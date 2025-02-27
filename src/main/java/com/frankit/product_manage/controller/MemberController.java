package com.frankit.product_manage.controller;

import com.frankit.product_manage.Dto.Request.MemberSignInRequsetDto;
import com.frankit.product_manage.jwt.JwtToken;
import com.frankit.product_manage.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**todo: 로그인 - 회원가입,로그인 기능 구현 -> 비밀번호 변경, 회원 삭제기능 옵션 구상 -> 삭제 시 비밀번호 체크 로직 추가
 * dto 수정 : 회원가입과 로그인이 동일하게 사용할지, 동일하다면 명칭변경
 *
 */


@RestController
@RequestMapping("/frankit/product-manage/member")
public class MemberController {

    private MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerMember(@RequestBody MemberSignInRequsetDto memberSignInRequsetDto){
        memberService.signUP(memberSignInRequsetDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody MemberSignInRequsetDto memberSignInRequsetDto){
        String id = memberSignInRequsetDto.getMemberId();
        String password = memberSignInRequsetDto.getPassword();
        JwtToken jwtToken = memberService.signIn(memberSignInRequsetDto);
        return jwtToken;
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateMember(@RequestBody MemberSignInRequsetDto memberSignInRequsetDto){
        memberService.updateMember(memberSignInRequsetDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMember(@RequestBody MemberSignInRequsetDto memberSignInRequsetDto){
        memberService.deleteMember(memberSignInRequsetDto);
        return ResponseEntity.ok().build();
    }
}
