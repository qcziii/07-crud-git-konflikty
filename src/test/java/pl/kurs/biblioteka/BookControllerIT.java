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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerIT {

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
    void getAllBooksShouldReturn200AndAuthorData()
            throws Exception {

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].authorId")
                        .value(author.getId().intValue()))
                .andExpect(jsonPath("$[0].authorName")
                        .value("Stanisław Lem"))
                .andExpect(jsonPath("$[0].author")
                        .doesNotExist());
    }

    @Test
    void getBookByIdShouldReturn200AndAuthorData()
            throws Exception {

        mockMvc.perform(get("/books/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(book.getId().intValue()))
                .andExpect(jsonPath("$.title")
                        .value("Solaris"))
                .andExpect(jsonPath("$.authorId")
                        .value(author.getId().intValue()))
                .andExpect(jsonPath("$.authorName")
                        .value("Stanisław Lem"));
    }

    @Test
    void getBookByIdShouldReturn404WhenBookDoesNotExist()
            throws Exception {

        mockMvc.perform(get("/books/{id}", MISSING_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad")
                        .value("Book not found with id: " + MISSING_ID));
    }

    @Test
    void createBookShouldReturn201AndLocationHeader()
            throws Exception {

        String body = """
                {
                  "title": "Cyberiada",
                  "isbn": "978-83-08-06175-2",
                  "availableCopies": 3,
                  "authorId": %d
                }
                """.formatted(author.getId());

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header()
                        .string("Location", startsWith("/books/")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title")
                        .value("Cyberiada"))
                .andExpect(jsonPath("$.authorId")
                        .value(author.getId().intValue()))
                .andExpect(jsonPath("$.authorName")
                        .value("Stanisław Lem"));
    }

    @Test
    void createBookShouldReturn400ForInvalidData()
            throws Exception {

        String body = """
                {
                  "title": "",
                  "isbn": "",
                  "availableCopies": -1,
                  "authorId": %d
                }
                """.formatted(author.getId());

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad").exists());
    }

    @Test
    void createBookShouldReturn404WhenAuthorDoesNotExist()
            throws Exception {

        String body = """
                {
                  "title": "Testowa książka",
                  "isbn": "978-83-11-11111-1",
                  "availableCopies": 2,
                  "authorId": %d
                }
                """.formatted(MISSING_ID);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad")
                        .value("Author not found with id: " + MISSING_ID));
    }

    @Test
    void createBookShouldReturn409ForDuplicateIsbnWithoutDatabaseDetails()
            throws Exception {

        String body = """
                {
                  "title": "Inna książka",
                  "isbn": "978-83-08-04907-1",
                  "availableCopies": 2,
                  "authorId": %d
                }
                """.formatted(author.getId());

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.blad")
                        .value(
                                "Book with ISBN 978-83-08-04907-1 already exists"
                        ))
                .andExpect(content()
                        .string(not(containsString("insert into"))))
                .andExpect(content()
                        .string(not(containsString("constraint"))))
                .andExpect(content()
                        .string(not(containsString("org.hibernate"))));
    }

    @Test
    void deleteBookShouldReturn204()
            throws Exception {

        mockMvc.perform(delete("/books/{id}", book.getId()))
                .andExpect(status().isNoContent());

        assertFalse(
                bookRepository.existsById(book.getId())
        );
    }

    @Test
    void deleteBookShouldReturn404WhenBookDoesNotExist()
            throws Exception {

        mockMvc.perform(delete("/books/{id}", MISSING_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad")
                        .value("Book not found with id: " + MISSING_ID));
    }
}