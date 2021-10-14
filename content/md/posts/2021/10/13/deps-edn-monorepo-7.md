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

## Part VII

Another update, less than two weeks since the last one? Yes! Polylith has had
another alpha release with a very cool new feature added, we've completed our
first multi-implementation component in Polylith, and my development workflow
has had a major facelift with the latest releases from [Portal](https://github.com/djblue/portal)!

### Portal

Let's start with the development workflow. I've been using `tap>` in my development
workflow ever since it was added in the Clojure 1.10 prerelease cycle (Fall 2018 I think?). When Cognitect's REBL came out, I loved what could be done with `datafy` and `nav`
and I also loved that it began to keep a `tap>` history so you could browse through
all of that data. I used REBL for a long time before switching to [Reveal](https://github.com/vlaaad/reveal) and I've been enjoying using that on both macOS and Windows,
although having a WSLg-powered X window for Reveal is a much less pleasant experience
on Windows than the drop-snap behavior for fullscreen apps on macOS.

I'd looked at Portal fairly early on in its life and really didn't like that it
required a browser window for rendering but recently decided to take another look
because it was clear that Chris Badahdah ([@djblue](https://twitter.com/djblue_live))
had put in a lot of work since then. It turned out that Chris had been considering
a VS Code extension that encapsulated Portal into a webview panel and I got to test
an early `.vsix` file and, for me, that was a game changer! I no longer needed my
VS Code window to share space with either a JavaFX app or a browser: I could have
my data visualizer right there inside my editor, and have my editor be completely
full screen on both macOS _and_ Windows!

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

http-client

Polylith / shell / tools.build

VS Code / Portal extension
