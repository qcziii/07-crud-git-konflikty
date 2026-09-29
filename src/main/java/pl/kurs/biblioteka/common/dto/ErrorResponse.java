package pl.kurs.biblioteka.common.dto;

public record ErrorResponse(
        String name,
        String message
) {
}
