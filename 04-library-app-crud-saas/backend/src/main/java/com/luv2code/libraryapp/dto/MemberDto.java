package com.luv2code.libraryapp.dto;

import java.time.LocalDate;

public class MemberDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate joinDate;
    private long activeLoanCount;

    public MemberDto() {
    }

    public MemberDto(Long id, String firstName, String lastName, String email,
                      LocalDate joinDate, long activeLoanCount) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.joinDate = joinDate;
        this.activeLoanCount = activeLoanCount;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public long getActiveLoanCount() {
        return activeLoanCount;
    }
}
