package pl.kurs.biblioteka.loan;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getByReader(@RequestParam String readerEmail) {
        List<Loan> loans = loanService.findByReader(readerEmail);
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Loan> borrow(@Valid @RequestBody LoanRequest request) throws LoanLimitExceededException {
        Loan loan = loanService.borrow(request.bookId(), request.readerEmail());
        URI location = URI.create("/loans/" + loan.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(loan, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void giveBack(@PathVariable Long id) {
        loanService.giveBack(id);
    }
}
