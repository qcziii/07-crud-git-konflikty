package pl.kurs.library.loan;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.library.book.Book;
import pl.kurs.library.book.BookRepository;
import pl.kurs.library.exception.BookAvailableException;
import pl.kurs.library.exception.BookNotFoundException;
import pl.kurs.library.exception.LoanLimitExceededException;
import pl.kurs.library.exception.LoanNotFoundException;

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

    public List<Loan> findByReader(String readerEmail) {
        return loanRepository.findByReaderEmail(readerEmail);
    }

    @Transactional
    public Loan borrow(Long bookId, String readerEmail) throws LoanLimitExceededException {

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        if (book.getAvailableCopies() == 0) {
            throw new BookAvailableException(bookId);
        }
        if (loanRepository.countByReaderEmail(readerEmail) >= MAX_LOANS_PER_READER) {
            throw new LoanLimitExceededException(readerEmail, MAX_LOANS_PER_READER);
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return loanRepository.save(new Loan(book, readerEmail, LocalDate.now()));
    }

    @Transactional
    public void giveBack(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new LoanNotFoundException(loanId));
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        loanRepository.delete(loan);
    }
}
