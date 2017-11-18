---
layout: post
title: "Boot localrepo?"
date: 2017-11-17 12:50:00
comments: true
categories: [boot, clojure]
---
Sometimes you just can't help having a "random 3rd part JAR file" in your project. The best practice is, of course, to upload it to your preferred Maven-compatible repository via whatever service or software you use for all your in-house shared artifacts. But sometimes you just want to play with that JAR file locally, or you haven't gotten around to running your own shared repository.

If you're using Leiningen, you'll probably reach for the excellent [`lein-localrepo`](https://github.com/kumarshantanu/lein-localrepo) which lets you "install" your random JAR file into your local Maven cache (in `~/.m2/repository`).

What do you do if you're using Boot instead?<!-- more -->

Well, you _could_ just keep `lein` installed and use it for this (with the `lein-localrepo` plugin dependency in your `~/.lein/profiles.clj`, for example):

``` bash
lein localrepo install foobar.jar foo/bar 1.0.1
```

Or you could run some command line Boot to make that happen:

``` bash
boot pom -p foo/bar -v 1.0.1 target \
    install -f foobar.jar -p target/META-INF/maven/foo/bar/pom.xml
# Don't forget to clean up!
rm -rf target
```

What the what? Yeah, that's really kind of ugly. Unfortunately, the `install` task seems to expect a physical `pom.xml` file when you specify a physical JAR file. That means we need to drop a `target` task into the pipeline into order for the generated `pom.xml` to be written to disk. It would be nice if it would look in the fileset first, because then at least this should work:

``` bash
# This does NOT work!
boot pom -p foo/bar -v 1.0.1 install -f foobar.jar -p foo/bar
```

So what other options do we have with Boot?

Well, it turns out that you can run some Leiningen plugins directly from Boot, from the command line! Boot lets you specify dependencies on the command line, and it lets you "call" Clojure code from the command line too. Let's start a REPL with `lein-localrepo` as a dependency:

``` bash
boot -d leiningen -d lein-localrepo repl
```

We need Leiningen as well since plugins rely on it as a dependency. Now, in the REPL we can require the main namespace from the plugin, and get information about the entry point:

``` clojure
boot.user=> (require '[leiningen.localrepo :as lr])
nil
boot.user=> (doc lr/localrepo)
-------------------------
leiningen.localrepo/localrepo
([_] [_ command & args])
  Work with local Maven repository

Usage: lein localrepo <command> (commands are listed below)
...
```

For any given plugin, `lein-foo`, the entry point is `leiningen.foo/foo`. Note that it is called with an unused argument and then the actual command line arguments. So we can ask for help on the `install` command:

``` clojure
boot.user=> (lr/localrepo nil "help" "install")
Install artifact to local repository
  Arguments:
    [options] <filename> <artifact-id> <version>
  Options:
...
```

So we can call that function to perform the JAR install we want:

``` clojure
boot.user=> (lr/localrepo nil "install" "foobar.jar" "foo/bar" "1.0.1")

>
```

Oh, that's a bit disappointing -- we exited the REPL and got our command prompt back. I guess that calls `(System/exit)` which is a bit unfriendly (but, perhaps, not unexpected given the use case for this plugin). But we can verify that the JAR file was installed:

``` bash
> ls -R ~/.m2/repository/foo
bar

/Users/sean/.m2/repository/foo/bar:
1.0.1				maven-metadata-local.xml

/Users/sean/.m2/repository/foo/bar/1.0.1:
_remote.repositories	bar-1.0.1.jar		bar-1.0.1.pom
```

Now, remember that I said you can "call" code from the command line with Boot? Since we know the code we need to execute (the `require` and the call to `lr/localrepo`), we'll just supply those as command line arguments to the `call` task:

``` bash
boot -d leiningen -d lein-localrepo \
  call -e "(require '[leiningen.localrepo :as lr])" \
       -e '(lr/localrepo nil "install" "foobar.jar" "foo/bar" "1.0.1")'
```

Voila!
