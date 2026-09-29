package pl.kurs.biblioteka.loan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.biblioteka.book.BookNotFoundException;
import pl.kurs.biblioteka.book.BookResponseDetails;
import pl.kurs.biblioteka.book.NoFreeBookException;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    private LoanResponse biegunLoan;
    private LoanResponse solarisLoan;

    @BeforeEach
    void init() {
        biegunLoan = new LoanResponse(1L,
                new BookResponseDetails(3L, "Bieguni", "978-83-08-04151-8", 4),
                "jan@example.com", LocalDate.of(2026, 9, 29));
        solarisLoan = new LoanResponse(2L,
                new BookResponseDetails(1L, "Solaris", "978-83-08-04907-1", 1),
                "jan@example.com", LocalDate.of(2026, 9, 29));
    }

    @Test
    void shouldGetLoansByReader() throws Exception {
        when(loanService.findByReader("jan@example.com")).thenReturn(List.of(biegunLoan, solarisLoan));

        mockMvc.perform(MockMvcRequestBuilders.get("/loans")
                        .param("readerEmail", "jan@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].book.title").value("Bieguni"))
                .andExpect(jsonPath("$[0].readerEmail").value("jan@example.com"))
                .andExpect(jsonPath("$[0].loanDate").value("2026-09-29"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].book.title").value("Solaris"));

        verify(loanService, times(1)).findByReader("jan@example.com");
    }

    @Test
    void shouldBorrowBook() throws Exception {
        when(loanService.borrow(3L, "jan@example.com")).thenReturn(biegunLoan);

        mockMvc.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "bookId": 3,
                                    "readerEmail": "jan@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/loans/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.book.id").value(3))
                .andExpect(jsonPath("$.book.title").value("Bieguni"))
                .andExpect(jsonPath("$.readerEmail").value("jan@example.com"));

        verify(loanService, times(1)).borrow(3L, "jan@example.com");
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "bookId": 3,
                                    "readerEmail": "xd"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("readerEmail"));

        verify(loanService, never()).borrow(anyLong(), anyString());
    }

    @Test
    void shouldReturn404WhenBookNotFoundOnBorrow() throws Exception {
        when(loanService.borrow(99L, "jan@example.com")).thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "bookId": 99,
                                    "readerEmail": "jan@example.com"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nie znaleziono książki o id: 99"));

        verify(loanService, times(1)).borrow(99L, "jan@example.com");
    }

    @Test
    void shouldReturn409WhenNoFreeCopies() throws Exception {
        when(loanService.borrow(eq(2L), anyString()))
                .thenThrow(new NoFreeBookException("Brak wolnych egzemplarzy książki Cyberiada"));

        mockMvc.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "bookId": 2,
                                    "readerEmail": "jan@example.com"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Brak wolnych egzemplarzy książki Cyberiada"));

        verify(loanService, times(1)).borrow(2L, "jan@example.com");
    }

    @Test
    void shouldReturn409WhenLoanLimitExceeded() throws Exception {
        when(loanService.borrow(3L, "jan@example.com"))
                .thenThrow(new LoanLimitExceededException("jan@example.com", 3));

        mockMvc.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "bookId": 3,
                                    "readerEmail": "jan@example.com"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Czytelnik jan@example.com przekroczył limit 3 wypożyczeń"));

        verify(loanService, times(1)).borrow(3L, "jan@example.com");
    }

    @Test
    void shouldGiveBackBook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/loans/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(loanService, times(1)).giveBack(1L);
    }

    @Test
    void shouldReturn404WhenLoanNotFound() throws Exception {
        doThrow(new LoanNotFoundException("Nie znaleziono wypożyczenia o id 99"))
                .when(loanService).giveBack(99L);

        status().isNoContent();
        mockMvc.perform(MockMvcRequestBuilders.delete("/loans/{id}", 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nie znaleziono wypożyczenia o id 99"));

        verify(loanService, times(1)).giveBack(99L);
    }
}