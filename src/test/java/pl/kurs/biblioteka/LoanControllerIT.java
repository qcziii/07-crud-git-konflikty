package pl.kurs.biblioteka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorRepository;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookRepository;
import pl.kurs.biblioteka.loan.Loan;
import pl.kurs.biblioteka.loan.LoanRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoanControllerIT {

    private static final long MISSING_ID = 999999L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    private Book availableBook;
    private Book unavailableBook;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();

        Author author = authorRepository.save(
                Author.builder()
                        .name("Olga Tokarczuk")
                        .build()
        );

        availableBook = bookRepository.save(
                Book.builder()
                        .title("Bieguni")
                        .isbn("978-83-08-04151-8")
                        .availableCopies(5)
                        .author(author)
                        .build()
        );

        unavailableBook = bookRepository.save(
                Book.builder()
                        .title("Brak egzemplarzy")
                        .isbn("978-83-99-99999-1")
                        .availableCopies(0)
                        .author(author)
                        .build()
        );
    }

    @Test
    void getLoansByReaderShouldReturn200WithoutRecursiveBookObject()
            throws Exception {

        loanRepository.save(
                Loan.builder()
                        .book(availableBook)
                        .readerEmail("jan@example.com")
                        .loanDate(LocalDate.now())
                        .build()
        );

        mockMvc.perform(get("/loans")
                        .param(
                                "readerEmail",
                                "jan@example.com"
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].bookId")
                        .value(
                                availableBook
                                        .getId()
                                        .intValue()
                        ))
                .andExpect(jsonPath("$[0].bookTitle")
                        .value("Bieguni"))
                .andExpect(jsonPath("$[0].readerEmail")
                        .value("jan@example.com"))
                .andExpect(jsonPath("$[0].book")
                        .doesNotExist());
    }

    @Test
    void createLoanShouldReturn201AndLocationHeader()
            throws Exception {

        String body = """
                {
                  "bookId": %d,
                  "readerEmail": "adam@example.com"
                }
                """.formatted(availableBook.getId());

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header()
                        .string("Location", startsWith("/loans/")))
                .andExpect(jsonPath("$.bookId")
                        .value(
                                availableBook
                                        .getId()
                                        .intValue()
                        ))
                .andExpect(jsonPath("$.bookTitle")
                        .value("Bieguni"))
                .andExpect(jsonPath("$.readerEmail")
                        .value("adam@example.com"));

        assertEquals(
                1,
                loanRepository
                        .countByReaderEmail("adam@example.com")
        );

        assertEquals(
                4,
                bookRepository
                        .findById(availableBook.getId())
                        .orElseThrow()
                        .getAvailableCopies()
        );
    }

    @Test
    void createLoanShouldReturn400ForInvalidData()
            throws Exception {

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookId": null,
                                  "readerEmail": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad").exists());
    }

    @Test
    void createLoanShouldReturn400ForInvalidEmail()
            throws Exception {

        String body = """
                {
                  "bookId": %d,
                  "readerEmail": "to-nie-jest-email"
                }
                """.formatted(availableBook.getId());

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad").exists());
    }

    @Test
    void createLoanShouldReturn404WhenBookDoesNotExist()
            throws Exception {

        String body = """
                {
                  "bookId": %d,
                  "readerEmail": "jan@example.com"
                }
                """.formatted(MISSING_ID);

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad")
                        .value(
                                "Book not found with id: "
                                        + MISSING_ID
                        ));
    }

    @Test
    void createLoanShouldReturn409WhenNoCopiesAreAvailable()
            throws Exception {

        String body = """
                {
                  "bookId": %d,
                  "readerEmail": "jan@example.com"
                }
                """.formatted(unavailableBook.getId());

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.blad")
                        .value(
                                "Brak wolnych egzemplarzy książki "
                                        + "Brak egzemplarzy"
                        ));
    }

    @Test
    void createLoanOverLimitShouldReturn409AndNotChangeDatabase()
            throws Exception {

        String readerEmail = "jan@example.com";

        borrowBook(
                availableBook.getId(),
                readerEmail
        );

        borrowBook(
                availableBook.getId(),
                readerEmail
        );

        borrowBook(
                availableBook.getId(),
                readerEmail
        );

        int copiesBeforeFailedLoan =
                bookRepository
                        .findById(availableBook.getId())
                        .orElseThrow()
                        .getAvailableCopies();

        long loansBeforeFailedLoan =
                loanRepository
                        .countByReaderEmail(readerEmail);

        String body = """
                {
                  "bookId": %d,
                  "readerEmail": "%s"
                }
                """.formatted(
                availableBook.getId(),
                readerEmail
        );

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.blad")
                        .value(
                                "Czytelnik jan@example.com "
                                        + "przekroczył limit 3 wypożyczeń"
                        ));

        int copiesAfterFailedLoan =
                bookRepository
                        .findById(availableBook.getId())
                        .orElseThrow()
                        .getAvailableCopies();

        long loansAfterFailedLoan =
                loanRepository
                        .countByReaderEmail(readerEmail);

        assertEquals(
                2,
                copiesBeforeFailedLoan
        );

        assertEquals(
                3,
                loansBeforeFailedLoan
        );

        assertEquals(
                copiesBeforeFailedLoan,
                copiesAfterFailedLoan
        );

        assertEquals(
                loansBeforeFailedLoan,
                loansAfterFailedLoan
        );
    }

    @Test
    void deleteLoanShouldReturn204AndGiveBookBack()
            throws Exception {

        String readerEmail = "return@example.com";

        borrowBook(
                availableBook.getId(),
                readerEmail
        );

        Loan loan = loanRepository
                .findByReaderEmail(readerEmail)
                .get(0);

        assertEquals(
                4,
                bookRepository
                        .findById(availableBook.getId())
                        .orElseThrow()
                        .getAvailableCopies()
        );

        mockMvc.perform(
                        delete(
                                "/loans/{id}",
                                loan.getId()
                        )
                )
                .andExpect(status().isNoContent());

        assertFalse(
                loanRepository.existsById(
                        loan.getId()
                )
        );

        assertEquals(
                5,
                bookRepository
                        .findById(availableBook.getId())
                        .orElseThrow()
                        .getAvailableCopies()
        );
    }

    @Test
    void deleteLoanShouldReturn404WhenLoanDoesNotExist()
            throws Exception {

        mockMvc.perform(
                        delete(
                                "/loans/{id}",
                                MISSING_ID
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad")
                        .value(
                                "Loan not found with id: "
                                        + MISSING_ID
                        ));
    }

    private void borrowBook(
            Long bookId,
            String readerEmail
    ) throws Exception {

        String body = """
                {
                  "bookId": %d,
                  "readerEmail": "%s"
                }
                """.formatted(
                bookId,
                readerEmail
        );

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }
}