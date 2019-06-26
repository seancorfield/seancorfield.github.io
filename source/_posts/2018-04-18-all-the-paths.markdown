---
layout: post
title: "All The Paths"
date: 2018-04-18 10:40:00
comments: true
categories: [boot, clojure, leiningen]
---
With the recent arrival of [`clj` and `tools.deps.alpha`](https://clojure.org/guides/deps_and_cli) as a "standard" lightweight way to run Clojure programs and the seed for tooling based on `deps.edn` dependency files, it's time to take a look at the terminology used across Clojure's various tools.<!-- more -->

## Running Java/JVM Programs

Before we dive into Clojure's tools, let's first consider the basics. When you run a compiled Java (or any JVM-based) program, you need two things: a classpath and a `main` function or, rather, a "main class". Everything boils down to `java -cp ... MyClass` where the `...` is the "classpath": a colon-delimited (or semicolon-delimited on Windows) list of directories or JAR files to search for `MyClass` and all the classes it needs. A quick look at the many introductory tutorials for Java online shows that the classpath is mostly just glossed over, despite being one of the most important (and, for beginnners, often one of the most perplexing) aspects of running programs on the JVM.

When you are creating deployable versions of programs, you typically use some sort of "build" tool to gather up all your (compiled) class files and JAR files (dependencies) and put them all together in a single "uber" JAR file, so that it can be run with just `java -jar MyApplication.jar`. This is shorthand for specifying `MyApplication.jar` as the entire classpath and assuming some "magic" inside the JAR file that specifies how to find the main class (via something called a "manifest"). The build tools know how to create the manifest (based on some information you provide) as well as managing the assembly of all the component pieces that will make up your complete application.

It's no wonder this can be a minefield for beginners!

## Leiningen

In the very early days of Clojure, folks relied on the JVM ecosystem for build tools and dealt with all the sharp edges directly. Then along came [Leiningen](https://leiningen.org/) with the goal of "automating Clojure projects without setting your hair on fire" by hiding all the sharp edges of Maven and `pom.xml` files and manifests and so on. It was so successful that it soon became the de facto standard in the Clojure world and most of the Clojure books out there can safely assume you have Leiningen installed. You can even just type `lein new app myapp` and you have a nicely structured skeleton of a Clojure project to get you up and running! Leiningen dispensed with all the XML and arcane invocations of the Java world and used a simple `project.clj` file to declare everything about your program. Your dependencies, your main namespace (class), various different ways of running your program (profiles), and all the tools you needed for development and testing (plugins).

The main aspect of Leiningen that is relevant to this blog post is the concept of "paths". As noted above, as far as the JVM is concerned, there's really only the classpath: that's how you run your code. The various build tools need to know what else should be packaged up for deployment (e.g., configuration files, HTML/CSS/JS assets), as well as what you need to run tests or other development-related tasks that do not need to be in the deployment artifact.

Leiningen chose the following terminology for these various things:

* `source-paths` -- your Clojure code that should be part of every runtime, as well as being packaged up for deployment.
* `java-source-paths` -- any Java code in your project that should be compiled, and whose `.class` files should be packaged up for deployment.
* `test-paths` -- your Clojure code needed for development/testing of your application, which should _not_ be part of the deployment artifact.
* `resource-paths` -- your non-code files that should be made available at runtime (i.e., on the classpath), as well as being packaged up for deployment.
* `dependencies` -- the list of project artifacts (and versions) that your program needs at runtime, and therefore should be packaged up for deployment -- or at least declared in the deployment artifact as being needed for runtime.

There's a subtlety here that Leiningen glosses over: there are really two types of files that are not code, that both need to be in the deployment artifact -- those that need to be on the classpath during Leiningen's runtime (configuration files etc) and those that do not (HTML/CSS/JS etc). In Leiningen's world they are all "resources" and end up on the classpath anyway. This is a harmless convenience, but it's still an important distinction (note: once packaged up, everything in the JAR will be available on that program's runtime and, in general, during development/testing you will usually want your "web root" to be on your classpath, so that `io/resource` can find those files).

## Boot

[Boot](http://boot-clj.com/) came along later in Clojure's evolution and approached build tools from a different direction. While most build tools (across most languages) are "declarative", i.e., they have some sort of Domain-Specific Language (DSL) and a file that describes the various properties of your development/testing/packaging strategies, Boot decided to provide a library and a set of abstractions that would let you write pure Clojure to implement your needs. As Boot's website says "It's not a build tool - it's build tooling."

I've [written about Boot](http://corfield.org/blog/categories/boot/) quite a bit since we made our decision at [World Singles llc](http://worldsinglesnetworks.com/) to switch our stack from Leiningen to Boot, back in late 2015. Boot feels more like Clojure: it's composable and "it's just code". Boot can do everything that Leiningen can do, and quite a bit more. And of course it chose its own terminology for the various JVM-related paths:

* `resource-paths` -- any files that need to both be on the classpath and packaged up for deployment.
* `source-paths` -- any files that need to be on the classpath, but not part of the deployment artifact.
* `asset-paths` -- any files that do not need to be on the classpath, but should be packaged up for deployment.
* `dependencies` -- the list of project artifacts (and versions) that your program needs at runtime (etc).

We see that Boot distinguishes between the two types of files that are not code, that both need to be in the deployment artifact, but blurs the lines between code and non-code files that need to be on the classpath. That means that when we move back and forth between Boot and Leiningen, we need to remember that they call certain core concepts by different names. Boot's `source-paths` are pretty much equivalent to Leiningen's `test-paths`. Boot's `asset-paths` are mapped into Leiningen's `resource-paths` (despite not needing to be on the classpath during the tool's runtime) and Leiningen's `source-paths` are mapped into Boot's `resource-paths` (despite being specifically _source_ code). It definitely has potential to be confusing but it's born out of different approaches to how the fileset is managed across the lifecycle of both tools. In particular, Boot has a fileset abstraction that is key to how tasks are written and how various types of files are manipulated during Boot's execution -- see https://github.com/boot-clj/boot/wiki/Boot-Environment for more detail about this.

## clj - Bringing It Back Home

As Clojure 1.9 was being developed, it became important to be able to treat "Clojure core" as a single artifact, even tho' it consisted of "core" plus "spec.alpha" plus "core.specs.alpha". Out of that came the `deps.edn` file for specifying dependencies, the `tools.deps.alpha` library for manipulating the `deps.edn` files and the dependencies they declare, and the `clj` and `clojure` command-line scripts that provide a standardized way to run Clojure programs based on dependencies specified in `deps.edn` files.

Just like the underlying Java (JVM) ecosystem, Clojure's new Command-Line Interface (CLI) is primarily about the classpath. It provides a mechanism to specify a set of paths and a set of dependencies, across (typically) three files (the "install" `deps.edn`, your "home" `deps.edn`, and your "project" `deps.edn`). It supports "aliases" for merging in alternative paths and dependencies. It also supports JVM options, and the options already available in `clojure.main`: initial and main options, to specify files to load, code to run, and a main namespace.

By getting back to basics, `clj` mirrors the underlying JVM ecosystem that really only cares about the classpath, constructed in appropriate ways for developing, testing, and running your program. It leaves decisions about packaging up to other tools, by design, and through the `tools.deps.alpha` library it offers a straightforward API for tool authors to build upon.

Late last year (November 2017), I started work on [boot-tools-deps](https://github.com/seancorfield/boot-tools-deps) which was intended as a bridge to add Boot-based tooling on top of an existing `deps.edn` project. There is also [lein-tools-deps](https://github.com/RickMoynihan/lein-tools-deps) which takes a slightly different approach but is intended to integrate `deps.edn` files into a Leiningen project.

We've also seen the first two "build tools" based on `deps.edn` appear: [depstar](https://github.com/seancorfield/depstar) and [juxt.pack](https://github.com/juxt/pack.alpha) which, like the Boot and Leiningen tools above, take different approaches but are both intended to build (uber) JAR files from projects based on `deps.edn`.

This means that we need to look closely at the decisions each of these tools make about paths. The recommended approach with `clj` and `deps.edn` is to use aliases (liberally) to delineate the different classpaths (and JVM options and main options and so on) that all your various tasks require. The end result in each case is a specific classpath and options to run your code. For build tools, "run your code" means take the files on the classpath and package them up into a JAR file -- so the entire file selection process is driven by aliases. This is rather different to the Leiningen and Boot build tools outlined above, since they provide explicit categories in which to declare files and folders for consideration in the classpath, non-classpath, packaging, and non-packaging categories.

`lein-tools-deps` is intended primarily to replace your `:dependencies` vector by dependencies drawn from various `deps.edn` files. It doesn't (currently) worry about aliases, and it relies on the existing Leiningen infrastructure for all development/testing/packaging decisions. It's a nice, straightforward metaphor: `:mvn/version` dependencies in `deps.edn` are mapped into Leiningen's `:dependencies` and nothing else changes. The project maintainer, Rick Moynihan, intends to expand its functionality over time.

Both `juxt.pack` and `depstar` assume that you know what you're doing with aliases and they take whatever you've decided is your classpath and they turn it into a JAR file. `depstar` takes the simpler approach, producing just a JAR file with no assumptions built-in -- you need to tell it what main namespace to run and so on. `juxt.pack` is closer to a standard build tool and targets both AWS Lambda and traditional uber-JAR approaches.

`boot-tools-deps` is intended to be a hybrid that envelops the underlying `deps.edn` and `tools.deps.alpha` machinery, exposing most of the same options, and then allows you to either run Boot tasks using the same, underlying classpath, or merge the `clj` environment back into the Boot environment to support different tooling. Specifically, it assumes that what comes out of `deps.edn` in the `:paths` key should be the `:resource-paths` -- source and non-source, that is intended to be on the classpath and intended to be packaged for deployment. This includes `:git/url` and `:local/root` dependencies (since those are source code paths/folders). It also assumes that what comes out of `deps.edn` in the `:extra-paths` key should be the `:source-paths` -- source code that is intended to be on the classpath but not packaged for deployment. While this is a fairly arbitrary interpretation of an artificial divide between entities on the classpath, I believe this is "likely" in the real world. In any case, this can be modified by Boot's `sift` task. Finally, it assumes that what comes out of `deps.edn` as a map of actual libraries and versions should be the `:dependencies` for Boot itself. This ought to be a straightforward assumption but it can cause problems if the dependencies use `:scope` extensively since that is not propagated by the Maven/Resolver library behind `tools.deps.alpha` and is of dubious value in the first place. In particular, transitive dependencies will not have the same scope as the parent that introduced them! Using aliases to assemble your dependencies is a much safer bet.

## Looking Forward

`clj` and `deps.edn` and `tools.deps.alpha` provide a core, standardized way of dealing with dependencies and various options that are required to run Clojure programs. Over time, this should become the standard baseline for "installing" and running Clojure code on Linux, Mac, and Windows. Both Leiningen and Boot have a strongly entrenched user base and both provide substantially enhanced behavior above and beyond `clj` and its components -- that's by design. It is reasonable to assume that tooling based on `clj`, `deps.edn`, and `tools.deps.alpha` will become much more commonplace and there will come a day, possibly soon, where developers who pick up Clojure no longer need to consider installing Boot or Leiningen for their day-to-day work.

## p.s. Building New Projects

Another common function that both Leiningen and Boot provide is to generate new projects based on a template. Check out [clj-new](https://github.com/seancorfield/clj-new) for `clj`-based tooling that generates new `deps.edn`-based projects, as well as leveraging existing `lein-template` and `boot-template` projects.

## Credits

Thank you to Alan Dipert, Alex Miller, Richiardi Andrea, and Rick Moynihan for reviewing a draft of this post and providing feedback and suggestions!
