package pl.kurs.biblioteka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorRepository;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookRepository;
import pl.kurs.biblioteka.loan.LoanRepository;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorControllerIT {

    private static final long MISSING_ID = 999999L;

    @Autowired
    private MockMvc mockMvc;

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
                Author.builder()
                        .name("Stanisław Lem")
                        .build()
        );

        book = bookRepository.save(
                Book.builder()
                        .title("Solaris")
                        .isbn("978-83-08-04907-1")
                        .availableCopies(2)
                        .author(author)
                        .build()
        );
    }

    @Test
    void getAllAuthorsShouldReturn200() throws Exception {
        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAuthorByIdShouldReturn200AndBooks() throws Exception {
        mockMvc.perform(get("/authors/{id}", author.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(author.getId().intValue()))
                .andExpect(jsonPath("$.name")
                        .value("Stanisław Lem"))
                .andExpect(jsonPath("$.books").isArray())
                .andExpect(jsonPath("$.books[0].id")
                        .value(book.getId().intValue()))
                .andExpect(jsonPath("$.books[0].title")
                        .value("Solaris"))
                .andExpect(jsonPath("$.books[0].author")
                        .doesNotExist());
    }

    @Test
    void getAuthorByIdShouldReturn404WhenAuthorDoesNotExist()
            throws Exception {

        mockMvc.perform(get("/authors/{id}", MISSING_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad")
                        .value("Author not found with id: " + MISSING_ID));
    }

    @Test
    void createAuthorShouldReturn201AndLocationHeader()
            throws Exception {

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Andrzej Sapkowski"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header()
                        .string("Location", startsWith("/authors/")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name")
                        .value("Andrzej Sapkowski"))
                .andExpect(jsonPath("$.books").isArray());
    }

    @Test
    void createAuthorShouldReturn400ForInvalidData()
            throws Exception {

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad").exists());
    }
}