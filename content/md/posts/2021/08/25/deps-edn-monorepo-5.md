{:title "deps.edn and monorepos V (Polylith)",
 :date "2021-08-25 18:05:00",
 :tags ["clojure" "monorepo" "polylith" "tools.build"]}

This is part of an ongoing series of blog posts about our ever-evolving use of the Clojure CLI,
`deps.edn`, and [Polylith](https://polylith.gitbook.io/), with our monorepo at
[World Singles Networks](https://worldsinglesnetworks.com).<!--more-->

### The Monorepo/Polylith Series

_This blog post is part of an ongoing series following our experiences with our Clojure monorepo and our migration to Polylith:_

1. _[deps.edn and monorepos](https://corfield.org/blog/2021/02/23/deps-edn-monorepo/)_
2. _[deps.edn and monorepos II](https://corfield.org/blog/2021/04/21/deps-edn-monorepo-2/)_
3. _[deps.edn and monorepos III (Polylith)](https://corfield.org/blog/2021/06/06/deps-edn-monorepo-3/)_
4. _[deps.edn and monorepos IV](https://corfield.org/blog/2021/07/21/deps-edn-monorepo-4/)_
5. _[deps.edn and monorepos V (Polylith)](https://corfield.org/blog/2021/08/25/deps-edn-monorepo-5/) (this post)_
6. _[deps.edn and monorepos VI (Polylith)](https://corfield.org/blog/2021/10/01/deps-edn-monorepo-6/)_
7. _[deps.edn and monorepos VII (Polylith)](https://corfield.org/blog/2021/10/13/deps-edn-monorepo-7/)_
8. _[deps.edn and monorepos VIII (Polylith)](https://corfield.org/blog/2021/11/28/deps-edn-monorepo-8/)_
9. _[deps.edn and monorepos IX (Polylith)](https://corfield.org/blog/2022/11/05/deps-edn-monorepo-9/)_
10. _[deps.edn and monorepos X (Polylith)](https://corfield.org/blog/2022/12/07/deps-edn-monorepo-10/)_
11. _[deps.edn and monorepos XI (Polylith)](https://corfield.org/blog/2023/07/15/deps-edn-monorepo-11/)_

## Part V

It's been about a month since my last post in this series so I felt an update is about due.
I've spent a lot of the last month focused on rewriting our legacy (non-Clojure) code, mostly
into existing subprojects that already included earlier parts of the rewrite so we haven't
added much to our Polylith journey recently (just two new `components`).
I've also spent some time streamlining our
test/build/deploy process and preparing for our migration from one network zone at the data
center to all new virtualized servers in another network zone, which has been its own kind of
special "joy", but our automated upload-and-deploy machinery is in place on the seven new application
servers and now we're back to waiting on the managed services company to do their thing with
file and database synchronization (we have a tiny software team and no "ops" folks so we have
relied on the same managed services company for a lot of the infrastructure "heavy lifting" for years).

That's not to say there hasn't been anything exciting happening!

### Polylith

The Polylith branch that we've been working against (`issue-66`) has now been merged and versions
[0.2.0-alpha10](https://github.com/polyfy/polylith/releases/tag/v0.2.0-alpha10) and
[0.2.0-alpha11](https://github.com/polyfy/polylith/releases/tag/v0.2.0-alpha11) have been released,
along with a `migrate` command in the `poly` tool to help folks move from earlier versions to the
new CLI/`deps.edn`-based structure we've been using for several months.

I've been contributing
to Polylith as well, with a PR to fix a [problem with how `git` deps were handled](https://github.com/polyfy/polylith/issues/108)
and a PR to support [installing Polylith as a CLI "tool"](https://github.com/polyfy/polylith/issues/114).
If you are using my [dot-clojure repo](https://github.com/seancorfield/dot-clojure), you'll find
a `tools` folder in it, with `clojure -Ttools install`'d versions of `clj-new`, `depstar`, `deps-new` (as `new`),
and now the `poly` tool (via a full SHA on the main branch).

In addition, my suggestion to [provide per-project test suite fixtures](https://github.com/polyfy/polylith/issues/110)
has also been implemented (and we're already using that at work). Several other [Polylith issues](https://github.com/polyfy/polylith/issues)
are being worked on as well, so the next release of the `poly` tool will be an exciting one!

### `tools.build`

We've continued to expand our use of `tools.build` at work, completing the work of replacing
all our legacy bash `build` script, and it has had a number of
[useful enhancements](https://github.com/polyfy/polylith/issues)
during the past month, adding the ability to update the `<scm>` properties in `pom.xml` and
a `pom-path` function that returns the computed path of where `pom.xml` will be written so
that you can use it in your deployment step. I've updated several of my open source projects
to take advantage of this. Here's what I typically have for my `:build` alias now:

```clojure
  ;; for help: clojure -A:deps -T:build help/doc
  :build {:deps {io.github.clojure/tools.build {:git/tag "v0.1.9" :git/sha "6736c83"}
                 io.github.slipset/deps-deploy {:sha "b4359c5d67ca002d9ed0c4b41b710d7e5a82e3bf"}}
          :ns-default build}
```

Then in my `build.clj` script (here's the [`next.jdbc` `build.clj` script](https://github.com/seancorfield/next-jdbc/blob/develop/build.clj)),
I have my `deploy` function (using [Erik Assum's `deps-deploy` library](https://github.com/slipset/deps-deploy)):

```clojure
(defn deploy "Deploy the JAR to Clojars." [opts]
  (dd/deploy (merge {:installer :remote :artifact jar-file
                     :pom-file (b/pom-path {:lib lib :class-dir class-dir})}
                    opts)))
```

I'll probably curate the reusable bits of my various `build.clj` scripts into a "build library"
on GitHub soon to reduce the duplication across my projects (and so folks can reuse those pieces
if they wish).

> Note: I am no longer using `depstar` for building library JARs in my open source projects -- relying on `tools.build`'s `jar` task for this (although, at work we still have to use `depstar` for building uberjar files since we rely on a few features that `uber` does not yet have in `tools.build`).
