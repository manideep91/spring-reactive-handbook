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

