{:title "deps.edn and monorepos XI (Polylith)",
 :date "2023-07-14 11:00:00",
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
5. _[deps.edn and monorepos V (Polylith)](https://corfield.org/blog/2021/08/25/deps-edn-monorepo-5/)_
6. _[deps.edn and monorepos VI (Polylith)](https://corfield.org/blog/2021/10/01/deps-edn-monorepo-6/)_
7. _[deps.edn and monorepos VII (Polylith)](https://corfield.org/blog/2021/10/13/deps-edn-monorepo-7/)_
8. _[deps.edn and monorepos VIII (Polylith)](https://corfield.org/blog/2021/11/28/deps-edn-monorepo-8/)_
9. _[deps.edn and monorepos IX (Polylith)](https://corfield.org/blog/2022/11/05/deps-edn-monorepo-9/)_
10. _[deps.edn and monorepos X (Polylith)](https://corfield.org/blog/2022/12/07/deps-edn-monorepo-10/)_
11. _[deps.edn and monorepos XI (Polylith)](https://corfield.org/blog/2023/07/15/deps-edn-monorepo-11/) (this post)_

## Part XI

In my last post -- about eight months ago -- I said we were about 86% through
our migration with 114,114 lines of code converted to Polylith. We completed
it a while back but I've been lax about writing the "final" (maybe) chapter
of this series.

We completed the migration a few weeks after I posted my last update, moving the
last two large applications into `bases` and renaming all the namespaces,
then migrated the shared subproject to a `component`. Initially, we kept all
the shared code in the `interface`, but then we made several refactoring
passes over this code to split it into smaller components, as well as breaking the
code into `interface` and `impl` namespaces. This allowed us to simplify our
`build.clj` file since everything was now part of the Polylith structure:
no special "billing artifacts" to deal with, and we also streamlined our CI
pipelines since we no longer had to test and build the billing applications
separately.

We currently have 138,497 lines of Clojure code in Polylith now. We build 21
projects from 21 bases and 144 components. We have 1,160 Clojure files
(source and test).

Probably our biggest remaining challenge at this point is that we still have
some `components` that "do too much" and end up being depended on by too many
other `components`. When those change, they trigger a lot of repetitive
testing and building -- which is indicative of too much coupling. We've started
teasing some of those apart, often along Command/Query lines (a la CQRS), but
it's a slow process. It emphasizes the importance of `components` doing just
one thing and being as self-contained as possible!

## Polylith

Polylith itself has had a number of enhancements over the past seven months:

* It reports unnecessary component dependencies (previously, it only reported missing dependencies).
* It checks and reports missing and unnecessary component `:test` dependencies separately.
* It checks and reports missing and unnecessary base dependencies separately.
* It uses [edamame](https://github.com/borkdude/edamame) so its parsing is more robust and it now supports reader conditionals.
* It can show outdated library dependencies (leveraging [antq](https://github.com/liquidz/antq)).
* The parsing and dependency analysis have been dramatically speeded up -- which makes a big difference on a project our size!

I'm currently working with the Polylith team on moving the project and all the
documentation away from my `tools.build` wrapper (`build-clj`) so that it uses
a more standard approach to the core Clojure tooling.

[Cursive 1.13.0](https://groups.google.com/g/cursive/c/9dTn12AkHzA/m/R_dj2fzRBAAJ)
introduces a lot of changes to `deps.edn` support that make it much easier to
work with Polylith projects. In particular, the workaround of using `:extra-paths`
for component dependencies in `:dev` is no longer necessary! `:extra-deps` can
now be used with `:local/root` dependencies for all editors and tools.
