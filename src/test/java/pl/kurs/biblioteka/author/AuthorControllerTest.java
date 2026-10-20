package pl.kurs.biblioteka.author;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookRepository;

import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest {

    private final MockMvc postman;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Autowired
    public AuthorControllerTest(MockMvc postman, AuthorRepository authorRepository, BookRepository bookRepository) {
        this.postman = postman;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Test
    void shouldFindAuthorWithBooksWithoutRecursion() throws Exception {
        //given
        Author author = authorRepository.save(new Author("Jan Nowak"));
        Book book = bookRepository.save(new Book("Kajak", UUID.randomUUID().toString(), 2, author));

        //when+then
        postman.perform(get("/authors/{id}", author.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(author.getId()))
                .andExpect(jsonPath("$.name").value("Jan Nowak"))
                .andExpect(jsonPath("$.books[*].id", hasItem(book.getId().intValue())))
                .andExpect(jsonPath("$.books[*].title", hasItem("Kajak")))
                .andExpect(jsonPath("$.books[0].author").doesNotExist());

    }
}