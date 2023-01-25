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
guides to [`clojure.java.jdbc`](https://github.com/clojure/java.jdbc) and
[`clojure.core.typed`](https://github.com/clojure/core.typed) that are now
very dated.

In addition to cleaning up and modernizing `clojure-doc.org`, I also said that
I wanted to help streamline the beginner experience around Clojure tooling,
and I've talked a lot with Alex Miller about possible avenues for that.

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
(which in turn derived from `boot-new` and `lein new`), with the idea of
supporting more declarative templates for projects. That led to more
"knobs & dials" on my `tools.build` wrapper as the projects created by
`deps-new` depended heavily on the wrapper.

In January, I stripped the wrapper out of my open source projects'
`build.clj` files as a way to make them a better example for beginners.

**Plan to strip the wrapper from [usermanager](https://github.com/seancorfield/usermanager-example)**

**Plan to strip the wrapper from `deps-new`**

## `clojure-doc.org`

As noted above, `clojure.org` has grown dramatically since `clojure-doc.org`
was created and provides more up-to-date content in many areas, so I've
focused on pruning the duplicated and outdated content and linking to the
official site for:

* [books]()
* [user groups]()
* [how to start/run a user group]()
* [getting started with Clojure]() -- although the official site doesn't cover Leiningen so that is still covered on `clojure-doc.org`
* ... what else ...
