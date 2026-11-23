package pl.kurs.library.author;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@Component
@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    AuthorControllerIT(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

//    @Test
//    void shouldGetAll
}