{:title "Calva, Joyride, and Portal",
 :date "2022-12-18 10:00:00",
 :tags ["clojure" "calva" "joyride" "portal" "vs code"]}

I've mentioned in several posts over the years that I switched my
development setup from Emacs to Atom, initially with ProtoREPL and later
with Chlorine, and then to VS Code, initially with Clover (a port of
Chlorine) and more recently with
[Calva](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva).
There were several detours along the way, but that is the overall arc.

I've also mentioned a couple of times that I use
[Portal](https://marketplace.visualstudio.com/items?itemName=djblue.portal)
now, as an extension inside VS Code (after previously using Reveal and, before
that, Cognitect's REBL).

I've also published my
[VS Code and Calva setup](https://github.com/seancorfield/vscode-calva-setup)
files on GitHub.

But I haven't really talked about what that experience is like on a
day-to-day basis or any specifics of my integrated workflow.<!--more-->

## My REPL

When starting a REPL, I use a number of aliases (which can be found in
[my dot-clojure `deps.edn`](https://github.com/seancorfield/dot-clojure/blob/develop/deps.edn))
to add dependencies that are roughly equivalent to this:

```clojure
    org.clojure/tools.deps.alpha ; add-lib3 branch
    {:git/url "https://github.com/clojure/tools.deps.alpha"
      :git/sha "8f8fc2571e721301b6d52e191129248355cb8c5a"}
    jedi-time/jedi-time {:mvn/version "0.2.1"}
    cider/cider-nrepl {:mvn/version "RELEASE"}
    djblue/portal {:mvn/version "RELEASE"}
    party.donut/dbxray {:mvn/version "RELEASE"}
    com.datomic/dev.datafy {:git/sha "4a9dffb"
                            :git/tag "v0.1"
                            :git/url "https://github.com/Datomic/dev.datafy"}
    io.github.stuarthalloway/reflector
    {:git/url "https://github.com/stuarthalloway/reflector"
      :git/sha "93a0c19b4526c1180959e940202928d35e5c3cef"}
```

I have my own script to start various types of REPL but most of the time I
have a CIDER-enhanced nREPL server running with Portal middleware. See
[my dot-clojure `dev.clj`](https://github.com/seancorfield/dot-clojure/blob/develop/dev.clj)
for full details. The TL;DR is that the script enables whatever is on the
classpath of the things it knows about but a basic version boils down to:

```bash
clojure -M:<dev-aliases> -m nrepl.cmdline \
  --middleware '[portal.nrepl/wrap-portal cider.nrepl/cider-middleware]'
```

## Portal

Inside VS Code, I use `ctrl+alt+c ctrl+alt+c` to connect Calva to that running
nREPL server and then I have a key binding to open Portal (`ctrl+alt+space p`),
using `:launcher :vs-code`, so that it starts up inside VS Code, and finally
I arrange the workspace so I have Portal on the right and my editor(s) on the
left (when Portal opens, I press `ctrl+alt+e ctrl+alt+right` to move it to
the right group, then `ctrl+1` to put focus back on my code editor in the left
group). See my
[key bindings](https://github.com/seancorfield/vscode-calva-setup/blob/develop/keybindings.json)
and my
[Calva configuration](https://github.com/seancorfield/vscode-calva-setup/blob/develop/calva/config.edn)
which has my custom REPL command snippets, activated via the `ctrl+alt+space`
prefix.

I do not bother having the REPL output window visible --
with the Portal middleware in place, any code you evaluate in VS Code/Calva
will cause the result to be `tap>`'d and it will appear in Portal. This lets
me use Portal instead of the plain text REPL output window.

I start my REPL from a external terminal window and leave it running, because it
typically outlives my editor sessions. With the add-lib3 branch of `tools.deps.alpha`
I can add new dependencies without restarting my REPL.

My `dev.clj` script will wire up `clojure.tools.logging` (if it's on the
classpath) so that all log output is also `tap>`'d into Portal.

## Joyride

The final piece of the puzzle is
[Joyride](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.joyride)
which lets you script VS Code (and Calva) using ClojureScript -- powered by
[@borkdude's excellent `sci`](https://github.com/babashka/sci).

I'm only just getting started with Joyride! I have a key binding that opens
ClojureDocs.org in a browser (in VS Code) for the symbol my cursor is on and
another that opens the Java documentation for a class name (or the class type
of an expression I evaluate). You can see those two
[Joyride scripts](https://github.com/seancorfield/vscode-calva-setup/tree/develop/joyride/scripts)
in my VS Code/Calva setup repo.

I'm currently working on automating my remote debugging setup and that's what
the rest of this blog post will cover!

## Remote Debugging with Calva and Portal

There are quite a few moving parts in this section so I'm going to go through
the code and configuration first and then talk about how it all fits
together.

The idea behind this setup is that we can conditionally run an nREPL server
and/or a Portal server in any process, triggered via JVM properties much like
the built-in Socket server in Clojure. Then we start SSH tunnels over the VPN
into the data center, connect a browser to the Portal server and connect
Calva to the remote nREPL server, then debug the remote process by evaluating
code within it and viewing the results in Portal.

### Starting nREPL and Portal

We've chosen `nrepl.server.port` and `portal.server.port` as our two JVM
properties and we have the following code in a Polylith component
implementation:

```clojure
(defn maybe-start-server
  "Given a symbol identifying a function to start a server,
  derive a property name for the port to use and see if we
  can start the server."
  [start-server-fn]
  (let [[server-type] (str/split (namespace start-server-fn) #"\.")
        property      (str server-type ".server.port")]
    (when-let [port-str (System/getProperty property)]
      (try
        (let [port         (parse-long port-str)
              start-server (requiring-resolve start-server-fn)]
          (start-server {:port port})
          (logger/info server-type "server started on port" port))
        (catch Throwable t
          (logger/error property
                        "property exists, but either"
                        "the port is invalid,"
                        "the library is not available,"
                        "or the server cannot be started:"
                        (type t) (ex-message t)))))))
```

In the interface for that component, we have:

```clojure
(defonce ^{:doc "Optional nREPL server instance."} nrepl-server
  (impl/maybe-start-server 'nrepl.server/start-server))

(defonce ^{:doc "Optional Portal server instance."} portal-server
  (impl/maybe-start-server 'portal.api/start))
```

We add that interface namespace to the `:require` clause of the `ns` form
in the main namespace of each application for which we want to enable
remote debugging. Right now, we're doing that in just one application which
is internal-facing but we've run Socket servers in several of our applications
for years to enable debugging via the REPL so we'll probably add this to a
few more applications as we polish the workflow.

The script that starts each of our applications looks for a `.jvm_opts` file
named for the application and adds those JVM options -- which is how we
specify the ports for the Socket server, the nREPL server, and the Portal
server.

### Connecting VS Code to the Remote Server

As noted above, there are several steps needed here but Joyride can help
automate them.

Here's the Joyride script I'm developing:

```clojure
(ns remote-repl
  (:require ["vscode" :as vscode]
            [promesa.core :as p]))

(defn- start-tunnel [nrepl-port portal-port label remote-server]
  (let [terminal (vscode/window.createTerminal #js {:isTransient true
                                                    :name label
                                                    :message (str label " Remote REPL...")})]
    (.show terminal)
    (.sendText terminal (str "ssh -N"
                             " -L " nrepl-port ":localhost:" nrepl-port
                             " -L " portal-port ":localhost:" portal-port
                             " " remote-server))))

(defn- start-browser [portal-port]
  (vscode/commands.executeCommand "simpleBrowser.show" (str "http://localhost:" portal-port))
  (p/do
    (p/delay 2000)
    (vscode/commands.executeCommand "workbench.action.moveEditorToRightGroup")
    (p/delay 1000)
    (vscode/commands.executeCommand "workbench.action.focusFirstEditorGroup")))

(defn- connect-repl []
  (vscode/commands.executeCommand "calva.disconnect")
  (vscode/commands.executeCommand "calva.connect"))

(defn- repl-setup [nrepl-port portal-port label remote-server]
  (start-tunnel nrepl-port portal-port label remote-server)
  (p/do
    (p/delay 2000)
    (start-browser portal-port)
    (p/delay 1000)
    (connect-repl)))

(repl-setup 6666 7777 "QA" "qauser@10.0.1.2")
```

This does the following:
* start a terminal in VS Code and run the `ssh` command to setup the tunnel with two ports mapped,
* open a Simple Browser inside VS Code, pointing to the Portal server,
* move that browser to the right group (and set focus back to the left group),
* disconnect any existing REPL session and connect a new one.

The `delay` calls are fairly arbitrary but allow for each preceding step to
complete before the next step starts (otherwise you get fairly unpredictable
behavior as all the steps tend to overlap!).

To avoid manually filling in the various REPL connection dialogs, we add this
Calva setting:

```json
    "calva.replConnectSequences": [
      {
        "name": "QA nREPL Server",
        "projectType": "deps.edn",
        "cljsType": "none",
        "nReplPortFile": ["development", "resources", "qa.nrepl"],
        "menuSelections": {}
      }
    ],
```

This adds `QA nREPL Server` at the top of the list of REPL types that Calva
displays and references a file that contains the known port number in
`development/resources/qa.nrepl`.

This reduces the connect sequence to:
* accept the suggested directory for the project
* select the `QA nREPL Server` REPL type

There's an open issue in Calva to
[provide an API for the whole connection sequence](https://github.com/BetterThanTomorrow/calva/issues/1984)
so this process can, hopefully, be simplified at some point!

Then we have a key binding to run this script:

```clojure
    {
      "key": "ctrl+alt+b q",
      "command": "joyride.runUserScript",
      "args": "remote_repl.cljs"
    },
```

At this point, my process is:
* connect my VPN to the data center
* open VS Code in the project
* press `ctrl+alt+b q`

Joyride will start the SSH tunnel in terminal, open a browser in VS Code
connected to the Portal server, and start the REPL connection sequence for me!

### Debugging the Remote Process

At this point I can open my REPL scratch file and evaluate the whole file
and then start evaluating expressions in the Rich Comment Form as needed:

```clojure
;; scratch pad for debugging against Admin on QA/production

(ns sean-repl-scratch
  (:require [next.jdbc :as jdbc]
            [portal.api :as p]
            [ws.admin-web.interface.system :refer [sys]]))

(set! *warn-on-reflection* true)

(defn- as-table [v] (with-meta v {:portal.viewer/default :portal.viewer/table}))

(comment

  ;; set the theme:
  (p/eval-str (str
               '(portal.ui.state/dispatch! portal.ui.state/state
                                           portal.ui.state/set-theme!
                                           :portal.colors/zerodark)))

  ;; start tapping...
  (add-tap #'p/submit)

  ;; don't forget this:
  (p/clear)
  (remove-tap #'p/submit)

  ;; a useful datasource:

  (def ds (-> sys :application :datasource))

  ;; =================== scratch code for debugging ===================

  (as-table (jdbc/execute! ds ["describe table"]))

  (as-table
   (jdbc/execute! ds [(str "select * from table"
                           " where name like 'email%'")]))
  )
```

This uses the Portal API to set the theme in the connected browser -- the
default for a headless Portal server is a light theme and I have VS Code
set to a dark theme so setting the browser to `zerodark` makes things a lot
more readable for me.

I have expressions to add Portal as a `tap>` listener, to clear Portal's
history, and to remove Portal as a `tap>` listener. This is intended to
avoid leaving the results of my debugging session in memory and to ensure
that any `tap>` calls in the production code don't continue to send results
to Portal when I'm no longer actively debugging things.

Our `-main` function in the "admin web app" stores the running system
[Component](https://github.com/stuartsierra/component) in a global Var
called `sys` purely for REPL-based debugging sessions. It contains the
(running) web server and our application state, which in turn contains
a datasource (a HikariCP connection pooled datasource, specifically).
The `as-table` function is a convenience to wrap values so that `tap>`'ing
them into Portal will use the table viewer by default, instead of the
inspector viewer.

Portal supports `datafy`/`nav` and by using `next.jdbc` to query
tables, I can then navigate through foreign key relationships in the data.
At some point soon, I hope to create some screencasts showing how this
works because it's harder to describe than to show!

### Work in Progress

This is very much a work in progress. I have all of the above working
locally but it needs some polishing, and then I'll add the scripts and
configuration to my `vscode-calva-setup` repo.

In addition, once Calva offers an API to start a REPL session without
needing any dialogs or user input, this process can be streamlined further
to potentially remove the REPL connect sequence and to allow for the
automated process to automatically set the Portal theme in the browser.
