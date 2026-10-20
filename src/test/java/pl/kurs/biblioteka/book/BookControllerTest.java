package pl.kurs.biblioteka.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorRepository;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    private final MockMvc postman;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookControllerTest(MockMvc postman, BookRepository bookRepository, AuthorRepository authorRepository) {
        this.postman = postman;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Test
    void shouldFindAllBooksWithoutRecursion() throws Exception {
        postman.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldCreateBookWithoutRecursion() throws Exception {
        //given
        Author author = authorRepository.save(new Author("Jan Nowak"));
        String isbn = UUID.randomUUID().toString();
        String request =  """
                {
                    "title": "Kajak",
                    "isbn": "%s",
                    "availableCopies": 5,
                    "authorId": %d
                }
                """.formatted(isbn, author.getId());

        //when + then
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Kajak"))
                .andExpect(jsonPath("$.isbn").value(isbn))
                .andExpect(jsonPath("$.availableCopies").value(5))
                .andExpect(jsonPath("$.authorId").value(author.getId()))
                .andExpect(jsonPath("$.authorName").value("Jan Nowak"));
    }
}