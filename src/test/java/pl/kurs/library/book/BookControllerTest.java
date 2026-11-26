package pl.kurs.library.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest("LibraryApplication.class")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookControllerTest {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSaveBook() throws Exception {
        //given
        long id = 4;
        postman.perform(get("/books/{id}", id))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.status").value("Not Found"))
                .andExpect(jsonPath("$.method").value("GET"));

        BookRequest request = new BookRequest("Witcher", "123123123", 1, 1L);

        //when

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.title").value("Witcher"))
                .andExpect(jsonPath("$.isbn").value("123123123"))
                .andExpect(jsonPath("$.availableCopies").value(1))
                .andExpect(jsonPath("$.authorSummary.id").value(1))
                .andExpect(jsonPath("$.authorSummary.name").value("Stanisław Lem"));

        //then
        postman.perform(get("/books/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.title").value("Witcher"))
                .andExpect(jsonPath("$.isbn").value("123123123"))
                .andExpect(jsonPath("$.availableCopies").value(1))
                .andExpect(jsonPath("$.authorSummary.id").value(1))
                .andExpect(jsonPath("$.authorSummary.name").value("Stanisław Lem"));

    }

    @Test
    void shouldNotSaveAuthorDuplicateISBN() throws Exception {
        //given
        long id = 4;
        postman.perform(get("/books/{id}", id))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.status").value("Not Found"))
                .andExpect(jsonPath("$.method").value("GET"));

        BookRequest request = new BookRequest("Witcher", "978-83-08-04907-1", 1, 1L);

        //when

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isConflict());

    }

    @Test
    void shouldNotSaveBookWhenTitleIsBlank() throws Exception {
        //given
        long id = 4;
        postman.perform(get("/books/{id}", id))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.status").value("Not Found"))
                .andExpect(jsonPath("$.method").value("GET"));

        BookRequest request = new BookRequest("", "123123123", 1, 1L);

        //when + then

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldFindById() throws Exception {
        //given
        long id = 1;

        //when + then
        postman.perform(get("/books/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Solaris"))
                .andExpect(jsonPath("$.isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$.availableCopies").value(2))
                .andExpect(jsonPath("$.authorSummary.id").value(1))
                .andExpect(jsonPath("$.authorSummary.name").value("Stanisław Lem"));
    }

    @Test
    void shouldNotFindByIdWhenDoesNotExist() throws Exception {
        //given
        long id = 5;

        //when + then
        postman.perform(get("/books/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldFindAll() throws Exception {
        //when + then
        postman.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("Solaris"))
                .andExpect(jsonPath("$[0].isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$[0].availableCopies").value(2))
                .andExpect(jsonPath("$[0].authorSummary.id").value(1))
                .andExpect(jsonPath("$[0].authorSummary.name").value("Stanisław Lem"));
    }

    @Test
    void shouldDelete() throws Exception {

        //given
        long id = 1;
        postman.perform(get("/books/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Solaris"))
                .andExpect(jsonPath("$.isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$.availableCopies").value(2))
                .andExpect(jsonPath("$.authorSummary.id").value(1))
                .andExpect(jsonPath("$.authorSummary.name").value("Stanisław Lem"));

        //when

        postman.perform(delete("/books/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());
        //then

        postman.perform(get("/books/{id}", id))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.status").value("Not Found"))
                .andExpect(jsonPath("$.method").value("GET"));
    }

}