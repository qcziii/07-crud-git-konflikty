package pl.kurs.biblioteka.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookRepository;
import pl.kurs.biblioteka.book.BookService;
import pl.kurs.biblioteka.common.BookNotFoundException;
import pl.kurs.biblioteka.common.BookUnavailableException;
import pl.kurs.biblioteka.common.LoanNotFoundException;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LoanService {

    private static final int MAX_LOANS_PER_READER = 3;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public List<LoanResponse> findByReader(String readerEmail) {
        return loanRepository.findByReaderEmail(readerEmail)
                .stream()
                .map(LoanResponse::from)
                .toList();
    }

    @Transactional
    public LoanResponse borrow(Long bookId, String readerEmail) throws LoanLimitExceededException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new BookNotFoundException("Book not found with id: " + bookId)
                );

        if (book.getAvailableCopies() == 0) {
            throw new BookUnavailableException(
                    "Brak wolnych egzemplarzy książki " + book.getTitle()
            );
        }

        if (loanRepository.countByReaderEmail(readerEmail) >= MAX_LOANS_PER_READER) {
            throw new LoanLimitExceededException(readerEmail, MAX_LOANS_PER_READER);
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Loan loan = loanRepository.save(
                Loan.builder()
                        .book(book)
                        .readerEmail(readerEmail)
                        .loanDate(LocalDate.now())
                        .build()
        );
        return LoanResponse.from(loan);
    }

    @Transactional
    public void giveBack(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new LoanNotFoundException("Loan not found with id: " + loanId)
                );
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        loanRepository.delete(loan);
    }
}
