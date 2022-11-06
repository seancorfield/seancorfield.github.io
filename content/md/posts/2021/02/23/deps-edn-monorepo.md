{:title "deps.edn and monorepos",
 :date "2021-02-23 15:30:00",
 :tags ["clojure" "monorepo"]}

At [World Singles Networks llc](https://worldsinglesnetworks.com) we have been using
a monorepo for several years and it has taken us several iterations to settle on a
structure that works well with the Clojure CLI and `deps.edn`.

_Updated April 21st, 2021 to reflect recent changes in our setup.
See [`deps.edn` and monorepos II](/blog/2021/04/21/deps-edn-monorepo-2/)
for more details._<!--more-->

### The Monorepo/Polylith Series

_This blog post is part of an ongoing series following our experiences with our Clojure monorepo and our migration to Polylith:_

1. _[deps.edn and monorepos](https://corfield.org/blog/2021/02/23/deps-edn-monorepo/) (this post)_
2. _[deps.edn and monorepos II](https://corfield.org/blog/2021/04/21/deps-edn-monorepo-2/)_
3. _[deps.edn and monorepos III (Polylith)](https://corfield.org/blog/2021/06/06/deps-edn-monorepo-3/)_
4. _[deps.edn and monorepos IV](https://corfield.org/blog/2021/07/21/deps-edn-monorepo-4/)_
5. _[deps.edn and monorepos V (Polylith)](https://corfield.org/blog/2021/08/25/deps-edn-monorepo-5/)_
6. _[deps.edn and monorepos VI (Polylith)](https://corfield.org/blog/2021/10/01/deps-edn-monorepo-6/)_
7. _[deps.edn and monorepos VII (Polylith)](https://corfield.org/blog/2021/10/13/deps-edn-monorepo-7/)_
8. _[deps.edn and monorepos VIII (Polylith)](https://corfield.org/blog/2021/11/28/deps-edn-monorepo-8/)_
9. _[deps.edn and monorepos IX (Polylith)](https://corfield.org/blog/2022/11/05/deps-edn-monorepo-9/)_

### What does our monorepo look like?

Our main git repo has a `build` folder containing scripts, tooling, and configuration,
and a `clojure` folder containing all our Clojure source and test code.

That `clojure` folder has over three dozen subprojects that represent either
reusable "libraries" or "applications". We build just over a dozen application
artifacts from this codebase for deployment (as uberjars) to production.

We have about 111,000 lines of Clojure: about 88,000 is source code and the rest,
23,000, is test code.

If you don't want to read all the back story, you can <a href="#the-third-way">jump
straight to the TL;DR</a> and see the solution we've settled on.

### Why do we use a monorepo?

Even though we _can_ (and sometimes _do_) build and deploy an application artifact
on its own, we tend to want to build and deploy all of our application artifacts
together, tied to a single git tag and/or SHA.

~~Although each of our subprojects has different dependencies, we generally want to
control the specific versions of many third-party library dependencies across the whole
codebase so that we don't have to worry about a transitive dependency in one
"library" (subproject) conflicting with a different version of the same dependency
in another "library". We find value in being able to update a version in one place
and have it apply to all of the subprojects.~~ _[2021-04-21: while this has been
convenient, we've decided that the benefits do not outweigh the inconveniences
caused by our use of the `:defaults` alias -- see below.]_

When working in a REPL, we like to have all of the source code and all of the
test code (and all of its dependencies) available in our editor, and can make
changes across multiple subprojects if we wish, as well as being able to run
any combination of tests from our editor.

~~An additional goal is to avoid, as much as possible, duplication of configuration
across subprojects -- in particular, in the context of this article, to avoid
duplication of dependencies and aliases across `deps.edn` files in these
subprojects.~~ _[2021-04-21: as with pinning versions above, we have decided to accept some duplication
as a trade-off for simple use of tooling -- see below.]_

### How does the Clojure CLI and `deps.edn` help us?

When we started building this codebase a decade ago, we used Leiningen because
that was the only game in town. For a long time, it worked well, but it started
to feel constraining as we wanted to automate more and more of our dev/test/build
processes. I've [talked about our switch from Leiningen to Boot](https://corfield.org/blog/2016/01/29/rebooting-clojure/) before, about five years ago.

Once we switched to Boot, we decided to put our dependencies in EDN files so
that we had more control over them and could manipulate them programmatically.
When the Clojure CLI first appeared (2017), we saw similarities between the
`deps.edn` approach and our own handling of dependencies and I started work
on a Boot task to read the new `deps.edn` format with a view to providing a
migration path from Boot to the Clojure CLI. I [compared Leiningen, Boot, and the Clojure CLI](https://corfield.org/blog/2018/04/18/all-the-paths/) about three years ago.
We migrated completely to the Clojure CLI and `deps.edn` some time in 2018.

We like the simplicity and performance of the Clojure CLI: it computes a cache
of dependencies and command line options only as needed and so it mostly runs
just a single JVM (unlike Leiningen) with just your project's code and dependencies.

We can build any tooling we want for our dev/test/build pipeline, using regular
Clojure code (much like Boot's approach, only without even the small "framework"
of Boot itself). This also allows us to mix'n'match tooling as we need, much
more smoothly than with either Leiningen or Boot.

We like that the Clojure CLI is official, supported tooling from Cognitect (Nubank),
and gets regular updates that are carefully considered in a holistic manner alongside
Clojure itself.

We like that you can choose to perform project-based CLI operations in a "reproducible"
way that excludes user-level configuration in `~/.clojure/deps.edn` but that
you can also perform operations with the full power of your personal customizations
if you want, giving our developers freedom to set up their environments however they wish.

We like that dependencies can be local -- via `:local/root` -- so that our subprojects
can very easily depend upon each other at a source code level, mirroring how we work
with all the source code in our editor already, across the whole monorepo.

~~We particularly like `:override-deps` so that we can "pin" versions of dependencies
across all of our subprojects, by including a single alias when invoking the
CLI.~~ _[2021-04-21: we did like it but it doesn't play all that well with a lot of CLI tooling.]_

### How did the Clojure CLI and `deps.edn` hinder us?

The CLI assumes there are three `deps.edn` files: the root one baked into
the CLI installation (actually, into the version of [`org.clojure/tools.deps.alpha`](https://github.com/clojure/tools.deps.alpha)
that underpins the CLI), the user-level one (usually in `~/.clojure/`), and the
project-level one.

#### Overriding the user-level `deps.edn` file

Our initial approach was to leverage the `CLJ_CONFIG` environment variable that
the CLI supports to select a different directory for the user-level `deps.edn`
file. This allowed us to have a single `deps.edn` containing all of the "control"
aliases we wanted, for pinning dependencies via `:override-deps`, for testing
tools, for building JAR files, etc, and then for each subproject to have its own
project-level `deps.edn` file. This worked well:

```bash
$ cd monorepo/subproject
$ CLJ_CONFIG=../versions clojure -M:defaults:other:aliases -m some.tooling
```

We wrapped this in a `build` shell script so we could run the following and it
would handle `cd` and adding the other bits of the command-line:

```bash
$ build other:aliases subproject -m some.tooling
```

We also had a pseudo-project called `everything` and a small Clojure script that
merged all the subproject `deps.edn` files into a single `deps.edn` file in the
`everything` subproject that we used as the basis for our REPL with "all code"
available. _[Since almost all dependency versions were specified via `:override-deps` in `versions/deps.edn`, this (generated) `everything/deps.edn` file only changed occasionally]_

But it had the downside that developers could not leverage their user-level `deps.edn`
because `CLJ_CONFIG` was overriding that with our `../versions/deps.edn`. That led
to that file slowly accruing an amalgam of any and all tooling that each team member
wanted, and it became unwanted incidental complexity in terms of maintenance.

#### Generating project-level `deps.edn` files

Eventually, we broke down and decided to figure out a way to restore access to the
user-level `deps.edn` file while still maintaining our "control" file. After talking
to a number of other Clojurians who were also using monorepos, it seemed a common
option was to programmatically generate the project-level `deps.edn` file, as needed,
from a repo-wide template (essentially our `versions/deps.edn` file) and a template
in each project. Since it's "just data" in EDN files, this is trivial to do in
Clojure -- and we were already doing a little of this for our `everything/deps.edn` file.
We added some code to our `build` script to compute hashes for the template EDN files
and to automatically generate subproject `deps.edn` files "on demand" and for a while
that worked well: we simplified `versions/deps.edn` to remove all the per-developer
cruft and we were happy that we could customize our developer experience as much as
we wanted, outside of the company repo!

This also made it easier to run the `clojure` command without our `build` script
since the `CLJ_CONFIG=../versions` prefix was no longer required (and sometimes
we'd been running it manually outside the `build` script even when we had to provide
that environment variable override).

However, `deps.edn` already has a subtle "gotcha" around local dependencies (`:local/root`),
and that is regarding what happens if a (transitive) dependency changes: if you're
working inside the subproject where that dependency changes, the CLI will see the
change and regenerate the cache and everything will "just work". If you're working
in a different subproject, that depends locally on the one that changed, that change
can't be detected "at a distance" and you need to remember `-Sforce` (or just blow
away the `.cpcache` directory the CLI maintains).

Once we started generating subproject `deps.edn` files "on demand", it amplified
that problem because using `clojure -Sforce` was no longer enough to pick up
transitive changes and our "on demand" code wasn't always smart enough to figure
it out either so running `build ... -Sforce` wasn't quite reliable enough.

The final straw for us was a recent change to the CLI in [version 1.10.2.790](https://clojure.org/releases/tools#v1.10.2.790)
where a "warning [is issued] if `:paths` or `:extra-paths` refers to a directory
outside the project root (**in the future will become an error**)" (emphasis mine).
Our `everything` project depended on the other subproject's source code via `:local/root`
but since that didn't include the tests, we used `:extra-paths` to provide all of
the (relative) subproject paths to the tests, e.g., `"../subproject/test"`, which
falls foul of this new warning. The warning is sensible and I can understand why
the Clojure team want to disable "random external paths" in projects -- but it
meant we needed to rethink our "everything" setup.

#### Not just us

It's probably worth mentioning at this point
that the [Polylith](https://polylith.gitbook.io/) team are also
looking at supporting the Clojure CLI / `deps.edn` and their architecture is
a type of monorepo -- and they are also struggling with effective ways to organize
`deps.edn` files and how to invoke them. Monorepos come in many forms!
_[2021-04-21: I've spent quite a bit of time looking at Polylith since I wrote this
post and that experience has influenced some of the changes we have made since then.]_ <a name="the-third-way">&nbsp;</a>

### The Third Way

At this point, I pressed Alex Miller pretty hard on how he might tackle
this problem if he were forced -- gun to his head -- to work on a monorepo
like ours?

After a bit of back and forth with Alex in a thread on Slack and several DMs later, the
path we agreed that I should explore was to create a top-level `deps.edn` file -- a variant of our
former `versions/deps.edn` file -- that had an alias for every subproject that
contained a `:local/root` dependency on the subproject itself.

Since we can't activate aliases on local dependencies' `deps.edn` files, I
also added a `:*-test` alias for every subproject into the top-level `deps.edn` file,
"lifting" the testing dependencies and paths up one level.

Finally, I added an `:everything` alias that had `:extra-deps` containing every
subproject and `:extra-paths` for all of the subprojects' test code.

All `clojure` commands are now run from that top-level directory, with our
`:defaults` alias (bringing in `:override-deps`), an alias for the subproject(s)
you want to operate on, maybe an alias for the tests for those subprojects,
and then any tooling alias(es) and arguments. We still have our `build` shell
script to make this a little less verbose (its "API" hasn't changed at all but
it uses the subproject name as an alias now instead of changing into that
subdirectory). _[2021-04-21: we've abandoned the `:defaults` alias and the use
of `:override-deps` and accepted a small amount of duplication of dependency
version declarations, in exchange for simpler tooling interactions.]_

In summary, here's the structure of our monorepo now:

```
<repo>
|____build # our shell scripts / config / etc
...
|____clojure # our Clojure code "root"
| |____activator # a subproject
| | |____deps.edn # bare dependencies: no versions, no test
. . .    [2021-04-21: src dependencies with versions, no test]
| | |____src
| | | |____ws
| | | | |____activator.clj
| | |____test
| | | |____ws
| | | | |____activator_expectations.clj
...
| |____classes
| | |____.keep
...
| |____deps.edn # control deps.edn file
...
| |____worldsingles # another subproject
| | |____deps.edn
| | |____resources
...
| | |____src
...
| | |____test
...
```

Then `clojure/activator/deps.edn` has:

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

And in `clojure/deps.edn` we have:

```clojure
  ;; for each subproject, we have two aliases:
  :activator {:extra-deps {worldsingles/activator {:local/root "activator"}}}
  :activator-test
  {:extra-paths ["activator/test"]
   :extra-deps {worldsingles/worldsingles-test {:local/root "worldsingles-test"}}}
```

That `clojure/deps.edn` also has tooling aliases, such as:

```clojure
  :test ; for a testing context
  {:extra-deps {com.gfredericks/test.chuck {:mvn/version "0.2.10"}
                expectations/clojure-test {:mvn/version "1.2.1"}
                org.clojure/test.check {}}
   :jvm-opts ["-Dclojure.core.async.go-checking=true"
              "-Dclojure.tools.logging.factory=clojure.tools.logging.impl/log4j2-factory"
              "-Dlogged-future=synchronous"
              "-XX:-OmitStackTraceInFastThrow"
              "--illegal-access=warn"]}

  :runner ; to run tests (test:<subproject>-test:runner) -- the <task> called test
  {:extra-deps {org.clojure/tools.namespace {}
                org.clojure/tools.reader {}
                com.cognitect/test-runner
                {:git/url "https://github.com/cognitect-labs/test-runner"
                 :sha "b6b3193fcc42659d7e46ecd1884a228993441182"}}
   :jvm-opts ["-Dlog4j2.configurationFile=log4j2-silent.properties"]
   :main-opts ["-m" "cognitect.test-runner"
               "-r" ".*[-\\.](expectations|test)(\\..*)?$"]}
```

So we would run:

```bash
$ clojure -M:defaults:activator:activator-test:test:runner -d activator/test
# or just
$ build test activator
```

All of the `{}` versions are specified in the `:defaults` alias via `:override-deps`
_[2021-04-21: this has gone away -- versions are specified directly in subprojects' `deps.edn` files now.]_:

```clojure
  ;; "pinned" versions for all cross-project dependencies
  :defaults
  {:override-deps
   {...
    camel-snake-kebab/camel-snake-kebab {:mvn/version "0.4.2"}
    clj-time/clj-time {:mvn/version "0.15.2"}
    clojure.java-time/clojure.java-time {:mvn/version "0.3.2"}
    ...
    org.clojure/tools.logging {:mvn/version "1.1.0"}
    org.clojure/tools.namespace {:mvn/version "1.0.0"}
    org.clojure/tools.reader {:mvn/version "1.3.3"}
    ...}}
```

When we build an uberjar:

```bash
$ clojure -X:uberjar :aliases '[:defaults :activator]' :jar '"test.jar"' :main-class ws.activator :aot true
```

Where the `:uberjar` alias is:

```clojure
  :uberjar
  {:replace-deps {seancorfield/depstar {:mvn/version "2.0.187"}}
   :exec-fn hf.depstar/uberjar}
```

And that `:everything` alias?

```clojure
  :everything
  {:extra-deps {worldsingles/activator {:local/root "activator"}
                ...
                worldsingles/worldsingles {:local/root "worldsingles"}
                ...}
                worldsingles/wsseogeo {:local/root "wsseogeo"}}
   :extra-paths ["activator/test"
                 ...
                 "worldsingles/test"
                 "worldsingles-test/test"
                 ...]}
```

Starting a REPL:

```clojure
$ clj -A:defaults:everything:test
Clojure 1.10.3-rc1
user=> (require 'ws.activator)
nil
user=>
```

_[2021-04-21: see [`deps.edn` and monorepos II](/blog/2021/04/21/deps-edn-monorepo-2/)
for more details about the changes made since this post was originally written.]_

### Got questions?

Find me on the [Clojurians Slack](https://clojurians.slack.com), or just ask in the comments
below.