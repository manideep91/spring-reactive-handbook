package org.mmdworks.reactive.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.time.Duration;

public class MonoAndFluxOperations {

    public Flux<String> testConcatOperation() {

        var fruits = Flux.just("Mango", "Banana");
        var veggies = Flux.just("carrot", "beetroot");

        return Flux.concat(fruits, veggies);
    }

    public Flux<String> testConcatWithOperator() {

        var fruits = Flux.just("Mango", "Banana");
        var veggies = Flux.just("carrot", "beetroot");

        return fruits.concatWith(veggies);
    }

    public Flux<String> testMonoConcatOperation() {
        var fruits = Mono.just("Mango");
        var veggies = Mono.just("carrot");
        return Flux.concat(fruits, veggies);
    }


    public Flux<String> testMergeOperation() {

        var fruits = Flux.just("Mango", "Banana").delayElements(Duration.ofMillis(100));
        var veggies = Flux.just("carrot", "onion").delayElements(Duration.ofMillis(5));

        return Flux.merge(fruits, veggies);


    }

    public Flux<String> testMergeWithOperation() {

        var fruits = Flux.just("Mango", "Banana").delayElements(Duration.ofMillis(100));
        var veggies = Flux.just("carrot", "onion").delayElements(Duration.ofMillis(5));

        return fruits.mergeWith(veggies);


    }

    public Flux<String> testMergeSequentialOperation() {
        var fruits = Flux.just("Mango", "Banana").delayElements(Duration.ofMillis(100));
        var veggies = Flux.just("carrot", "onion").delayElements(Duration.ofMillis(5));
        return Flux.mergeSequential(fruits,veggies);
    }

    public Flux<Tuple2<String,String>> testZipFluxTupleOperation() {
        var fruits = Flux.just("Mango", "Banana");
        var veggies = Flux.just("carrot", "onion");

        return Flux.zip(fruits,veggies);
    }

    public Flux<String> testZipFluxOperation() {
        var fruits = Flux.just("Mango", "Banana");
        var veggies = Flux.just("carrot", "onion");

        return Flux.zip(fruits,veggies,( one, two) -> one +","+ two);
    }

    public Flux<Tuple3<String,String,String>> testZipFluxWith3Publishers() {
        var fruits = Flux.just("Mango", "Banana");
        var veggies = Flux.just("carrot", "onion");
        var moreVeggies = Flux.just("soda", "water");

        return Flux.zip(fruits,veggies,moreVeggies);
    }

    public Flux<String> testOnErrorReturn() {

        return Flux.just("Mango", "Banana")
                .concatWith(Flux.error(new RuntimeException()))
                .onErrorReturn("Error Fruit");
    }

    public Flux<String> testOnErrorContinue () {

        return Flux.just("Mango","Orange","Banana")
                .map(e -> {
                    if(e.equalsIgnoreCase("orange")) throw new RuntimeException();
                    return e;
                })
                .onErrorContinue((e,obj) -> System.out.println());

    }


    public static void main(String[] args) {
        MonoAndFluxOperations monoAndFluxOperations = new MonoAndFluxOperations();
        //concat
        Flux<String> concatFlux = monoAndFluxOperations.testConcatOperation();
        Flux<String> concactWithFlux = monoAndFluxOperations.testConcatWithOperator();
        Flux<String> concactWithMono = monoAndFluxOperations.testMonoConcatOperation();

        //merge
        Flux<String> mergeFlux = monoAndFluxOperations.testMergeOperation();
        Flux<String> mergeWithFlux = monoAndFluxOperations.testMergeWithOperation();
        Flux<String> mergeSequentialFlux = monoAndFluxOperations.testMergeSequentialOperation();

        //Zip
        Flux<Tuple2<String, String>> tuple2Flux = monoAndFluxOperations.testZipFluxTupleOperation();
        Flux<String> zipFlux = monoAndFluxOperations.testZipFluxOperation();
        Flux<Tuple3<String, String, String>> tuple3Flux = monoAndFluxOperations.testZipFluxWith3Publishers();

        //On- ERROR
        Flux<String> errorReturnFlux = monoAndFluxOperations.testOnErrorReturn();
        Flux<String> onErroContinue = monoAndFluxOperations.testOnErrorContinue();

        //CONCAT
        concatFlux.subscribe(s -> System.out.println("Concat Flux ::" + s));
        concactWithFlux.subscribe(s -> System.out.println("Concat with Flux ::" + s));
        concactWithMono.subscribe(s -> System.out.println("Concat Mono ::" + s));

        //MERGE
        mergeFlux.doOnNext(letter -> System.out.println("Thread Merge Flux: " + Thread.currentThread().getName() + " → " + letter)).blockLast();
        mergeWithFlux.doOnNext(letter -> System.out.println("Thread Merge With Flux: " + Thread.currentThread().getName() + " → " + letter)).blockLast();
        mergeSequentialFlux.doOnNext(letter -> System.out.println("Thread Merge Sequential Flux: " + Thread.currentThread().getName() + " → " + letter)).blockLast();

        //ZIP
        tuple2Flux.subscribe(s -> System.out.println(" The Zip with Tuple Operation Result :"+ s));
        zipFlux.subscribe(s -> System.out.println("The Zip Operation Result  :"+s));
        tuple3Flux.subscribe(s -> System.out.println("Triple 3 Result :"+ s));

        //On- ERROR
        errorReturnFlux.subscribe(s -> System.out.println( "Error Return flux ::"+s));
        onErroContinue.subscribe(s -> System.out.println( "On Error Continue flux ::"+s));
    }
}
