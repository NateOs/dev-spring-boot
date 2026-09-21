package com.luv2code.libraryapp.dto;

import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotNull(message = "Book is required")
    private Long bookId;

    @NotNull(message = "Member is required")
    private Long memberId;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
