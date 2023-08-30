package com.spring.app.member.controller;

import com.spring.app.member.entity.Member;
import com.spring.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
//@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
public class MemberControllerR {

    private final MemberService memberService;

    // 목록조회
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Member>> memberList() {
        List<Member> members = memberService.getMembers();
        return ResponseEntity.status(HttpStatus.OK).body(members);
    }

    // 등록하기
    //@PreAuthorize("permitAll()")
    @PostMapping
    public String join(@RequestBody Member member) {
        memberService.addMember(member);
        return "inserted";
    }

}
