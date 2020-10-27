{:title "next.jdbc Compendium II",
 :date "2020-10-24 18:00",
 :tags ["clojure" "jdbc"]}
## seancorfield/next.jdbc 1.1.610

I recently released [1.1.610](https://github.com/seancorfield/next-jdbc/releases/tag/v1.1.610) and since it has been about five months since my last post summarizing advances in this library, I thought another summary post would be helpful.

As before, this post is organized into fixative changes, accretive changes, and documentation improvements, with each section organized by functionality or by database as appropriate.<!-- more -->

### Bug Fixes

* Spec fixes:
  * `next.jdbc/with-options` (was missing).
  * `next.jdbc.connection/component`.
  * `next.jdbc.prepare/statement`.

* `next.jdbc.prepare/statement` is now type hinted correctly.

* Result set builder adapters
  * `:cols` and `:rsmeta` access are now supported on these builders via `clojure.lang.ILookup`.

### Enhancements

* `jdbc.next`
  * `execute!` can return multiple result sets (if the `:multi-rs` option is set to `true`). This is useful when calling stored procedures and, for SQL Server, when working with T-SQL scripts. This was considered a big enough enhancement to bump the library from 1.0.z to 1.1.z because it has long been requested against `clojure.java.jdbc` (and `next.jdbc`).
  * Adds `with-options` that lets you wrap up a connectable along with default options that should be applied to all operations on that connectable. This provides most of the much-requested "default options" functionality (with caveats about functions that return native Java types).
  * `get-connection` now has arities that accept username and password to support datasources that allow per-connection credentials.
  * Adds `snake-kebab-opts`, `unqualified-snake-kebab-opts`, for use with `with-options`. These are conditionally defined if `camel-snake-kebab` is on your classpath (see also `next.jdbc.result-set` enhancements below).
  * The result of `plan` is now foldable, as well as reducible (in the `clojure.core.reducers` sense). Folding the result of a `plan` call will use fork/join to provide some degree of parallelization of processing when streaming large result sets.
  * Inside the reducing function over `plan`, you can call `next.jdbc.result-set/metadata` to obtain a datafication of the `ResultSetMetaData` object from the underlying `ResultSet` object.

* `next.jdbc.connection`
  * Adds `jdbc-url` to turn a "db spec" into a JDBC URL, for use with `->pool` and `component`.

* `next.jdbc.datafy` (new namespace)
  * Requiring this namespace causes `datafy`/`nav` functionality to be extended to several JDBC object types, making it easier to navigate around metadata from the database and to examine schemas and result set metadata.

* `next.jdbc.plan` (new namespace)
  * Adds `select!` and `select-one!` functions that simplify common uses (reductions) of `next.jdbc/plan`. `select!` lets you select just the values from a single column across the result set, or select a subset of columns by name. `select-one!` does the same but just for the first row (using `reduced` to short-circuit the reduction).

* `next.jdbc.prepare`
  * `execute-batch!` now supports a `:return-generated-keys` option so you can get back (all) the generated keys instead of just update counts.

* `next.jdbc.result-set`
  * `as-kebab-maps` and `as-unqualified-kebab-maps` are two new builders that are conditionally defined if `camel-snake-kebab` is on your classpath.
  * `datafiable-result-set` now allows the connectable and the options to be omitted. Passing `nil` for the connectable (or omitting it) suppresses foreign key navigation (when using `datafy`/`nav`), instead of throwing an obscure exception.
  * Exposes `reducible-result-set` and `foldable-result-set` for users who want more control over processing result sets obtained from database metadata.
  * Adds `with-column-value` to `RowBuilder` protocol and provides a more generic `builder-adapter` that offers more control over how column values are read.

* `next.jdbc.sql`
  * `find-by-keys` supports additional options to control how queries are run and results are returned: `:columns` selects just a subset of columns from the query, `:top`, `:limit`, `:offset`, `:fetch` provide DB-specific ways to perform pagination (with all of the caveats of the underlying DB-specific syntax). In addition, `:all` may be specified in place of the where clause or example hash map to return all rows of a table (generally, for use with pagination).
  * The functions in this namespace support connectables built via `next.jdbc/with-options`.

* `next.jdbc.transaction`
  * A new dynamic Var `*nested-tx*` allows users to control how attempts to create nested transactions should behave. The default `next.jdbc` behavior is `:allow` (essentially "caveat programmer"). Behavior more compatible with `clojure.java.jdbc` can be obtained with the `:ignore` value. A `:prohibit` value will cause such attempts to throw exceptions.

* `next.jdbc.types` (new namespace)
  * Contains `as-*` functions corresponding to `java.sql.Types/*` values, so you can "type hint" how values should be handled by the underlying JDBC driver. _Note: these initially wrapped values in vectors but in order to provide better compatibility with HoneySQL they now use thunks._

* Microsoft SQL Server: the jTDS driver is now officially supported.

* PostgreSQL 12.2.0 is officially supported (previously only 10.11 was officially supported).

* Some (badly-behaved) JDBC drivers do not correctly implement empty result sets, returning `null` instead of empty collections from some driver methods. `next.jdbc` is now more tolerant of this -- treating such collections as empty instead of throwing a `NullPointerException`.

### Documentation

* Migration: offers an equivalent for `db-do-commands` from `clojure.java.jdbc`.
* MySQL **Tips & Tricks** describes batch statement rewriting (`:rewriteBatchedStatements`).
* SQLite **Tips & Tricks** describes the handling of `bool` and `bit` column types.
* Clarifies realization actions in the docstrings for `row-number`, `column-names`, and `metadata`.

## Thanks!

As always, thanks to everyone who uses `next.jdbc`, thanks to everyone who has provided feedback (especially on the documentation), reported bugs, suggested improvements, or sent pull requests. `next.jdbc` wouldn't be what it is today without all that input!

See the [change log](https://github.com/seancorfield/next-jdbc/blob/master/CHANGELOG.md) for all the changes in release order and [cljdoc.org](https://cljdoc.org/d/seancorfield/next.jdbc) for overall documentation of the latest release.
