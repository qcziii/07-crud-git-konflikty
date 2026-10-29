package pl.kurs.biblioteka.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn404WhenBookDoesNotExist() throws Exception {
        mockMvc.perform(get("/books/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn201AndLocationWhenPostBook() throws Exception {
        String json = """
                 {
                        "title": "HarryPotter",
                        "isbn": "978-83-00-00000-1",
                        "availableCopies": 2,
                        "authorId":1
                }
                """;
        mockMvc.perform(post("/books").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldReturn400WhenBookTitleIsEmpty() throws Exception {
        String json = """
                 {
                        "title": "",
                        "isbn": "978-83-00-00000-1",
                        "availableCopies": 2,
                        "authorId":999
                }
                """;
        mockMvc.perform(post("/books").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("title")));
    }

    @Test
    void shouldReturn404WhenAuthorDoesNotExist() throws Exception {
        String json = """
                 {
                        "title": "HarryPotter",
                        "isbn": "978-83-00-00000-1",
                        "availableCopies": 2,
                        "authorId":999
                }
                """;
        mockMvc.perform(post("/books").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenBooksISBNIsDuplicated() throws Exception {
        String json = """
                 {
                        "title": "HarryPotter",
                        "isbn": "978-83-08-04907-1",
                        "availableCopies": 2,
                        "authorId":1
                }
                """;
        mockMvc.perform(post("/books").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn204WhenBookIsDeleted() throws Exception {
        String json = """
                 {
                        "title": "Skibidi",
                        "isbn": "178-83-00-00000-1",
                        "availableCopies": 2,
                        "authorId":1
                }
                """;
        String location = mockMvc.perform(post("/books").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location")).andReturn().getResponse().getHeader("Location");
        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenBookIsNotFoundWhenTryingToDelete() throws Exception {
        mockMvc.perform(delete("/books/999"))
                .andExpect(status().isNotFound());
    }


}