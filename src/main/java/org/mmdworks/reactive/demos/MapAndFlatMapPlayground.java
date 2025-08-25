package org.mmdworks.reactive.demos;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MapAndFlatMapPlayground {

    public static void main(String[] args) {

        //➡️ You want to convert the name into its length (an Integer).
        Mono<String> monoName = Mono.just("Manideep");
        Mono<Integer> nameLength = monoName.map(String::length);
        nameLength.subscribe(s ->System.out.println("The length is :"+nameLength));

//        //➡️ You want to get user details from an async service.
//        Mono<String> userIdMono = Mono.just("user123");
//        Mono<User> userMono = userIdMono.flatMap( e -> service.getUserById(e));

//        //➡️ You want to get all books by their IDs, one at a time.
//        Flux<String> bookIds = Flux.just("1", "2", "3");
//        Flux<Book> books = bookIds.flatMap( e -> service.getBookById(e));

        //➡️ You want to convert each name to uppercase (sync logic).
        Flux<String> names = Flux.just("java", "spring", "flux");
        Flux<String> upperNames = names.map(String::toUpperCase);

//        //➡️ You want to get the invoice for a given order ID (async service).
//        Mono<String> orderId = Mono.just("order123");
//        Mono<Invoice> invoiceMono = orderId.flatMap(e -> service.getInvoiceForOrder(e));


        List<List<String>> listOfLists = List.of(
                List.of("a", "b"),
                List.of("c", "d")
        );

        List<String> list = listOfLists.stream().flatMap(e -> e.stream()).toList();
        System.out.println(list);

    }
}
