package com.app.projectjar.repository.diary;


import com.app.projectjar.entity.diary.DiaryLike;
import com.app.projectjar.entity.diary.QDiaryLike;
import com.app.projectjar.entity.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.app.projectjar.entity.diary.QDiaryLike.diaryLike;
import static com.app.projectjar.entity.suggest.QSuggestLike.suggestLike;

@RequiredArgsConstructor
public class DiaryQueryLikeDslImpl implements DiaryQueryLikeDsl {
    private final JPAQueryFactory query;

    @Override
    public Member findMemberByDiaryLike(Long diaryId, Long memberId) {
        return query.select(diaryLike.member)
                .from(diaryLike)
                .where(diaryLike.diary.id.eq(diaryId))
                .where(diaryLike.member.memberId.eq(memberId))
                .fetchOne();
    }

    @Override
    public Long getDiaryLikeCount(Long diaryId) {
        return query.select(diaryLike.count())
                .from(diaryLike)
                .where(diaryLike.diary.id.eq(diaryId))
                .fetchOne();
    }

    @Override
    public void deleteByMemberIdAndDiaryId(Long diaryId, Long memberId) {
        query.delete(diaryLike)
                .where(diaryLike.member.memberId.eq(memberId))
                .where(diaryLike.diary.id.eq(diaryId))
                .execute();
    }

    @Override
    public Page<DiaryLike> findByLikeMemberIdWithPaging_QueryDsl(Pageable pageable, Long memberId) {
        List<DiaryLike> foundDiaries = query.select(diaryLike)
                .from(diaryLike)
                .where(diaryLike.member.memberId.eq(memberId))
                .orderBy(diaryLike.diary.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long count = query.select(diaryLike.count())
                .from(diaryLike)
                .where(diaryLike.member.memberId.eq(memberId))
                .fetchOne();

        return new PageImpl<>(foundDiaries, pageable, count);

    }
//    @Override
//    public Page<SuggestLike> findByLikeMemberIdWithPaging_QueryDsl(Pageable pageable, Long id) {
//        List<Suggest> foundSuggest = query.select(suggestLike.suggest)
//                .from(suggestLike)
//                .leftJoin(suggestLike.suggest)
//                .fetchJoin()
////                .where(suggestLike.suggest.id.eq(id))
//                .where(suggestLike.member.id.eq(id))
//                .orderBy(suggestLike.suggest.createdDate.desc())
//                .offset(pageable.getOffset() -1)
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        Long count = query.select(suggestLike.count())
//                .from(suggestLike)
//                .where(suggestLike.member.id.eq(id))
//                .fetchOne();
//        return new PageImpl<SuggestLike>(foundSuggest, pageable, count);
//    }

}
