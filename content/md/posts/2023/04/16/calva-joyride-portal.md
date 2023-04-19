{:title "Calva, Joyride, and Portal",
 :date "2023-04-16 12:00:00",
 :tags ["clojure" "calva" "joyride" "portal" "vs code"]}

Back in December, 2022, I described my [original Calva, Joyride, and Portal setup](https://corfield.org/blog/2022/12/07/calva-joyride-portal/).
I've been very happy with it all but, of course, I continue to tweak and update
my development environment and my projects, and now that
[Clojure 1.12.0 Alpha 2 is available](https://clojure.org/news/2023/04/14/clojure-1-12-alpha2)
with `add-libs`-style functionality built-in, I've updated various projects
and my [dot-clojure](https://github.com/seancorfield/dot-clojure)
and [vscode-calva-setup](https://github.com/seancorfield/vscode-calva-setup)
GitHub repos to take advantage of that, so I figured an updated version of that post
was warranted.

My development environment is VS Code, running on Windows, with all my
Clojure-related files and processes running on WSL2 (Ubuntu).
I use [Calva](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva),
[Portal](https://marketplace.visualstudio.com/items?itemName=djblue.portal),
and [Joyride](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.joyride)
to enhance and automate my day-to-day work.<!--more-->

## My REPL

When starting a REPL, I use a number of aliases (which can be found in
[my dot-clojure `deps.edn`](https://github.com/seancorfield/dot-clojure/blob/develop/deps.edn))
to add dependencies that are roughly equivalent to this:

```clojure
    com.datomic/dev.datafy {:git/sha "4a9dffb"
                            :git/tag "v0.1"
                            :git/url "https://github.com/Datomic/dev.datafy"}
    djblue/portal {:mvn/version "RELEASE"}
    io.github.seancorfield/dot-clojure
    {:git/tag "v1.0.1"
     :git/sha "e932f96"}
    io.github.stuarthalloway/reflector
    {:git/url "https://github.com/stuarthalloway/reflector"
     :git/sha "93a0c19b4526c1180959e940202928d35e5c3cef"}
    jedi-time/jedi-time {:mvn/version "0.2.1"}
    party.donut/dbxray {:mvn/version "RELEASE"}
```

> Note 1: I no longer need `tools.deps.alpha`'s `add-lib3` branch -- I use Clojure 1.12.0 Alpha 2 and `clojure.repl.deps` instead!

> Note 2: my `dot-clojure` repo is now a library (as well as a potential source of aliases).

With my `dot-clojure` library on the classpath, I can use the following
`:main-opts` in an alias to start a REPL based on whatever is on the classpath:

```clojure
  :main-opts ["-e" "((requiring-resolve 'org.corfield.dev.repl/start-repl))"]
```

If I'm starting a REPL from the command-line, I also want:

```clojure
    cider/cider-nrepl {:mvn/version "RELEASE"}
```

When I jack-in from Calva, that is supplied automatically.

All of the above are available via aliases in my `dot-clojure` repo's `deps.edn`
file (matching the order of the dependencies above):
* `:datomic/dev.datafy`
* `:portal`
* `:dev/repl` -- the `dot-clojure` library and the `:main-opts` to start it
* `:reflect`
* `:jedi-time`
* `:dbxray`
* and `:cider-nrepl`

At work, I've combined all of the above into a single `:vscode-calva-jack-in`
alias. I most of my OSS projects, I would typically use the following aliases:
* `:1.12` -- from `dot-clojure`'s `deps.edn`: selects Clojure 1.12.0 Alpha 2
* `:dev/repl` -- the `dot-clojure` library and `:main-opts`
* `:portal` -- from `dot-clojure`'s `deps.edn`: add Portal
* `:test` -- add `"test"` to `:extra-paths` and [Cognitect's `test-runner`](https://github.com/cognitect-labs/test-runner) to `:extra-deps`

Also in `:test`, I typically have:

```clojure
  :exec-fn cognitect.test-runner.api/test
```

That allows me to run tests via `clojure -X:test` without introducing additional
`:main-opts` that could interfere with other aliases.

I can start a REPL from the command-line if I want, generally like this:

```bash
clojure -M:1.12:cider-nrepl:dev/repl:portal:test
```

This starts a CIDER-enhanced nREPL server with the Portal middleware enabled.

Or I can use Calva's jack-in feature and specify:
* project type `deps.edn`
* aliases `:1.12`, `:dev/repl`, `:portal`, and `:test` (Calva provides CIDER/nREPL automatically)

Or I can go a step further with the recently-updated Calva feature
[REPL Connect Sequences](https://calva.io/connect-sequences/)
and add something like the following to my project's `.vscode/settings.json` file:

```json
{
  "calva.replConnectSequences": [
    {
      "name": "next.jdbc (Jack-In)",
      "projectType": "deps.edn",
      "autoSelectForJackIn": true,
      "menuSelections": {
        "cljAliases": ["1.12", "dev/repl", "portal", "test"]
      }
    },
    {
      "name": "next.jdbc (Connect)",
      "projectType": "deps.edn",
      "autoSelectForConnect": true
    }
  ],
  "calva.autoConnectRepl": true
}
```

This is [`next.jdbc`'s VS Code configuration](https://github.com/seancorfield/next-jdbc/blob/develop/.vscode/settings.json).
I have similar settings in HoneySQL and also at work, and plan to add it to
other projects as I work on them.

With this configuration in place, I can just open up one of my projects and
press `ctrl+alt+c ctrl+alt+j` to have Calva start a CIDER-enhanced nREPL
server with Portal middleware, using Clojure 1.12.0 Alpha 2, which also has
`clojure.tools.logging` (if present) wired into Portal as well.

For working in a similar way on other projects, my user-level VS Code
`settings.json` file has:

```json
{
  ...,
  "calva.myCljAliases": [
    ":1.12",
    ":portal",
    ":dev/repl"
  ]
}
```

That makes those (user-level `dot-clojure`) aliases available for jack-in when
I'm working inside any project.

## Portal

Inside VS Code, once I have Calva connected to a running nREPL server, either
via jack-in or connecting to an external process, then I can use a key binding
to open Portal (`ctrl+alt+space p` -- from my `vscode-calva-setup`'s
`calva/config.edn` file, which has been merged into my `~/.config/calva/config.edn`
file, providing custom REPL snippets).

This launches **two** Portal windows into VS Code:
* one named for the project directory that just gets explicit `tap>` data, and
* one named `**logging**` that gets the output from the middleware (from regular evaluation of expressions) and the output from `clojure.tools.logging` (if present)

> See my [Portal: Launch and Usage](https://github.com/seancorfield/vscode-calva-setup/blob/develop/README.md#portal-launch-and-usage) notes in my `vscode-calva-setup` repo for more details.

The I arrange the workspace so I have the two Portal windows on the right
(stacked so the `tap>` window is top-right and the `**logging**` window is
bottom-right) and my editor(s) on the left. When Portal opens, I press
`ctrl+alt+e ctrl+alt+right`, if needed, to move them to the right group,
then `ctrl+1` to put focus back on my code editor in the left
group. See my
[key bindings](https://github.com/seancorfield/vscode-calva-setup/blob/develop/keybindings.json)
and my
[Calva configuration](https://github.com/seancorfield/vscode-calva-setup/blob/develop/calva/config.edn)
which has my custom REPL command snippets, activated via the `ctrl+alt+space`
prefix.

I do not generally bother having the REPL output window visible --
with the Portal middleware in place, any code you evaluate in VS Code/Calva
will cause the result to be `tap>`'d and it will appear in Portal. This lets
me use Portal instead of the plain text REPL output window.

My `dot-clojure` library will wire up `clojure.tools.logging` (if it's on the
classpath) so that all log output is also `tap>`'d into Portal, just like the
Portal middleware provides for regular evaluations.

## Clojure 1.12.0 Alpha 2

New since my last post about my dev setup is
[Clojure 1.12.0 Alpha 2 is available](https://clojure.org/news/2023/04/14/clojure-1-12-alpha2).

This provides a built-in API for running Clojure CLI tooling (`-T` invocation)
via the new `clojure.tools.deps.interop` namespace, as a subprocess via the
new `clojure.java.process` namespace.

On top of this is built the new `clojure.repl.deps` namespace which provides
functions to dynamically add new libraries to your running REPL, including a
function to synchronize your (updated) `deps.edn` file with the dependencies
running already in your REPL.

In many of my projects, including at work, I used to depend on the `add-lib3`
branch of `org.clojure/tools.deps.alpha` which had an `add-libs` function in
the `clojure.tools.deps.alpha.repl` namespace. Clojure 1.12.0 Alpha 2 has that
same function in `clojure.repl.deps` so I've switched over the various instances
of that I used to have in Rich Comment Forms. For example, here's an RCF in
`next.jdbc` that
[loads the project's `:test` dependencies](https://github.com/seancorfield/next-jdbc/blob/develop/test/next/jdbc/test_fixtures.clj#L239-L251)
into your running REPL.

Even with the old `add-libs` function, my workflow was generally to update my
`deps.edn` file and then invoke `add-libs` on the (updated) map of dependencies
in that file. I've retained the `ctrl+alt+space a` hot key in my Calva setup
but now that custom REPL snippet prompts for a list of aliases and uses the
new `clojure.repl.deps/sync-deps` function to load any new dependencies from
the (updated) `deps.edn` file under those aliases. Here's that snippet:

```clojure
  {:name "Add Libs (Sync w/aliases)"
   :key "a"
   :snippet (tap>
             ((requiring-resolve 'clojure.repl.deps/sync-deps)
              :aliases (read-string (str "[" (read-line) "]"))))}
```

The result is a sequence of any new libraries loaded (which I send to my
primary Portal window via `tap>` in the snippet).

One area where this can be particularly helpful, when working on a project,
is when I decide to edit `build.clj` and realize I didn't specify the
`:build` alias when I started up my REPL (because I didn't need all
the `tools.build`/`tools.deps` dependencies for working with my project's
source and test code). Now I can press `ctrl+alt+space a`, enter `:build`
in the prompt (popup window), press return, and let Clojure load everything
from the `:build` alias into my REPL!

## Joyride

The final piece of the puzzle is
[Joyride](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.joyride)
which lets you script VS Code (and Calva) using ClojureScript -- powered by
[@borkdude's excellent `sci`](https://github.com/babashka/sci).

I have a Joyride script to
[evaluate the `ns` form](https://github.com/seancorfield/vscode-calva-setup/blob/develop/joyride/scripts/ns.cljs)
of a file I'm editing
without moving the cursor, which can be useful when you let Calva/LSP add
an alias to your `:require` form (via Quick Fix, for example).

I also have key bindings to run Joyride scripts that open
ClojureDocs.org in a browser (in VS Code) for the symbol my cursor is on or
the Java documentation for a class name (or the class type
of an expression I evaluate). You can see those two
[Joyride scripts](https://github.com/seancorfield/vscode-calva-setup/tree/develop/joyride/scripts)
in my VS Code/Calva setup repo.

## Remote Debugging with Calva and Portal

The rest of my original blog post on this subject talked about how I was
using Joyride to automate connecting to remote nREPL servers and using
Portal to help debug issues or run analyses in the context of QA or production.

The generic Joyride script for that is published in GitHub now:
* [remote_repl.cljs](https://github.com/seancorfield/vscode-calva-setup/blob/develop/joyride/src/remote_repl.cljs) -- lives in `joyride/src`
* [example_repl.cljs](https://github.com/seancorfield/vscode-calva-setup/blob/develop/joyride/scripts/example_repl.cljs) -- example usage in `joyride/scripts`

_Updated April 18th, 2023 after input from [@djblue](https://github.com/djblue)_

Previously, my `remote_repl.cljs` script opened a "Simple Browser" in VS Code
that connected to the main port that the Portal server was running on and,
while this worked, it wasn't the best developer experience.

Chris submitted a
[pull request against my VS Code setup repo](https://github.com/seancorfield/vscode-calva-setup/pull/3/files)
that allowed me to use the Portal extension in VS Code directly against the
remote Portal server, instead of a browser. The `calva.connect` command is now
passed to bypass all the menus and connect directly to the nREPL server on
the specified port:

```clojure
#js {:port nrepl-port :connectSequence "Generic"}
```

My workflow now is:
* Connect my VPN so I have access to remote servers,
* Press `ctrl+alt+b q` or `ctrl+alt+b p` to run a QA or Production version of `example_repl.cljs`:
  - Starts the `ssh` tunnel
  - Connects Calva to the remote nREPL server
  - Reads the local `.portal/vs-code.edn` file and tells the REPL to `spit` it out on the remote server
* Press `ctrl+alt+space p` to run my Portal startup sequence in [`~/.config/calva/config.edn](https://github.com/seancorfield/vscode-calva-setup/blob/develop/calva/config.edn):
  - Starts up two Portal windows in VS Code
  - One for logging/middleware output
  - One for plain `tap>` operations

With all the auto-connect and auto-jack-in REPL connect sequences in place,
this makes it very easy to work with either a local REPL (`ctrl+alt+c ctrl+alt+j`
if there isn't a known REPL for Calva to auto-connect to) or a remote REPL
(`ctrl+alt+b q` or `ctrl+alt+b p`). Then to switch back from remote to local,
I can press `ctrl+t ctrl+f` (terminal focus) `ctrl+c` (quit the `ssh` command),
`exit <return>` to close out that terminal, `ctrl+alt+c ctrl+alt+c` to auto-connect
back to my local REPL. And `ctrl+alt+space p` whenever I want to bring up
Portal windows connected to my current REPL!
