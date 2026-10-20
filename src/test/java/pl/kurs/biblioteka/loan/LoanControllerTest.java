package pl.kurs.biblioteka.loan;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorRepository;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookRepository;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerTest {


    private final MockMvc postman;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Autowired
    public LoanControllerTest(MockMvc postman, AuthorRepository authorRepository, BookRepository bookRepository) {
        this.postman = postman;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Test
    void shouldBorrowAndReturnLoanWithoutRecursion() throws Exception {
        //given
        Author author = authorRepository.save(new Author("Jan Nowak"));
        Book book = bookRepository.save(
                new Book(
                        "Kajak",
                        UUID.randomUUID().toString(),
                        3,
                        author));
        String email = UUID.randomUUID() + "@gmail.com";
        String request =  """
                {
                    "bookId": %d,
                    "readerEmail": "%s"
                }
                """.formatted(book.getId(), email);

        //when
        postman.perform(post("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(book.getId()))
                .andExpect(jsonPath("$.bookTitle").value("Kajak"))
                .andExpect(jsonPath("$.readerEmail").value(email))
                .andExpect(jsonPath("$.book").doesNotExist());
        //then
        postman.perform(get("/loans")
                .param("readerEmail", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(book.getId()))
                .andExpect(jsonPath("$[0].bookTitle").value("Kajak"))
                .andExpect(jsonPath("$[0].readerEmail").value(email))
                .andExpect(jsonPath("$[0].book").doesNotExist());
    }
}