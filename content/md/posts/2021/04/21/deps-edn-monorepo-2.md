{:title "deps.edn and monorepos II",
 :date "2021-04-21 20:40:00",
 :tags ["clojure" "monorepo" "polylith"]}

A couple of months ago, I wrote about our use of [`deps.edn` with our monorepo](/blog/2021/02/23/deps-edn-monorepo/) at work.
I've updated that post to reflect changes we've made recently and I'm going to talk
in more detail about those changes in this post.<!--more-->

### The Monorepo/Polylith Series

_This blog post is part of an ongoing series following our experiences with our_
_Clojure monorepo and our migration to Polylith:_

1. _[deps.edn and monorepos](https://corfield.org/blog/2021/02/23/deps-edn-monorepo/)_
2. _[deps.edn and monorepos II](https://corfield.org/blog/2021/04/21/deps-edn-monorepo-2/) (this post)_
3. _[deps.edn and monorepos III (Polylith)](https://corfield.org/blog/2021/06/06/deps-edn-monorepo-3/)_
4. _[deps.edn and monorepos IV](https://corfield.org/blog/2021/07/21/deps-edn-monorepo-4/)_

## Part II

The main change is that we have abandoned the use of a `:defaults` alias in the repo-level `deps.edn` file -- and the use of `:override-deps` to pin versions -- and now we specify versions explicitly in subprojects' `deps.edn` directly.

## Pinning Dependencies

We had originally focused on pinning dependency versions across the whole repo
because we had historically been doing this via `:exclusions` and additional
top-level dependencies, all the way back to when we used Leiningen. We had carried
this over into our Boot setup and then into our CLI / `deps.edn` setup, although
we lost the `:exclusions` at that point since `tools.deps.alpha` will use an
explicit top-level dependency as an override for any transitive version.
We knew that some of our subprojects had problems
with more recent versions of a few of our dependencies -- we had tried to update
some dependencies and ran into broken tests so we'd created a Jira ticket to
revisit the dependency "at some future date" and moved on -- but we hadn't dug
very deep into those problems because we had more important things to deal with
at the time.

Luckily, we work in a company that understands "technical debt" and is happy with
us creating tickets to track it whenever we identify it, and working on those
tickets is considered valuable, productive work, and is balanced against feature
work when prioritizing what we work on. Lately I've been digging into quite a bit
of that technical debt and so I finally spent some time identifying exactly which
subprojects are broken by which dependencies.

The two problematic ones have turned out to be `clj-http` and, no surprise, the
Jackson JSON libraries. I talked about our ongoing [switch from `clj-http` to `http-kit`](/blog/2021/03/25/little-things/) a month ago.
I've never bothered to dig into _why_ more recent versions break several of our
file upload tests because I was happy to stay on 3.4.1 with a view to migrating
off that library anyway. With the Jackson library, it turned out that all of our
subprojects were fine with the most recent version, except for one that also
relies heavily on GraphQL and it turned out that 2.9.0 introduced changes in
how `null` was handled and those are breaking for that one subproject.

So we decided to pin that single subproject to version 2.8.11 (and 2.8.11.6) of
the Jackson libraries and let all of the other subprojects use more recent versions.
Because we still rely on the `:everything` alias in the repo-level `deps.edn` file
for our REPL expericence with the entire codebase,
we have Jackson pinned in that alias but when we run tests, those use the individual
subproject's dependencies and therefore the code is tested with whatever is the
natural transitive Jackson dependency -- apart from that one subproject which is
tested _and built into an uberjar_ using the pinned 2.8.11 version.

## `:defaults`/`:override-deps`

Although we'd settled on using an alias in the repo-level `deps.edn` file to
pin versions using `:override-deps`, as noted above it wasn't really necessary
for the vast majority of our subprojects.

There is another problem with this approach: any third-party tooling you use
with your monorepo has to be able to use this alias in order to analyze the
`deps.edn` files. For some tools, that's fine -- either they read those files
as pure EDN and don't care that a version is specified as `{}` or they have
a way to specify aliases to be used while processing those files. Even so,
it makes those tools harder to use, and it makes some tools impossible to use.

### Polylith

I mentioned [Polylith](https://polylith.gitbook.io/) in passing in the previous
article about our monorepo and I've spent quite a bit of time investigating it
since then. A new project landed on my plate at work and I decided to try out
Polylith, since I was very skeptical about it. Writing this new project as a
series of very small, independent, focused components was a good experience --
I plan to write a lot more about Polylith in the future -- but because of how
[Polylith currently works with the Clojure CLI and `deps.edn`](https://github.com/polyfy/polylith/tree/issue-66),
there was no way to provide the `:defaults` alias and therefore no way to
have these new components depend on our existing subprojects (because the `{}`
version couldn't be resolved). In contrast, it was straightforward for code
in our existing subprojects to depend on these new Polylith `components`.

This was another nail in our use of `:defaults` and `:override-deps` (although
the creator of Polylith says that some way of pinning versions of dependencies
is under consideration).

### Retiring `:defaults`

We made the decision to retire our global `:defaults` alias and to propagate
the actual dependency versions down into the subprojects' `deps.edn` files.
This was also a good opportunity to remove dependencies that were actually
provided by other subprojects. In the earlier post about our monorepo, I
showed the `activator` subproject's `deps.edn` as having these dependencies:

```clojure
{:deps
 {worldsingles/worldsingles {:local/root "../worldsingles"}
  ;; originally:
  camel-snake-kebab/camel-snake-kebab {}
  com.stuartsierra/component {}
  seancorfield/next.jdbc {}
  ;; 2021-04-21:
  camel-snake-kebab/camel-snake-kebab {:mvn/version "0.4.2"}
  com.stuartsierra/component {:mvn/version "1.0.0"}
  seancorfield/next.jdbc {:mvn/version "1.1.646"}
  }}
```

In reality, the `worldsingles` subproject already brought those dependencies
in, mostly via its own transitive dependencies, so `activator`'s `deps.edn`
file ended up looking like this:

```clojure
{:deps
 {worldsingles/worldsingles {:local/root "../worldsingles"}}}
```

This was true for a number of other dependencies too, so in the end we
did not end up duplicating as many dependency declarations as I had
expected and the loss of the central version declaration -- in the
`:defaults` alias -- wasn't as painful as I had feared.

Interestingly, removing the pinned dependencies from the repo-level
`deps.edn` file and the superfluous dependencies from the subproject
`deps.edn` files also led to a reduction in size of our deployable
artifacts of up to 200K per JAR file -- so clearly several of the
explicit dependencies we had in place were not actually necessary
at all!

Another benefit of this change is that our new Polylith `components`
can now depend on our legacy subprojects while we continue to
refactor our code into smaller, more reusable pieces.

## Current Status

At this point, we can actually work directly with many of our subprojects,
running `clojure` inside those subprojects and ignoring the
repo-level `deps.edn` file if we want.

It has also simplified our dev/test/build chain -- because we no longer
need to worry about always passing `:defaults` into all our tooling --
and by writing a small `:exec-fn` wrapper for
[Cognitect's `test-runner`](https://github.com/cognitect-labs/test-runner)
we can now specify directories to scan for tests as `:exec-args` in
our `:*-test` aliases and no longer need to specify `-d` options when
running tests (the limitation of `:main-opts` being that "last one wins"
when merging aliases, but `:exec-args` _do_ merge across aliases).

We use [antq](https://github.com/liquidz/antq/) for tracking outdated
dependencies and if you have a mix of versions in the `deps.edn` files
you ask it to analyze, it will correctly show outdated versions even
when some `deps.edn` files have the latest version -- so we haven't
lost any capability there by moving away from pinned versions.

And finally, as noted above, this paves the way for us to mix'n'match
our "legacy" subprojects with newer, Polylith-style `components` so that
it will be easier to push forward with Polylith and refactor older code
in a piecemeal manner rather than requiring "big bang" changes.

As before, if you have questions, find me on the [Clojurians Slack](https://clojurians.slack.com),
or [Twitter](https://twitter.com/seancorfield), or just ask in the comments
below.