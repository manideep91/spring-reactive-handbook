| Flux Factory Operator    | Purpose                              | Example Use Case                             |
| ------------------------ | ------------------------------------ | -------------------------------------------- |
| `just(...)`              | Emit known values                    | Predefined list of messages                  |
| `fromIterable(...)`      | Stream a collection                  | Reactive processing of CSV lines             |
| `range(start, count)`    | Number sequence with start/count     | Generating IDs or sequence data naturally    |
| `interval(...)`          | Timed tick producer                  | Heartbeat, polling, real-time clock          |
| `empty()` / `error(...)` | Represent nothing or immediate error | Control paths / error testing                |
| `defer(...)`             | Lazy Flux per subscriber             | Replaying current timestamp for each request |
| `flatMapIterable(...)`   | Expand nested collections            | Expanding parent-child relationships         |
