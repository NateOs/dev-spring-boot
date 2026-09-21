package com.luv2code.libraryapp.service;

import com.luv2code.libraryapp.dto.AuthorDto;
import com.luv2code.libraryapp.dto.AuthorRequest;
import com.luv2code.libraryapp.entity.Author;
import com.luv2code.libraryapp.exception.BusinessRuleException;
import com.luv2code.libraryapp.exception.ResourceNotFoundException;
import com.luv2code.libraryapp.repository.AuthorRepository;
import com.luv2code.libraryapp.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    // Constructor injection: Spring wires this automatically since there is
    // exactly one constructor, no @Autowired needed (see course module 02-01).
    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AuthorDto findById(Long id) {
        return toDto(getAuthorOrThrow(id));
    }

    @Transactional
    public AuthorDto create(AuthorRequest request) {
        Author author = new Author(request.getFirstName(), request.getLastName(), request.getBio());
        return toDto(authorRepository.save(author));
    }

    @Transactional
    public AuthorDto update(Long id, AuthorRequest request) {
        Author author = getAuthorOrThrow(id);
        author.setFirstName(request.getFirstName());
        author.setLastName(request.getLastName());
        author.setBio(request.getBio());
        return toDto(author);
    }

    @Transactional
    public void delete(Long id) {
        Author author = getAuthorOrThrow(id);
        if (bookRepository.existsByAuthorId(id)) {
            throw new BusinessRuleException(
                    "Cannot delete " + author.getFirstName() + " " + author.getLastName()
                            + " while they still have books in the catalog");
        }
        authorRepository.delete(author);
    }

    private Author getAuthorOrThrow(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
    }

    private AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(), author.getFirstName(), author.getLastName(),
                author.getBio(), author.getBooks().size());
    }
}
