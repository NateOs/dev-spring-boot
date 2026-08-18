package com.luv2code.libraryapp.repository;

import com.luv2code.libraryapp.entity.Loan;
import com.luv2code.libraryapp.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByStatusOrderByDueDateAsc(LoanStatus status);

    long countByMemberIdAndStatus(Long memberId, LoanStatus status);

    List<Loan> findByMemberIdOrderByLoanDateDesc(Long memberId);

    List<Loan> findByBookIdOrderByLoanDateDesc(Long bookId);
}
