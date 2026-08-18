package com.luv2code.libraryapp.repository;

import com.luv2code.libraryapp.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Spring Data derives the query from the method name: match either the
    // title or the author's last name, case-insensitively, and page the result.
    Page<Book> findByTitleContainingIgnoreCaseOrAuthor_LastNameContainingIgnoreCase(
            String title, String authorLastName, Pageable pageable);

    boolean existsByAuthorId(Long authorId);
}
