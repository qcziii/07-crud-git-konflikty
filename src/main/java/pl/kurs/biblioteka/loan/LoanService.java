package pl.kurs.biblioteka.loan;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private static final int MAX_LOANS_PER_READER = 3;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }


    public List<LoanResponse> findByReader(String readerEmail) {
        List<Loan> loans = loanRepository.findByReaderEmail(readerEmail);
        return loans.stream().map(LoanResponse::from).toList();
    }

    @Transactional
    public LoanResponse borrow(Long bookId, String readerEmail) throws LoanLimitExceededException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book with id: " + bookId + " do not exist"));
        if (book.getAvailableCopies() == 0) {
            throw new IllegalStateException("Brak wolnych egzemplarzy książki " + book.getTitle());
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Loan savedLoan = loanRepository.save(new Loan(book, readerEmail, LocalDate.now()));
        if (loanRepository.countByReaderEmail(readerEmail) > MAX_LOANS_PER_READER) {
            throw new LoanLimitExceededException(readerEmail, MAX_LOANS_PER_READER);
        }
        return LoanResponse.from(savedLoan);
    }

    @Transactional
    public void giveBack(Long loanId) {
        Loan loan = loanRepository.findById(loanId).get();
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        loanRepository.delete(loan);
    }
}
