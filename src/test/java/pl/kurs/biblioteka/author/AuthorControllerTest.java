package pl.kurs.biblioteka.author;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;


import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthorControllerTest {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldFindAllAuthors() throws Exception {
        postman.perform(get("/authors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].books").isArray());
    }

    @Test
    void shouldReturn200WithAuthorAndHisBooks() throws Exception {
        postman.perform(get("/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.books[0].id").exists())
                .andExpect(jsonPath("$.books[0].title").exists());
    }

    @Test
    void shouldReturn404WhenAuthorDoesNotExist() throws Exception {
        postman.perform(get("/authors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void shouldReturn201WithLocationWhenAuthorCreated() throws Exception {

        AuthorRequest authorRequest = new AuthorRequest("Test");

        postman.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/authors/")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.books").isEmpty());
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {

        AuthorRequest authorRequest = new AuthorRequest("");

        postman.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("name")));
    }
}