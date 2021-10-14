{:title "deps.edn and monorepos III (Polylith)",
 :date "2021-06-06 10:30:00",
 :tags ["clojure" "monorepo" "polylith"]}

Back in April, I talked about us dipping into Polylith at work in [`deps.edn` and monorepos II](/blog/2021/04/21/deps-edn-monorepo-2/),
and also our [planned migration away from `clj-http`](/blog/2021/03/25/little-things/). Since then, we've completed the migration to
`http-kit` and we've also migrated away from `clj-time` (which is deprecated, because it is based on Joda Time). We've also started
refactoring our subprojects into Polylith components. This is another periodic update on where we are in our journey.<!--more-->

### The Monorepo/Polylith Series

_This blog post is part of an ongoing series following our experiences with our Clojure monorepo and our migration to Polylith:_

1. _[deps.edn and monorepos](https://corfield.org/blog/2021/02/23/deps-edn-monorepo/)_
2. _[deps.edn and monorepos II](https://corfield.org/blog/2021/04/21/deps-edn-monorepo-2/)_
3. _[deps.edn and monorepos III (Polylith)](https://corfield.org/blog/2021/06/06/deps-edn-monorepo-3/) (this post)_
4. _[deps.edn and monorepos IV](https://corfield.org/blog/2021/07/21/deps-edn-monorepo-4/)_
5. _[deps.edn and monorepos V (Polylith)](https://corfield.org/blog/2021/08/25/deps-edn-monorepo-5/)_
6. _[deps.edn and monorepos VI (Polylith)](https://corfield.org/blog/2021/10/01/deps-edn-monorepo-6/)_
7. _[deps.edn and monorepos VII (Polylith)](https://corfield.org/blog/2021/10/13/deps-edn-monorepo-7/)_

## Part III

While I've worked on a few new features for our online dating platform over the last month and a half,
I've been fortunate enough to mostly work on eliminating "technical debt". I say "fortunate" because
many companies do not formally recognize technical debt and do not value working on it. By contrast,
at [World Singles Networks](https://worldsinglesnetworks.com), we are encouraged to create Jira tickets
identifying any technical debt that we deliberately
(or incidentally) take on and we are also encouraged to work on those tickets as part of our
overall workload.

Part of that technical debt work for me, recently, was completing our migration from `clj-http` to `http-kit`
and also migrating from `clj-time` to Java Time and starting to migrate away from `date-clj`
(also to Java Time). Previously, it involved migrating from `Cheshire` to `data.json`.

In addition, I've been able to spend time
reviewing some of our oldest code and refactoring it -- and finding out that
some of it is no longer used and removing it! I have deleted several thousand lines of code over the last
six weeks, both legacy non-Clojure code as well as some "legacy" Clojure code.
Our Clojure codebase stretches back over a decade now
and many of our early coding decisions were made while we were still learning Clojure and still
figuring out reasonable approaches to architecture.

Another part of that technical debt work has been to start picking apart some of our older subprojects
and figuring our better names for groups of functionality as Polylith `components`. Our first foray
into Polylith had produced four `components`, one item in `bases` and one in `projects`. We now have
sixteen `components` so I feel like I have a better handle on how Polylith is going to serve us
going forward.

Per the [high-level Polylith documentation](https://polylith.gitbook.io/), it is an architecture
that focuses on simple, composable components ("bricks"). Refactoring our codebase to follow the Polylith
architecture has produced the following benefits so far:
* Naming,
* Modularization,
* Focusing on dependencies.

## Naming

Polylith has made us think about naming more than we have before.
Previously, as our monorepo has grown, code has been
"organized" into very coarse-grained subprojects that either have fairly generic names
(`lowlevel`, `datamapper`) or that represent entire applications (services) we build.
Accordingly, these have accrued a lot of functionality over the years and some of them --
especially the more generic ones -- have become somewhat random "grab bags" of functionality.

Because Polylith favors much smaller components -- because it makes you think about individual
units of reuse (a.k.a `components`) -- I've broken apart several of our generic subprojects
and we've had discussions about meaningful names for the reusable pieces. So far, we have
sixteen components that have mostly been extracted from three legacy subprojects.

Several of these new components have come from single namespaces within our old subprojects
or even sometimes just a handful of functions from a single namespace. Extracting them out
into standalone components has made us think more carefully about the names we should give
each of them: sometimes their original namespace was reasonable but, with hindsight, several
of them needed better names, especially where we've extracted just a subset of a single
namespace. And by giving them better names -- often names we have now discussed as a team --
we are more likely to think before adding new functions: do they really belong in this
well-named component or should they go in their own component?

This should make discoverability of functionality easier which in turn should reduce
reimplementation of functionality (compared to figuring out when a function might already
exist in the codebase in some more randomly-named location).

## Modularization

Following on from the deeper consideration of naming, Polylith components encourage you to
separate out concerns. Even aside from Polylith's separation of `projects` (what you build and how),
from `bases` (how you expose functionality -- APIs, command-lines, web apps), from `components`
(the basic functionality -- domain logic etc -- of your systems), just the focus on small, well-named components
means that you also tend to think more about improving the modularization of your code.

The mindfulness associated with identifying and constructing those components means
that you think about narrowing your focus and teasing apart the code, decomplecting and
simplifying what each "piece" does and separating it from its neighbors.

The very fact that three of our subprojects have been refactored to sixteen components
speaks to the increased focus on modularization. We're very happy with that so far.

## Focusing on Dependencies

Polylith components only specify the external libraries they depend on: they deliberately
do not specify which other `components` they depend on. The reason for this is that `projects`
assemble the "bill of materials" that goes into a finished, deployable artifact. Multiple
`components` can implement the same (functional) interface so you can mix'n'match them,
as needed, when you build an artifact.

In addition, the `poly` tool that supports the Polylith architecture can check that components
only call into each others' `interface` namespace and don't try to access the implementation
of any components.

This separation means that you also think carefully about any 3rd party library dependency you
add to a component -- as well as considering, should you find yourself adding the same library
to multiple components, whether a component should depend directly on that library or on another
component that already uses that library. You tend to ask "Is this component wrapping
the library? Or it is just using it an implementation detail? Is there useful commonality
between the components that use this same library -- is there an abstraction I'm missing?".

When code is less modular, adding more external dependencies carries less semantic "weight":
one more dependency "doesn't matter". With smaller pieces that have better names and are
more modular, each new dependency becomes an important consideration.

## Beyond Components

The increased focus on naming, modularization, and dependencies that Polylith has
encouraged has spread beyond just "components": this new-found critical eye is being
applied to our dev/test/build infrastructure as well.
