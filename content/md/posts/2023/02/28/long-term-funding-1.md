{:title "Long-Term Funding, Update #1",
 :date "2023-02-28 10:00:00",
 :draft? true
 :tags ["clojure" "open source" "tools.build" "community"]}

As part of [Clojurists Together's Long-Term Funding for 2023](https://www.clojuriststogether.org/news/clojurists-together-2023-long-term-funding-announcement/)
I talked about working on [clojure-doc.org](https://clojure-doc.org)
which I had resurrected a few years ago, as a GitHub Pages project,
powered by [Cryogen](https://cryogenweb.org/).<!--more-->

It was originally created over a decade ago, intended as a community hub for
general documentation related to Clojure that couldn't be found on
[clojure.org](https://clojure.org) and which wasn't bound by the
[Clojure Contributor Agreement](https://clojure.org/dev/contributor_agreement).

A lot has changed since then. The Contributor Agreement has gone from a purely
paper and "snail mail" process to an online e-form. The `clojure.org` website
is now [on GitHub](https://github.com/clojure/clojure-site) and accepts
Pull Requests (if you've e-signed the CA) -- and it has expanded massively,
compared to the material it covered back then.

`clojure-doc.org` itself grew a lot of ambitious content, including extensive
guides to `clojure.java.jdbc` and `clojure.core.typed` that are now very dated.

In addition to cleaning up and modernizing `clojure-doc.org`, I also said that
I wanted to help streamline the beginner experience around Clojure tooling,
and I've talked with Alex Miller about possible avenues for that.

My first couple of months have focused on reviewing the content on
`clojure-doc.org` to establish what is still relevant and pruning outdated
content, as well as streamlining my open source projects around tooling.

## `build-clj` & `tools.build`

I was very pleased when [`tools.build`](https://github.com/clojure/tools.build)
landed and I aggressively switched both my open source projects and my work
projects over to it.

I initially felt that there was a lot of boilerplate and duplication in the
`build.clj` files I was creating and my initial reaction was to create a
simple wrapper that provided "sane" defaults to make build files smaller
and simpler. As people started using this wrapper, it began to develop more
"knobs & dials" to make it more configurable -- and then I created
[`deps-new`](https://github.com/seancorfield/deps-new) as a modern
"replacement" for [`clj-new`](https://github.com/seancorfield/clj-new)
(which in turn derived from `boot-new`, which derived from `lein new`), with the idea of
supporting more declarative templates for projects. That led to more
"knobs & dials" on my `tools.build` wrapper as the projects created by
`deps-new` depended heavily on the wrapper.

In January, I stripped the wrapper out of all my open source projects'
`build.clj` files as a way to make them a better example for beginners.
I also stripped the wrapper out of the
[usermanager example application](https://github.com/seancorfield/usermanager-example)
that I regularly link beginners to, as a basic web application, and out of
[`deps-new`](https://github.com/seancorfield/deps-new) so that people will
(hopefully) stop creating projects that use my wrapper.

The net result should be many more examples of how to use `tools.build`
directly and a normalization of how `build.clj` files should be written.

## `clojure-doc.org`

As noted above, `clojure.org` has grown dramatically since `clojure-doc.org`
was created and provides more up-to-date content in many areas, so I've
focused on pruning the duplicated and outdated content and linking to the
official site where up-to-date content now exists.

In particular, the following pages have been unlinked and removed from
the navigation, in favor of other sources:

* Books -- deferring to clojure.org
* Emacs & vim-fireplace -- the former was very outdated, the latter is still pending review
* Typed Clojure -- or `core.typed` as the old section was called, which was a decade old
* User Groups, including how to start/run a user group

The following pages have been overhauled/updated:

* About -- which is also the README now
* Community -- several sections defer to new material on clojure.org
* Content -- a work in progress as the site evolves
* Editors -- a new overview page has been added with key links to other material

Nearly 60 pages have been updated to fix broken links.
[Changes so far this year!](https://github.com/clojure-doc/clojure-doc.github.io/compare/03d64232651eb6ca77630edca6059c0c70fa72be..source)

## What's Next?

In March/April, I plan to review and/or overhaul the Getting Started,
Introduction, and Web Development sections, with a focus on the latter.
