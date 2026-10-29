package pl.kurs.biblioteka.author;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn200WhenAuthorDoesExist() throws Exception {
        mockMvc.perform(get("/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].id").isNumber())
                .andExpect(jsonPath("$.books[0].title").isString())
                .andExpect(jsonPath("$.books[0].author").doesNotExist());
    }

    @Test
    void shouldReturn404WhenAuthorDoesNotExist() throws Exception {
        mockMvc.perform(get("/authors/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn201AndLocationWhenPostAuthor() throws Exception {
        String json = """
                 {
                        "name": "Mikolaj Chylinski"
                }
                """;
        mockMvc.perform(post("/authors").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("Mikolaj Chylinski"));
    }

    @Test
    void shouldReturn400WhenAuthorNameIsEmpty() throws Exception {
        String json = """
                 {
                        "name": ""
                }
                """;
        mockMvc.perform(post("/authors").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("name")));
    }

}