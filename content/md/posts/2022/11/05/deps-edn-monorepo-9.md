{:title "deps.edn and monorepos IX (Polylith)",
 :date "2022-11-05 22:00:00",
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
8. _[deps.edn and monorepos VIII (Polylith)](https://corfield.org/blog/2021/11/28/deps-edn-monorepo-8/)_
9. _[deps.edn and monorepos IX (Polylith)](https://corfield.org/blog/2022/11/05/deps-edn-monorepo-9/) (this post)_
10. _[deps.edn and monorepos X (Polylith)](https://corfield.org/blog/2022/12/07/deps-edn-monorepo-10/)_

## Part IX

Hard to believe but it has been almost a year since my last post in this series!

It has been a busy, heads-down year at work, getting a lot of new features launched
and ploughing ahead with our migration to Polylith.

At this point, we have 112 components (with 110 implementations), 17 bases, and 20 projects,
totalling 100,984 lines of code, out of a total of 132,765 lines -- so we are 76% through our migration!

### New Relic

A year ago, I was pleased that New Relic released 7.4.0 and gave us compatibility
with JDK 17. They recently released 7.11.0 and JDK 19 support. For years we've
stayed with LTS versions of the JDK, even though we're not using the Oracle
binaries. With the CVEs found against JDK 17, we moved to JDK 18 in production
and we're on JDK 19 in dev/CI right now, so we're pleased to see the update
from New Relic, so we can move forward with JDK 19 and, via `--enable-preview`,
we can start to take advantage of virtual threads!

### Automating Builds

We've continued to move our ad hoc scripts into `build.clj` and we discussed
whether [Babashka](https://github.com/babashka/babashka) would make sense for us.
It's an amazing project, and I've adopted it in some of my OSS projects, but we
ultimately decided that we're already reliant on `build.clj` and the JVM so as
long as we can continue to leverage that, we have no need for additional tools.

## Polylith

We've continued our migration to Polylith. We're very close now to getting rid
of one of our "big" legacy subprojects (our "kitchen sink" `worldsingles`
subproject -- just two namespaces left to refactor). And we have just three
legacy projects to move to `bases` and refactor the common code to `components`.
We're very happy with the incremental testing that this migration has enabled:
our build times have generally been cut dramatically, unless we touch a "brick"
that is widely used (which still happens while we are refactoring but we expect
will happen a lot less once we're "done").

We continue to contribute to Polylith, with recent PRs to address classloader
and memory usage issues, as well as support for `:as-alias`, and the maintainers
continue to be responsive so we're still happy to recommend this as an
architectural approach.

We now have two swappable component implementations. I've talked before about
our HTTP client component (using either Hato on modern JDKs or http-kit on
legacy JDKs) but we also have two i18n implementations now. Our traditional
i18n component uses a database-backed implementation as part of our content
management system that supports over a dozen languages, with dedicated
translators using our Admin app to work with out content, that can then be
promoted to production automatically. In order to support our UI/UX folks,
we've added an i18n implementation that uses a local JSON file, so that they
can work on our (Selmer) HTML templates and not need all of the machinery that
our production system requies. This swappable implementation allows them to
work with a local JSON file to specify all the keys in multiple languages,
while keeping the HTML template format the same as our main system. We have a
`project` that builds a small "preview" JAR that our UI/UX folks can run
locally (in a Docker instance -- their choice) and they can work with a
JSON file. Then we take those HTML templates, and incorporate the JSON content
into our database, and everything "just works" in our production system.
Another "win" for Polylith and swappable component implementations!

## Portal

[Portal](https://github.com/djblue/portal) has become increasingly important
to my day-to-day workflow. I have recently adopted the nREPL middleware
so that I can completely ignore the nREPL window that Calva provides and rely
entirely on the Portal window displaying results.

I've enhanced my [VS Code/Calva/Portal setup](https://github.com/seancorfield/vscode-calva-setup)
as well as my [dot-clojure dev/repl startup](https://github.com/seancorfield/dot-clojure).
Feel free to DM me on Slack with any questions about my setup. I hope to find
some time to do some screencasts, showcasing my VS Code/Calva/Portal workflow.

## CVE Checking

In part 8, I talked about `nvd-clojure` and using it from the CLI. We've
since switched to [clj-watson](https://github.com/clj-holmes/clj-watson)
because we prefer the output it produces, highlighting the dependency
path to the vulnerabilities and the recommended updates, if known.

We've recently migrated from Jetty 9 to Jetty 11, via Ning Sun's excellent
[Jetty 10/11 adapter](https://github.com/sunng87/ring-jetty9-adapter/)
which provides very slick WebSocket socket (which we'd had to do manually
with Jetty 9). When we first moved to 0.17.9, `clj-watson` warned about
several CVEs against Jetty 10 and its dependencies, so it was nice to upgrade
to 0.18.1 and see all those CVEs go away!
