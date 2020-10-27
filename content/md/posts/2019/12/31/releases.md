{:title "Happy New Releases!",
 :date "2019-12-31 23:59:59",
 :tags ["clojure" "jdbc"]}

## Wrapping Up 2019

It's been a while since I blogged about the projects I maintain so I figured New Year's Eve 2019 was a good time to provide an update!

### expectations/clojure-test 1.2.1

Mocking in `side-effects`, optional message argument in `expect` (like `is`), `between` predicate, official support for Clojure 1.8 onward, lots of documentation improvements!

See the [change log](https://github.com/clojure-expectations/clojure-test/blob/master/CHANGELOG.md) for more details.

### jkk/honeysql 0.9.8

Lots of bug fixes, some enhancements (`composite`, `truncate`), and some documentation enhancements.

See the [change log](https://github.com/jkk/honeysql/blob/master/CHANGES.md) for more details.

### org.clojure/core.cache 0.8.2

Add `clojure.core.cache.wrapped` to make using the library easier!

See the [change log](https://github.com/clojure/core.cache/blob/master/README.md#change-log) for more details.

### org.clojure/core.memoize 0.8.2

Bug fixes and a new `memoizer` function that makes it easier to define custom function caches.

See the [change log](https://github.com/clojure/core.memoize/blob/master/README.md#change-log) for more details.

### org.clojure/java.data 0.1.5

Yup, I took over maintenance of this in October!

`set-properties` to set arbitrary properties post-construction, constructor argument support, numerous bug fixes and reflection warnings addressed.

See the [change log](https://github.com/clojure/java.data/blob/master/README.md#change-log) for more details.

### org.clojure/java.jdbc 0.7.11

This library is officially "stable" and no longer being actively maintained beyond critical bug fixes -- please consider migrating to `seancorfield/next.jdbc` (see below).

That said, this got the same transaction rollback exception edge case fix and Turkish locale fix that `next.jdbc` already had.

See the [change log](https://github.com/clojure/java.jdbc/blob/master/README.md#change-log) for more details.

### seancorfield/clj-new 0.8.2

AOT compilation in `app` uberjar, `pom.xml` generation, `:uberjar` in `app` and `:jar` in `lib` and `template` projects, `-?` / `--query` option to explain what `clj-new` will attempt to do, `-e` / `--env` option to define "environment" variables that set substitution values in project templates, dependency updates, documentation improvements.

See the [change log](https://github.com/seancorfield/clj-new/blob/master/CHANGELOG.md) for more details.

### seancorfield/depstar 0.4.2

Support for Multi-Release JARs, AOT compilation for uberjars (with `pom.xml`), JDK 11 support, better handling of unknown file types.

See the [change log](https://github.com/seancorfield/depstar#changes) for more details.

### seancorfield/next.jdbc 1.0.13

`next.jdbc.prepare/statement` and support for `java.sql.Statement`, arbitrary `Connection` and `Statement` property support, fix transaction rollback exception edge case (thank you Jepsen!), expose SQL builders, now tested against MS SQL Server and MySQL (formally), provide additional date/time/timestamp conversion support, CLOB support, `:jdbcUrl` support, HugSQL quick start guide, result set builder adapters, expanded Tips & Tricks, lots of documentation enhancements!

See the [change log](https://github.com/seancorfield/next-jdbc/blob/master/CHANGELOG.md) for more details.

### seancorfield/dot-clojure

If you are getting started with the [Clojure CLI and `deps.edn`](https://clojure.org/guides/deps_and_cli) then you might want to check out my [.clojure/deps.edn file](https://github.com/seancorfield/dot-clojure) which contains a lot of useful aliases and hints & tips to get you up and running with a powerful set of composable tools: create new projects, start various types of REPLs, run tests -- against multiple versions of Clojure, build JARs and uberjars, benchmark your code, check for outdated dependencies, and more!
