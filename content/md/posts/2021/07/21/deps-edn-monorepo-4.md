{:title "deps.edn and monorepos IV",
 :date "2021-07-24 00:25:00",
 :tags ["clojure" "monorepo" "polylith"]}

This is part of an ongoing series of blog posts about our ever-evolving use of the Clojure CLI,
`deps.edn`, and [Polylith](https://polylith.gitbook.io/), with our monorepo at
[World Singles Networks](https://worldsinglesnetworks.com).<!--more-->

Since [last month's post](/blog/2021/06/06/deps-edn-monorepo-3/), we've created Polylith `projects`
for each of our deployable artifacts, as well as refactoring all of our cron job tasks into a new
base called `batch-jobs`. We now have 16 `projects`, 3 `bases`, and 23 `components`. This has
nicely separated the actual artifacts we build from the code we use to build them, providing a
clear "bill of materials" for each artifact (as a `deps.edn` file) and now we can build each artifact with:

```bash
cd projects/<artifact>
clojure -X:uberjar
```

We still have the ability to run a single REPL with all our code and tests, and we also
have the ability to run incremental tests on `bases`, `components`, and `projects` via
the `poly` tool. We're looking forward to having incremental testing across the whole
codebase, once we have refactored everything -- but that is definitely a ways off.

## A Clojure Build Script

The other big change we've made in the last month is something we've been talking about
internally, on and off, for a long time: switching away from an ad hoc set of bash scripts
that manage our test/build/deploy processes and instead having a single Clojure script
that orchestrates all of that. We've had Clojure scripts for many parts of that pipeline
for a long time but we've never made the jump to running those scripts from Clojure itself.
With the knowledge that [`tools.build`](https://github.com/clojure/tools.build) was on the
way, this seems like a good time to do that last leg of the work.

We've run our database migrations via Clojure for years. Our "cold start" to stand up a
dev/test environment involves two sets of database migrations, an Elastic Search setup
(for indices), and a "publishing" step to take dev/test data from MySQL and propagate
the appropriate pieces into Elastic Search. We've run our test suite in part and in whole
using [Cognitect's `test-runner`](https://github.com/cognitect-labs/test-runner) for a
long time as well and we were very pleased to see an "exec function" entry point added
recently -- we've had our own "exec function" entry point for a while that performs
some test setup before invoking the `test-runner` so this just made our lives a bit easier.

We've been building (uber) JAR files with [`depstar`](https://github.com/seancorfield/depstar)
for quite some time too, and our deployment process to our staging server was also written
in Clojure, uploading JAR files to S3 buckets and then notifying our auto-deploy (bash) scripts
by posting "flag" files to the servers that need to retrieve those JAR files and deploy them.

We replaced two bash scripts with a single Clojure `build.clj` script that can run database
migrations, run tests, build uberjars, upload JAR files, and notifying the staging server.
Our first run at this ended up with `tools.deps.alpha` and all our code on the classpath,
at least for test running, which caused some interesting race conditions, due to the optional
S3 transporter code in `tools.deps.alpha` performing asynchronous requires of some libraries
that we also use in our tests (including `core.async`). We knew this wasn't ideal -- the core
team think that running tests should be separate from all the rest and that it's OK to run
multiple commands but I wanted things a bit more integrated and was willing to pay the price:
as a workaround, our build script required the t.d.a S3 transporter namespace to force it to
load upfront, even though we don't use any of that functionality.

## `tools.build`

And then [`tools.build` was released](https://clojure.org/news/2021/07/09/source-libs-builds) on
July 9th, along with enhancements to the Clojure CLI and `tools.deps.alpha`, and the ability to
install "tools" locally, per-developer, without needing to edit your `deps.edn` file.

> Aside: At this point, I updated both [`depstar`](https://github.com/seancorfield/depstar) and [`clj-new`](https://github.com/seancorfield/clj-new) to work with the new "tools" functionality, adding `:tools/usage` to their `deps.edn` files and updating the documentation to show how to install them under this new system. I also broke `depstar` up into tasks so that I could expose `hf.depstar.api/jar` and `hf.depstar.api/uber` as direct, drop-in replacements for `clojure.tools.build.api/jar` and `clojure.tools.build.api/uber` -- because `depstar` still has a lot of functionality and options that `tools.build` does not yet have.

`tools.build` provided features that allowed us to simplify our `build.clj` script and also offered
the possibility of running our tests in an isolated environment, without the pollution of `tools.deps.alpha`'s
many dependencies: using `create-basis`, `java-command`, and `process` from
[`clojure.tools.build.api`](https://clojure.github.io/tools.build/clojure.tools.build.api.html) makes it
fairly easy to run Clojure `-main` programs with command-line arguments.

Unfortunately, we relied heavily on `-X` execution of many of our previously scripted test/build/deploy
functions and we didn't have `-main` programs for most of those. What we really needed was a variant of
`java-command` that built a command-line for running "exec functions". The way this is done in the Clojure
CLI is via the `exec.jar` that is part of its install and a `clojure.run.exec` namespace, which has a
`-main` that reads the command-line arguments as EDN and then invokes the specified function with a
hash map of those arguments, including `:exec-fn`, `:exec-args`, etc from the project basis under the
specified aliases.

This wasn't too hard to write, but it involved taking a snapshot of `clojure.run.exec` under a new name
since it isn't yet a stable API and isn't published as a library. You can see the approach we took in
[TBUILD-6](https://clojure.atlassian.net/browse/TBUILD-6) which allows us to easily run `-X` executions
in subprocesses. Unfortunately, `clojure.run.exec` in `exec.jar` has already changed in ways that would
break our workaround (hence the snapshot under a different name). It seems a shame that with all the
work done to support "exec functions" as command-line entry points, `tools.build` doesn't support that
out of the box. We are happy with our workaround though.

At this point, we had a `build.clj` and a `:build` alias and our entire CI pipeline came down to just:

```bash
clojure -T:build all-tests-ci
clojure -T:build tag-build-and-upload-all
```

The former is an exec function that runs our "cold start" (described above) and then runs the tests for all our
subprojects, both in subprocesses, and the latter uses `process` to run some `git` commands, followed by calls
to `depstar` as a library, and then calls to our artifact uploader. Our `:build` alias looks like this:

```clojure
  :build
  {:paths ["."] ; required to allow -M legacy invocation
   :deps {org.clojure/clojure {:mvn/version "1.11.0-alpha1"}

          io.github.clojure/tools.build {:git/tag "v0.1.6" :git/sha "5636e61"
                                         :exclusions [org.slf4j/slf4j-nop]}
          ;; add depstar for building uberjars:
          com.github.seancorfield/depstar {:git/tag "v2.1.267" :git/sha "1a45f79"
                                           :exclusions [org.slf4j/slf4j-nop]}
          ;; and local build tools:
          worldsingles/build {:local/root "build"}
          poly/base-artifact-uploader-cli {:local/root "bases/artifact-uploader-cli"}
          poly/artifact-uploader {:local/root "components/artifact-uploader"}}
   :ns-default build}
```

As the comment indicates, the `:paths` entry is there so that we can still invoke `build.clj` via `-M` which we
do to support what's left of our legacy `build` bash script until we retire it (which will probably happen next week).
The local `build` subproject includes some legacy app support tasks that we run from our new `build.clj` script
(which will soon be refactored to Polylith `bases` and `components`). The `artifact-uploader` is what I mentioned
above, uploading JAR files to S3.

## Footnote: `depstar` and `clj-new`

Already since `tools.build` was released, it has had several enhancement releases that add features `depstar` has had for a while and,
talking to Alex Miller, it's likely that most (or perhaps even all) of `depstar`'s functionality will be implemented in
the built-in `jar` and `uber` tasks over time -- which makes me very happy since I will be able to direct my open source
maintenance energy into other projects!

I've also observed to a few people that most of what `clj-new` does for internal templates (`app`, `lib`, and `template`)
could easily be done via `clojure.tools.build.api/copy-dir` since it supports text substitutions. With `create-basis`,
`java-command`, and `process`, it looks possible to reproduce most of `clj-new`'s external template functionality as a
very thin wrapper around `tools.build` as well. My thoughts on this are less well-formed than around `depstar` but my
hope is that both of my libraries/tools will essentially either go away or be substantially simplified which in turn
should reduce the sense of fragmentation of tools around the CLI and `deps.edn` and encourage more people to standardize
on the tooling that the core team are carefully producing.
