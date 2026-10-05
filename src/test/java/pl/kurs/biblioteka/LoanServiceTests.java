package pl.kurs.biblioteka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookService;
import pl.kurs.biblioteka.loan.LoanRepository;
import pl.kurs.biblioteka.loan.LoanService;

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
        //todo czy na pewno dobrze
        String readerEmail = "test@wp.pl";
        Book book = new Book("Czarny Łabędź", "0123", 2, new Author("Nassim Nicholas Taleb"));
        when(bookService.findById(1L)).thenReturn(book);

        loanService.borrow(1L, readerEmail);
        loanService.borrow(1L, readerEmail);

        assertThrows(IllegalStateException.class,
                () -> loanService.borrow(1L, readerEmail),
                "Brak wolnych egzemplarzy książki " + book.getTitle());
        assertEquals(0, book.getAvailableCopies());
        verify(loanRepository, times(2)).save(any());
    }
}
