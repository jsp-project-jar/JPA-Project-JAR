package com.app.projectjar.service.suggest;

import com.app.projectjar.domain.file.FileDTO;
import com.app.projectjar.domain.suggest.SuggestDTO;
import com.app.projectjar.entity.suggest.Suggest;
import com.app.projectjar.repository.file.suggest.SuggestFileRepository;
import com.app.projectjar.repository.member.MemberRepository;
import com.app.projectjar.repository.suggest.SuggestLikeRepository;
import com.app.projectjar.repository.suggest.SuggestReplyRepository;
import com.app.projectjar.repository.suggest.SuggestRepository;
import com.app.projectjar.type.FileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Qualifier("suggest") @Primary
@Slf4j
public class SuggestServiceImpl implements SuggestService {
    private final SuggestRepository suggestRepository;

    private final SuggestFileRepository suggestFileRepository;

    private final MemberRepository memberRepository;

    private final SuggestLikeRepository suggestLikeRepository;

    private final SuggestReplyRepository suggestReplyRepository;


    @Override @Transactional
    public void register(SuggestDTO suggestDTO, Long memberId) {
        List<FileDTO> fileDTOS = suggestDTO.getFileDTOS();

        memberRepository.findById(memberId).ifPresent(
                member -> suggestDTO.setMemberDTO(toMemberDTO(member))
        );

        suggestRepository.save(toSuggestEntity(suggestDTO));
        if(fileDTOS != null){
            for (int i = 0; i < fileDTOS.size(); i++) {
                if(i == 0){
                    fileDTOS.get(i).setFileType(FileType.REPRESENTATIVE);
                }
                fileDTOS.get(i).setSuggest(getCurrentSequence());
                suggestFileRepository.save(toSuggestFileEntity(fileDTOS.get(i)));
            }
        }
    }

    @Override
    public Page<SuggestDTO> getPersonalSuggestList(Pageable pageable) {
        Page<Suggest> suggests = suggestRepository.findByPersonalWithPaging_QueryDsl(pageable);
        List<SuggestDTO> suggestDTOS = suggests.getContent().stream()
                .map(this::toSuggestDTO)
                .collect(Collectors.toList());

        suggestDTOS.forEach(suggestDTO -> {
            suggestDTO.setLikeCount(getLikeCount(suggestDTO.getId()));
            suggestDTO.setReplyCount(getReplyCount(suggestDTO.getId()));
        });

        return new PageImpl<>(suggestDTOS, suggests.getPageable(), suggests.getTotalElements());
    }

    @Override
    public Page<SuggestDTO> getGroupSuggestList(Pageable pageable) {
        Page<Suggest> suggests = suggestRepository.findByGroupWithPaging_QueryDsl(pageable);
        List<SuggestDTO> suggestDTOS = suggests.getContent().stream()
                .map(this::toSuggestDTO)
                .collect(Collectors.toList());

        suggestDTOS.forEach(suggestDTO -> {
            suggestDTO.setLikeCount(getLikeCount(suggestDTO.getId()));
            suggestDTO.setReplyCount(getReplyCount(suggestDTO.getId()));
        });

        return new PageImpl<>(suggestDTOS, suggests.getPageable(), suggests.getTotalElements());
    }

    @Override
    public SuggestDTO getSuggest(Long suggestId) {
        Optional<Suggest> suggest = suggestRepository.findByIdSuggest_QueryDsl(suggestId);
        return toSuggestDTO(suggest.get());
    }

    @Override
    public Suggest getCurrentSequence() {
        return suggestRepository.getCurrentSequence();
    }

    @Override
    public Long getLikeCount(Long suggestId) {
        return suggestReplyRepository.getReplyCount(suggestId);
    }

    @Override
    public Long getReplyCount(Long suggestId) {
        return suggestLikeRepository.getSuggestLikeCount(suggestId);
    }

}
