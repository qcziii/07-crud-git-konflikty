package pl.kurs.biblioteka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kurs.biblioteka.book.BookService;
import pl.kurs.biblioteka.loan.LoanLimitExceededException;
import pl.kurs.biblioteka.loan.LoanRepository;
import pl.kurs.biblioteka.loan.LoanService;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoanServiceTest {
    private LoanRepository loanRepository;
    private BookService bookService;
    private LoanService sut;

    @BeforeEach
    void setUp() {
        loanRepository = mock(LoanRepository.class);
        bookService = mock(BookService.class);
        sut = new LoanService(loanRepository, bookService);

    }

    @Test
    void borrowingOverLimitDoesNotTouchDataBase() {
        // givenm
        String reader = "test.example.com";
        when(loanRepository.countByReaderEmail(reader)).thenReturn(3L);

        // when
        assertThatThrownBy(() -> sut.borrow(1L, reader)).isInstanceOf(LoanLimitExceededException.class);

        //then
        verify(loanRepository, never()).save(any());
        verify(bookService, never()).findById(any());

    }
}
