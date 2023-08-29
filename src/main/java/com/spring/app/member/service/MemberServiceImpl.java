package com.spring.app.member.service;

import com.spring.app.member.entity.Member;
import com.spring.app.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    
    // 등록하기
    @Override
    public void addMember(Member member) {
        // 암호화
        String encryption = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryption);

        memberRepository.insertOne(member);
    }
    
    // 조회하기
    @Override
    public Optional<Member> getMemberByUsername(String username) {
        Optional<Member> optional = memberRepository.selectOneByUsername(username);
        return optional;
    }
    
}
