package pl.kurs.biblioteka.common;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorMessage {
    int code;
    String status;
    String message;
    String uri;
    String method;
}
