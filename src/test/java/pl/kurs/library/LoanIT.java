package pl.kurs.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kurs.library.author.AuthorRequest;
import pl.kurs.library.book.BookRepository;
import pl.kurs.library.book.BookRequest;
import pl.kurs.library.loan.LoanRepository;
import pl.kurs.library.loan.LoanRequest;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")


public class LoanIT {

    private static final int MAX_LOANS_PER_READER = 3;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final DatabaseCleaner databaseCleaner;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    @Autowired
    public LoanIT(MockMvc mockMvc, ObjectMapper objectMapper, DatabaseCleaner databaseCleaner, BookRepository bookRepository, LoanRepository loanRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.databaseCleaner = databaseCleaner;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    @BeforeEach
    void cleanDatabase() {
        databaseCleaner.clean();
    }

    @Test
    void shouldNotChangeDatabaseWhenBorrowOverLimitAndReturn409() throws Exception {
        // given
        Long authorId = createAuthor("Henryk Sienkiewicz");
        Long bookId = createBook("Potop", "978-83-08-04151-8", 10, authorId);
        LoanRequest loanRequest = new LoanRequest(bookId, "misio@o2.pl");

        for (int i = 0; i < MAX_LOANS_PER_READER; i++) {

            mockMvc.perform(post("/loans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loanRequest)))
                    .andExpect(status().isCreated());
        }
        int copiesBefore = bookRepository.findById(bookId).orElseThrow().getAvailableCopies();
        long loansBefore = loanRepository.countByReaderEmail("misio@o2.pl");

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());

        int copiesAfter = bookRepository.findById(bookId).orElseThrow().getAvailableCopies();
        long loansAfter = loanRepository.countByReaderEmail("misio@o2.pl");

        assertThat(copiesAfter).isEqualTo(copiesBefore);
        assertThat(loansAfter).isEqualTo(loansBefore);


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
