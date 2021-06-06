{:title "VS Code and Clover",
 :date "2020-11-09 17:00:00",
 :tags ["clojure" "vs code" "editors"]}

I've written before about how I switched from Emacs to Atom
at the end of 2016,
where I initially used ProtoREPL (which is no longer maintained)
and then I switched to [Chlorine](https://atom.io/packages/chlorine)
at the end of 2018. I've been very impressed with the work that
Mauricio Szabo has done on Chlorine, adding a way to
[extend the functionality using ClojureScript](https://github.com/mauricioszabo/atom-chlorine/blob/master/docs/extending.md)
so that you can add your own commands --
as I do in my [atom-chlorine-setup repo](https://github.com/seancorfield/atom-chlorine-setup)
so that I can easily work with [Reveal](https://github.com/vlaaad/reveal/)
(and previously with Cognitect's REBL). I've posted a few
[Atom/Chlorine/REBL videos to YouTube](https://www.youtube.com/channel/UC8GD-smsEvNyRd3EZ8oeSrA)
showing my workflow.<!--more-->

One of Mauricio's goals with Chlorine was to structure it in
such a way that the editor-specific functionality was separate
from the core REPL management and evaluation functionality,
with the latter being in its own [repl-tooling repo](https://github.com/mauricioszabo/repl-tooling).
Based on this separation, he's been working on an extension for
VS Code that provides the same core functionality, but a potential
roadblock has been that VS Code doesn't allow you to add new
commands at startup via the sort of user-supplied `init`-file
machinery that Atom supports. That has held me back from trying
VS Code, despite feeling that Atom isn't really getting the same
level of love and support (not from Clojurians themselves, nor from the company behind them).

> The [State of Clojure 2020 Survey](https://clojure.org/news/2020/02/20/state-of-clojure-2020) showed VS Code usage climbing to 10% this year (from 6.6% last year) while Atom usage dropped to 3.9% (from just over 4.5%). Microsoft owns GitHub, which produced Atom and Electron, but has the wildly successful VS Code which is where most of the attention is.

The other day on Slack, however, Mauricio announced that he had been able to port the ClojureScript-based extension machinery from Chlorine to [Clover for VS Code](https://github.com/mauricioszabo/clover/), and the customizations are surfaced as "Tasks" that can be run via the VS Code API and which can have key bindings associated with them.
The only downside is that the tasks don't show up in the VS Code "Command Palette" so they're not as discoverable.

This encouraged me to install VS Code over the weekend and, sure enough, every single piece of my Atom/Chlorine setup works out-of-the-box with VS Code/Clover, and I was able to use the exact same key bindings that I'm used to from Atom!

I've put up a [vscode-clover repo](https://github.com/seancorfield/vscode-clover-setup) with my current VS Code settings, key bindings, and the Clover `config.cljs` (the latter is identical to the `chlorine-config.cljs` file in my atom-chlorine-setup repo mantioned earlier).

I set up VS Code first on my Mac, and then set it up on my Windows laptop (a recently-purchased Microsoft Surface Laptop 3 -- which I am loving!). VS Code has an experimental feature to synchronize your setup, so I enabled that on both machines and it replicated all of the extensions and settings from my Mac onto my Windows laptop -- which saved me a bunch of time (although it doesn't sync key bindings across different platforms but, luckily, I am sync'ing that via my setup repo on GitHub!). The other aspect that impressed me with VS Code on Windows is that it has a "remote" mode for working with WSL2, which is where I do all of my development on Windows, and that allows you to have all of your code and REPLs on WSL2 -- as well as all the VS Code extensions so they run "natively" on Linux -- but still use VS Code natively on Windows! That's much nicer than what I had been doing with Atom, where I installed the Linux version of Atom on WSL2 and used VcXsrv (Xlaunch) to display the GUI on Windows.

Now I have the _exact_ same development experience on both machines with everything synchronized!