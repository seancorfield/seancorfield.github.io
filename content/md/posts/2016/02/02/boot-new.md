{:title "boot-new",
 :date "2016-02-02 17:00:00",
 :tags ["boot" "clojure"]}
In my previous three blog posts about [Boot](http://boot-clj.com/) -- [Rebooting Clojure](https://corfield.org/blog/2016/01/29/rebooting-clojure/), [Building On Boot](https://corfield.org/blog/2016/01/30/building-on-boot/), and [Testing With Boot](https://corfield.org/blog/2016/01/31/testing-with-boot/) -- I looked at why World Singles decided to switch from Leiningen to Boot, as well discussing one of the missing pieces for us (testing). Once I had [boot-expectations](https://github.com/seancorfield/boot-expectations) written, I was casting around for other missing pieces in the ecosystem and one glaring one was the lack of something to generate new projects from templates.<!-- more -->

Leiningen has long-supported the generation of new projects from templates and it's pretty slick. Want to get a new [Framework One](https://github.com/framework-one/fw1-clj) application up and running?

    lein new fw1 myapp
    cd myapp
    PORT=8123 lein run

That's all it takes. No directories and files to create, no editing. Just tell Leiningen to create a new `fw1` project called `myapp`, drop into that newly created directory and run the generated skeleton application.

Behind the scenes, Leiningen looks for the most recent release of the `fw1/lein-template` artifact on Clojars (or Maven Central), downloads it and adds it to the classpath, then it `require`s the `leiningen.new.fw1` namespace (assumed to be in that artifact) and calls the `fw1` function within that namespace. That `fw1` template project in turn relies on the `leiningen.new.templates` namespace to provide a number of functions to `render` new project files from mustache-style templates, using the [Stencil library](https://github.com/davidsantiago/stencil). Leiningen templates may also depend on Leiningen's core code, as well as a few libraries that Leiningen always makes available (such as [Slingshot](https://github.com/scgilardi/slingshot)).

I figured that in order to kickstart any `boot new` functionality, it would make sense for it to be able to render existing Leiningen templates, as well as Boot-specific templates. Since Boot deliberately includes source code directly from other projects so as to minimize the number of dependencies it brings in, and it already had several pieces of Leiningen copied into it, I reached out to [Leiningen's current maintainer, Jean Niklas L'orange](https://github.com/hyPiRion) and asked permission to include parts of Leiningen's new/template support. He graciously said yes -- thank you! -- so I created a raw first cut of a `new` task for Boot, based directly on Leiningen's code, which in turn depended on Bultitude and `leiningen-core` (and a few other bits and pieces). Because Boot tasks are "just Clojure", it was fairly straightforward to get to a [working 0.1.0 version](https://github.com/seancorfield/boot-new/blob/v0.1.0/src/boot/new.clj) that had basic parity with `lein new`.

Since Boot already provided ways to manage dependencies and the classpath, my next goal was [an 0.2.0 version that didn't rely on Leiningen's core](https://github.com/seancorfield/boot-new/blob/v0.2.0/src/boot/new.clj). This version provided the same functionality (well, almost, it had compatibility bugs that took another version to iron out) but no longer needed to bring in `leiningen-core` as a dependency (unless you were generating a Leiningen template which might itself rely on that).

At this point, I was able to implement built-in templates to match Leiningen's `app`, `default` (library), `plugin`, and `template`, which would produce Boot-specific versions. The [0.2.1 version included built-in templates](https://github.com/seancorfield/boot-new/tree/v0.2.1/src/boot/new) for `app`, `default`, `task` (the Boot equivalent of a plugin), and `template`.

Boot `new` had both `leiningen.new.templates` (adapted to run inside Boot) and `boot.new.templates` to support Boot templates. A Boot template is just like a Leiningen template, with a couple of important exceptions: the artifact name is of the form `foo/boot-template` (instead of `foo/lein-template`) and the template's main namespace is `boot.new.foo` (instead of `leiningen.new.foo`). In addition, a Boot template is expected to rely on Boot's internals or explicitly specify its own dependencies -- instead of depending on Leiningen's core library. As a final piece of clean up for this "initial" version of Boot `new`, I removed the dependency on Bultitude (which actually hadn't been needed for a while) and deferred the addition of the `leiningen-core` and `slingshot` dependencies so they wouldn't be pulled in if you were generating a project from a Boot template.

At this point, I felt Boot `new` was ready to announce to the world! I could do:

    boot -d seancorfield/boot-new new -t fw1 -n myapp

and get a freshly generated Framework One app, even if that still used Leiningen to actually run the new app. I could do:

    boot -d seancorfield/boot-new new -t app -n myapp

to get a skeleton for an application that was "powered by Boot": the generated `build.boot` file provides tasks for `build`ing the application uber-JAR and `run`ing the application itself.

[Alan Dipert](https://github.com/alandipert) and [Paulus Esterhazy](https://github.com/pesterhazy) were the first two people to uncover compatibility bugs with existing Leiningen templates (for Hoplon and Chestnut, respectively). Thank you! And that brought me to Release 0.3.1 of [boot-new](https://github.com/seancorfield/boot-new).

The next thing on my roadmap is to add some sort of "generator" function (not unlike [James Reeves' lein-generate](https://github.com/weavejester/lein-generate)) which will allow you to add new pieces to your existing projects, much Rails/Grails have built-in commands to let you add new models, controllers, and so on. That will be part of 0.4.0 and probably go through a couple of revisions. At some point, I'll feel comfortable declaring a 1.0.0 release and then we'll see about getting Boot `new` merged into the core of Boot itself.

Boot originally started life as a part of Hoplon and for a long time, you needed Leiningen in order to generate a new Hoplon project, even tho' Hoplon itself was powered-by-Boot. It's nice to see [Hoplon's Getting Started](https://github.com/hoplon/hoplon/wiki/Get-Started#start-from-a-template) using `boot-new` as the recommended way to generate a new Hoplon project (it's still a Leiningen template!).
