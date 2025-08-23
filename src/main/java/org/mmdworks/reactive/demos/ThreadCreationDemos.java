package org.mmdworks.reactive.demos;

import java.util.concurrent.*;

public class ThreadCreationDemos {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        /**
         * Runnable → no return, use with Thread.
         *
         * Callable<T> → returns T, use with ExecutorService or wrap in FutureTask.
         *
         * Thread alone doesn’t understand Callable.
         */
        Thread thread = new Thread(() -> System.out.println("5"));
        thread.start();


        //✅ Options for running a Callable<T> in Java
        //1. ExecutorService + Future
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<String> task = () -> "Hello Callable!";
        Future<String> future = executor.submit(task);

        System.out.println(future.get()); // blocks until result available
        executor.shutdown();


        //2. FutureTask + Thread
        Callable<String> task1 = () -> "Hello FutureTask!";
        FutureTask<String> futureTask = new FutureTask<>(task1);

        Thread t = new Thread(futureTask);
        t.start();
        System.out.println(futureTask.get()); // result from call()

        //3. CompletableFuture.supplyAsync
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> "Hello CF!");
        cf.thenAccept(result -> System.out.println("Got: " + result));



    }
}
