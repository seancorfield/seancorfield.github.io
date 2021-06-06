{:title "Release 0.7.0 of clojure.java.jdbc",
 :date "2017-07-17 10:40:00",
 :tags ["clojure" "jdbc" "open source"]}

The stable 0.7.0 release of `java.jdbc` -- the [Clojure Contrib JDBC library](https://github.com/clojure/java.jdbc) -- has been baking for over a year, across of a trail of alpha and beta releases, and is now, finally, available!

While you _could_ read the [`java.jdbc` Change Log](https://github.com/clojure/java.jdbc/blob/master/CHANGES.md) to figure out what is new in this release, I thought it would be easier to consolidate all the changes into a blog post, with changes organized by category, and provide justification for the various changes.<!-- more -->

I recently ran a [survey about versions](https://www.surveymonkey.com/results/SM-CJY2YMHP/) of both Clojure and `java.jdbc` in use and had nearly 100 responses (thank you to everyone who completed the survey!). Only one respondent is still on Clojure 1.7.0, with about two thirds on 1.8.0, and a full third of respondents on a 1.9.0 alpha build. No one responded that they're on an earlier version of Clojure. That's great news for Clojure library maintainers, as it means we can take advantage of modern features in the language (such as transducers), and it also means that it is worthwhile to offer optional namespaces providing specs. **Accordingly, `java.jdbc` now requires Clojure 1.7.0 or later.**

Also very encouraging was to see that almost a third of `java.jdbc` users are on alpha builds of what has become 0.7.0, with only a handful of users still on "legacy" versions (prior to 0.6.1).

Let's look at the major changes in 0.7.0, compared to 0.6.1!

## Reducible Queries

Now that Clojure 1.7.0 is the minimum supported version, `java.jdbc` can offer query result collections that work with transducers. Two new functions -- `reducible-query` and `reducible-result-set` -- produce collections that implement `IReduce` and will perform resource management when `reduce`d. A reducible query will run the specified query only when the result is reduced -- and it will run it each time it is reduced. A reducible result set can be constructed from any `ResultSet` object and will provide a one-off, managed forward read of the underlying data. Both of these support `reduced` results, which short-circuit the processing of the result set. They also support both the `init`-arity `reduce` and the no-`init`-arity `reduce`. Consult the docstring for `reduce` to see the full implications of this: the function passed into the no-`init`-arity `reduce` will be called with no arguments if the result set is empty!

`reducible-query` works by invoking `db-query-with-resultset` only when it is `reduce`d, passing in a function that calls `reducible-result-set` and then `reduce`s the result. `reducible-result-set` works by computing metadata and key names only when it is `reduce`d, and then walking through the rows of the result, calling the supplied function (as specified by `reduce`), stopping if that function returns a `reduced?` value.

## Improved Database Vendor Support

The `:dbtype` key in a `db-spec` is the preferred way to specify a database vendor, so you don't have to worry about "subprotocols" and "subnames" and so on. Just provide `:dbtype`, `:dbname`, `:host`, `:port`, `:user`, and `:password`, and `java.jdbc` should be able to connect you to most databases. The full list of supported `:dbtype` values is:

* `derby` -- Apache Derby
* `h2`
* `hsql` or `hsqldb`
* `jtds` or `jtds:sqlserver` -- the jTDS JDBC driver for Microsoft SQL Server
* `mssql` -- an alias for `sqlserver` below
* `mysql`
* `oracle` or `oracle:thin`, `oracle:oci` -- for the two main Oracle JDBC drivers (which use `@` in front of the `host` name, instead of `//`)
* `pgsql` -- the Impossibl PostgresSQL JDBC driver
* `postgres` or `postgresl` -- the standard PostgresSQL JDBC driver
* `redshift` -- Amazon's Red Shift JDBC driver
* `sqlite`
* `sqlserver` -- Microsoft's default JDBC driver for SQL Server (they have finally released a version on Maven Central!)

If your preferred database is not listed, head on over to [`java.jdbc`s JIRA site](https://dev.clojure.org/jira/browse/JDBC) and create an enhancement issue, with details of the JDBC driver and I'll take a look! Note that you can can specify `:classname`, along with `:dbtype` if you want to override just the JDBC driver class name that `java.jdbc` would deduce from `:dbtype`.

## Option Handling

An overall goal of 0.7.0 has been to make the API more consistent, especially in the way options are handled and passed around. The intention is that you can provide a hash map of options, as the last argument to any function, and `java.jdbc` will pass all of those options through the whole call chain, allowing you to control a lot more of the behavior of lower-level functions that are invoked by the higher-level functions. In addition, defaults for all options can be provided in your `db-spec`, and those will apply to all API functions. This is especially useful for specifying the `:entities` and `:identifiers` functions that control how SQL entity names and Clojure identifiers are constructed, everywhere in `java.jdbc`.

Here are all the new options available:

* `:auto-commit?` -- Can be passed into any API function that might cause a new database connection to be created. This allows you to turn auto-commit off for _new connections_ in databases that use this setting to control whether a query can stream its results. The behavior is inherently vendor-specific, so you may need additional options to fully enable streaming results (setting `:fetch-size` to a non-zero value is common).
* `:conditional?` -- For the two DDL-generating functions to add an existence check on the table to be created or dropped. May be a simple boolean value, a string (to be inserted between `DROP TABLE`/`CREATE TABLE` and the table name), or a function. In the latter case, the DDL string is generated as usual and then that function is called on it. This allows for databases that don't support `DROP TABLE IF EXISTS` / `CREATE TABLE IF NOT EXISTS` but instead require the `DROP` or `CREATE` statement to be wrapped in a specific existence checking query.
* `:explain?` and `:explain-fn` -- So you can get the database to explain how it will run your query. Supported by `query`, `find-by-keys`, and `get-by-id`, these options will run the generated SQL with an "explain" option first, and then for real. `:explain?` can be `true` which simply prefixes the SQL with `"EXPLAIN "` or it can be a string if your database needs a different syntax (such as HSQLDB needing `"EXPLAIN PLAN FOR "`). The explanation output is processed by the `:explain-fn`, which defaults to `println`, but can be used to send explanations to a logging system etc.
* `:qualifier` -- Lets you easily produce namespace-qualified column names from your queries.
* `:read-columns` -- To specify how columns of data are read from a `ResultSet` object. The default behavior is to call `result-set-read-column` which implements the `IResultSetReadColumn` protocol. This allows finer-grained control when you are working with multiple databases in a single application.
* `:read-only?` -- Like `:auto-commit?` this can be passed into any API function that might cause a new database connection to be created. This allows you to specify that _new connections_ be treated as readonly, which may allow the database driver to perform optimizations on how the connection and its queries behave.

## clojure.spec

`java.jdbc` provides an optional `clojure.java.jdbc.spec` namespace that is compatible with Clojure 1.9.0 Alpha 17 (it uses `clojure.spec.alpha`). This namespace provides `fdef` specs for all of the public functions in `clojure.java.jdbc` so you can `instrument` your code and get validation on all your interaction with `java.jdbc`. Note that you cannot do generative testing against `java.jdbc` because it uses a lot of Java types and a lot of side effects. _I have an experimental branch where I've been investigating what generators would look like for `java.jdbc` but don't hold your breath for that becoming part of the **master** branch!_

## New and Changed API Functions

* `as-sql-name` dropped its single arity (curried) version.
* `get-isolation-level` will return the current isolation level inside a transaction, if any.
* `quoted` dropped its 2-arity version, and now supports certain keywords for common entity-quoting strategies: `:ansi` (wraps entities in double quotes), `:mysql` (wraps entities in backticks), `:oracle` (wraps entities in double quotes), and `:sqlserver` (wraps entities in square brackets). These match the options in HoneySQL.
* `reducible-query` and `reducible-result-set` -- see **Reducible Queries** above.

## Documentation

At present, the auto-generated API documentation (on https://clojure.github.io) is stale due to some bugs in the toolchain used by the Clojure Build Server, so I recommend reading the [community-maintained `java.jdbc` documentation](http://clojure-doc.org/articles/ecosystem/java_jdbc/home.html) on clojure-doc.org, in addition to using `clojure.repl/doc` to review the docstrings for API functions. That community-maintained documentation is currently going through a major overhaul to expand, clarify, and update the information presented -- I encourage users of `java.jdbc` to get involved by submitting Pull Requests to help improve the documentation for everyone!
