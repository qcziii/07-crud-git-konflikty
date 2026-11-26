package pl.kurs.library.author;

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
import pl.kurs.library.book.BookRequest;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorControllerIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final DatabaseCleaner databaseCleaner;


    @Autowired
    AuthorControllerIT(MockMvc mockMvc, ObjectMapper objectMapper, DatabaseCleaner databaseCleaner) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.databaseCleaner = databaseCleaner;
    }

    @BeforeEach
    void cleanDatabsae() {
        databaseCleaner.clean();
    }

    @Test
    void shouldGetAll() throws Exception {
        // given
        mockMvc.perform(get("/authors/3"))
                .andDo(print())
                .andExpect(status().isNotFound());

        AuthorRequest authorRequest = new AuthorRequest("Jan Brzechwa");

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
        // when then
        mockMvc.perform(get("/authors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Jan Brzechwa"))
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].name").value(authorRequest.name()))
                .andExpect(jsonPath("$[?(@.name == 'Jan Brzechwa')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Jan Brzechwa')].books[*]").isEmpty());
    }

    @Test
    void shouldGetById() throws Exception {
        // given
        mockMvc.perform(get("/authors/{id}", 99L))
                .andDo(print())
                .andExpect(status().isNotFound());

        AuthorRequest authorRequest = new AuthorRequest("Jan Brzechwa");

        // when
        MvcResult result = mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        Long authorId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        // then
        mockMvc.perform(get("/authors/{id}", authorId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authorId))
                .andExpect(jsonPath("$.name").value("Jan Brzechwa"))
                .andExpect(jsonPath("$.books").isArray())
                .andExpect(jsonPath("$.books").isEmpty());
    }

    @Test
    void shouldCreate() throws Exception {
        // when
        AuthorRequest authorRequest = new AuthorRequest("Jan Brzechwa");

        // when
        MvcResult result = mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        Long authorId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        // then
        mockMvc.perform(get("/authors/{id}", authorId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authorId))
                .andExpect(jsonPath("$.name").value("Jan Brzechwa"))
                .andExpect(jsonPath("$.books").isArray())
                .andExpect(jsonPath("$.books").isEmpty());
    }

    @Test
    void shouldNotFindWhenWrongId() throws Exception {
        // given when then
        mockMvc.perform(get("/authors/{id}", 99L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400_WhenNameIsBlank() throws Exception {
        AuthorRequest authorRequest = new AuthorRequest(" ");

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void shouldGetAll_withBooks() throws Exception {
        // given
        AuthorRequest authorRequest = new AuthorRequest("J.R.R Tolkien");
        MvcResult authorResult = mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long authorId = objectMapper.readTree(authorResult.getResponse().getContentAsString()).get("id").asLong();

        BookRequest bookRequest1 = new BookRequest("Waldaca pierscieni", "978-83-08-04151-9", 2, authorId);
        BookRequest bookRequest2 = new BookRequest("Silmarilon", "978-83-08-04151-1", 2, authorId);

        // when
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest2)))
                .andExpect(status().isCreated());

        // then
        mockMvc.perform(get("/authors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'J.R.R Tolkien')].books").isNotEmpty())
                .andExpect(jsonPath("$[?(@.name == 'J.R.R Tolkien')].books.length()").value(2))
                .andExpect(jsonPath("$[?(@.name == 'J.R.R Tolkien')].books[?(@.title == 'Waldaca pierscieni')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'J.R.R Tolkien')].books[?(@.title == 'Silmarilon')]").exists());
    }


}