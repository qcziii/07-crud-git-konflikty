package pl.kurs.biblioteka.loan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.biblioteka.book.BookService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    private static final String READER_EMAIL = "jan@example.com";

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private LoanService loanService;

    @Test
    void shouldNotTriggerDatabaseWhenLoanLimitExceeded() {
        Mockito.when(loanRepository.countByReaderEmail(READER_EMAIL)).thenReturn(3L);

        assertThrows(LoanLimitExceededException.class,
                () -> loanService.borrow(3L, READER_EMAIL));

        Mockito.verify(loanRepository).countByReaderEmail(READER_EMAIL);
        Mockito.verify(bookService, never()).findById(anyLong());
        Mockito.verify(loanRepository, never()).save(any(Loan.class));
    }
}