package com.luv2code.libraryapp.dto;

public class AuthorDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String bio;
    private int bookCount;

    public AuthorDto() {
    }

    public AuthorDto(Long id, String firstName, String lastName, String bio, int bookCount) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.bookCount = bookCount;
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

    public String getBio() {
        return bio;
    }

    public int getBookCount() {
        return bookCount;
    }
}
