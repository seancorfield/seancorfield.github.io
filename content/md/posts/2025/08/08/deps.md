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

This tells us there is a `:test` alias in the root (as noted above), and in the
user `deps.edn`, and in the project itself -- the latter version will be used.

## find-versions

When you add a new dependency to your project, how do you figure out the most
recent version so that you can specify that in `deps.edn`? Do you look in the
project README? Do you look on Clojars? Do you look at the releases (or tags)
page on GitHub?

Why not use a tool you already have right there on the command-line?

```
> clojure -X:deps help/doc :fn find-versions
-------------------------
clojure.tools.deps.cli.api/find-versions
([{:keys [lib tool n], :or {n 8}, :as args}])
  Find available tool versions given either a lib (with :lib) or
  existing installed tool (with :tool). If lib, check all registered
  procurers and print one coordinate per line when found.

  Options:
    :lib  Qualified lib symbol
    :tool Tool name for installed tool
    :n    Number of coordinates to return, default = 8, :all for all
```

For example, what version of Hiccup should we use?

```
> clojure -X:deps find-versions :lib hiccup/hiccup
Downloading: hiccup/hiccup/maven-metadata.xml from clojars
{:mvn/version "2.0.0-alpha1"}
{:mvn/version "2.0.0-alpha2"}
{:mvn/version "2.0.0-RC1"}
{:mvn/version "2.0.0-RC2"}
{:mvn/version "2.0.0-RC3"}
{:mvn/version "2.0.0-RC4"}
{:mvn/version "2.0.0-RC5"}
{:mvn/version "2.0.0"}
```

Less useful perhaps, but you can also use this to lookup versions of tools
you have installed (with `clojure -Ttools install...`):

```
> clojure -X:deps find-versions :tool tools :n 3
{:git/tag "v0.3.2", :git/sha "886f893"}
{:git/tag "v0.3.3", :git/sha "2f4d299"}
{:git/tag "v0.3.4", :git/sha "0e9e6c8"}
```

## git-resolve-tags

This is a weird one. It takes no arguments: it reads your (project) `deps.edn`
file and if you have `:git/tag` versions with no `:git/sha`, it will
**rewrite your `deps.edn` file** to add the appropriate `:git/sha` to those
`:git/tag` versions.

That rewrite will _remove any comments_ in your `deps.edn` and completely
reformat it, likely changing the order of any hash map keys in it.

I cannot imagine using this on any of my projects, since I often have comments
in `deps.edn` and I generally take care to order my `:aliases` alphabetically.

## list

In its simplest form, this produces an alphabetical list of the libraries
that your project depends on, including transitive dependencies, along with
the version selected and an indication of the license for each library.

```
> clojure -X:deps help/doc :fn list
-------------------------
clojure.tools.deps.cli.api/list
([params])
  List all deps on the classpath, optimized for knowing the final set of included
  libs. The `tree` program can provide more info on why or why not a particular
  lib is included.
...
```

The license information can omitted (`:license :none`) or expanded
(`:license :full`). You can get the output as EDN if you want (`:format :edn`).

You can use the `:aliases` option to show all your test dependencies (for
example, from the `next.jdbc` project):

```
> clojure -X:deps list :aliases '[:test]' | head
camel-snake-kebab/camel-snake-kebab 0.4.3  (EPL-1.0)
com.google.protobuf/protobuf-java 4.31.1  (BSD-3-Clause)
com.h2database/h2 2.3.232  (MPL 2.0)
com.mchange/c3p0 0.11.2  (LGPL-2.1-or-later)
com.mchange/mchange-commons-java 0.3.2  (GNU Lesser General Public License, Version 2.1)
com.microsoft.sqlserver/mssql-jdbc 12.10.0.jre11  (MIT)
com.mysql/mysql-connector-j 9.4.0  (The GNU General Public License, v2 with Universal FOSS Exception, v1.0)
com.zaxxer/HikariCP 7.0.1  (Apache-2.0)
commons-codec/commons-codec 1.17.1  (Apache-2.0)
commons-io/commons-io 2.16.1  (Apache-2.0)
```

=====
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
