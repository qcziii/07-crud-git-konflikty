package pl.kurs.library.loan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kurs.library.DatabaseCleaner;
import pl.kurs.library.author.AuthorRequest;
import pl.kurs.library.book.BookRequest;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoanControllerIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final DatabaseCleaner databaseCleaner;

    @Autowired
    LoanControllerIT(MockMvc mockMvc, ObjectMapper objectMapper, DatabaseCleaner databaseCleaner) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.databaseCleaner = databaseCleaner;
    }

    @BeforeEach
    void cleanDatabase() {
        databaseCleaner.clean();
    }

    @Test
    void shouldGetLoansByReader() throws Exception {
        // given
        String readerEmail = "misio@o2.pl";
        Long authorId = createAuthor("Henryk Sienkiewicz");
        Long bookId = createBook("Potop", "978-83-08-04151-8", 2, authorId);

        LoanRequest loanRequest = new LoanRequest(bookId, readerEmail);

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isCreated());

        // when then
        mockMvc.perform(get("/loans").param("readerEmail", readerEmail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].bookId").value(bookId))
                .andExpect(jsonPath("$[0].readerEmail").value(readerEmail));
    }

    @Test
    void shouldCreateLoanAndReturnLocationHeader() throws Exception {
        // given
        Long authorId = createAuthor("Henryk Sienkiewicz");
        Long bookId = createBook("Potop", "978-83-08-04151-8", 2, authorId);
        LoanRequest loanRequest = new LoanRequest(bookId, "misio@o2.pl");

        MvcResult result = mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertThat(location).contains("/loans");

    }

    @Test
    void shouldReturn404_whenBookDoesNotExist() throws Exception {
        // given
        LoanRequest loanRequest = new LoanRequest(999L, "misio@o2.pl");
        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                loanRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturn409_whenNoAvailableCopies() throws Exception {
        Long authorId = createAuthor("Henryk Sienkiewicz");
        Long bookId = createBook("Potop", "978-83-08-04151-8", 0, authorId);
        LoanRequest loanRequest = new LoanRequest(bookId, "misio@o2.pl");
        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                loanRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturn409_whenLoanLimitExceeded() throws Exception {
        Long authorId = createAuthor("Henryk Sienkiewicz");
        Long bookId = createBook("Potop", "978-83-08-04151-8", 10, authorId);  // ← dużo kopii
        LoanRequest loanRequest = new LoanRequest(bookId, "misio@o2.pl");

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/loans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loanRequest)))
                    .andExpect(status().isCreated());
        }

        // when
        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturn400_whenReaderEmailIsBlank() throws Exception {
        Long authorId = createAuthor("Henryk Sienkiewicz");
        Long bookId = createBook("Potop", "978-83-08-04151-8", 2, authorId);
        LoanRequest loanRequest = new LoanRequest(bookId, " ");

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.readerEmail").exists());
    }

    @Test
    void shouldReturnLoan() throws Exception {
        // given
        Long authorId = createAuthor("Henryk Sienkiewicz");
        Long bookId = createBook("Potop", "978-83-08-04151-8", 1, authorId);
        LoanRequest loanRequest = new LoanRequest(bookId, "misio@o2.pl");

        MvcResult loanResult = mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long loanId = objectMapper.readTree(loanResult.getResponse().getContentAsString())
                .get("id").asLong();

        // when then
        mockMvc.perform(delete("/loans/{id}", loanId))
                .andDo(print())
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/loans/{id}", loanId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404_whenReturningNonExistentLoan() throws Exception {
        mockMvc.perform(delete("/loans/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }



    private Long createBook(String title, String isbn, int copies, Long authorId) throws Exception {
        BookRequest request = new BookRequest(title, isbn, copies, authorId);
        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    private Long createAuthor(String name) throws Exception {
        AuthorRequest request = new AuthorRequest(name);
        MvcResult result = mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }


}