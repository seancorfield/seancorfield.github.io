{:title "deps.edn and monorepos VIII (Polylith)",
 :date "2021-11-28 22:00:00",
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
6. _[deps.edn and monorepos VI (Polylith)](https://corfield.org/blog/2021/10/01/deps-edn-monorepo-6/)_
7. _[deps.edn and monorepos VII (Polylith)](https://corfield.org/blog/2021/10/13/deps-edn-monorepo-7/)_
8. _[deps.edn and monorepos VIII (Polylith)](https://corfield.org/blog/2021/11/28/deps-edn-monorepo-8/) (this post)_

## Part VIII

It's been a month and a half since the last update so I figured an update was due, although I don't
really feel like I have much to report.

### New Relic

New Relic finally released a version of their Java Agent that is compatible with JDK 17
([7.4.0 on October 28th](https://docs.newrelic.com/docs/release-notes/agent-release-notes/java-release-notes/java-agent-740/))
so we are finally rolling JDK 17 out into production. Early signs are good: with no
changes in configuration at all it looks like the G1 collector has become a bit more aggressive and,
although we saw marginally higher CPU usage for GC for the first few days (and
marginally lower throughput), now that we've been running for about a week, it
looks like GC has settled back down (to ~0.1% of CPU), throughput has settled
back down, and overall memory usage is lower with garbage being collected more
aggressively than on JDK 11. We're planning to roll more processes over to JDK 17
this week and if things continue to go smoothly, we'll start experimenting with
ZGC since several people have had good things to say about that.

## `tools.build`

This library has seen several new releases since my last update and we have
now replaced all of the `ant` / `build.xml` / `bash` scripts around our "modern"
(Clojure) apps by switching from `.tar.gz` files built by the latter to `.zip`
files built by the former. They're about 50% larger but bandwidth and disk
space really aren't issues these days and having "everything" controllable via
a single `build.clj` script, which we can use from the REPL, simplifies that
last piece of our build/deploy chain.

Our legacy (non-Clojure) apps still rely on `ant` / `build.xml` / `bash` scripts
but the rewrite continues aggressively so I expect to ditch that in 2022!

### Automating Builds

On the subject of automation and `tools.build`, I finally bit the bullet and
updated both [`next.jdbc`](https://github.com/seancorfield/next-jdbc) and
[`honeysql`](https://github.com/seancorfield/honeysql) to automatically,
test, build, and deploy, full "release" JARs to Clojars whenever I cut a
new release on GitHub (effectively whenever I push a new tag that starts with `v`).
This removes the last manual step that I was performing with these libraries
when making a new release.

## Clojure CLI (and `tools.deps.alpha`)

These have also had several new releases since my last update. There is now
a very nice `clojure -X:deps list` command that shows a sorted list of the
actual resolved dependencies and versions. In addition, the long-standing
issue of `:local/root` deps becoming stale and not recomputed has been
addressed, reducing the need for `-Sforce` in many situations (especially
in monorepos, such as you have with Polylith!).

## Polylith

Nothing specific to report here, except that I have declared Fridays to
be "refactoring Fridays" for a while at work so I can more aggressively
refactor our existing subprojects into `components` (and `bases`) and
expand the leverage that incremental testing provides, via Polylith.

We're at 18 projects, 6 bases, 35 components, and just shy of 26k lines
of our production code using Polylith now (out of just over 96k lines).

## Other Important Releases

In closing, I want to call out [Clojure 1.11 Alpha 3](https://clojure.org/releases/devchangelog#v1.11.0-alpha3)
which has a number of convenience features (and bug fixes). We have Alpha 3 in QA
and we're already using `parse-long`, `parse-double`, and `random-uuid` (we'll
probably start using the new `clojure.java.math` namespace next week). We have
had Alpha 2 in production for a couple of months and I expect we'll have Alpha
3 in production next week.

In addition, the [National Vulnerability Database dependency-checker library](https://github.com/rm-hull/nvd-clojure/)
has merged the Pull Request I submitted to allow it to be installed as a CLI "tool"
so you can now do:

```bash
$ clojure -Ttools install nvd-clojure/nvd-clojure '{:mvn/version "RELEASE"}' :as nvd

$ clojure -Tnvd nvd.task/check :classpath '"'$(clojure -Spath)'"'
# or with aliases to pull in dependencies:
$ clojure -Tnvd nvd.task/check :classpath '"'$(clojure -Spath -A:any:aliases)'"'
```

This will highlight any security vulnerabilities you may have in your dependencies
so it's definitely worthwhile to install and run this tool on all of your projects!
