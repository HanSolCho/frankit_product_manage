package com.frankit.product_manage.controller;

import com.frankit.product_manage.Dto.Request.MemberSignInRequsetDto;
import com.frankit.product_manage.Dto.Request.MemberUpdateRequestDto;
import com.frankit.product_manage.Dto.Request.MemberUpdateRoleRequestDto;
import com.frankit.product_manage.jwt.JwtToken;
import com.frankit.product_manage.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
        JwtToken jwtToken = memberService.signIn(memberSignInRequsetDto);
        return jwtToken;
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateMember(@RequestBody MemberUpdateRequestDto memberUpdateRequestDto){
        memberService.updateMember(memberUpdateRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMember(@RequestBody MemberSignInRequsetDto memberSignInRequsetDto){
        memberService.deleteMember(memberSignInRequsetDto);
        return ResponseEntity.ok().build();
    }
}
