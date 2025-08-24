package org.mmdworks.reactive.demos;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionalInterfacesPlayGround {

    public static void main(String[] args) {

        Function<String, String> upperFunction = ( e -> e.toUpperCase() );
        BiFunction<String,String, String> concatenation = ((a,b) -> a+":"+b );


        Supplier<String> supplier = (() -> "hello from supplier");

        Consumer<String> consumer = ((s) -> System.out.println("Consumed :"+ s));

        String Upper = upperFunction.apply("hello");
        System.out.println("Upper ::"+ Upper);

        System.out.println(concatenation.apply("Hello","hi"));

        System.out.println(supplier.get());

        consumer.accept("consume this");

    }



}
