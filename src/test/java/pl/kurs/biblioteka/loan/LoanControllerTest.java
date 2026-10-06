package pl.kurs.biblioteka.loan;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookRequest;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerTest {
    private MockMvc postman;
    private ObjectMapper objectMapper;

    @Autowired
    public LoanControllerTest(MockMvc postman, ObjectMapper objectMapper) {
        this.postman = postman;
        this.objectMapper = objectMapper;
    }

    @Test
    void shouldGetAllLoansByReader() throws Exception {
        // given
        String readerEmail = "test@gmail.com";
        LoanRequest loanRequest = new LoanRequest(1L, readerEmail);
        LoanRequest loanRequest2 = new LoanRequest(2L, readerEmail);

        // when
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.book.id").value(1))
                .andExpect(jsonPath("$.book.title").value("Solaris"))
                .andExpect(jsonPath("$.book.isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$.readerEmail").value(readerEmail))
                .andExpect(header().exists("Location"));


        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.book.id").value(2))
                .andExpect(jsonPath("$.book.title").value("Cyberiada"))
                .andExpect(jsonPath("$.book.isbn").value("978-83-08-06175-2"))
                .andExpect(jsonPath("$.readerEmail").value(readerEmail))
                .andExpect(header().exists("Location"));



        // then
        postman.perform(get("/loans")
                        .param("readerEmail", readerEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].book.id").value(1))
                .andExpect(jsonPath("$[0].book.title").value("Solaris"))
                .andExpect(jsonPath("$[0].book.isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$[0].readerEmail").value(readerEmail))
                .andExpect(jsonPath("$[1].book.id").value(2))
                .andExpect(jsonPath("$[1].book.title").value("Cyberiada"))
                .andExpect(jsonPath("$[1].book.isbn").value("978-83-08-06175-2"))
                .andExpect(jsonPath("$[1].readerEmail").value(readerEmail));
    }

    @Test
    void shouldBorrowAndReturnLoanResponse() throws Exception {
        // given
        Long bookId = 3L;
        String readerEmail = "test@gmail.com";

        LoanRequest loanRequest = new LoanRequest(bookId, readerEmail);

        // when \ then
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(jsonPath("$.book.id").value(3))
                .andExpect(jsonPath("$.book.title").value("Bieguni"))
                .andExpect(jsonPath("$.book.isbn").value("978-83-08-04151-8"))
                .andExpect(jsonPath("$.readerEmail").value(readerEmail))
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldGive404StatusWhenBookDoesNotExists() throws Exception {
        // given
        Long bookId = 555L;
        String readerEmail = "test242@gmail.com";

        LoanRequest loanRequest = new LoanRequest(bookId, readerEmail);

        // when \ then
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGive409StatusWhenThereIsNoAvailablePieces() throws Exception {
        // given
        Long bookId = 4L;
        String readerEmail = "test2424@gmail.com";

        BookRequest bookRequest = new BookRequest("Maly Ksiaze", "999-000-242", 0, 2L);
        LoanRequest loanRequest = new LoanRequest(bookId, readerEmail);

        // when
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated());

        // then
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldGive409StatusWhenUserHasAlreadyMaximumAvailableLoans() throws Exception {
        // given
        Long bookId = 3L;
        String readerEmail = "test2424@gmail.com";

        LoanRequest loanRequest = new LoanRequest(bookId, readerEmail);

        //when
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isCreated());

        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isCreated());

        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isCreated());

        //then
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isConflict());

        postman.perform(get("/loans").param("readerEmail","test2424@gmail.com"))
                .andExpect(jsonPath("$", hasSize(3)));
    }
}
