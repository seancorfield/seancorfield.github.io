{:title "A look at the Clojure CLI REPL",
 :date "2026-10-07 10:30:00",
 :tags ["clojure" "open source"]}

Just before this year's Clojure/conj, the Clojure team released a new
[Clojure CLI REPL](https://github.com/clojure/clojure-cli.repl). Per the
README:

> A REPL for the Clojure CLI featuring multi-line editing with proper indentation, bracket highlighting, structural editing, inline eval, doc lookup for Clojure and Java, a data inspector, configurable prompts, keybindings, and much more.

The repo includes an [example configuration](https://github.com/clojure/clojure-cli.repl/tree/main/examples/.cljconf)
and I think it is the first use case for the recently created
[tools.deps.config](https://github.com/clojure/tools.deps.config) which is
intended to provide a standardized way for Clojure tooling to manage its
configuration, on a per-project or user-level basis. Per the README:

> Clojure tools need a well-known place to store user and project configuration. This library defines that place and provides functions for reading the files stored there.

The idea is that each tool's configuration lives at `<location>/.cljconf/<lib-ns>/<lib-name>.edn` 
(and is expected to contain a 1-level map with keyword keys). The CLI REPL's
configuration therefore lives at `.cljconf/org.clojure/clojure-cli.repl/clojure-cli.repl.edn`
and alongside that there is a data directory at `.cljconf/org.clojure/clojure-cli.repl/`
which, in this case, contains a small Clojure project containing code that
supports the customization of the CLI REPL.<!-- more -->

That `.cljconf` directory can be in your user-level Clojure configuration
(for me, that's `$HOME/.config/clojure` following XDG conventions, but for
many people that would be `$HOME/.clojure/.cljconf`), or it can be in your
project directory, alongside other configuration directories.

With all that preamble out of the way, I'm going to walk through how I have
the new CLI REPL set up for my development workflow.

### My `dot-clojure` Repository

Back in March, 2018, I decided to keep my Clojure configuration on GitHub in
case anyone found it useful. Back then, it was just my `deps.edn` file
containing the aliases I found useful. In December, 2020, I added a `dev.clj`
file which was my first "adaptive REPL startup code" -- see the
[initial version of it here](https://github.com/seancorfield/dot-clojure/commit/a945f8a2e68eb4c88b52aaf6edd0b1c9e3700007).

The idea was that you could use a basic alias to "start a REPL" and,
depending on what else was on your classpath, the REPL would include that
additional tooling. Back then, I was using Cognitect's REBL data browser,
now [Nubank'sMorse](https://github.com/nubank/morse), and
[Reveal](https://github.com/vlaaad/reveal), as well as
[Rebel Readline](https://github.com/bhauman/rebel-readline). Sometimes, I
would just fire up a bare REPL, sometimes I would use Rebel Readline's REPL,
and sometimes I would use one of the visualization tools.

Over the intervening five and a half years, my setup evolved a lot, as new
tools and libraries appeared, and I changed my workflow to adopt them. By
May of this year (2026), I was using this, 
[much more sophisticated REPL setup](https://github.com/seancorfield/dot-clojure/tree/v1.4.2),
which uses Portal (instead of REBL or Reveal), CIDER/nREPL, and my
[`rephrase`](https://github.com/seancorfield/rephrase) library to produce
more readable error messages. I was still using Rebel Readline.

### Enter `clojure-cli.repl`

The new Clojure CLI REPL is based on nREPL and aims to provide a similar
experience to Rebel Readline (it isn't quite there yet but I expect it will
surpass it fairly soon), as well as providing an integrated data inspector
built into the REPL (based on `datafy`/`nav`).

In addition, it is designed to be extremely configurable, with easily
customizable keybindings, including Paredit-style structural editing.

You can browse the [latest version of my REPL setup](https://github.com/seancorfield/dot-clojure)
but I'm going to dive into my specific configuration and customization of it.

### My `clojure-cli.repl.edn` Configuration

I'll start with my configuration file, and then talk about the code I wrote
to customize the behavior further.

```
{:auto-require [[clojure.repl.deps :refer [add-lib]]
                [dev.uptime :refer [uptime]]]
```

The `:auto-require` setting lets you specify namespaces (and symbols) that 
should be required (and referred) into every namespace you use via the REPL.
I like to be able to invoke `add-lib` to add new dependencies, and I didn't
want to have to require/refer it manually as I work. The `uptime` function
is a little utility that tells me how long my REPL has been running, in a
friendly format. I'm quite proud of how long many of my REPL sessions
actually live!

```                
 :bracket-pairs true
```

I'm a bit surprised that the default for auto-close `(`, `[`, and `{` isn't 
enabled by default, but I guess this is subjective.

```
 :doc-at-cursor "M-d"
 :eval-form-at-cursor "M-e"
```

Unchanged from the default in the `examples` provided with `clojure-cli.repl`.

```
 :history :project
```

The default is for all your REPL history to be shared across projects, in the
user-level configuration, but I prefer a per-project history.

```
 :inspect "M-i"
 :keybindings dev.keybindings/install
```

Unchanged from the default in the `examples`.

```
 :middleware [dev.heap/middleware
              dev.timing/middleware
```

Unchanged from the default in the `examples`. These are examples of custom
middleware that enhance the REPL experience by showing how much heap is
currently used and how long each evaluation takes. A very nice touch!

```
              ^:optional cider.nrepl/cider-middleware
              ^:optional org.corfield.rephrase.nrepl/middleware
              ;; add the following setting to ~/.config/nrepl/nrepl.edn
              ;; {:portal.nrepl/wrap-portal {:all-evals true}}
              ;; that will tap> all REPL evaluations
              ^:optional portal.nrepl/middleware]
```

These are the three pieces of middleware I use at different times, depending
on whether I want a quick, basic REPL or something more feature-rich (for
integration with editors, for example). The `^:optional` metadata tells the
CLI REPL to just ignore the middleware if it is not on the classpath. This
provides the same "adaptive REPL startup code" that I used to have in my
previous setup, but in a much cleaner and more maintainable way.

The `examples` configuration has no Paredit keybindings, but here's what I use
the most. Currently, the CLI REPL seems to only support single-key bindings,
with a single-modifier, so these are only "similar" to the bindings I'm used
to in VS Code/Calva:

```
 :paredit/barf-forward "M-,"
 :paredit/join "M-j"
 :paredit/kill "M-k"
 :paredit/move-to-prev "M-b"
 :paredit/raise "M-r"
 :paredit/slurp-forward "M-."
 :paredit/slurp-forward-fully "M-/"
 :paredit/splice "M-h"
 :paredit/split "M-l"
 :paredit/wrap-list "M-p"
 :paredit/wrap-map "M-c"
 :paredit/wrap-vector "M-s"
```

The following are unchanged from the `examples`:

```
 ;; note: portal middleware overrides the print hook:
 :print-hook dev.hooks/pprint-values
 :prompt dev.full-width-prompt/prompt
```

And, finally, I like to see all reflection warnings in all the code I load:

```
 :warn-on-reflection true}
```

### My `clojure-cli.repl` Customizations

The `examples` provided in the CLI REPL repo, provide these files in the data
directory (under `src/dev`, so all the namespaces start with `dev.`):

```
full_width_prompt.clj  
heap.clj  
hooks.clj  
keybindings.clj  
timing.clj
```

The example `keybindings.clj` binds `C-t` to insert a UUID into the REPL,
which seems a bit random to me, so I changed it to insert `tap->` which is
new in Clojure 1.13 and I'm already finding very useful.

In the absence of any global initialization code, I've added a top-level form
to the examples `hooks.clj` file, that "installs" some extra goodies I like
to use with Portal - see below.

I mentioned my `uptime` function above - that is in my 
[`uptime.clj` file](https://github.com/seancorfield/dot-clojure/blob/503aaed7249b5c83bbd972d6c4141233959b8e4f/.cljconf/org.clojure/clojure-cli.repl/src/dev/uptime.clj).
It is auto-required into every namespace, and `uptime` is referred in for
unqualified use.

And, finally, I added a
[`portal.clj` file](https://github.com/seancorfield/dot-clojure/blob/503aaed7249b5c83bbd972d6c4141233959b8e4f/.cljconf/org.clojure/clojure-cli.repl/src/dev/portal.clj).
If Portal is on the classpath, and either `clojure.tools.logging` and/or my
[`logging4j2` library](https://github.com/seancorfield/logging4j2)
is on the classpath, the underlying logging function in those libraries is
redefined to also `tap>` a "logging information" data structure that Portal understands,
so that all my logging information also appears in Portal.
The `dev.portal/install!` function is currently call from the example `hooks.clj` file,
but I'm hoping the CLI REPL adds some sort of `:init-fn` configuration option
in the future.

### Wrapping Up

All of this has allowed me to retire my custom REPL setup, and some of my old
aliases, and rely entirely on the new CLI REPL with my enhancements.

If you're on the Clojurians Slack, join the
[#clojure-cli-repl channel](https://app.slack.com/client/T03RZGPFR/C0C5YK5PAQ0)
to chat about this new tooling.

Major kudos to Jarrod Taylor, on the Clojure team, for creating this!
I look forward to future enhancements (and possibly seeing this integrated into
the Clojure CLI itself, at some point).
