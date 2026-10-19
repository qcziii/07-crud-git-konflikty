package pl.kurs.biblioteka.author;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookRepository;
import pl.kurs.biblioteka.loan.LoanRepository;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorControllerTest {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;

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

        author = authorRepository.save(new Author("Stanisław Lem"));
        book = bookRepository.save(new Book("Solaris",
                "978-83-08-04907-1",
                2,
                author));
    }

    @Test
    void shouldGetAllAuthors() throws Exception {
        postman.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(author.getId().intValue()))
                .andExpect(jsonPath("$[0].name").value("Stanisław Lem"));
    }

    @Test
    void shouldGetAuthorById() throws Exception {
        postman.perform(get("/authors/{id}", author.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.id")
                        .value(author.getId().intValue()))
                .andExpect(jsonPath("$.name")
                        .value("Stanisław Lem"))
                .andExpect(jsonPath("$.books.length()").value(1))
                .andExpect(jsonPath("$.books[0].id")
                        .value(book.getId().intValue()))
                .andExpect(jsonPath("$.books[0].title")
                        .value("Solaris"))
                .andExpect(jsonPath("$.books[0].authorId")
                        .value(author.getId().intValue()))
                .andExpect(jsonPath("$.books[0].authorName")
                        .value("Stanisław Lem"))
                .andExpect(jsonPath("$.books[0].author")
                        .doesNotExist());
    }

    @Test
    void shouldThrowExceptionWhenAuthorNotFound() throws Exception {
        postman.perform(get("/authors/{id}", 99999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("blad").value(String.format("Autor o id %d nie istnieje", 99999)));
    }

    @Test
    void shouldCreateAuthor() throws Exception {
        postman.perform(get("/authors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Stanisław Lem"));

        AuthorRequest request = new AuthorRequest("Andrzej Sapkowski");


        MvcResult result = postman.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Andrzej Sapkowski"))
                .andExpect(jsonPath("$.books").isEmpty())
                .andReturn();

        AuthorResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthorResponse.class
        );


        header().string("Location", "/authors/" + response.id())
                .match(result);

        postman.perform(get("/authors/{id}", response.id()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id().intValue()))
                .andExpect(jsonPath("$.name").value("Andrzej Sapkowski"))
                .andExpect(jsonPath("$.books").isEmpty());
    }
}