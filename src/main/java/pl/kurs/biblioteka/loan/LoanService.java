package pl.kurs.biblioteka.loan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookService;
import pl.kurs.biblioteka.common.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final int maxLoansPerReader;

    public LoanService(LoanRepository loanRepository,
                       BookService bookService,
                       @Value("${biblioteka.wypozyczenia.limit-na-czytelnika}") int maxLoansPerReader) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
        this.maxLoansPerReader = maxLoansPerReader;
    }

    public List<Loan> findByReader(String readerEmail) {
        return loanRepository.findByReaderEmail(readerEmail);
    }

    @Transactional
    public Loan borrow(Long bookId, String readerEmail) throws LoanLimitExceededException {
        Book book = bookService.findById(bookId);
        if (book.getAvailableCopies() == 0) {
            throw new IllegalStateException("Brak wolnych egzemplarzy książki " + book.getTitle());
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Loan loan = loanRepository.save(new Loan(book, readerEmail, LocalDate.now()));

        long activeLoans = loanRepository.countByReaderEmail(readerEmail);
        if (activeLoans > maxLoansPerReader) {
            throw new LoanLimitExceededException(readerEmail, maxLoansPerReader);
        }
        return loan;
    }

    @Transactional
    public void giveBack(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono wypożyczenia " + loanId));
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        loanRepository.delete(loan);
    }
}
