package org.mmdworks.reactive.bookApp.service;

import lombok.AllArgsConstructor;
import org.mmdworks.reactive.bookApp.model.Book;
import org.mmdworks.reactive.bookApp.model.BookInfo;
import org.mmdworks.reactive.bookApp.model.Review;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class BookService {

    private final BookInfoService bookInfoService;
    private final ReviewService reviewService;

    public Flux<Book> getAllBooksWithReviews() {

        Flux<BookInfo> bookInfoFlux = bookInfoService.getAllBooks();

        return bookInfoFlux.flatMap(bookInfo -> reviewService.getReviews(bookInfo.getBookId())
                .collectList()
                .map(reviews -> new Book(bookInfo,reviews))

        );

    }

    public Mono<Book> getBookById(String bookId) {

        Mono<BookInfo> bookInfoFlux = bookInfoService.getBookById(bookId);
        Mono<List<Review>> reviewFlux = reviewService.getReviews(bookId).collectList();
        return bookInfoFlux.zipWith(reviewFlux, (book,  review) -> new Book(book,review));



    }


}
