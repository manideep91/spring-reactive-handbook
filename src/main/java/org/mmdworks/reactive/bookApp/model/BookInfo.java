package org.mmdworks.reactive.bookApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookInfo {
    private String bookId;
    private String title;
    private String author;
    private String genre;
    private int publishedYear;
}
