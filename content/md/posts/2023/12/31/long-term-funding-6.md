{:title "Long-Term Funding, Update #6",
 :date "2023-12-24 15:15:00", :draft? true,
 :tags ["clojure" "clojure-doc.org" "expectations" "honeysql" "jdbc" "open source" "community" "clojurists together" "watson"]}

In my [previous Long-Term Funding update](https://corfield.org/blog/2023/10/31/long-term-funding-5/)
I said I would review and update of the
"cookbooks" section and make another pass of "TBD" items in the "language"
section.<!--more-->

## `clojure-doc.org`

I reviewed and updated the cookbooks for
[Files and Directories](https://clojure-doc.org/articles/cookbooks/files_and_directories/)
[Mathematics](https://clojure-doc.org/articles/cookbooks/math/),
[Middleware](https://clojure-doc.org/articles/cookbooks/middleware/),
[Parsing XML in Clojure](https://clojure-doc.org/articles/cookbooks/parsing_xml_with_zippers/), and
[Strings](https://clojure-doc.org/articles/cookbooks/strings/),
bringing them all up to Clojure 1.11 (and testing the examples -- and
fixing the broken ones).

For the **Mathematics** cookbook, that
meant rewriting the content that previously used Java interop and/or
[math.numeric-tower](https://github.com/clojure/math.numeric-tower/) to use
the new-in-1.11 `clojure.math` namespace.

Several cookbooks got minor updates
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
**Volunteers welcome!**

Thank you to [@adham-omran](https://github.com/adham-omran) for a PR that
added the [Date and Time cookbook](https://clojure-doc.org/articles/cookbooks/date_and_time/)
and to [@samhedin](https://github.com/samhedin) for a PR that added
a section about [adding Java code to Clojure projects](https://clojure-doc.org/articles/cookbooks/cli_build_projects/#including-java-code-in-a-clojure-project) to the `tools.build` cookbook.

Finally, I made a logo and a favicon for the site with my very limited
artistic "talents"!

To wrap up the year of work on `clojure-doc.org`, I consider the Clojurists
Together funding to have been a massive success. The site has been completely
overhauled at this point, bringing it up to date with Clojure 1.11 and
removing all the outdated (and now-duplicated) material that was originally
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

Although I haven't cut a new release of
[`deps-new`](https://github.com/seancorfield/deps-new/)
yet, it has had several
documentation updates, a new `:src-dirs` option to make it
easier to use `deps-new` as a library and use templates from the local
file system, and a new `:post-process-fn` to make it possible to modify
the generated project programmatically.

## Expectations

No new release yet but several documentation updates for
the `clojure.test`-compatible version of
[Expectations](https://github.com/clojure-expectations/clojure-test).

## HoneySQL

[HoneySQL](https://github.com/seancorfield/honeysql/) 2.5.1103
was released with smarter quoting of entities, smarter
handling of metadata in formatting, and new options to provide more control
over both of those features.

## `next.jdbc`

[`next.jdbc`](https://github.com/seancorfield/next-jdbc/) 1.3.909
brings improved compatibility with `clojure.java.jdbc` for
`insert-multi!` and adds a `:schema-opts` option to provide more control over
schema conventions for `datafy`/`nav`. There have also been several
documentation updates, in particular around how to use `next.jdbc/plan` and
`next.jdbc.sql/find-by-keys`. The `build.clj` has been updated to use the
`:pom-data` option introduced in `tools.build` 0.9.6, as a better example
for the community.

## `clj-watson`

[`clj-watson`](https://github.com/clj-holmes/clj-watson/) is a great tool
for checking your dependencies for known security vulnerabilities. It's a
wrapper around
[OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)
and NIST is requiring users of its NVD (National Vulnerability Database)
to switch from using data feed downloads to a new API that requires a free key for
access. The DependencyCheck library that `clj-watson` uses has been updated
to use the new API, but it isn't backward compatible so `clj-watson` needed
changes to use the new version of the library -- and to provide an easier
way for users to specify their own NVD API key.

Although the `clj-watson` maintainer has moved on from Clojure,
they've been receptive to Pull Requests to update the documentation,
update the library dependencies, add a new, optional properties file that
users can provide to override defaults, as well as a new command line option
to specify that file, if you don't want it on the classpath, and to update
the DependencyCheck library and provide documentation on how to obtain an
NVD API key and how to use it with `clj-watson`.

A v5.0.0 release of `clj-watson` has been made, with all these changes, and
is available as a git dependency. A Pull Request is pending with the README
updates.

## `org.clojure/java.data`

Finally, the [`java.data`](https://github.com/clojure/java.data/) Contrib
library has a new release, 1.1.103, which removes the dependency on
`org.clojure/tools.logging` -- which in turn means that `next.jdbc` no longer
depends on `tools.logging`, reducing the chance of conflicts for users of
either library.
