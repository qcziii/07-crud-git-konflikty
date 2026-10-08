package pl.kurs.biblioteka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pl.kurs.biblioteka.book.BookRepository;
import pl.kurs.biblioteka.loan.LoanRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoanLimitTest {

    private static final String READER = "limit@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void exceedingLoanLimitDoesNotChangeDatabase() throws Exception {
        long bookId = createBookWithCopies(5);

        borrow(bookId, READER).andExpect(status().isCreated());
        borrow(bookId, READER).andExpect(status().isCreated());
        borrow(bookId, READER).andExpect(status().isCreated());

        long loansBefore = loanRepository.countByReaderEmail(READER);
        int copiesBefore = bookRepository.findById(bookId).orElseThrow().getAvailableCopies();
        assertThat(loansBefore).isEqualTo(3);
        assertThat(copiesBefore).isEqualTo(2);

        borrow(bookId, READER)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.blad").value(
                        "Czytelnik " + READER + " przekroczył limit 3 wypożyczeń"));

        assertThat(loanRepository.countByReaderEmail(READER)).isEqualTo(loansBefore);
        assertThat(bookRepository.findById(bookId).orElseThrow().getAvailableCopies())
                .isEqualTo(copiesBefore);

        mockMvc.perform(get("/loans").param("readerEmail", READER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    private long createBookWithCopies(int copies) throws Exception {
        MvcResult created = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Limit test","isbn":"978-44-00-00000-1","availableCopies":%d,"authorId":1}
                                """.formatted(copies)))
                .andExpect(status().isCreated())
                .andReturn();
        String location = created.getResponse().getHeader("Location");
        return Long.parseLong(location.replaceFirst(".*/", ""));
    }

    private ResultActions borrow(long bookId, String email) throws Exception {
        return mockMvc.perform(post("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"bookId\":%d,\"readerEmail\":\"%s\"}".formatted(bookId, email)));
    }
}
