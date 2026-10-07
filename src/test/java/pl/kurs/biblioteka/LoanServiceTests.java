package pl.kurs.biblioteka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookService;
import pl.kurs.biblioteka.loan.Loan;
import pl.kurs.biblioteka.loan.LoanLimitExceededException;
import pl.kurs.biblioteka.loan.LoanRepository;
import pl.kurs.biblioteka.loan.LoanService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTests {

    @Mock
    BookService bookService;

    @Mock
    LoanRepository loanRepository;

    @InjectMocks
    LoanService loanService;

    @Test
    void shouldDoesNotChangeDatabaseAfterLoanExceedLimit() {
        //test, który udowadnia, że wypożyczenie ponad limit nie zmienia stanu bazy.
        //given
        String readerEmail = "test@wp.pl";
        Book book = new Book("Czarny Łabędź", "0123", 5, new Author("Nassim Nicholas Taleb"));
        when(bookService.findById(1L)).thenReturn(book);
        List<Loan> loans = new ArrayList<>();
        loans.add(loanService.borrow(1L, readerEmail));
        loans.add(loanService.borrow(1L, readerEmail));
        loans.add(loanService.borrow(1L, readerEmail));

        //when
        when(loanRepository.countByReaderEmail(readerEmail)).thenReturn(loans.stream().count());

        //then
        assertThrows(LoanLimitExceededException.class,
                () -> loanService.borrow(1L, readerEmail),
                "Czytelnik " + readerEmail + " przekroczył limit 3 wypożyczeń");
        assertEquals(3, loanRepository.countByReaderEmail(readerEmail));
        verify(loanRepository, times(3)).save(any());
    }
}
