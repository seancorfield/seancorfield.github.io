{:title "SQL NULL, s/nilable, and optionality",
 :date "2018-12-06 11:30:00",
 :tags ["clojure" "jdbc"]}
Rich Hickey gave a very thought-provoking talk at Clojure/conj 2018
called [Maybe Not](https://www.youtube.com/watch?v=YR5WdGrpoug&list=PLZdCLR02grLpMkEBXT22FTaJYxB92i3V3&index=2), where he mused
on optionality and how we represent the absence of a value.<!-- more -->

His talk covered many things, including how `clojure.spec/keys` currently
complects both structure and optionality (and his thoughts on fixing that
in a future version of `clojure.spec`), but his mention of `s/nilable` was what
triggered an "ah-ha!" moment for me.

At [World Singles Networks](https://worldsinglesnetworks.com), we deal with a lot
of data in SQL (specifically in Percona's fork of MySQL) and, in SQL, you represent
the absence of a value with `NULL` in a column. Columns that represent optional
data must be declared as nullable and when you read data from them with
[clojure.java.jdbc](https://github.com/clojure/java.jdbc) you get hash map
entries in the rows that have `nil` values. If you're using `clojure.spec` to
describe your tables, rows, and columns, then you are going to have lots of
`s/nilable` specs -- and now your "optionality" has been reified into `nil`
values, cast in the stone of your specs... which is clearly not an ideal situation!

This made me realize that `java.jdbc` probably should just omit keys whose
values represent SQL `NULL`. They are, after all, _optional_ values rather than
truly _nilable_ values.

That would be a potentially breaking change in behavior for `java.jdbc` users.
Sure, in most cases, if you have a hash map representing a row in a database
table, you're not really going to care whether `(:col row)` gives you `nil`
because `:col` maps to `nil` or because `row` doesn't contain `:col`. There are
use cases where it matters: `contains?`, row/column specs, tabular printing.

Along with changing the behavior of `NULL` columns and
[supporting `datafy` and `nav`](http://corfield.org/blog/2018/12/03/datafy-nav/),
I have a lot of other changes that I'd like to apply to `java.jdbc`, such as
automatically qualifying column keys with the table from which they came,
improving overall performance (by no longer converting `ResultSet` objects to
sequences of hash maps), dramatically simplifying and streamlining the options
that are available (since many of them are very rarely used), and focusing on a
reducible-first API. All of which would be breaking changes.

I've learned a lot -- about Clojure, idioms, and databases -- over the seven
years that I've been maintaining `org.clojure/java.jdbc`, and it is time for a
new namespace or perhaps even a completely new project, that offers a better
way to deal with SQL databases from Clojure! I'll be writing a series of blog
posts about the differences I envisage between the current de facto standard
JDBC wrapper and where I'd like to go with this, so that I can get community
feedback on what should stay, what should change, and what should go. Stay tuned!
