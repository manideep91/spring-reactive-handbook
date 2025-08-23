package org.mmdworks.reactive.demos;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileProcessingComparison {

    public static void main(String[] args) throws Exception {
        List<String> lines = List.of("1001", "1002", "1003", "1004", "1005");

        System.out.println("🔴 Imperative Blocking:");
        long start1 = System.currentTimeMillis();
        for (String line : lines) {
            String enriched = mockApiCall(line);
            mockDbSave(enriched);
        }
        System.out.println("Time: " + (System.currentTimeMillis() - start1) + " ms");

        System.out.println("\n🟠 Imperative with ExecutorService:");
        ExecutorService executor = Executors.newFixedThreadPool(5);
        long start2 = System.currentTimeMillis();
        List<Callable<Void>> tasks = lines.stream()
                .map(line -> (Callable<Void>) () -> {
                    String enriched = mockApiCall(line);
                    mockDbSave(enriched);
                    return null;
                }).toList();
        executor.invokeAll(tasks);
        executor.shutdown();
        System.out.println("Time: " + (System.currentTimeMillis() - start2) + " ms");

        System.out.println("\n🟢 Reactive with Flux:");
        long start3 = System.currentTimeMillis();
        Flux.fromIterable(lines)
                .flatMap(line -> mockApiCallReactive(line)
                        .flatMap(FileProcessingComparison::mockDbSaveReactive))
                .doOnComplete(() -> {
                    long time = System.currentTimeMillis() - start3;
                    System.out.println("Time: " + time + " ms");
                })
                .blockLast(); // block to wait for entire flow
    }

    // Mock blocking API call
    private static String mockApiCall(String input) {
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        return "enriched-" + input;
    }

    // Mock blocking DB save
    private static void mockDbSave(String data) {
        try { Thread.sleep(50); } catch (InterruptedException e) {}
    }

    // Mock async API call (Mono)
    private static Mono<String> mockApiCallReactive(String input) {
        return Mono.just("enriched-" + input).delayElement(Duration.ofMillis(100));
    }

    // Mock async DB save (Mono)
    private static Mono<Void> mockDbSaveReactive(String data) {
        return Mono.delay(Duration.ofMillis(50)).then();
    }
}

//        | Approach                 | What it Solves                             | Pros                                      | Cons                                      |
//        | ------------------------ | ------------------------------------------ | ----------------------------------------- | ----------------------------------------- |
//        | 🟥 Imperative Blocking   | Simple to write, good for small loads      | Easy to understand                        | High latency, one line at a time          |
//        | 🟧 Imperative + Executor | Faster by parallelizing using threads      | Uses CPU better, better latency           | More memory & threads, manual thread mgmt |
//        | 🟩 Reactive with Flux    | Async + backpressure + resource efficiency | Scales with fewer threads, clean chaining | Steeper learning curve, not always needed |

