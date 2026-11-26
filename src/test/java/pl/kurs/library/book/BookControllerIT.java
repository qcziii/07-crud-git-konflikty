package pl.kurs.library.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kurs.library.DatabaseCleaner;
import pl.kurs.library.author.AuthorRequest;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final DatabaseCleaner databaseCleaner;

    @Autowired
    BookControllerIT(MockMvc mockMvc, ObjectMapper objectMapper, DatabaseCleaner databaseCleaner) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.databaseCleaner = databaseCleaner;
    }

    @BeforeEach
    void cleanDatabase() {
        databaseCleaner.clean();
    }

    @Test
    void shouldGetAll() throws Exception {
        // given
        Long authorId = createAuthor("Henryk Sienkiewicz");
        BookRequest bookRequest = new BookRequest("Potop", "978-83-08-04151-8", 2, authorId);

        // when
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andDo(print())
                .andExpect(status().isCreated());

        // then
        mockMvc.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Potop"))
                .andExpect(jsonPath("$[0].title").isNotEmpty())
                .andExpect(jsonPath("$[0].isbn").value(bookRequest.isbn()))
                .andExpect(jsonPath("$[0].isbn").value("978-83-08-04151-8"))
                .andExpect(jsonPath("$[0].authorId").value(bookRequest.authorId()))
                .andExpect(jsonPath("$[0].availableCopies").value(2));

    }

    @Test
    void shouldGetById() throws Exception {
        // given
        AuthorRequest authorRequest = new AuthorRequest("Henryk Sienkiewicz");

        MvcResult result = mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        Long authorId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        BookRequest bookRequest = new BookRequest("Potop", "978-83-08-04151-8", 2, authorId);

        // when
        MvcResult bookResult = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        Long bookId = objectMapper.readTree(bookResult.getResponse().getContentAsString()).get("id").asLong();

        //then
        mockMvc.perform(get("/books/{id}", bookId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Potop"))
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.isbn").value(bookRequest.isbn()))
                .andExpect(jsonPath("$.isbn").value("978-83-08-04151-8"))
                .andExpect(jsonPath("$.authorId").value(bookRequest.authorId()))
                .andExpect(jsonPath("$.availableCopies").value(2));
    }

    @Test
    void shouldReturn404_whenNotFindById() throws Exception {
        mockMvc.perform(get("/books/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateBookAndReturnLocationHeder() throws Exception {
        // given
        Long authorId = createAuthor("Henryk Sienkiewicz");
        BookRequest request = new BookRequest("Potop", "978-83-08-04151-8", 2, authorId);
        // when then
        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Potop"))
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertThat(location).contains("/books/");
    }

    @Test
    void shouldReturn409_whenIsbnAlreadyExists() throws Exception {
        // given
        Long authorId = createAuthor("Henryk Sienkiewicz");
        BookRequest first = new BookRequest("Potop", "978-83-08-04151-8", 2, authorId);
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isCreated());

        BookRequest duplicate = new BookRequest("Ogniem i mieczem", "978-83-08-04151-8", 1, authorId);

        // when then
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturn404_whenAuthorNotExist() throws Exception {
        // given
        BookRequest bookRequest = new BookRequest("Potop", "978-83-08-04151-8", 2, 999L);

        // when then
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturn400_whenTitleIsBlank() throws Exception {
        // given
        Long authorId = createAuthor("Henryk Sienkiewicz");
        BookRequest bookRequest = new BookRequest(" ", "978-83-08-04151-8", 2, authorId);

        // when then
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void shouldDeleteBook() throws Exception {
        // given
        Long authorId = createAuthor("Henryk Sienkiewicz");
        Long bookId = createBook("Potop", "978-83-08-04151-8", 2, authorId);

        // when
        mockMvc.perform(delete("/books/{id}", bookId))
                .andDo(print())
                .andExpect(status().isNoContent());

        // then
        mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404_whenDeleteBookNotExist() throws Exception {
        // given when then
        mockMvc.perform(delete("/books/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound());
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