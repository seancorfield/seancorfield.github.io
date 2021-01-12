{:title "Atom, Chlorine, and Windows",
 :date "2019-01-22 20:15:00",
 :tags ["clojure"]}
About a month ago, I was [praising Chlorine, the new Clojure package for
Atom](https://corfield.org/blog/2018/12/19/atom-chlorine/) and I've been using
it, day-in, day-out, for all my Clojure development. On a Mac, that's
straightforward because I start a Socket REPL on the Mac and I run Atom on the
Mac so when I connect via Chlorine and issue the `Chlorine: Load File`
command (via `Ctrl-, f` in my keymap), it sends `(load-file "/path/to/file.clj")`
to the REPL, for the file being edited, and that is evaluated and loads the
source from disk and compiles it. On Windows...<!-- more --> Well, on Windows
there are a few obstacles to this workflow.

The first obstacle (for me) is that `clj` doesn't run on Windows and that's
what I use for everything Clojure on a Mac and on Linux now. There's a
PowerShell implementation in progress but Windows has always been a bit of a
second-class citizen so it's still a ways off. But Windows has WSL (Windows
Subsystem for Linux) so I can
and do run (user-mode) Ubuntu on my Windows laptop and I do all of my Clojure
work on that...

...but that brings the second obstacle: file paths on Windows look like
`C:\path\to\file.clj` and under WSL they look like `/mnt/c/path/to/file.clj`.
That means your editor on Windows and your REPL on Linux don't quite speak the
same language.

Fortunately, the latest release of Chlorine, 0.0.8, has a "smart" `Load File`
command that will work with both a Windows-based Socket REPL and a WSL-based
Socket REPL! It wraps the call to `load-file` in logic that looks at the
requested file path and also at the
`user.dir` JVM property and if the former starts with a drive identifier and `:`,
and the latter does not, it assumes the REPL is running on WSL on Windows and
maps the file path appropriately.
