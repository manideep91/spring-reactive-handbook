package org.mmdworks.reactive.demos;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class CompletableFuturePlayground {

    // Simulate I/O (API/DB): returns after ms, prints thread
    static <T> T io(String label, T value, long ms) {
        try {
            System.out.printf("[%s] %s on %s%n", label, value,
                    Thread.currentThread().getName());
            Thread.sleep(ms);
            return value;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static Supplier<String> apiCall(String name, long ms) {
        return () -> io("API", "Hello " + name, ms);
    }

    static Supplier<Integer> dbCall(int id, long ms) {
        return () -> io("DB", id, ms);
    }

    public static void main(String[] args) throws Exception {
        // ⭐ Custom executor to show control (avoid ForkJoin commonPool)
        ExecutorService ioPool = Executors.newFixedThreadPool(4);

        // 1) CREATE
        CompletableFuture<Void> fireAndForget =
                CompletableFuture.runAsync(() -> io("RUN", "side-effect", 200), ioPool);

        CompletableFuture<String> computed =
                CompletableFuture.supplyAsync(apiCall("Manideep", 300), ioPool);

        // 2) TRANSFORM (map)
        CompletableFuture<Integer> length =
                computed.thenApply(greeting -> {
                    System.out.println("thenApply on " + Thread.currentThread().getName());
                    return greeting.length();
                });

        // 3) COMPOSE (flatMap): when the next step itself returns CF
        CompletableFuture<String> composed =
                computed.thenCompose(g -> CompletableFuture.supplyAsync(
                        () -> io("ENRICH", g + " ✨", 200), ioPool));

        // 4) COMBINE two independent CFs
        CompletableFuture<Integer> number =
                CompletableFuture.supplyAsync(dbCall(42, 250), ioPool);

        CompletableFuture<String> combined =
                composed.thenCombine(number, (greet, id) -> greet + " with id=" + id);

        // 5) PARALLEL FAN-OUT + ALL TOGETHER (allOf)
        CompletableFuture<String> f1 =
                CompletableFuture.supplyAsync(apiCall("A", 200), ioPool);
        CompletableFuture<String> f2 =
                CompletableFuture.supplyAsync(apiCall("B", 300), ioPool);
        CompletableFuture<String> f3 =
                CompletableFuture.supplyAsync(apiCall("C", 150), ioPool);

        CompletableFuture<Void> all =
                CompletableFuture.allOf(f1, f2, f3); // waits for all

        CompletableFuture<List<String>> allResults =
                all.thenApply(v -> List.of(f1.join(), f2.join(), f3.join()));

        // 6) RACE (anyOf): first responder wins
        CompletableFuture<Object> first =
                CompletableFuture.anyOf(
                        CompletableFuture.supplyAsync(apiCall("fast", 100), ioPool),
                        CompletableFuture.supplyAsync(apiCall("slow", 600), ioPool)
                );

        // 7) TIMEOUTS (Java 9+): fail or fallback if slow
        CompletableFuture<String> mayTimeout =
                CompletableFuture.supplyAsync(apiCall("timeoutCandidate", 500), ioPool)
                        .orTimeout(300, TimeUnit.MILLISECONDS)            // throws TimeoutException
                        .exceptionally(ex -> "fallback after timeout");   // graceful fallback

        CompletableFuture<String> fallbackOnTimeout =
                CompletableFuture.supplyAsync(apiCall("sometimesSlow", 600), ioPool)
                        .completeOnTimeout("default-value", 300, TimeUnit.MILLISECONDS);

        // 8) ERROR HANDLING
        CompletableFuture<Object> withError =
                CompletableFuture.supplyAsync(() -> {
                            throw new RuntimeException("boom");
                        }, ioPool)
                        .exceptionally(ex -> {
                            System.out.println("exceptionally: " + ex.getMessage());
                            return -1; // fallback value
                        });

        CompletableFuture<Integer> handleBoth =
                CompletableFuture.supplyAsync(() -> io("SAFE", 10, 100), ioPool)
                        .thenApply(x -> x / 0) // will throw
                        .handle((val, ex) -> {
                            if (ex != null) {
                                System.out.println("handle caught: " + ex);
                                return 0; // recover
                            }
                            return val;
                        })
                        .whenComplete((v, ex) ->
                                System.out.println("whenComplete: v=" + v + ", ex=" + ex));

        // 9) MANUAL COMPLETION (rare but good to know)
        CompletableFuture<String> manual = new CompletableFuture<>();
        ioPool.submit(() -> {
            io("MANUAL", "completing manually", 120);
            manual.complete("manual-result");
        });

        // 10) PIPELINE WITH CONTROLLED PARALLELISM (fan-out + thenCombine each)
        CompletableFuture<String> pipeline =
                CompletableFuture.supplyAsync(apiCall("root", 200), ioPool)
                        .thenCompose(root -> {
                            // launch two independent tasks and combine
                            CompletableFuture<String> left =
                                    CompletableFuture.supplyAsync(apiCall(root + " -> L", 250), ioPool);
                            CompletableFuture<String> right =
                                    CompletableFuture.supplyAsync(apiCall(root + " -> R", 200), ioPool);
                            return left.thenCombine(right, (l, r) -> l + " & " + r);
                        });

        // 11) SEQUENCING MANY CFs (collect -> allOf pattern)
        List<CompletableFuture<Integer>> nums = List.of(1, 2, 3, 4, 5).stream()
                .map(n -> CompletableFuture.supplyAsync(() -> io("SQUARE", n * n, 120), ioPool))
                .toList();

        CompletableFuture<List<Integer>> joinedSquares =
                CompletableFuture.allOf(nums.toArray(new CompletableFuture[0]))
                        .thenApply(v -> nums.stream().map(CompletableFuture::join).toList());

        // ====== Wait & print outcomes ======
        System.out.println("fireAndForget done? " + fireAndForget.join());
        System.out.println("computed: " + computed.join());
        System.out.println("length: " + length.join());
        System.out.println("composed: " + composed.join());
        System.out.println("combined: " + combined.join());
        System.out.println("allResults: " + allResults.join());
        System.out.println("first(anyOf): " + first.join());
        System.out.println("mayTimeout: " + mayTimeout.join());
        System.out.println("fallbackOnTimeout: " + fallbackOnTimeout.join());
        System.out.println("withError: " + withError.join());
        System.out.println("handleBoth: " + handleBoth.join());
        System.out.println("manual: " + manual.join());
        System.out.println("pipeline: " + pipeline.join());
        System.out.println("joinedSquares: " + joinedSquares.join());

        ioPool.shutdown();
    }
}

//Create & run: runAsync (no return), supplyAsync (returns a value), optionally with a custom Executor.

//Transform: thenApply (sync transform), thenApplyAsync (transform on pool).

//Compose: thenCompose (flat‑map: use when the mapping returns another CF).

//Combine: thenCombine (join two CF values), allOf/anyOf (fan‑in many).

//Parallel fan‑out: start multiple CFs and wait together (allOf), or race (anyOf).

//Errors: exceptionally (fallback value), handle (value + error), whenComplete (side‑effects).

//Timeouts: orTimeout (fail if slow), completeOnTimeout (fallback if slow).

//Manually complete: complete, obtrudeValue (rare), failedFuture (Java 9+).