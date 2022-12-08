{:title "deps.edn and monorepos VI (Polylith)",
 :date "2021-10-01 23:00:00",
 :tags ["clojure" "monorepo" "polylith" "tools.build" "new relic"]}

This is part of an ongoing series of blog posts about our ever-evolving use of the Clojure CLI,
`deps.edn`, and [Polylith](https://polylith.gitbook.io/), with our monorepo at
[World Singles Networks](https://worldsinglesnetworks.com).<!--more-->

### The Monorepo/Polylith Series

_This blog post is part of an ongoing series following our experiences with our Clojure monorepo and our migration to Polylith:_

1. _[deps.edn and monorepos](https://corfield.org/blog/2021/02/23/deps-edn-monorepo/)_
2. _[deps.edn and monorepos II](https://corfield.org/blog/2021/04/21/deps-edn-monorepo-2/)_
3. _[deps.edn and monorepos III (Polylith)](https://corfield.org/blog/2021/06/06/deps-edn-monorepo-3/)_
4. _[deps.edn and monorepos IV](https://corfield.org/blog/2021/07/21/deps-edn-monorepo-4/)_
5. _[deps.edn and monorepos V (Polylith)](https://corfield.org/blog/2021/08/25/deps-edn-monorepo-5/)_
6. _[deps.edn and monorepos VI (Polylith)](https://corfield.org/blog/2021/10/01/deps-edn-monorepo-6/) (this post)_
7. _[deps.edn and monorepos VII (Polylith)](https://corfield.org/blog/2021/10/13/deps-edn-monorepo-7/)_
8. _[deps.edn and monorepos VIII (Polylith)](https://corfield.org/blog/2021/11/28/deps-edn-monorepo-8/)_
9. _[deps.edn and monorepos IX (Polylith)](https://corfield.org/blog/2022/11/05/deps-edn-monorepo-9/)_
10. _[deps.edn and monorepos X (Polylith)](https://corfield.org/blog/2022/12/07/deps-edn-monorepo-10/)_

## Part VI

A lot of things have changed in the month (and a bit) since my last post in this series!
Our refactoring to Polylith has ramped back up, Polylith itself has had a new alpha release,
`tools.build` has continued to evolve nicely, and Java 17 has been released!

### Polylith

We have refactored two more applications into `bases` and `projects` so about a quarter of
our codebase is Polylith-shaped now. The `poly deps` matrix no longer looks like a couple
of dozen unrelated `components` and now shows `bases` that actually depend on them!

The rewriting of our legacy (non-Clojure) code is now all going to Polylith bricks and as
we add new features to our dating platform, that is also going into Polylith as new bricks.

I mentioned back in March that we were
[switching from `clj-http` to `httpkit`](https://corfield.org/blog/2021/03/25/little-things/)
and that has been completed but it had the side effect that we have lost some telemetry in
New Relic around "web external" activity in our apps because it doesn't recognize `httpkit`'s
calls. The obvious thing at this point would be to switch to something like
[`hato`](https://github.com/gnarroway/hato) since that wraps Java's built-in `HttpClient`
functionality but, unfortunately, our legacy (non-Clojure) code still has to run on Java 8
and a lot of our Clojure code is shared between those apps and our "modern" (pure Clojure) apps
that run on more recent Java versions. I've been experimenting with `hato` in our test suite
code, since that is not shared with the legacy apps, and I've decided that this is a good
opportunity to leverage the "swappable implementation" feature of Polylith's `components`.
We're going to write an `http-client` interface (component) and have two implementations:
one using `httpkit` and one using `hato` and then our legacy apps can use the `httpkit`-based
implementation while our other apps can use the `hato`-based implementation, with the shared
code written against the common interface. We'll be able to switch `projects` over one at a time
if we want and we'll be able to develop against either implementation based on
[profiles in Polylith](https://github.com/polyfy/polylith#profile).

As noted above, Polylith has had a new alpha release which includes the work I talked about
in the last post to install `poly` as a Clojure CLI "tool" and to run per-project test suite
fixtures, as well as now being able to test individual bricks. Right now, we're running against
a SHA on master so that we can pick up a recent fix for those per-project test suite fixtures
and support for `data_readers.clj`.

### `tools.build`

The `uber` task in `tools.build` has now reached parity with `depstar`'s functionality so
I have archived `depstar` to encourage people to switch over to `tools.build`. The log4j2
plugins cache merging from `depstar` has been spun out into a separate project that provides a
[log4j2 plugins cache conflict handler](https://github.com/seancorfield/build-uber-log4j2-handler)
for `tools.build` (as of v0.4.0).

In addition to switching all of my OSS projects over to `tools.build` for JAR building,
I've been able to switch our monorepo at work over to it as well for uberjar building,
and I've abstracted a lot of the common code from various `build.clj` files to a new
library -- [build-clj](https://github.com/seancorfield/build-clj) -- that lets you
simplify your `build.clj` file by providing sane defaults for a lot of the options
that `tools.build` offers. That `build-clj` library automatically includes the log4j2
conflict handler mentioned above.

I've also created a new, simpler tool for creating new CLI / `deps.edn` projects, based
on `tools.build` as I hinted at in part IV of this series:
[deps-new](https://github.com/seancorfield/deps-new). In particular, this new tool makes
it much, much easier to write your own project templates since they can be almost entirely
declarative with all the "heavy lifting" done by `tools.build` itself.

In part IV, I showed our `:build` alias which has now been simplified to this:

```clojure
  :build
  {:deps {org.clojure/clojure {:mvn/version "1.11.0-alpha2"}

          io.github.seancorfield/build-clj {:git/tag "v0.4.0" :git/sha "54e39ae"
                                            :exclusions [org.slf4j/slf4j-nop]}
          ;; and local build tools:
          worldsingles/build {:local/root "build"}
          poly/artifact-uploader-cli {:local/root "bases/artifact-uploader-cli"}
          poly/artifact-uploader {:local/root "components/artifact-uploader"}}
   :ns-default build}
```

Gone is the dependency on `depstar`, and the `tools.build` dependency has been
replaced by my `build-clj` wrapper which has simplified our artifact building
code substantially (all my OSS projects also use `build-clj` -- see, for example,
[`next.jdbc`'s `build.clj`](https://github.com/seancorfield/next-jdbc/blob/develop/build.clj)).

Oh, and we're on Clojure 1.11 Alpha 2 in production (as of today).

### Java 17

Even though our production systems run on Java 11 (except for our legacy apps), we've
been testing locally on Java 14, 15, and 16 so that we can be ready for the new LTS
release, Java 17. We've been testing on that for about a week now but we're still
blocked from upgrading production by lack of support for Java 17 in New Relic.

They just released [version 7.3.0](https://docs.newrelic.com/docs/release-notes/agent-release-notes/java-release-notes/java-agent-730/)
with support for Java 16 (yesterday!), and they plan to support Java 17 fully in
[version 7.4.0](https://github.com/newrelic/newrelic-java-agent/issues/384).
See [issue 418](https://github.com/newrelic/newrelic-java-agent/issues/418) for
the ongoing work, which is basically complete, but there are still
[a few issues to shake out](https://github.com/newrelic/newrelic-java-agent/issues?q=is%3Aissue+is%3Aopen+17)
before they're fully there.

The timing is pretty good for us really since 7.4.0 will likely be available before
we go live on our new virtual server farm at the data center so we expect to roll
straight to Java 17, from Java 11, at that point.
