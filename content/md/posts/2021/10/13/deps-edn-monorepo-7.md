{:title "deps.edn and monorepos VII (Polylith)",
 :date "2021-10-13 23:00:00",
 :tags ["clojure" "monorepo" "polylith" "tools.build" "vs code"]}

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
7. _[deps.edn and monorepos VII (Polylith)](https://corfield.org/blog/2021/10/13/deps-edn-monorepo-7/) (this post)_
8. _[deps.edn and monorepos VIII (Polylith)](https://corfield.org/blog/2021/11/28/deps-edn-monorepo-8/)_
9. _[deps.edn and monorepos IX (Polylith)](https://corfield.org/blog/2022/11/05/deps-edn-monorepo-9/)_
10. _[deps.edn and monorepos X (Polylith)](https://corfield.org/blog/2022/12/07/deps-edn-monorepo-10/)_

## Part VII

Another update, less than two weeks since the last one? Yes! Polylith has had
another alpha release with a very cool new feature added, we've completed our
first multi-implementation component in Polylith, and my development workflow
has had a major facelift with the latest releases from [Portal](https://github.com/djblue/portal)!

### Portal

Let's start with the development workflow. I've been using `tap>` in my development
workflow ever since it was added in the Clojure 1.10 prerelease cycle (Fall 2018 I think?).
When Cognitect's REBL came out, I loved what could be done with `datafy` and `nav`
and I also loved that it began to keep a `tap>` history so you could browse through
all of that data. I used REBL for a long time before switching to
[Reveal](https://github.com/vlaaad/reveal) and I've been enjoying using that on both macOS and Windows,
although having a WSLg-powered X window for Reveal is a much less pleasant experience
on Windows than the drop-snap behavior for fullscreen apps on macOS.

I'd looked at Portal fairly early on in its life and really didn't like that it
required a browser window for rendering but recently decided to take another look
because it was clear that Chris Badahdah ([@djblue](https://twitter.com/djblue_live))
had put in a lot of work since then. It turned out that Chris had been considering
a VS Code extension that encapsulated Portal into a webview panel and I got to test
an early `.vsix` file and, for me, that was a game changer! I no longer needed my
VS Code window to share space with either a JavaFX app or a browser: I could have
my data visualizer right there inside my editor and move it around like any
other tab, and have my editor be completely full screen on both macOS _and_ Windows!

My core workflow with `tap>` hasn't changed:
my [dot-clojure](https://github.com/seancorfield/dot-clojure)
repo still has a `dev.clj` script that starts a Socket REPL and Rebel Readline etc
and my [VS Code/Clover setup](https://github.com/seancorfield/vscode-clover-setup)
repo still has all the same key bindings and `config.cljs` file that sends all of
my evaluated code to `tap>`. But now everything happens inside VS Code. I've added
a key binding (`ctrl-; shift-p`) that runs a `Portal start` task, which uses the
REPL to launch a Portal window inside VS Code (see the
[Portal extension for VS Code](https://marketplace.visualstudio.com/items?itemName=djblue.portal))
and adds a few extra custom commands to it (via a nice custom predicate/function API!).

Best of all, my workflow on Windows and macOS is now "identical" (modulo a few
VS Code key binding differences between platforms)!

### Polylith & Swappable Implementations

I talked about our plan to implement an `http-client` component in part 6 and
that work has been completed and merged in (and is currently in QA). Overall,
it was fairly straightforward to leverage
[Polylith's "profiles"](https://polylith.gitbook.io/poly/workflow/profile)
and have two implementations of the same "interface" but integrating it into
our hybrid Polylith/legacy repo did have a couple of head-scratching moments.

We have `http-client-hato` and `http-client-httpkit`, both implementing the
`http-client` interface. We've added `:+default` and `:+httpkit` aliases to
our main `deps.edn` file -- following the Polylith profile naming convention --
so we are using the [Hato](https://github.com/gnarroway/hato) implementation
by default for development/testing and for nearly all of our Polylith `projects`.
Our legacy apps use the [httpkit](https://http-kit.github.io/client.html)
implementation. It's just a matter of which `:local/root` dependency you put
in your `deps.edn` file, so that's pretty slick.

You can run tests against either implementation:

* `poly test :dev` uses the default, Hato-based, profile (the `:+default` alias),
* `poly test :dev +httpkit` uses the httpkit-based profile (the `:+httpkit` alias).

Project-based tests use whichever `:local/root` dependency is declared in their
`deps.edn` -- we decided to keep one of them using the `httpkit` implementation
for now, just as a sanity check within the Polylith world.

Our hybrid repo also has `:everything` and `:runner` aliases which mirror
Polylith's `:dev` and `:test` aliases and we have added the Hato-based component
to the `:runner` alias so that our legacy test suite also uses Hato (and the
JDK11+ `HttpClient`).

What was head-scratching? Initially, I forgot to also add the Hato-based
component's `test` path to the `:runner` alias and just got a mysterious
`Namespace cannot be loaded` error from the Clojure CLI claiming that our
wrapper namespace for Cognitect's `test-runner` could not be found. Since the
"exec" functionality doesn't expose the underlying exception, that took a bit
of debugging.

In addition, Polylith currently doesn't support `:local/root` components
in profiles, only `:extra-paths` (see [issue #146](https://github.com/polyfy/polylith/issues/146)),
so getting the profiles working was not as straightforward as I had expected.

And, finally, actually developing the alternate implementations was a bit
frustrating since I was trying to develop them side-by-side, rather than
creating a new implementation against an existing interface so I was
evaluating both components into the same REPL -- and they both had the
same namespaces (`ws.http-client.interface` and `ws.http-client.impl`).
After a while, I got into a good rhythm but it took a few attempts before
I settled into "eval `.impl`, eval `.interface`, eval other code and test,
switch to the other component and repeat". I learned a lot about both
httpkit and Java's `HttpClient` while I was doing this, especially around
how they handle async operations, thread pools, executors, and also how
they handle different SSL setups!

Overall, I would consider this a big win for Polylith: our shared code
is written against a functional interface, we can easily run all the
tests against either implementation with just a command-line flag, and
our projects can decide which implementation to use at build time!

### Polylith 0.2.13-alpha

And finally, on to the first item I mentioned: Polylith's new alpha release.

The main new feature is a very nice, interactive `shell` command that
provides auto-complete and history so you can just run `poly` and then
keep that open and work with Polylith without any startup overhead.

You can type `i<TAB>l<TAB>` and get the `info :loc` command. It also knows
how to offer auto-complete for your bricks and projects so you save a lot
of typing, and can easily scroll back and forth through your history
with the arrow keys.

One of the first things I did when I started working with the `poly` tool
was to write a primitive interactive wrapper so I didn't have to keep
running the entire process from scratch every time so I'm **very** pleased
to see this slick interactive mode in the core tool!

The second "big feature" is that the monolithic README for the tool
has been moved to the [`poly` GitBook](https://polylith.gitbook.io/poly)
so it is much easier to navigate and much more pleasant to read. I think
this will be a big help for newcomers to Polylith: there's a huge amount
of documentation and a lot to learn before you can really become effective
with Polylith and its command-line tool and this is so much more digestible.

The final "big" change in this new release is that the project has switched
from a custom build/deploy pipeline, based on a mixture of `depstar` and Polylith's
own `deployer-cli` base and `deployer` component to using `tools.build`,
via my [`build-clj`](https://github.com/seancorfield/build-clj) wrapper library.
This helped iron out a couple of wrinkles in how monorepo-based artifacts
get built so `build-clj` now has a `:transitive` option for working with
`:local/root` components, such as Polylith relies heavily on.

This has also allowed for all [build documentation](https://polylith.gitbook.io/poly/workflow/build)
to recommend using `tools.build` to create artifacts from Polylith repos,
instead of relying on `depstar`, which makes me happy.

See the [0.2.13-alpha release notes](https://github.com/polyfy/polylith/releases/tag/v0.2.13-alpha)
for all the other changes in this release.

If you have a monorepo, or you are contemplating switching to a monorepo,
consider Polylith very seriously.
