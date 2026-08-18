package com.luv2code.libraryapp.repository;

import com.luv2code.libraryapp.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
