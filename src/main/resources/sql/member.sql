create database springboot;
use springboot;

# member table
drop table if exists member;
create table member (
    id bigint auto_increment primary key,
    username varchar(50) not null unique,
    password varchar(500) not null,
    name varchar(50) not null,
    role varchar(20) default 'ROLE_USER',
    regdate timestamp default now()
);