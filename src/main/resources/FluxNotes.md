| Flux Factory Operator    | Purpose                              | Example Use Case                             |
| ------------------------ | ------------------------------------ | -------------------------------------------- |
| `just(...)`              | Emit known values                    | Predefined list of messages                  |
| `fromIterable(...)`      | Stream a collection                  | Reactive processing of CSV lines             |
| `range(start, count)`    | Number sequence with start/count     | Generating IDs or sequence data naturally    |
| `interval(...)`          | Timed tick producer                  | Heartbeat, polling, real-time clock          |
| `empty()` / `error(...)` | Represent nothing or immediate error | Control paths / error testing                |
| `defer(...)`             | Lazy Flux per subscriber             | Replaying current timestamp for each request |
| `flatMapIterable(...)`   | Expand nested collections            | Expanding parent-child relationships         |


🧠 Use map for simple sync transformations.
🧠 Use flatMap when chaining an async operation (Mono/Flux) — especially while iterating a Flux.
🧠 Use zipWith to combine two Monos or Fluxes into one new object.

✅ “Use map() for sync transforms, flatMap() when the mapper is async, and zipWith() when you need to combine multiple streams.”
An async mapper is any function that returns a Mono<T> or Flux<T> instead of a plain T. 
You must use .flatMap() to avoid nested reactive types like Mono<Mono<T>>.


| Situation                                      | Use         |
| ---------------------------------------------- | ----------- |
| Your function returns a value (`T → R`)        | `map()`     |
| Your function returns a `Mono<R>` or `Flux<R>` | `flatMap()` |

| Operation    | Input Type | Function Returns   | Use When            |
| ------------ | ---------- | ------------------ | ------------------- |
| `.map()`     | T          | R                  | Pure transformation |
| `.flatMap()` | T          | Mono<R> or Flux<R> | Async chaining      |

| Concept         | Java 8 Streams             | Project Reactor             |
| --------------- | -------------------------- | --------------------------- |
| `.flatMap()` on | `Stream<T>`                | `Flux<T>` / `Mono<T>`       |
| Input function  | T → Stream<R>              | T → Publisher<R>            |
| Output          | Flat `Stream<R>`           | Flat `Flux<R>` or `Mono<R>` |
| Use case        | Flatten nested collections | Flatten nested publishers   |

|                | Java Streams `flatMap`         | Reactor `Flux.flatMap()`                       |
| -------------- | ------------------------------ | ---------------------------------------------- |
| **Input**      | `Function<T, Stream<R>>`       | `Function<T, Publisher<R>>` (`Flux` or `Mono`) |
| **Purpose**    | Flatten nested **collections** | Flatten nested **publishers** (async streams)  |
| **Sync/Async** | Purely **synchronous**         | Supports **asynchronous** chaining             |
| **Threading**  | Runs on **main thread**        | Can run on **multiple threads** (non-blocking) |
| **Example**    | `flatMap(List::stream)`        | `flatMap(id -> service.getById(id))`           |

**If your service is not reactive, below example when your making a DB call and DB is no reactive ** 
productIds.flatMap(pid ->
Mono.fromCallable(() -> service.getProduct(pid))  // 👈 offload blocking
.subscribeOn(Schedulers.boundedElastic())     // 👈 use worker thread
)

| What Server Sends  | What You Use in WebClient  | Result            |
| ------------------ | -------------------------- | ----------------- |
| Single JSON Object | `.bodyToMono(Class.class)` | `Mono<T>`         |
| JSON Array         | `.bodyToFlux(Class.class)` | `Flux<T>`         |
| Reactive (rare)    | Same as above              | Already Mono/Flux |
Mono<Product> getProduct(String pid) {
return webClient.get()
.uri("http://some-api.com/products/{id}", pid)
.retrieve()
.bodyToMono(Product.class);
}


| Scenario                                            | Source is Blocking? | What You Should Do                                            |
| --------------------------------------------------- | ------------------- | ------------------------------------------------------------- |
| **DB call (JPA, MySQL)**                            | ✅ Yes              | Use `Mono.fromCallable(...)` + `.subscribeOn(...)`            |
| **DB call (Reactive DB like R2DBC/Mongo Reactive)** | ❌ No               | Directly return Mono/Flux                                     |
| **REST API (WebClient)**                            | Doesn’t matter      | WebClient handles it — use `.bodyToMono()` or `.bodyToFlux()` |

| 🔧 Operator                     | 💡 Purpose         | 💬 Use When...                                                                        | ✅ Real Example                                                                     |
| ------------------------------- | ------------------ | ------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------- |
| **`map()`**                     | Sync transform     | You’re transforming value inside a `Mono`/`Flux`, and the function returns plain data | `Mono<User> → map(user -> user.getName())`                                         |
| **`flatMap()`**                 | Async chaining     | You want to return another `Mono`/`Flux` from inside a `Mono`/`Flux`                  | `Mono<UserId> → flatMap(id -> userService.getUser(id))`                            |
| **`zip()` / `zipWith()`**       | Combine values     | You want to combine multiple Monos/Flux into a single tuple to work with both results | `userMono.zipWith(cartMono)` → `Tuple2<User, Cart>`                                |
| **`merge()` / `mergeWith()`**   | Parallel combine   | You want to run **multiple publishers in parallel** and get interleaved results       | Merge prices from 2 different services: `Flux.merge(flipkartPrices, amazonPrices)` |
| **`concat()` / `concatWith()`** | Sequential combine | Same as `merge` but runs **in order** — second starts only after first completes      | `Flux.concat(api1(), api2())`                                                      |
| **`switchIfEmpty()`**           | Fallback logic     | Provide a default Mono/Flux if original is empty                                      | `repo.findUser(id).switchIfEmpty(defaultUser())`                                   |
