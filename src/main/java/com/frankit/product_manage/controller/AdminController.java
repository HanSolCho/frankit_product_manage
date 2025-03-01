package com.frankit.product_manage.controller;

import com.frankit.product_manage.Dto.Request.MemberUpdateRoleRequestDto;
import com.frankit.product_manage.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/frankit/product-manage/admin")
public class AdminController {

    private MemberService memberService;

    public AdminController(MemberService memberService){
        this.memberService = memberService;
    }

    @GetMapping("/member/all")
    public ResponseEntity<?> selectAllMember(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        return ResponseEntity.ok(memberService.selectAllMember(pageIndex,pageSize));
    }

    @GetMapping("/member/id")
    public ResponseEntity<?> selectMemberById(
            @RequestParam(value = "id") String id){
        return ResponseEntity.ok(memberService.selectMemberById(id));
    }

    @PutMapping("/update-role")
    public ResponseEntity<?> updateRoleMember(@RequestBody MemberUpdateRoleRequestDto memberUpdateRoleRequestDto){
        memberService.updateRoleMember(memberUpdateRoleRequestDto);
        return ResponseEntity.ok().build();
    }

}
