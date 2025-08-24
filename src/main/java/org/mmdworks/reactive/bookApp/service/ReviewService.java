package org.mmdworks.reactive.bookApp.service;

import org.mmdworks.reactive.bookApp.model.Review;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
public class ReviewService {

    public Flux<Review> getReviews(String bookId) {
        List<Review> reviews = List.of(
                new Review("John", 5, "Great book on Spring!"),
                new Review("Jane", 4, "Very detailed and clear."),
                new Review("Alex", 5, "Loved the examples.")
        );

        return Flux.fromIterable(reviews).delayElements(Duration.ofMillis(150)); // Simulate delay
    }
}
