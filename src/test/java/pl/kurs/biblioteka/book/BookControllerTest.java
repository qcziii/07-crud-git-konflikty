package pl.kurs.biblioteka.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorRepository;
import pl.kurs.biblioteka.loan.LoanRepository;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class BookControllerTest {

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

        author = authorRepository.save(
                new Author("Stanisław Lem")
        );

        book = bookRepository.save(
                new Book(
                        "Solaris",
                        "978-83-08-04907-1",
                        2,
                        author
                )
        );
    }

    @Test
    void shouldGetAllBooks() throws Exception {

        // when + then
        postman.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(book.getId().intValue()))
                .andExpect(jsonPath("$[0].title").value("Solaris"))
                .andExpect(jsonPath("$[0].isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$[0].availableCopies").value(2))
                .andExpect(jsonPath("$[0].authorId")
                        .value(author.getId().intValue()))
                .andExpect(jsonPath("$[0].authorName").value("Stanisław Lem"))
                .andExpect(jsonPath("$[0].author").doesNotExist());
    }

    @Test
    void shouldGetBookById() throws Exception {


        // when + then
        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(book.getId().intValue()))
                .andExpect(jsonPath("$.title").value("Solaris"))
                .andExpect(jsonPath("$.isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$.availableCopies").value(2))
                .andExpect(jsonPath("$.authorId")
                        .value(author.getId().intValue()))
                .andExpect(jsonPath("$.authorName").value("Stanisław Lem"))
                .andExpect(jsonPath("$.author").doesNotExist());
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        // given
        long missingId = Long.MAX_VALUE;

        // when + then
        postman.perform(get("/books/{id}", missingId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad").value(
                        String.format(
                                "Książka o id %d nie istnieje",
                                missingId
                        )
                ));
    }

    @Test
    void shouldCreateBook() throws Exception {
        // given
        postman.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        BookRequest request = new BookRequest(
                "Cyberiada",
                "978-83-08-06175-2",
                3,
                author.getId()
        );

        // when
        MvcResult result = postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Cyberiada"))
                .andExpect(jsonPath("$.isbn").value("978-83-08-06175-2"))
                .andExpect(jsonPath("$.availableCopies").value(3))
                .andExpect(jsonPath("$.authorId")
                        .value(author.getId().intValue()))
                .andExpect(jsonPath("$.authorName").value("Stanisław Lem"))
                .andExpect(jsonPath("$.author").doesNotExist())
                .andReturn();

        BookResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookResponse.class
        );

        // then
        header().string("Location", "/books/" + response.id())
                .match(result);

        postman.perform(get("/books/{id}", response.id()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id().intValue()))
                .andExpect(jsonPath("$.title").value("Cyberiada"))
                .andExpect(jsonPath("$.isbn").value("978-83-08-06175-2"))
                .andExpect(jsonPath("$.availableCopies").value(3))
                .andExpect(jsonPath("$.authorId")
                        .value(author.getId().intValue()));

        postman.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {
        // given
        BookRequest request = new BookRequest(
                "",
                "978-83-08-06175-2",
                3,
                author.getId()
        );

        // when
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad", not(emptyOrNullString())));

        // then — pozostała tylko książka z setUp()
        postman.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(book.getId().intValue()));
    }

    @Test
    void shouldReturn400WhenIsbnIsBlank() throws Exception {
        // given
        BookRequest request = new BookRequest(
                "Cyberiada",
                "",
                3,
                author.getId()
        );

        // when
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad", not(emptyOrNullString())));
        // then
        postman.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturn400WhenAvailableCopiesAreNegative() throws Exception {
        // given
        BookRequest request = new BookRequest(
                "Cyberiada",
                "978-83-08-06175-2",
                -1,
                author.getId()
        );

        // when
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad", not(emptyOrNullString())));

        // then
        postman.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturn400WhenAuthorIdIsNull() throws Exception {
        // given
        BookRequest request = new BookRequest(
                "Cyberiada",
                "978-83-08-06175-2",
                3,
                null
        );

        // when
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad", not(emptyOrNullString())));

        // then
        postman.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturn404WhenAuthorNotFound() throws Exception {
        // given
        long missingAuthorId = Long.MAX_VALUE;

        BookRequest request = new BookRequest(
                "Cyberiada",
                "978-83-08-06175-2",
                3,
                missingAuthorId
        );

        // when
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad").value(
                        String.format(
                                "Autor o id %d nie istnieje",
                                missingAuthorId
                        )
                ));

        // then
        postman.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturn409WhenIsbnAlreadyExists() throws Exception {
        // given
        BookRequest request = new BookRequest(
                "Inna książka",
                book.getIsbn(),
                3,
                author.getId()
        );

        // when
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.blad").value(
                        String.format(
                                "Książka o ISBN %s już istnieje",
                                book.getIsbn()
                        )
                ));

        // then
        postman.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(book.getId().intValue()))
                .andExpect(jsonPath("$[0].title").value("Solaris"))
                .andExpect(jsonPath("$[0].availableCopies").value(2));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        // given
        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        // when
        postman.perform(delete("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        // then
        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad").value(
                        String.format(
                                "Książka o id %d nie istnieje",
                                book.getId()
                        )
                ));

        postman.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturn404WhenDeletingMissingBook() throws Exception {
        // given
        long missingId = Long.MAX_VALUE;

        // when
        postman.perform(delete("/books/{id}", missingId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad").value(
                        String.format(
                                "Książka o id %d nie istnieje",
                                missingId
                        )
                ));

        // then
        postman.perform(get("/books/{id}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}