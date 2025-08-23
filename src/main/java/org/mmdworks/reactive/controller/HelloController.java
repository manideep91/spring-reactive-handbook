package org.mmdworks.reactive.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api")
public class HelloController {
    @GetMapping("/mono")
    public Mono<String> mono() {
        return Mono.just("Hello Mono");
    }

    @GetMapping("/flux")
    public Flux<Integer> flux() {
        return Flux.range(1, 5);
    }

    @GetMapping("/stream")
    public Flux<Long> stream() {
        return Flux.interval(Duration.ofSeconds(1)).take(5);
    }


    /**
     * MediaType.TEXT_EVENT_STREAM_VALUE tells Spring to use SSE format.
     * Server-Sent Events (SSE)
     * Flux values are pushed one at a time.
     * <p>
     * Client displays them live, not after collection.
     *
     * @return
     */
    @GetMapping(value = "/stream-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> streamEvents() {
        //return Flux.interval(Duration.ofSeconds(1)).take(5);
        return Flux.interval(Duration.ofMillis(500)).take(5);

    }

    @GetMapping(value = "/employees/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamEmployees() {
        return Flux.interval(Duration.ofSeconds(1)).take(5)
                .map(i -> "Employee-" + i);
    }


}
