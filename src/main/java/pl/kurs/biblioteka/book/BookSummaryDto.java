package pl.kurs.biblioteka.book;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BookSummaryDto {
    Long id;
    String title;
}
