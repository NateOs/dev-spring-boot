package com.luv2code.libraryapp.exception;

/**
 * Thrown when a request is well-formed but violates a domain rule
 * (e.g. checking out a book with no available copies).
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
