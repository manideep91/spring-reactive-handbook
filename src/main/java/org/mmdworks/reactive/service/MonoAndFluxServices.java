package org.mmdworks.reactive.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public class MonoAndFluxServices {

    public static void main(String[] args) {

        Flux<String> fruitFlux = Flux.fromIterable(List.of("Mango" , "Orange" , "Banana", "Apple"));
        fruitFlux.subscribe(s -> System.out.println("F- >"+ s));

        Mono<String> fruitMono = Mono.just("Mango");
        fruitMono.subscribe(s -> System.out.println( "M ->"+ s));

        Flux<String> furitsFluxMap = Flux.fromIterable(List.of("Mango", "Orange"))
                .map(String::toUpperCase) ;
        furitsFluxMap.subscribe(s -> System.out.println("Mapped :" + s));


        Flux<String> fruitsFluxFlatMap =  Flux.fromIterable(List.of("Mango","Banana"))
                .flatMap(s -> Flux.just(s.split("")));
        fruitsFluxFlatMap.subscribe(s -> System.out.println("FlatMap :"+ s));

        //This will not get printed as this is printing async and main thread will not be waiting
            Flux.fromIterable(List.of("Mango", "Banana"))
                .flatMap(s -> Flux.just(s.split("")))
                .delayElements(Duration.ofMillis(100))
                .blockLast();
        //fruitFluxFlatMapAsync.subscribe(s -> System.out.println("FlatMap Async:"+ s));

        //waiting for the async to complete, we can;t use subscribe since subscribe returns Disposable not Flux
            Flux.fromIterable(List.of("Mango", "Banana"))
                .flatMap(word -> Flux.fromArray(word.split(""))
                .delayElements(Duration.ofMillis(500)))
                .doOnNext(letter -> System.out.println("Thread: " + Thread.currentThread().getName() + " → " + letter))
                .blockLast();


        //concatmap
            Flux.fromIterable(List.of("Mango","Banana"))
                .concatMap(e -> Flux.fromArray(e.split("")))
                .delayElements(Duration.ofMillis(100))
                .doOnNext(letter -> System.out.println("Thread: " + Thread.currentThread().getName() + " → " + letter))
                .blockLast();











    }
}
