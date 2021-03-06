{:title "next.jdbc Compendium",
 :date "2020-05-23 13:55",
 :tags ["clojure" "jdbc"]}

## seancorfield/next.jdbc 1.0.445

This morning I released [1.0.445](https://github.com/seancorfield/next-jdbc/releases/tag/v1.0.445) and realized it's the sixth release since I last mentioned it in a blog post, so I thought it would be helpful to summarize all of the changes made so far in 2020. 1.0.13 came out at the end of December and I decided to switch from MAJOR.MINOR.PATCH versioning to MAJOR.MINOR.COMMITS versioning since I'd already made the commitment to no breaking changes -- only fixative/accretive changes -- when the library originally moved from Alpha to Beta a year ago.

Unlike the change log and release notes which are chronological, this post is organized into fixative changes, accretive changes, and documentation improvements, with each section organized by functionality or by database as appropriate.<!-- more -->

### Bug Fixes

* `next.jdbc.result-set`
  * Fixed an edge case in how qualified column names are constructed when either the table name is unavailable or the `:qualifier-fn` results `nil` or an empty string.
  * Fixed how `:qualifier-fn` is used when the table name is unavailable so that `:qualifier-fn (constantly "qual")` works the same way as `clojure.java.jdbc`'s `:qualifier "qual"` option, which makes migration easier, as well as making use of `:qualifier-fn` more consistent.
  * Improved performance of some result set building code (poor performance is a bug!).

### Enhancements

* `next.jdbc/plan`
  * Added `InspectableMapifiedResultSet` protocol that provides "meta" information access from the abstract row that is passed into the reducing function used on the `IReduceInit` produced by `plan`. Currently, this exposes the current `row-number` (from the underlying `ResultSet`) and the `column-names` -- a vector of column name keywords, in "natural" order based on the SQL query, produced by the builder function passed into `plan`. These functions are only available from `plan` (or a `datafiable-row` produced from `plan`) -- they are not available on the results of `execute!` or `execute-one!` -- and additional functions will probably be added over time.
  * Use of `plan` with array-based builders has been made easier by adding support for numeric `Associative` keys and `Indexed` access (`nth`) to read column values by index, instead of by label.
* `next.jdbc/with-transaction`
  * If you call this with an existing `Connection`, it is mutated to set up and commit (or rollback) the transaction. This made concurrent execution unpredictable so this is now synchronized via `locking`. It is recommended to use `with-transaction` on a `DataSource` anyway, but this makes the `Connection` case more predictable.
* `next.jdbc.connection`
  * Improved the error messages when an unknown `:dbtype` is used and clarified how to use `:classname`.
  * Added `component` function for built-in support of the [Component library](https://github.com/stuartsierra/component) when creating a connection pooled datasource. Note that Component is not a dependency so you can simply ignore this feature if you don't use Component.
  * The result of calling `get-datasource` on a db-spec hash map now supports `.getLoginTimeout` and `.setLoginTimeout` to match `javax.sql.DataSource` and improve `next.jdbc`'s support for timeouts.
  * MariaDB is supported out of the box.
* `next.jdbc.date-time`
  * Add `read-as-instant` and `read-as-local` functions to extend `ReadableColumn` so that SQL `DATE` and `TIMESTAMP` columns can be read as Java Time types.
* `next.jdbc.specs`
  * The Spec for JDBC URL strings explicitly requires that they start with `jdbc:` to match the behavior of the code (and the JDBC specification), which can improve error messages when you use `instrument`.
* Options:
  * `:return-keys` now accepts both keywords and strings in the vector of keys to return (the documentation, code, and Spec were in conflict so allowing both was the safest choice).

### Documentation

I try to improve the documentation in each release, based on feedback via Slack and GitHub. The bullet points below are what I consider to be just the most notable improvements.

* **README**
  * Promote the stable documentation on [cljdoc.org](https://cljdoc.org/d/seancorfield/next.jdbc) and make it clear the GitHub documentation is for changes made since the last release.
* **Getting Started**
  * Make it clearer that `plan` requires `reduce` or some sort of reduction process.
  * Shows how to work with database metadata (this information was previously only mentioned in the `clojure.java.jdbc` migration guide).
* **Tips & Tricks**
  * This has been split out from **Friendly SQL Functions** and now contains a lot more information.
  * Timeouts: Four different types of timeout are explained, along with how `next.jdbc` supports them.
  * MySQL: How to stream result sets; `BLOB` columns are returned as `byte[]`, not `java.sql.Blob`.
  * PostgreSQL: How to stream result sets; Examples of how to work with SQL array types; Clarify the need for `next.jdbc.date-time` for `java.util.Date` conversion (this is also mentioned in **Getting Started**).
* `next.jdbc.prepare/execute-batch!` now has an example of how to get generated keys from a batch insert.

## Thanks!

As always, thanks to everyone who uses `next.jdbc`, thanks to everyone who has provided feedback (especially on the documentation), reported bugs, suggested improvements, or sent pull requests. `next.jdbc` wouldn't be what it is today without all that input!

See the [change log](https://github.com/seancorfield/next-jdbc/blob/master/CHANGELOG.md) for all the changes in release order and [cljdoc.org](https://cljdoc.org/d/seancorfield/next.jdbc) for overall documentation of the latest release.
