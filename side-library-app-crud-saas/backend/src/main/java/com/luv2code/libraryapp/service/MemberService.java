package com.luv2code.libraryapp.service;

import com.luv2code.libraryapp.dto.MemberDto;
import com.luv2code.libraryapp.dto.MemberRequest;
import com.luv2code.libraryapp.entity.LoanStatus;
import com.luv2code.libraryapp.entity.Member;
import com.luv2code.libraryapp.exception.BusinessRuleException;
import com.luv2code.libraryapp.exception.ResourceNotFoundException;
import com.luv2code.libraryapp.repository.LoanRepository;
import com.luv2code.libraryapp.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public MemberService(MemberRepository memberRepository, LoanRepository loanRepository) {
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberDto> findAll() {
        return memberRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemberDto findById(Long id) {
        return toDto(getMemberOrThrow(id));
    }

    @Transactional
    public MemberDto create(MemberRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("A member with email " + request.getEmail() + " already exists");
        }
        Member member = new Member(request.getFirstName(), request.getLastName(),
                request.getEmail(), LocalDate.now());
        return toDto(memberRepository.save(member));
    }

    @Transactional
    public void delete(Long id) {
        Member member = getMemberOrThrow(id);
        long activeLoans = loanRepository.countByMemberIdAndStatus(id, LoanStatus.ACTIVE);
        if (activeLoans > 0) {
            throw new BusinessRuleException(
                    member.getFirstName() + " " + member.getLastName() + " still has " + activeLoans
                            + " active loan(s) and cannot be removed");
        }
        memberRepository.delete(member);
    }

    private Member getMemberOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
    }

    private MemberDto toDto(Member member) {
        long activeLoans = loanRepository.countByMemberIdAndStatus(member.getId(), LoanStatus.ACTIVE);
        return new MemberDto(member.getId(), member.getFirstName(), member.getLastName(),
                member.getEmail(), member.getJoinDate(), activeLoans);
    }
}
