package com.luv2code.libraryapp.service;

import com.luv2code.libraryapp.dto.CheckoutRequest;
import com.luv2code.libraryapp.dto.LoanDto;
import com.luv2code.libraryapp.entity.Book;
import com.luv2code.libraryapp.entity.Loan;
import com.luv2code.libraryapp.entity.LoanStatus;
import com.luv2code.libraryapp.entity.Member;
import com.luv2code.libraryapp.exception.BusinessRuleException;
import com.luv2code.libraryapp.exception.ResourceNotFoundException;
import com.luv2code.libraryapp.repository.BookRepository;
import com.luv2code.libraryapp.repository.LoanRepository;
import com.luv2code.libraryapp.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private static final int LOAN_PERIOD_DAYS = 14;
    private static final long MAX_ACTIVE_LOANS_PER_MEMBER = 5;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository,
                        MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<LoanDto> findActive() {
        return loanRepository.findByStatusOrderByDueDateAsc(LoanStatus.ACTIVE).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoanDto> findAll() {
        return loanRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public LoanDto checkout(CheckoutRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + request.getBookId()));
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + request.getMemberId()));

        if (book.getAvailableCopies() <= 0) {
            throw new BusinessRuleException("\"" + book.getTitle() + "\" has no available copies right now");
        }

        long activeLoans = loanRepository.countByMemberIdAndStatus(member.getId(), LoanStatus.ACTIVE);
        if (activeLoans >= MAX_ACTIVE_LOANS_PER_MEMBER) {
            throw new BusinessRuleException(
                    member.getFirstName() + " " + member.getLastName()
                            + " already has " + MAX_ACTIVE_LOANS_PER_MEMBER + " active loans");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(book, member, today, today.plusDays(LOAN_PERIOD_DAYS), LoanStatus.ACTIVE);
        return toDto(loanRepository.save(loan));
    }

    @Transactional
    public LoanDto returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + loanId));

        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new BusinessRuleException("This loan has already been returned");
        }

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        return toDto(loan);
    }

    private LoanDto toDto(Loan loan) {
        LoanStatus effectiveStatus = loan.getStatus() == LoanStatus.ACTIVE
                && loan.getDueDate().isBefore(LocalDate.now())
                ? LoanStatus.OVERDUE
                : loan.getStatus();

        return new LoanDto(loan.getId(), loan.getBook().getId(), loan.getBook().getTitle(),
                loan.getMember().getId(), loan.getMember().getFirstName() + " " + loan.getMember().getLastName(),
                loan.getLoanDate(), loan.getDueDate(), loan.getReturnDate(), effectiveStatus);
    }
}
