{:title "tools.build",
 :date "2021-08-02 22:10:00",
 :tags ["clojure" "tools.build" "jdbc"]}

With the recent release of [`tools.build`](https://clojure.org/guides/tools_build),
I wanted to provide a quick example of using it for a CI-like pipeline.

`tools.build` is focused on "building" things and when the subject has come up
on Slack, the feedback has been that the CLI already has a good story for running
tests etc, and the consensus seems to be that running multiple CLI commands is
the intended usage.

That consensus has never stopped me from wanting something more integrated.
I'd like to be able to run a single CLI command that runs tests and if they
pass then create a JAR file, ready to deploy -- as a minimum CI-like pipeline.

`tools.build` provides `create-basis`, `java-command`, and `process` functions
so that you can fairly easily run arbitrary `java` commands in a subprocess.
That makes it fairly easy to run your tests from a `build.clj` script:

```clojure
(defn run-tests
  [_]
  (let [basis    (b/create-basis {:aliases [:test]})
        combined (t/combine-aliases basis [:test])
        cmds     (b/java-command {:basis     basis
                                  :java-opts (:jvm-opts combined)
                                  :main      'clojure.main
                                  :main-args ["-m" "cognitect.test-runner"]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit)
      (throw (ex-info "Tests failed" {})))))
```

As a concrete example of this, I've added a [`build.clj` script to `next.jdbc`](https://github.com/seancorfield/next-jdbc/blob/develop/build.clj)
so that I can run:

```bash
clojure -T:build ci
```

and have it run tests and, if they pass, go ahead and create the `pom.xml` and
build the JAR file for the project based on MAJOR.MINOR.COMMITS for the version.

Deploying to Clojars is then a single CLI command:

```bash
clojure -X:deploy :artifact target/next<TAB>
```

this will autocomplete to `target/next.jdbc-1.2.nnn.jar` where `nnn` is the number
of commits. A more confident workflow would be to run `deps-deploy` via
`create-basis`, `java-command`, and `process` to automatically deploy the JAR
to Clojars as part of the `ci` process.

_Update: As of August 12th, [HoneySQL also has `build.clj`](https://github.com/seancorfield/honeysql/blob/develop/build.clj) and the `ci` function there runs the `readme` tests, Eastwood, ClojureScript tests, and multi-version Clojure testing, before building the JAR file!_
