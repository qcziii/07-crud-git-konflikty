package pl.kurs.biblioteka.loan;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<List<LoanResponse>> getByReader(@RequestParam String readerEmail) {
        return ResponseEntity.ok(loanService.findByReader(readerEmail));
    }

    @PostMapping
    public ResponseEntity<LoanResponse> borrow(@RequestBody @Valid LoanRequest request) {
        LoanResponse response = loanService.borrow(request.bookId(), request.readerEmail());
        return ResponseEntity.created(URI.create("/loans/" + response.id())).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> giveBack(@PathVariable Long id) {
        loanService.giveBack(id);
        return ResponseEntity.noContent().build();
    }
}
