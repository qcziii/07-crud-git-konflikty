package pl.kurs.biblioteka.loan;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import pl.kurs.biblioteka.book.BookRequest;
import pl.kurs.biblioteka.book.BookResponse;
import tools.jackson.databind.ObjectMapper;


import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoanControllerTest {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn200WithLoansOfReader() throws Exception {

        LoanRequest loanRequest = new LoanRequest(3L, "test@test.com");

        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest))
                )
                .andExpect(status().isCreated());

        postman.perform(get("/loans").param("readerEmail", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].bookId").value(3))
                .andExpect(jsonPath("$[0].readerEmail").value("test@test.com"));
    }

    @Test
    void shouldReturn201WithLocationWhenBookBorrowed() throws Exception {

        LoanRequest loanRequest = new LoanRequest(3L, "test@test.com");

        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/loans/")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.bookId").value(3))
                .andExpect(jsonPath("$.readerEmail").value("test@test.com"))
                .andExpect(jsonPath("$.loanDate").exists());
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {

        LoanRequest loanRequest = new LoanRequest(3L, "invalidEmail");

        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("readerEmail")));
    }

    @Test
    void shouldReturn404WhenBookDoesNotExist() throws Exception {

        LoanRequest loanRequest = new LoanRequest(999L, "test@test.com");

        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void shouldReturn409WhenNoCopiesAvailable() throws Exception {

        BookRequest bookRequest = new BookRequest("Test", "978-83-00-00001-1", 0, 1L);

        String bookResponseBody = postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookId = objectMapper.readValue(bookResponseBody, BookResponse.class).id();

        LoanRequest loanRequest = new LoanRequest(bookId, "test@test.com");

        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    void shouldReturn409WhenLoanLimitExceeded() throws Exception {

        BookRequest bookRequest = new BookRequest("Test", "978-83-00-00001-2", 10, 1L);

        String bookResponseBody = postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookId = objectMapper.readValue(bookResponseBody, BookResponse.class).id();

        LoanRequest loanRequest = new LoanRequest(bookId, "jan@example.com");
        String loanJson = objectMapper.writeValueAsString(loanRequest);

        postman.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content(loanJson))
                .andExpect(status().isCreated());
        postman.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content(loanJson))
                .andExpect(status().isCreated());
        postman.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content(loanJson))
                .andExpect(status().isCreated());

        postman.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content(loanJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value(containsString("limit")));
    }

    @Test
    void shouldReturn204WhenBookReturned() throws Exception {

        LoanRequest loanRequest = new LoanRequest(3L, "test@test.com");

        String responseBody = postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long loanId = objectMapper.readValue(responseBody, LoanResponse.class).id();

        postman.perform(delete("/loans/{id}", loanId))
                .andExpect(status().isNoContent());

        postman.perform(delete("/loans/{id}", loanId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenReturningNonExistingLoan() throws Exception {
        postman.perform(delete("/loans/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}