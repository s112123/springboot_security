package com.spring.app.member.repository;

import com.spring.app.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberRepository {

    // 등록하기
    public abstract Long insertOne(Member member);

    // 조회하기
    public abstract Optional<Member> selectOneByUsername(String username);

}
