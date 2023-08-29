package com.spring.app.member.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private Long id;
    private String username;
    private String password;
    private String name;
    private Role role;
    private LocalDateTime regDate;

    public Member(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

}
