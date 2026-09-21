package com.luv2code.libraryapp.dto;

public class BookDto {

    private Long id;
    private String title;
    private String isbn;
    private String genre;
    private Integer publishedYear;
    private Integer totalCopies;
    private Integer availableCopies;
    private Long authorId;
    private String authorName;

    public BookDto() {
    }

    public BookDto(Long id, String title, String isbn, String genre, Integer publishedYear,
                    Integer totalCopies, Integer availableCopies, Long authorId, String authorName) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.genre = genre;
        this.publishedYear = publishedYear;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.authorId = authorId;
        this.authorName = authorName;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }
}
