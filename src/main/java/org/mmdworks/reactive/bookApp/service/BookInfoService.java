package org.mmdworks.reactive.bookApp.service;

import org.mmdworks.reactive.bookApp.model.BookInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class BookInfoService {

    public Flux<BookInfo> getAllBooks() {
        // Simulate async service
        return Flux.just(new BookInfo(
                "1",
                "Spring in Action",
                "Craig Walls",
                "Programming",
                2021
        )).delayElements(Duration.ofMillis(100)); // Simulate delay
    }

    public Mono<BookInfo> getBookById(String bookId) {

        return Mono.just(new BookInfo(
                "1",
                "Spring in Action",
                "Craig Walls",
                "Programming",
                2021
        )).delayElement(Duration.ofMillis(100));
    }
}
