package pl.kurs.biblioteka.loan;

import jakarta.validation.Valid;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    public List<LoanResponse> getByReader(@RequestParam String readerEmail) {
        return loanService.findByReader(readerEmail)
                .stream()
                .map(LoanResponse::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<LoanResponse> borrow(@RequestBody @Valid LoanRequest request) {
        Loan created = loanService.borrow(request.bookId(), request.readerEmail());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .queryParam("readerEmail", created.getReaderEmail())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(LoanResponse.from(created));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void giveBack(@PathVariable Long id) {
        loanService.giveBack(id);
    }
}
