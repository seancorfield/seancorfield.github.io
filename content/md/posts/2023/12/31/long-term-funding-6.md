{:title "Long-Term Funding, Update #6",
 :date "2023-12-31 15:15:00",
 :tags ["clojure" "clojure-doc.org" "expectations" "honeysql" "jdbc" "open source" "community" "clojurists together"]}

In my [previous Long-Term Funding update](https://corfield.org/blog/2023/10/31/long-term-funding-5/)
I said I would review and update of the
"cookbooks" section and make another pass of "TBD" items in the "language"
section.<!--more-->

## `clojure-doc.org`

I reviewed and updated the cookbooks for
[Files and Directories](https://clojure-doc.org/articles/cookbooks/files_and_directories/)
[Mathematics](https://clojure-doc.org/articles/cookbooks/math/),
[Middleware](https://clojure-doc.org/articles/cookbooks/middleware/), and
[Strings](https://clojure-doc.org/articles/cookbooks/strings/),
bringing them all up to Clojure 1.11. For the **Mathematics** cookbook, that
meant rewriting the content that previously used Java interop and/or
[math.numeric-tower](https://github.com/clojure/math.numeric-tower/) to use
the new-in-1.11 `clojure.math` namespace. Several cookbooks got minor updates
to take advantage of functions in `clojure.core` and `clojure.string` that
have been added since Clojure 1.4, when most of the original material on
`clojure-doc` was written.

I also went through all the Java documentation links and updated those to
point to the Java 17 versions (they were mostly pointing at Java 7 previously!).

Thank you to [@adham-omran](https://github.com/adham-omran) for a PR that
added the [Date and Time cookbook](https://clojure-doc.org/articles/cookbooks/date_and_time/).

## `tools.build` and `deps-new`

* 0.5.next -- doc updates

`clj-new`?

## HoneySQL

* 2.5.next?

## Expectations

* 2.1.next -- doc updates

## `next.jdbc`

* 1.3.next -- improve compatibility of `insert-multi!`; doc updates for `find-by-keys` and database drivers
