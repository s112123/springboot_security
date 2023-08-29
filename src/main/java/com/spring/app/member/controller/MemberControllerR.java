package com.spring.app.member.controller;

import com.spring.app.member.entity.Member;
import com.spring.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberControllerR {

    private final MemberService memberService;

    // 목록조회
    @GetMapping
    public ResponseEntity<String> memberList() {
        return ResponseEntity.status(HttpStatus.OK).body("memberList");
    }

    // 등록하기
    @PostMapping
    public String join(@RequestBody Member member) {
        memberService.addMember(member);
        return "inserted";
    }

}
