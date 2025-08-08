{:title "The power of the :deps alias",
 :date "2025-08-08 17:00:00",
 :tags ["clojure"]}

Most of us who use the Clojure CLI, are familiar with the `-M` (main), `-X`
(exec), and `-T` (tool) options, and may have used `clojure -X:deps tree` at
some point to figure out version conflicts in our dependencies. The `:deps`
alias can do a lot more, so let's take a look!

First off, what is the `:deps` alias and where does it come from?

The "root" `deps.edn` file is where the `:deps` alias is defined. If you run
`clojure -Sdescribe`, it'll show you the `:config-files` it uses: the "root",
the "user" (see `:config-user` in the output of that command), and if you are
in a project folder, the "project" `deps.edn` file (see also `:config-project`).
Even though the "root" `deps.edn` file exists, that isn't the "source of truth"
as the real root `deps.edn` file is a resource inside the `tools.deps` project:

The [root `deps.edn` resource](https://github.com/clojure/tools.deps/blob/master/src/main/resources/clojure/tools/deps/deps.edn)
contains two `:aliases`: `:deps` and `:test`. The latter is a convenience
that adds the `test` folder to your classpath. The former has the following
entries:

* `:replace-paths []`
* `:replace-deps {org.clojure/tools.deps.cli {:mvn/version "0.11.93"}}` (as of August 8th, 2025)
* `:ns-default clojure.tools.deps.cli.api`
* `:ns-aliases {help clojure.tools.deps.cli.help}`

The first two entries ensure that when you use `-X:deps`, your user/project
paths and dependencies are ignored -- so that the `deps` tooling can operate
in a clean environment without any interference from your local setup.

The `:ns-default` entry is what allows `-X:deps tree` to run, without needing
to specify the namespace explicitly (so it executes `clojure.tools.deps.cli.api/tree`).

The `:ns-aliases` entry allows you to use the `help` alias instead of the full
namespace when invoking functions within `clojure.tools.deps.cli.help`.

The `help` namespace provides two public functions:

* `dir` -- lists the (public) functions available in a namespace
* `doc` -- displays the docstrings for (public) functions in a namespace

```
> clojure -X:deps help/dir :ns help
dir
doc
```

We specify `:ns` so that the `help` alias (namespace) is used when looking up
the functions. Without `:ns`, we get the functions from the CLI API namespace
instead:

```
> clojure -X:deps help/dir
aliases
find-versions
git-resolve-tags
list
mvn-install
mvn-pom
prep
tree
```

We can use `help/doc` on a whole namespace or just a specific function:

```
> clojure -X:deps help/doc :ns help :fn help/dir
-------------------------
clojure.tools.deps.cli.help/dir
([{:keys [ns], :as args}])
  Prints a sorted directory of public vars in a namespace. If a namespace is not
  specified :ns-default is used instead.
```

## aliases

Let's see what that `aliases` function is about:

```
> clojure -X:deps help/doc :fn aliases
-------------------------
clojure.tools.deps.cli.api/aliases
([params])
  List all aliases available for use with the CLI using -M, -X or -T execution
  (note that some aliases may be usable with more than one of these). Also, the
  deps.edn sources of the alias are specified.
...
```

The docstring is much longer than that, and it shows you how to exclude the
root, user, and/or project `deps.edn` from consideration. For example, if we
want to see the aliases available from just the root and project deps, we can
run:

```
> clojure -X:deps aliases :user nil
:deps (root)
:test (root)
:build (project)
:fast (project)
:serve (project)
```

There are `:deps` and `:test` as mentioned above from the root file and for
my blog project (which uses Cryogen), there are `:build`, `:fast`, and `:serve`
aliases.

If the same alias is defined in multiple files, you'll see that in the output
(this was run in the `next.jdbc` project):

```
> clojure -X:deps aliases
...
:test (root, user, project)
...
```

This tells me there is a `:test` alias in the root (as noted above), and in my
user `deps.edn`, and in the project itself -- the latter version will be used.

=====
find-versions
git-resolve-tags
list
mvn-install
mvn-pom
prep
tree
=====


## build.clj help

The mighty `:deps` alias can help you learn about the `build.clj` in a project.

For example, if we `git clone` the `next.jdbc` project, we can run the
following command to see what tasks are defined:

```
> clojure -T:deps:build help/dir
deploy
jar
```

We need both the `tools.deps` library and the `build` namespace on the classpath,
and we need to use `-T` so that the current directory is on the classpath so
that the `build.clj` file can be found.

We could also use the more verbose `clojure -A:deps -T:build help/dir` here.

Now, we can use `help/doc` to see the full docstrings for the namespace and those
functions:

```
> clojure -T:deps:build help/doc
next.jdbc's build script.

  clojure -T:build jar
  clojure -T:build deploy

  Run tests via:
  bb test

  For more information, run:

  clojure -A:deps -T:build help/doc

-------------------------
build/deploy
([opts])
  Deploy the JAR to Clojars.
-------------------------
build/jar
([opts])
  Build the JAR file.
```

## Project help

Since `-X:deps` replaces the paths/deps to avoid interference from your
local setup, you cannot use it directly to get help for your project's
source namespaces:

```
> clojure -X:deps help/dir :ns next.jdbc
Execution error (FileNotFoundException) at clojure.tools.deps.cli.help/dir (help.clj:76).
Could not locate next/jdbc__init.class, next/jdbc.clj or next/jdbc.cljc on classpath.
```

However, the CLI allows you to specify extra `deps.edn` data on the command-line
so we can add our project back in like this:

```
> clojure -Sdeps '{:deps {this/project {:local/root "."}}}' -X:deps help/dir :ns next.jdbc
active-tx?
execute!
execute-batch!
execute-one!
get-connection
get-datasource
on-connection
on-connection+options
plan
prepare
transact
with-logging
with-options
with-transaction
with-transaction+options
```

We can make that cleaner by adding an alias to our user `deps.edn` file:

```clojure
  :this {:deps {this/project {:local/root "."}}}
```

Now we can run:

```
> clojure -X:deps:this help/dir :ns next.jdbc
active-tx?
execute!
execute-batch!
execute-one!
...
```

> Note: I have `:this` in my
[dot-clojure `deps.edn` file](https://github.com/seancorfield/dot-clojure/blob/b3076b6b32de99220e904478b3fcb6c3ddfe6c66/deps.edn#L8).
