package org.mmdworks.reactive.bookApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private String reviewer;
    private int rating;
    private String comment;
}
