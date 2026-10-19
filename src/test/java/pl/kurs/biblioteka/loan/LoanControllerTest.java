package pl.kurs.biblioteka.loan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorRepository;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoanControllerTest {

    private static final String READER_EMAIL = "jan@example.com";

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    private Author author;
    private Book book;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();

        author = authorRepository.save(
                new Author("Stanisław Lem")
        );

        book = bookRepository.save(
                new Book(
                        "Solaris",
                        "978-83-08-04907-1",
                        2,
                        author
                )
        );
    }

    @Test
    void shouldGetLoansByReader() throws Exception {
        // given
        Loan loan = loanRepository.save(
                new Loan(
                        book,
                        READER_EMAIL,
                        LocalDate.of(2026, 10, 19)
                )
        );

        loanRepository.save(
                new Loan(
                        book,
                        "anna@example.com",
                        LocalDate.of(2026, 10, 19)
                )
        );

        book.setAvailableCopies(0);
        bookRepository.save(book);

        // when + then
        postman.perform(get("/loans")
                        .param("readerEmail", READER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(loan.getId().intValue()))
                .andExpect(jsonPath("$[0].readerEmail").value(READER_EMAIL))
                .andExpect(jsonPath("$[0].loanDate").value("2026-10-19"))
                .andExpect(jsonPath("$[0].book.id")
                        .value(book.getId().intValue()))
                .andExpect(jsonPath("$[0].book.title").value("Solaris"))
                .andExpect(jsonPath("$[0].book.authorId")
                        .value(author.getId().intValue()))
                .andExpect(jsonPath("$[0].book.authorName")
                        .value("Stanisław Lem"))
                .andExpect(jsonPath("$[0].book.author").doesNotExist());
    }

    @Test
    void shouldReturnEmptyListWhenReaderHasNoLoans() throws Exception {


        // when + then
        postman.perform(get("/loans")
                        .param("readerEmail", READER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldBorrowBook() throws Exception {
        // given
        postman.perform(get("/loans")
                        .param("readerEmail", READER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        LoanRequest request = new LoanRequest(
                book.getId(),
                READER_EMAIL
        );

        // when
        MvcResult result = postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.readerEmail").value(READER_EMAIL))
                .andExpect(jsonPath("$.loanDate").isNotEmpty())
                .andExpect(jsonPath("$.book.id")
                        .value(book.getId().intValue()))
                .andExpect(jsonPath("$.book.title").value("Solaris"))
                .andExpect(jsonPath("$.book.availableCopies").value(1))
                .andExpect(jsonPath("$.book.authorId")
                        .value(author.getId().intValue()))
                .andExpect(jsonPath("$.book.author").doesNotExist())
                .andReturn();

        LoanResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                LoanResponse.class
        );

        // then
        header().string("Location", "/loans/" + response.id())
                .match(result);

        postman.perform(get("/loans")
                        .param("readerEmail", READER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(response.id().intValue()))
                .andExpect(jsonPath("$[0].readerEmail").value(READER_EMAIL))
                .andExpect(jsonPath("$[0].book.id")
                        .value(book.getId().intValue()));

        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(1));
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        // given
        LoanRequest request = new LoanRequest(
                book.getId(),
                "niepoprawny-email"
        );

        // when
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad", not(emptyOrNullString())));

        // then
        postman.perform(get("/loans")
                        .param("readerEmail", request.readerEmail()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(2));
    }

    @Test
    void shouldReturn400WhenEmailIsBlank() throws Exception {
        // given
        LoanRequest request = new LoanRequest(
                book.getId(),
                ""
        );

        // when
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad", not(emptyOrNullString())));

        // then
        postman.perform(get("/loans")
                        .param("readerEmail", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(2));
    }

    @Test
    void shouldReturn400WhenBookIdIsNull() throws Exception {
        // given
        LoanRequest request = new LoanRequest(
                null,
                READER_EMAIL
        );

        // when
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad", not(emptyOrNullString())));

        // then
        postman.perform(get("/loans")
                        .param("readerEmail", READER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        // given
        long missingBookId = Long.MAX_VALUE;

        LoanRequest request = new LoanRequest(
                missingBookId,
                READER_EMAIL
        );

        // when
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad").value(
                        String.format(
                                "Książka o id %d nie istnieje",
                                missingBookId
                        )
                ));

        // then
        postman.perform(get("/loans")
                        .param("readerEmail", READER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturn409WhenNoAvailableCopies() throws Exception {
        // given
        book.setAvailableCopies(0);
        bookRepository.save(book);

        LoanRequest request = new LoanRequest(
                book.getId(),
                READER_EMAIL
        );

        // when
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.blad").value(
                        "Brak wolnych egzemplarzy książki Solaris"
                ));

        // then
        postman.perform(get("/loans")
                        .param("readerEmail", READER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(0));
    }

    @Test
    void shouldGiveBackBook() throws Exception {
        // given
        book.setAvailableCopies(1);
        bookRepository.save(book);

        Loan loan = loanRepository.save(
                new Loan(book, READER_EMAIL, LocalDate.now())
        );

        postman.perform(get("/loans")
                        .param("readerEmail", READER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(loan.getId().intValue()));

        // when
        postman.perform(delete("/loans/{id}", loan.getId()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        // then
        postman.perform(get("/loans")
                        .param("readerEmail", READER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(2));


        postman.perform(delete("/loans/{id}", loan.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());

        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(2));
    }

    @Test
    void shouldReturn404WhenLoanNotFound() throws Exception {
        // given
        long missingLoanId = Long.MAX_VALUE;

        // when
        postman.perform(delete("/loans/{id}", missingLoanId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad").value(
                        String.format(
                                "Wypożyczenie o id %d nie istnieje",
                                missingLoanId
                        )
                ));

        // then
        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(2));
    }

    @Test
    void shouldAllowThirdLoan() throws Exception {
        //given
        loanRepository.save(new Loan(book, READER_EMAIL, LocalDate.now()));
        loanRepository.save(new Loan(book, READER_EMAIL, LocalDate.now()));

        book.setAvailableCopies(2);
        bookRepository.save(book);

        LoanRequest request = new LoanRequest(book.getId(), READER_EMAIL);
        //when
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.readerEmail").value(READER_EMAIL));

        //then
        postman.perform(get("/loans")
                .param("readerEmail", READER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(1));

    }

    @Test
    void shouldThrowExceptionWhenLimitIsExceededAndNotChangeDatabase() throws Exception {
        // given
        loanRepository.save(
                new Loan(book, READER_EMAIL, LocalDate.now())
        );
        loanRepository.save(
                new Loan(book, READER_EMAIL, LocalDate.now())
        );
        loanRepository.save(
                new Loan(book, READER_EMAIL, LocalDate.now())
        );

        book.setAvailableCopies(2);
        bookRepository.save(book);

        postman.perform(get("/loans")
                        .param("readerEmail", READER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(2));

        long loanCountBefore = loanRepository.count();

        LoanRequest request = new LoanRequest(
                book.getId(),
                READER_EMAIL
        );

        // when
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.blad").value(
                        String.format(
                                "Czytelnik %s przekroczył limit %d wypożyczeń",
                                READER_EMAIL,
                                3
                        )
                ));

        // then
        assertThat(loanRepository.count()).isEqualTo(loanCountBefore);
        assertThat(loanRepository.countByReaderEmail(READER_EMAIL))
                .isEqualTo(3L);

        Book bookAfterRequest = bookRepository.findById(book.getId())
                .orElseThrow();

        assertThat(bookAfterRequest.getAvailableCopies()).isEqualTo(2);


        postman.perform(get("/loans")
                        .param("readerEmail", READER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(2));
    }
}