package pl.kurs.library.author;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest("LibraryApplication.class")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthorControllerTest {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldNotSaveAuthorWhenNameIsBlank() throws Exception {
        //given
        long id = 3;
        postman.perform(get("/authors/{id}", id))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.status").value("Not Found"))
                .andExpect(jsonPath("$.method").value("GET"));

        AuthorRequest request = new AuthorRequest("");

        //when + then
        postman.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSaveAuthor() throws Exception {
        //given
        long id = 3;
        postman.perform(get("/authors/{id}", id))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.status").value("Not Found"))
                .andExpect(jsonPath("$.method").value("GET"));

        AuthorRequest request = new AuthorRequest("Andrzej Sapkowski");

        //when

        postman.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Andrzej Sapkowski"));

        //then
        postman.perform(get("/authors/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Andrzej Sapkowski"))
                .andExpect(jsonPath("$.books.length()").value(0));

    }

    @Test
    void shouldNotSaveAuthorDuplicateISBN() throws Exception {
        //given
        long id = 3;
        postman.perform(get("/authors/{id}", id))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.status").value("Not Found"))
                .andExpect(jsonPath("$.method").value("GET"));

        AuthorRequest request = new AuthorRequest("Andrzej Sapkowski");

        //when

        postman.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Andrzej Sapkowski"));

        //then
        postman.perform(get("/authors/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Andrzej Sapkowski"))
                .andExpect(jsonPath("$.books.length()").value(0));

    }

    @Test
    void shouldFindById() throws Exception {
        //given
        long id = 1;

        //when + then
        postman.perform(get("/authors/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Stanisław Lem"))
                .andExpect(jsonPath("$.books").isArray());
    }

    @Test
    void shouldNotFindByIdWhenDoesNotExist() throws Exception {
        //given
        long id = 10;

        //when + then
        postman.perform(get("/authors/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindAll() throws Exception {
        //when + then
        postman.perform(get("/authors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Stanisław Lem"))
                .andExpect(jsonPath("$[0].books").isArray());

    }


}