package com.spring.app.member.service;

import com.spring.app.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberService.getMemberByUsername(username).get();

        UserDetails userDetails = User.builder()
                .username(username)
                .password(member.getPassword())
                .authorities(member.getRole().name())
                .build();

        return userDetails;
    }

}
