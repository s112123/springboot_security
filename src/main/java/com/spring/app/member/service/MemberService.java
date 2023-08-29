package com.spring.app.member.service;

import com.spring.app.member.entity.Member;

import java.util.Optional;

public interface MemberService {

    // 등록하기
    public abstract void addMember(Member member);

    // 조회하기
    public abstract Optional<Member> getMemberByUsername(String username);

}
