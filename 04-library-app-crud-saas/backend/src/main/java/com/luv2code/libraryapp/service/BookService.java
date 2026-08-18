package com.luv2code.libraryapp.service;

import com.luv2code.libraryapp.dto.BookDto;
import com.luv2code.libraryapp.dto.BookRequest;
import com.luv2code.libraryapp.entity.Author;
import com.luv2code.libraryapp.entity.Book;
import com.luv2code.libraryapp.exception.BusinessRuleException;
import com.luv2code.libraryapp.exception.ResourceNotFoundException;
import com.luv2code.libraryapp.repository.AuthorRepository;
import com.luv2code.libraryapp.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public Page<BookDto> search(String query, Pageable pageable) {
        String term = StringUtils.hasText(query) ? query.trim() : "";
        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthor_LastNameContainingIgnoreCase(term, term, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public BookDto findById(Long id) {
        return toDto(getBookOrThrow(id));
    }

    @Transactional
    public BookDto create(BookRequest request) {
        Author author = getAuthorOrThrow(request.getAuthorId());
        Book book = new Book(request.getTitle(), request.getIsbn(), request.getGenre(),
                request.getPublishedYear(), request.getTotalCopies(), request.getTotalCopies(), author);
        return toDto(bookRepository.save(book));
    }

    @Transactional
    public BookDto update(Long id, BookRequest request) {
        Book book = getBookOrThrow(id);
        int copiesOnLoan = book.getTotalCopies() - book.getAvailableCopies();
        if (request.getTotalCopies() < copiesOnLoan) {
            throw new BusinessRuleException(
                    "Cannot set total copies below " + copiesOnLoan + " while that many are on loan");
        }

        Author author = getAuthorOrThrow(request.getAuthorId());
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setGenre(request.getGenre());
        book.setPublishedYear(request.getPublishedYear());
        book.setAvailableCopies(request.getTotalCopies() - copiesOnLoan);
        book.setTotalCopies(request.getTotalCopies());
        book.setAuthor(author);
        return toDto(book);
    }

    @Transactional
    public void delete(Long id) {
        Book book = getBookOrThrow(id);
        if (!book.getAvailableCopies().equals(book.getTotalCopies())) {
            throw new BusinessRuleException("Cannot delete \"" + book.getTitle() + "\" while copies are on loan");
        }
        bookRepository.delete(book);
    }

    private Book getBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    private Author getAuthorOrThrow(Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + authorId));
    }

    private BookDto toDto(Book book) {
        return new BookDto(book.getId(), book.getTitle(), book.getIsbn(), book.getGenre(),
                book.getPublishedYear(), book.getTotalCopies(), book.getAvailableCopies(),
                book.getAuthor().getId(),
                book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName());
    }
}
