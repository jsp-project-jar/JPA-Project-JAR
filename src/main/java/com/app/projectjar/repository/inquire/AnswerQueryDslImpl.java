package com.app.projectjar.repository.inquire;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnswerQueryDslImpl implements AnswerQueryDsl {
    private final JPAQueryFactory jpaQueryFactory;
}