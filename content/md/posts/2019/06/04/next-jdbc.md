{:title "Next.JDBC Release Candidate 1",
 :date "2019-06-04 18:30:00",
 :tags ["clojure" "jdbc" "open source"]}
## seancorfield/next.jdbc 1.0.0-rc1

`next.jdbc` -- the "next generation" of `clojure.java.jdbc` -- is a modern Clojure wrapper for JDBC. The first Release Candidate is now available to test -- containing only accretive and fixative changes from Beta 1. The API should be considered stable enough for production usage.

https://cljdoc.org/d/seancorfield/next.jdbc/1.0.0-rc1/doc/readme

The focus of this release is providing more flexibility in result set builders, so that it is easier to implement your own naming strategies, via new builders that accept `:label-fn` and `:qualifier-fn` options. These mirror the `:column-fn` and `:table-fn` options used in `next.jdbc.sql` -- which are updated versions of `clojure.java.jdbc`'s `:entities` option. These new builder options are updated versions of `clojure.java.jdbc`'s `:identifiers` option.

### Changes since Beta 1

* Fix #24 by adding return type hints to `next.jdbc` functions.
* Fix #22 by adding `next.jdbc.optional` with six map builders that omit `NULL` columns from the row hash maps.
* Documentation improvements (#27, #28, and #29), including changing "connectable" to "transactable" for the `transact` function and the `with-transaction` macro (for consistency with the name of the underlying protocol).
* Fix #30 by adding `modified` variants of column name functions and builders. The `lower` variants have been rewritten in terms of these new `modified` variants. This adds `:label-fn` and `:qualifier-fn` options that mirror `:column-fn` and `:table-fn` for row builders.

Please take it for a test drive and let me know if you run into any problems via [GitHub issues](https://github.com/seancorfield/next-jdbc/issues) or in the [`#sql` channel on the Clojurians Slack](https://clojurians.slack.com/messages/C1Q164V29/details/) or the [`#sql` stream on the Clojurians Zulip](https://clojurians.zulipchat.com/#narrow/stream/152063-sql).
