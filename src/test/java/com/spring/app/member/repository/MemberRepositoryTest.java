package com.spring.app.member.repository;

import com.spring.app.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName(value = "등록하기")
    void insertOne() {
        Member member = new Member("a1234", "1234", "이땡땡");
        Long res = memberRepository.insertOne(member);
        assertThat(res).isEqualTo(1);
    }

    @Test
    @DisplayName(value = "조회하기")
    void selectOneByUsername() {
        Member member = new Member("a1234", "1234", "이땡땡");
        memberRepository.insertOne(member);

        Member findMember = memberRepository.selectOneByUsername("a1234").get();
        assertThat(member.getUsername()).isEqualTo(findMember.getUsername());
    }

}
