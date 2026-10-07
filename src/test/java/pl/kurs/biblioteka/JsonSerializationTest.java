package pl.kurs.biblioteka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JsonSerializationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAuthorByIdReturnsFiniteJsonWithoutCycles() throws Exception {
        MvcResult result = mockMvc.perform(get("/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.books").isArray())
                .andExpect(jsonPath("$.books[0].id").exists())
                .andExpect(jsonPath("$.books[0].title").exists())
                .andExpect(jsonPath("$.books[0].author").doesNotExist())
                .andExpect(jsonPath("$.blad").doesNotExist())
                .andReturn();
        assertFiniteJson(result);
    }

    @Test
    void getAuthorsReturnsFiniteJsonWithoutCycles() throws Exception {
        MvcResult result = mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].books[0].id").exists())
                .andExpect(jsonPath("$[0].books[0].title").exists())
                .andExpect(jsonPath("$[0].books[0].author").doesNotExist())
                .andReturn();
        assertFiniteJson(result);
    }

    @Test
    void getBooksReturnsAuthorWithoutNestedBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author.id").exists())
                .andExpect(jsonPath("$[0].author.name").exists())
                .andExpect(jsonPath("$[0].author.books").doesNotExist())
                .andExpect(jsonPath("$[0].blad").doesNotExist())
                .andReturn();
        assertFiniteJson(result);
    }

    @Test
    void getLoansReturnsFiniteJsonWithoutCycles() throws Exception {
        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":1,\"readerEmail\":\"json-cycle@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.book.id").exists())
                .andExpect(jsonPath("$.book.title").exists())
                .andExpect(jsonPath("$.book.author").doesNotExist());

        MvcResult result = mockMvc.perform(get("/loans").param("readerEmail", "json-cycle@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].book.id").exists())
                .andExpect(jsonPath("$[0].book.title").exists())
                .andExpect(jsonPath("$[0].book.author").doesNotExist())
                .andExpect(jsonPath("$[0].blad").doesNotExist())
                .andReturn();
        assertFiniteJson(result);
    }

    private static void assertFiniteJson(MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        assertThat(body).doesNotContain("nesting depth");
        assertThat(body).doesNotContain("\"blad\"");
        assertThat(body.length()).isLessThan(4000);
        assertThat(body.trim()).satisfiesAnyOf(
                s -> assertThat(s).startsWith("{").endsWith("}"),
                s -> assertThat(s).startsWith("[").endsWith("]")
        );
    }
}
