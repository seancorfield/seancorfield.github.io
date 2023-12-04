{:title "Long-Term Funding, Update #6",
 :date "2023-12-31 15:15:00", :draft? true,
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
These will get updated again once use of JDK 21 has become more widespread.

I've been slowly working my way through the "TBD" items in the various
**Language** guides, including the **Glossary**, although some of them really need input from community
members who have specialist knowledge in those areas. In particular, the
[**Concurrency and Parallelism**](https://clojure-doc.org/articles/language/concurrency_and_parallelism/)
and
[**Polymorphism**](https://clojure-doc.org/articles/language/polymorphism/)
guides still have a number of "TBD" items that I don't feel qualified to write!
Volunteers welcome!

Thank you to [@adham-omran](https://github.com/adham-omran) for a PR that
added the [Date and Time cookbook](https://clojure-doc.org/articles/cookbooks/date_and_time/)
and to [@samhedin](https://github.com/samhedin) for a PR that added
a section about [adding Java code to Clojure projects](https://clojure-doc.org/articles/cookbooks/cli_build_projects/#including-java-code-in-a-clojure-project) to the `tools.build` cookbook.

To wrap up the year of work on `clojure-doc.org`, I consider the Clojurists
Together funding to have been a massive success. The site has been completely
overhauled at this point, bringing it up to date with Clojure 1.11 and
removing all the outdated and now-duplicated material that was previously
missing from the official Clojure documentation. In addition, by raising the
profile of `clojure-doc.org` in the community, contributions have increased
with two new cookbooks added via Pull Requests and several other sections of
the site either getting PRs or being updated by me in response to extensive
feedback from the community (mostly on Slack).

Keeping the site updated now feels like a tractable problem and I'm hoping
to find time in 2024 and beyond to add more content to the site, especially
when Clojure 1.12 is released and there are a lot of enhancements to Java
interop!

## `deps-new`

* 0.5.next -- doc updates -- `:src-dirs`

`clj-new`?

## HoneySQL

* 2.5.1103 -- smarter quoting of entities, smarter handling of metadata, new options

## Expectations

* 2.1.next -- doc updates

## `next.jdbc`

* 1.3.next -- improve compatibility of `insert-multi!`; doc updates for `find-by-keys` and database drivers
