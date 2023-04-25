{:title "Long-Term Funding, Update #2",
 :date "2023-04-30 10:00:00",
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

Practical.li

## `honeysql`

4 releases

## `next.jdbc`

4 releases

## What's Next?

In May/June, I hope to get the `tools.build` cookbook written and
to review/overhaul the Libraries pages (both authoring and the directory).
