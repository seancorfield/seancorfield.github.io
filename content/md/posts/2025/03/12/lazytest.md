{:title "Migrating to LazyTest",
 :date "2025-03-12 17:00:00",
 :tags ["clojure" "jdbc" "expectations"]}

I've been using the [Expectations](https://github.com/clojure-expectations/clojure-test)
testing library since early 2019 -- over six years. I love the expressiveness of
it, compared to `clojure.test`, and it exists because "Classic Expectations"
was not compatible with `clojure.test` tooling. At work, our tests use a
mixture of `clojure.test` and Expectations, but in my open source projects,
I've mostly stuck with `clojure.test` for familiarity's sake for contributors.

Being compatible with `clojure.test` comes at a price, though. Expectations
uses macros to produce `clojure.test`-compatible code under the hood, so it
is limited by the same reporting as `clojure.test` and the same potential
problems with tooling that tries to override parts of `clojure.test`'s
behavior -- namely that multiple tools do not play well together, so I've
had to avoid "improving" the expressiveness or reporting in ways that would
break compatibility with that tooling.

Those limitations have gradually become more frustrating, and I've been
watching Noah Bogart's work on [LazyTest](https://github.com/NoahTheDuke/lazytest)
with interest as he has resurrected and evolved
[S Sierra's old LazyTest project](https://github.com/stuartsierra/lazytest)
that had been archived back in 2017. At first, I was skeptical because it
is **not** compatible with `clojure.test` tooling: it has its own test runner
and that isn't supported by any of the editors, nor the various test runners
that exist for `clojure.test`
([Cognitect's](https://github.com/cognitect-labs/test-runner),
[Kaocha](https://github.com/lambdaisland/kaocha)), nor
[Polylith's](https://github.com/polyfy/polylith) incremental test runner.

We rely on the latter heavily at work, but we already use my
[external test runner](https://github.com/seancorfield/polylith-external-test-runner)
to avoid classloader and memory issues, so I figured I could adapt that to
also run LazyTest tests and experiment with it at work.

LazyTest provides its own DSL for writing tests -- `describe`, `it`, `expect` --
and it also has extension namespaces that provide a compatibility layer for
most of Expectations and also
[Nubank's matcher-combinators](https://github.com/nubank/matcher-combinators),
as well as experimental namespaces that provide a migration path for
`clojure.test` and parts of Midje, Qunit, and Xunit.

I migrated a few of our Expectations-based test namespaces over to LazyTest
and started using LazyTest's core DSL for new tests. I really like the
expressiveness of the DSL and the way it reports test results. I also like
the way it handles test fixtures -- as a "context" for tests that can be
applied before, after, or around each test or group of tests. LazyTest has
multiple reporting options, so you can pick different styles of output for
working in the REPL, for running tests locally, and for running tests in CI,
for example.

The next step was to see what it would take for Cognitect's test runner to
support LazyTest. As a proof of concept, I hacked up a version that followed
the pattern I'd used in my external test runner. It worked fine but the code
was ugly, and when I talked to [Alex Miller](https://github.com/puredanger)
about it, he quite rightly wanted to see a more extensible approach that
would allow others in the community to provide integrations for additional
test libraries and/or runners. There's a
[pull request](https://github.com/cognitect-labs/test-runner/pull/49)
awaiting review that adds a protocol for test runners and a default
implementation that supports `clojure.test`. I've also written a quick
[lazytest-runner](https://github.com/seancorfield/lazytest-runner)
implementation, so that `test-runner` can run either `clojure.test` tests
or LazyTest tests or both. If the PR gets merged, this implementation should
be added directly into LazyTest.

The next step for me was to migrate one of my open source projects to
LazyTest. Since LazyTest does not (currently) support ClojureScript, that
meant HoneySQL was off the table, so I picked
[`next.jdbc`](https://github.com/seancorfield/next-jdbc).

It's tests all used `clojure.test`, so I used the LazyTest experimental
compatibility namespace for the migration. You can look at the
[pull request for the migration](https://github.com/seancorfield/next-jdbc/pull/297)
for the gory details but here's the summary of how little I had to do:

* switch from `clojure -X:test` usage to `clojure -M:test:runner` usage
* switch Cognitect `test-runner` references to LazyTest's equivalent (deps, `-m` namespace for running tests)
* switch require of `[clojure.test :refer [deftest is testing]]` to `[lazytest.experimental.interfaces.clojure-test :refer [deftest is testing]]`
* where I used `clojure.test/use-fixtures :once`, I added a require for `[lazytest.core :refer [around set-ns-context!]]` and changed the `use-fixtures` call to `set-ns-context!`
* where I used `clojure.test/use-fixtures :each`, I added a require for `[lazytest.core :refer [around]]`, removed the `use-fixtures` call, and added an `around` context to each test

What's with the `-X` / `-M` change, you ask? I like that I can have a `:test`
alias in my `deps.edn` that includes the test runner and the test dependencies,
but uses `:exec-fn` to identify the function to run -- so that the same alias
can be used with `-M` as part of commands that run a different main function
(such as starting an nREPL server). If I had stuck with `-M` for Cognitect's
`test-runner`, I would have already had a separate `:runner` alias that
provided just the `:main-opts` for `-m` (and the main namespace to run).
LazyTest doesn't have a `-X`-compatible API, but it does expose functions to
run tests programmatically, so it doesn't really need to support `:exec-fn`.

Along the way, I did hit some bumps: version 1.5.0 of LazyTest did not
support Clojure 1.10 and I still needed to test `next.jdbc` against that.
LazyTest provides `throws?` (and `throws-with-msg?`) but it didn't support
`clojure.test`'s `thrown?` macro. `lazytest.core/throws?` expects an exception
type and a no-arg function to call, containing the code under test.
In `clojure.test`, `thrown?` is just "syntax" that `is` understands but it
takes an exception type and a form to evaluate, making the migration a bit
harder: `(is (thrown? Exception (some-expr :here)))` changed to
`(throws? Exception #(some-expr :here))`. Noah is very receptive to feedback
(and pull requests) so version 1.6.1 of LazyTest addresses both of those
concerns: it supports Clojure 1.10 and it provides a `thrown?` macro in the
experimental compatibility namespace (that maps the code to use `throws?`).

As I work on `next.jdbc` tests moving forward, I'll be able to mix'n'match
LazyTest's more expressive DSL for tests with the existing `clojure.test`
style of tests, and I'll probably migrate the latter to use the former over
time.

FYI: when I'm working with VS Code and Calva, I use a custom REPL snippet
to run tests in the REPL, in a way that supports both `clojure.test` and
LazyTest tests -- see this
[snippet](https://github.com/seancorfield/vscode-calva-setup/blob/e46f1ebd6984fb822ca7556362a0e51c99192d48/calva/config.edn#L118-L133) for details.
It runs `clojure.test/run-tests` first for the current namespace, then it
runs `lazytest.repl/run-tests` for that namespace, and merges the results
summary.
