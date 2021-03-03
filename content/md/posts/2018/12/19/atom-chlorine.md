{:title "Chlorine: Clojure integration for Atom",
 :date "2018-12-19 22:45",
 :tags ["clojure"]}
I've been using the [Atom](https://atom.io) editor for about two years now.
I switched from Emacs after Clojure/conj 2016, having seen
[Jason Gilman's talk about ProtoREPL](https://youtu.be/buPPGxOnBnk) [video].
It may sound like
heresy, but I'd never been happy with Emacs<!-- more --> -- not 17.x back when I first
started using it, not 18.x, not 19.x when I first stopped using it, nor 24.x onward
when I came back to it after learning Clojure a few decades later. I built
several configurations from scratch, I tried several "curated" configurations,
none of them felt like "home". Emacs just leeched too much of my development
time for my tastes. I wanted a simple, modern text editor, that offered a wide
variety of "plugins" and supported all the languages I used, with a sane set of
defaults. Atom -- with ProtoREPL -- seemed to be exactly what I was looking for!

And for two years, it has been my day-to-day development environment.

But it has been frustrating that ProtoREPL hasn't been getting a huge amount of
love lately. Two releases immediately after that Conj, eleven in the first
half of 2017, and just three since then (in a year and a half).
With the advent of Clojure 1.10 and
[REBL](https://github.com/cognitect-labs/REBL-distro), I wanted to extend
ProtoREPL so I could integrate REBL into my workflow, so I forked it and
hacked in `inspect-block` and `inspect-top-block` functionality (see the
[commit log](https://github.com/seancorfield/proto-repl/compare/4b13ebf9d0f1e228ab7800c4bc54fe071bacf29e...master)). But it's written in
CoffeeScript and that made me sad.

I've also had a bit of a long-running love/hate relationship with
[nREPL](https://github.com/nrepl/nrepl) over its lifetime. Something about the
wire protocol and overall architecture of it just bothered me and I longed for
tooling built on what Clojure provides out-of-the-box. When I saw `prepl` drop
in the Clojure 1.10 development cycle, I was very excited: built-in support for
future tooling, on top of the Socket REPL we've had for a few releases!

I'd previously used [Unravel](https://github.com/Unrepl/unravel) to interact
with several of our processes at work that spawn a Socket REPL and found that a
very pleasant experience (side-loading [Compliment](https://github.com/Unrepl/unravel)
to provide auto-completion while typing).

> Unravel is a simple command-line client for Clojure REPLs. It is based on the unrepl protocol, so instead of relying on nREPL, unravel communicates with your Clojure process through a Socket Server REPL.

"instead of relying on nREPL" was music to my ears -- I wanted editor integration
that worked this way!

A few days ago, in a side-thread in the `#off-topic` channel on the
[Clojurians Slack](https://clojurians.slack.com) ([sign up here](https://join.slack.com/t/clojurians/shared_invite/zt-lsr4rn2f-jealnYXLHVZ61V2vdi15QQ/)),
that had started with a question about [Cursive](https://cursive-ide.com/) vs
Emacs/CIDER, Maurício Szabo mentioned "a (very experimental) package for Atom
that ... uses socket repl". I was intrigued. Could this be what I'd been
looking for?

That package is [Chlorine for Atom](https://atom.io/packages/chlorine).
I installed it immediately (and disabled ProtoREPL) so I could take it for a
spin! It's written in ClojureScript. It only needs a Socket REPL. It uses
unrepl to "upgrade" the REPL as needed. It uses Compliment for auto-completion.
I could connect it directly to various running processes and I could start
REBL up from the command-line with a Socket REPL and connect to that as well.
**This** was exactly the workflow I'd been looking for!

Maurício has been awesome to work with and over the last few days he's added
the first version of "go to definition" as well as merging pull requests from
me to add support for running tests in a namespace or an individual test. I
have pull requests in for adding "load file" (it already had evaluate selection,
so you could "select all + evaluate selection" but I was used to "load file"
from ProtoREPL), and "show source".

I've also [ported across my "inspect" functions](https://github.com/seancorfield/atom-chlorine/commit/b5c4ec4fbe2572a97882e595f6280fec15592114) from ProtoREPL so my entire,
day-to-day workflow with ProtoREPL is now possible with Chlorine, REBL, `clj`, and
a couple of aliases -- and I have an eminently hackable editor plugin, written
in ClojureScript with a slick auto-compile, auto-reload workflow (thank you
Maurício!).

I'm still looking forward to tooling based on `prepl` so the client libraries
can be even simpler and smaller but, for now, I'm a very happy developer and
I'm very pleased to see how the Clojure community continues to innovate around
tooling (which, yes, has needed a lot of love over the years!).
