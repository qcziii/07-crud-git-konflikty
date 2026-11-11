package pl.kurs.biblioteka.common;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorMessage(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime dateTime,
        int code,
        String status,
        String message,
        String uri,
        String method
) {
}
