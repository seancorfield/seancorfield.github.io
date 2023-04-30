{:title "Long-Term Funding, Update #2",
 :date "2023-04-30 12:00:00",
 :tags ["clojure" "clojure-doc.org" "open source" "tools.build" "community"]}

In my [previous Long-Term Funding update](https://corfield.org/blog/2023/02/28/long-term-funding-1/)
I said that I planned "to review and/or overhaul the Getting Started,
Introduction, and Web Development sections, with a focus on the latter."
(of the [clojure-doc.org](https://clojure-doc.org) website).

I mostly achieved that goal but didn't get to the additional goal I set of writing
a `tools.build` cookbook. I have sketched out the topics I hope to cover in
that cookbook, however.

How did the past two months go?<!--more-->

## `clojure-doc.org`

I updated [Getting Started with Clojure](https://clojure-doc.org/articles/tutorials/getting_started/)
to talk about both Leiningen **and** the Clojure CLI and wrote a new
[Getting Started with the Clojure CLI](https://clojure-doc.org/articles/tutorials/getting_started_cli/)
page that also covers `tools.build` / `build.clj` and building uberjars.
Both pages now have a new section highlighting **Interactive Development**
(as opposed to "just" using a REPL).

I reviewed the [Introduction to Clojure](https://clojure-doc.org/articles/tutorials/introduction/)
and decided it was mostly sound, but added more examples, tweaked the formatting,
and added references to the Clojure CLI.

I reworked John Gabriele's excellent
[Basic Web Development guide](https://clojure-doc.org/articles/tutorials/basic_web_development/)
to use the Clojure CLI, updated all the library references, and reordered the
sections to work bottom-up so that code could always be evaluated in the REPL.

I didn't get as far as I wanted with the `tools.build` cookbook due to a
combination of writer's block (and stress over my mother being taken to hospital
unexpectedly for a fractured hip -- she's nearly 90, has osteoporosis, and her
oxygen levels are too low/precarious for surgery at this point; she's home now
but it's been a rough few weeks).

## `deps-new`

As the [Practical.li project templates](https://github.com/practicalli/project-templates)
were being developed, John found a bug in the `template` project which I fixed
and in turn I reviewed drafts of his
[articles about creating project templates using `deps.new`](https://practical.li/blog/posts/create-deps-new-template-for-clojure-cli-projects/),
both of which are now linked from the `deps-new` README and documentation.

## `honeysql`

During March/April, HoneySQL saw four new releases, which were mostly an even
split between improving documentation and expanding ANSI SQL support.

Many of the questions I see about HoneySQL on Slack (and other places) suggest
deficiencies in the documentation so, while I try to answer those questions
directly on Slack, I also tend to create GitHub issues for them to see if I
can improve the documentation in those areas.

Some of the ANSI SQL improvements including support for `INTERVAL`,
keyword arguments in function calls,
nested `JOIN`,
standardizing `TRUNCATE` syntax,
and `WITH ORDINALITY`. There were also bug fixes and documentation
improvements around `DO UPDATE SET` and a number of other constructs.

I'd also like to give a special shout out to
[Eugene Pakhomov](https://github.com/p-himik)
who contributed three pull requests to the release that went out in early March.

## `next.jdbc`

`next.jdbc` also saw four releases, which provided a mix of bug fixes,
compatibility improvements (with `clojure.java.jdbc`, to ease migration),
documentation improvements, and a few enhancements.

## `next.jdbc.xt`

The [Juxt](https://www.juxt.pro/) team were present in force at
[Clojure/conj](https://2023.clojure-conj.org/) this year and announced
early access to [XTDB 2.0](https://www.xtdb.com/blog/2x-early-access).

I think this will be a very exciting release, with improved bitemporality
(including temporal joins and range scans), a new columnar architecture,
and -- the part that interests me the most -- a dynamic relation engine
that provides both a Datalog API **and** a SQL API.

That inspired me to create a new project, offering experimental support
for XTDB 2.0 in `next.jdbc`: [`next.jdbc.xt`](https://github.com/seancorfield/next.jdbc.xt).
This allows you to treat an XTDB client "node" as a "connectable" so
you can call `execute!`, `execute-one!`, and `plan` on it (as well as the
full range of "friendly SQL functions").

As XTDB 2.0 evolves, I plan to continue to enhance this new project to
hopefully support batch operations and perhaps full transaction support,
if possible.

## What's Next?

In May/June, I hope to get the `tools.build` cookbook written and
to review/overhaul the Libraries pages (both authoring and the directory).
