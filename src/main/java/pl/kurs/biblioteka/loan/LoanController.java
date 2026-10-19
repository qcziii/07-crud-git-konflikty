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
import pl.kurs.biblioteka.author.AuthorService;
import pl.kurs.biblioteka.book.BookService;
import pl.kurs.biblioteka.book.BookShortView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final AuthorService authorService;

    public LoanController(LoanService loanService, BookService bookService, AuthorService authorService) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @GetMapping
    public List<LoanResponse> getByReader(@RequestParam String readerEmail) {
        List<Loan> loan = loanService.findByReader(readerEmail);

        List<LoanResponse> loanResponses = new ArrayList<>();

        for (Loan loan1 : loan) {
            BookShortView bookShortView1 = bookService.findProjectedById(loan1.getBook().getId());
            loanResponses.add(LoanResponse.from(loan1, bookShortView1));
        }

        return loanResponses;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> borrow(@Valid @RequestBody LoanRequest request) throws LoanLimitExceededException {

        Loan loan = loanService.borrow(request.bookId(), request.readerEmail());

        BookShortView bookShortView = bookService.findProjectedById(request.bookId());

        URI location = URI.create("/loan/" + loan.getId());

        return ResponseEntity.created(location).body(LoanResponse.from(loan, bookShortView));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> giveBack(@PathVariable Long id) {
        loanService.giveBack(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
