package pl.kurs.biblioteka.loan;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kurs.biblioteka.book.BookRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class LoanServiceIntegrationTest {

    private static final String READER_EMAIL = "jan@example.com";

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldNotChangeDatabaseWhenLoanLimitExceeded() {
        loanService.borrow(1L, READER_EMAIL);
        loanService.borrow(2L, READER_EMAIL);
        loanService.borrow(3L, READER_EMAIL);

        int copiesBefore = bookRepository.findById(3L).orElseThrow().getAvailableCopies();

        assertThrows(LoanLimitExceededException.class,
                () -> loanService.borrow(3L, READER_EMAIL));

        assertEquals(3, loanRepository.countByReaderEmail(READER_EMAIL));
        assertEquals(copiesBefore, bookRepository.findById(3L).orElseThrow().getAvailableCopies());
    }
}
