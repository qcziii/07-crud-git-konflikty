package pl.kurs.biblioteka.common.dto;

import java.util.Comparator;

public record FieldError(String field, String message) implements Comparable<FieldError> {

    @Override
    public int compareTo(FieldError o) {
        return Comparator.comparing((FieldError fieldError) -> fieldError.field)
                .thenComparing((FieldError fieldError) -> fieldError.message)
                .compare(this, o);
    }
}
