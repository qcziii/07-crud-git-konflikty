package pl.kurs.biblioteka.loan;

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
import pl.kurs.biblioteka.mapper.LoanMapper;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;

    public LoanController(LoanService loanService, LoanMapper loanMapper) {
        this.loanService = loanService;
        this.loanMapper = loanMapper;
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getByReader(@RequestParam String readerEmail) {
        return ResponseEntity.ok(loanService.findResponseByReader(readerEmail));
    }

    @PostMapping
    public  ResponseEntity<LoanResponse> borrow(@RequestBody LoanRequest request) throws LoanLimitExceededException {
        Loan loan = loanService.borrow(
                request.bookId(),
                request.readerEmail()
        );
        URI location = URI.create("/loans" + loan.getId());

        return ResponseEntity.created(location).body(loanMapper.fromLoan(loan));
    }

    @DeleteMapping("/{id}")
    public void giveBack(@PathVariable Long id) {
        loanService.giveBack(id);
    }
}
