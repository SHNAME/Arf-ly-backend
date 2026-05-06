package com.capstone.arfly.member.service;

import com.capstone.arfly.common.exception.UserNotExistsException;
import com.capstone.arfly.member.domain.Terms;
import com.capstone.arfly.member.domain.UserTermsAgreement;
import com.capstone.arfly.member.dto.LatestTermsResponseDto;
import com.capstone.arfly.member.repository.MemberRepository;
import com.capstone.arfly.member.repository.TermsRepository;
import com.capstone.arfly.member.repository.UserTermsAgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsService {
    private final TermsRepository termsRepository;
    private final MemberRepository memberRepository;
    private final UserTermsAgreementRepository userTermsAgreementRepository;

    @Transactional(readOnly = true)
    public List<LatestTermsResponseDto> getLatestAgreements() {
        List<Terms> latestTemrsList = termsRepository.findByLatestTrue();

        List<LatestTermsResponseDto> response = latestTemrsList.stream()
                .map(LatestTermsResponseDto::from)
                .sorted(Comparator.comparing(LatestTermsResponseDto::getOrderIndex))
                .toList();
        return  response;
    }

    @Transactional(readOnly = true)
    public boolean hasAgreedToLatestTerms(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> {throw new UserNotExistsException();});

        List<UserTermsAgreement> userTermsAgreements = userTermsAgreementRepository.findByMemberId(memberId);
        //동의를 하지 않은 경우
        if(userTermsAgreements == null || userTermsAgreements.isEmpty()){
            return true;
        }
        //필수 약관을 동의한 경우
        return false;
    }
}
