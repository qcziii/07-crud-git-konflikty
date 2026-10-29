package pl.kurs.biblioteka.loan;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Integer createBook(int copies) throws Exception {
        String title = UUID.randomUUID().toString();
        String isbn = UUID.randomUUID().toString();

        String json = """
                 {
                        "title": "%s",
                        "isbn": "%s",
                        "availableCopies": %d,
                        "authorId":1
                }
                """.formatted(title, isbn, copies);

        String body = mockMvc.perform(post("/books").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(body, "$.id");
    }

    private String randomEmail() {
        return UUID.randomUUID() + "@example.com";
    }

    @Test
    void shouldReturn201AndLocationWhenPostLoan() throws Exception {

        String loanJson = """
                {
                  "bookId": %d,
                  "readerEmail": "%s"
                }
                """.formatted(createBook(1), randomEmail());

        mockMvc.perform(post("/loans").content(loanJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        String invalidEmail = "janskibiemail.com";

        String loanJson = """
                {
                  "bookId": %d,
                  "readerEmail": "%s"
                }
                """.formatted(createBook(1), invalidEmail);

        mockMvc.perform(post("/loans").content(loanJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenBookDoesNotExist() throws Exception {
        String loanJson = """
                {
                  "bookId": %d,
                  "readerEmail": "%s"
                }
                """.formatted(999, randomEmail());

        mockMvc.perform(post("/loans").content(loanJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenLoanLimitIsExceeded() throws Exception {
        String email = randomEmail();
        Integer bookID = createBook(4);
        String loanJson = """
                {
                  "bookId": %d,
                  "readerEmail": "%s"
                }
                """.formatted(bookID, email);

        mockMvc.perform(post("/loans").content(loanJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/loans").content(loanJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/loans").content(loanJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/loans").param("readerEmail", email))
                .andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(get("/books/{id}", bookID))
                .andExpect(jsonPath("$.availableCopies").value(1));

        mockMvc.perform(post("/loans").content(loanJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("limit")));

        mockMvc.perform(get("/loans").param("readerEmail", email))
                .andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(get("/books/{id}", bookID))
                .andExpect(jsonPath("$.availableCopies").value(1));
    }

    @Test
    void shouldReturn409WhenThereAreNoAvailableCopies() throws Exception {
        String loanJson = """
                {
                  "bookId": %d,
                  "readerEmail": "%s"
                }
                """.formatted(createBook(0), randomEmail());

        mockMvc.perform(post("/loans").content(loanJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("wolnych")));
    }

    @Test
    void shouldReturn204WhenLoanIsDeleted() throws Exception {
        String loanJson = """
                {
                  "bookId": %d,
                  "readerEmail": "%s"
                }
                """.formatted(createBook(1), randomEmail());
        String location = mockMvc.perform(post("/loans").content(loanJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location")).andReturn().getResponse().getHeader("Location");

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenLoanIsNotFoundTryingToDelete() throws Exception {
        mockMvc.perform(delete("/loans/{id}", 47))
                .andExpect(status().isNotFound());
    }

}