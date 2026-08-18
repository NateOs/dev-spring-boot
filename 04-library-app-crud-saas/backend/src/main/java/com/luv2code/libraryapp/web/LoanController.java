package com.luv2code.libraryapp.web;

import com.luv2code.libraryapp.dto.CheckoutRequest;
import com.luv2code.libraryapp.dto.LoanDto;
import com.luv2code.libraryapp.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<LoanDto> findAll(@RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        return activeOnly ? loanService.findActive() : loanService.findAll();
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDto checkout(@Valid @RequestBody CheckoutRequest request) {
        return loanService.checkout(request);
    }

    @PostMapping("/{id}/return")
    public LoanDto returnLoan(@PathVariable Long id) {
        return loanService.returnLoan(id);
    }
}
