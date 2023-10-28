{:title "Long-Term Funding, Update #5",
 :date "2023-10-28 15:15:00",
 :tags ["clojure" "clojure-doc.org" "expectations" "honeysql" "jdbc" "open source" "community" "clojurists together"]}

In my [previous Long-Term Funding update](https://corfield.org/blog/2023/08/31/long-term-funding-4/)
I said I would review/overhaul the "ecosystem" and "tutorials" sections
(once I'd finished the "language" section).<!--more-->

## `clojure-doc.org`

I finished reviewing and updating the last three language sections to Clojure 1.11
([Concurrency and Parallelism](https://clojure-doc.org/articles/language/concurrency_and_parallelism/),
[Macros](https://clojure-doc.org/articles/language/macros/),
and [Laziness](https://clojure-doc.org/articles/language/laziness/)), and I
added a new section about transducers to the
[Collections and Sequences](https://clojure-doc.org/articles/language/collections_and_sequences/)
section. In the next period, I'll revisit the "TBD" items in the language
section to see what I can do to address them.

I rewrote the
[Generating Documentation](https://clojure-doc.org/articles/ecosystem/generating_documentation/)
section (ecosystem) to focus on [cljdoc.org](https://cljdoc.org/)
and removed the outdated content.

The [Web Development](https://clojure-doc.org/articles/ecosystem/web_development/)
section (ecosystem) also got a major rewrite, adding a lot of new content,
removing outdated content, and incorporating a lot of community feedback
on a draft version (thank you!).

Finally, I added a new section to the
[`tools.build` Cookbook](https://clojure-doc.org/articles/cookbooks/cli_build_projects/)
about template `pom.xml` file and the `:pom-data` approach (new in `tools.build` 0.9.6).

## `tools.build` and `deps-new`

Following on from the `tools.build` Cookbook update, I updated
[`deps-new`](https://github.com/seancorfield/deps-new) to use that new
release of `tools.build` and updated all the project templates to use
`:pom-data` for generating `pom.xml` files.
See the [v0.5.3](https://github.com/seancorfield/deps-new/releases/tag/v0.5.3)
release

I need to do the same for `clj-new` at some point -- perhaps in the next period!

## HoneySQL

HoneySQL also saw a lot of work with two releases that mostly focused on
improving BigQuery support (array subquery, select as struct, create or replace,
ignore/respect nulls, and new `:distinct` and `:expr` clauses to facilitate
certain non-standard queries), and introducing basic support for NRQL (New Relic Query Language)
as a new dialect.

NRQL has non-standard quoting rules, non-standard entity names, and inlines
all expression values, since it is mostly used directly within the New Relic
web UI or via their CLI, neither of which support parameterized queries. New
clauses and helpers have been added for `:compare-with`, `:since`, `:timeseries`,
`until`, and `:facet`.

In addition, an important bug in the helper merge functions was fixed that
affected anyone using the quoted-symbol style of DSL (instead of the keyword
style).

See [HoneySQL releases](https://github.com/seancorfield/honeysql/releases)
for more details of these two new versions.

## Expectations

[Expectations](https://github.com/clojure-expectations/clojure-test) also saw
two new releases this period. The first release focused on improving the
way test failures are reported to be more consistent and informative. The
second release was a minor one to improve `clj-kondo` support for `more-of`.

See [Expectations releases](https://github.com/clojure-expectations/clojure-test/releases)
for more details on these two new versions.

## `next.jdbc`

Finally, `next.jdbc` [1.3.894](https://github.com/seancorfield/next-jdbc/releases/tag/v1.3.894)
provides variants of `with-transaction` and `on-connection` that will rewrap an
options-wrapped connectable. This was a long-requested feature that I had
struggled with finding an elegant solution for. In addition, I updated most of
the JDBC drivers that `next.jdbc` is tested against, to flush out any issues
with those newer versions. Notably, SQLite no longer supports `:return-generated-keys true`
but you can specify `RETURNING *` in your SQL instead.

## What's Next?

In November/December, I'm hoping to complete a review and update of the
"cookbooks" section and make another pass of "TBD" items in the "language"
section.
