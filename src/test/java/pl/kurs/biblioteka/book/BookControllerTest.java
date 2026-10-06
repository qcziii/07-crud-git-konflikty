package pl.kurs.biblioteka.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {
    private MockMvc postman;
    private ObjectMapper objectMapper;

    @Autowired
    public BookControllerTest(MockMvc postman, ObjectMapper objectMapper) {
        this.postman = postman;
        this.objectMapper = objectMapper;
    }

    @Test
    void shouldGetAllBooks() throws Exception {
        // given \ when \ then
        postman.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Solaris"))
                .andExpect(jsonPath("$[0].isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Cyberiada"))
                .andExpect(jsonPath("$[1].isbn").value("978-83-08-06175-2"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].title").value("Bieguni"))
                .andExpect(jsonPath("$[2].isbn").value("978-83-08-04151-8"));
    }

    @Test
    void getBookById() throws Exception {
        // given \ when \ then
        postman.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Solaris"))
                .andExpect(jsonPath("$.isbn").value("978-83-08-04907-1"));
    }

    @Test
    void shouldGiveStatusNotFoundWhenBookNotExists() throws Exception {
        // given \ when \ then
        postman.perform(get("/books/777"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGiveStatusCreatedWhenBookCreated() throws Exception {
        // given
        BookRequest bookRequest = new BookRequest("Lalka", "888-99966-000", 5, 2L);

        // when \ then
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Lalka"))
                .andExpect(jsonPath("$.isbn").value("888-99966-000"))
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/books/4"));

    }

    @Test
    void shouldNotCreateBookBookWithEmptyTitle() throws Exception {
        // given
        BookRequest bookRequest = new BookRequest("", "888-999666666-000", 5, 2L);

        // when \ then
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldGiveStatusConflictWhenBookIsbnIsDuplicated() throws Exception {
        // given
        BookRequest bookRequest = new BookRequest("Lalka", "888-9996666-000", 5, 2L);

        // when
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Lalka"))
                .andExpect(jsonPath("$.isbn").value("888-9996666-000"));


        // then
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isConflict());

    }

    @Test
    void shouldGiveStatusNotFoundWhenAuthorDoesNotExists() throws Exception {
        // given
        BookRequest bookRequest = new BookRequest("Lalka", "888-99966-000", 5, 2222L);

        // when \ then
        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldGiveStatusNotFoundWhenBookDoesNotExists() throws Exception {
        // given \  when \ then
        postman.perform(delete("/books/555"))
                .andExpect(status().isNotFound());
    }


}