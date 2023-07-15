{:title "deps.edn and monorepos X (Polylith)",
 :date "2022-12-07 15:00:00",
 :tags ["clojure" "monorepo" "polylith" "tools.build"]}

This is part of an ongoing series of blog posts about our ever-evolving use of the Clojure CLI,
`deps.edn`, and [Polylith](https://polylith.gitbook.io/), with our monorepo at
[World Singles Networks](https://worldsinglesnetworks.com).<!--more-->

### The Monorepo/Polylith Series

_This blog post is part of an ongoing series following our experiences with our Clojure monorepo and our migration to Polylith:_

1. _[deps.edn and monorepos](https://corfield.org/blog/2021/02/23/deps-edn-monorepo/)_
2. _[deps.edn and monorepos II](https://corfield.org/blog/2021/04/21/deps-edn-monorepo-2/)_
3. _[deps.edn and monorepos III (Polylith)](https://corfield.org/blog/2021/06/06/deps-edn-monorepo-3/)_
4. _[deps.edn and monorepos IV](https://corfield.org/blog/2021/07/21/deps-edn-monorepo-4/)_
5. _[deps.edn and monorepos V (Polylith)](https://corfield.org/blog/2021/08/25/deps-edn-monorepo-5/)_
6. _[deps.edn and monorepos VI (Polylith)](https://corfield.org/blog/2021/10/01/deps-edn-monorepo-6/)_
7. _[deps.edn and monorepos VII (Polylith)](https://corfield.org/blog/2021/10/13/deps-edn-monorepo-7/)_
8. _[deps.edn and monorepos VIII (Polylith)](https://corfield.org/blog/2021/11/28/deps-edn-monorepo-8/)_
9. _[deps.edn and monorepos IX (Polylith)](https://corfield.org/blog/2022/11/05/deps-edn-monorepo-9/)_
10. _[deps.edn and monorepos X (Polylith)](https://corfield.org/blog/2022/12/07/deps-edn-monorepo-10/) (this post)_
11. _[deps.edn and monorepos XI (Polylith)](https://corfield.org/blog/2023/07/15/deps-edn-monorepo-11/)_

## Part X

Since my last post, a month ago, our Polylith migration has continued strongly,
adding eight more components with seven more implementations -- so that means
we have another swappable implementation which I'll talk about below -- and
two more bases and one more project. We've migrated 114,114 lines of code,
which is nearly 86% of our total codebase. The remaining code has two fairly
large applications and a shared subproject with almost twenty namespaces and
a large API surface area. It's also under very active development right now.
That final stretch of the migration will be a bear!

I feel like this part of the series could be subtitled "Polylith to the Rescue"
as it has helped us quickly deal with a number of unforeseen and somewhat
"last minute" problems that cropped up for us this past month.

To set the scene for that, we decided that since Jetty 9 was out of community
support (as of June 2022) and it
[doesn't look like Ring will update to Jetty 10/11 any time soon](https://github.com/ring-clojure/ring/issues/439),
we would switch to [Ning Sun's Jetty adapter](https://github.com/sunng87/ring-jetty9-adapter)
which, despite its name having `jetty9` in it, is a Jetty 11 adapter and it
provides a nice, built-in WebSocket API. The latter was very appealing to us
because we'd just wrestled with adding WebSocket support via Java interop on
top of Jetty 9 and it was a pretty nasty experience (mostly because Jetty
supports two completely different implementations with different APIs and the
documentation sucks). I was very happy to jettison that code, in favor of
Ning Sun's Clojure API!

### Polylith External Test Runner

But before we get to the (multiple) "rescues", I want to talk about an
interesting new feature that has been added to Polylith: the ability to have
an [external test runner](https://github.com/polyfy/polylith/issues/260) that
avoids the classloader and memory issues I mentioned in my last post. Although
those issues have had some mitigating Pull Requests merged in, our codebase is
large enough and complex enough that we were still running into some issues,
and we had to use `2x` BitBucket Pipelines instances to run `poly test`.

Although I created an initial Pull Request to refactor `poly test` to support
an external test runner, the real heavy lifting was done by
[Furkan Bayraktar](https://github.com/furkan3ayraktar) in an
[epic Pull Request](https://github.com/polyfy/polylith/pull/263) that garnered
a lot of discussion and was a far more idiomatic solution than my original
attempt! Thank you, Furkan!

Here's my [external test runner](https://github.com/seancorfield/polylith-external-test-runner)
that leverages this new feature. `poly test` determines which projects need
testing and which bricks' tests need to be run, then it hands control over to
the test runner which spawns a process for each project, to run setup, tests,
and teardown in complete isolation from the Polylith process itself. This
removes all the issues with classloader isolation and memory consumption
since each project is tested separately. There's an overhead here in terms
of performance since a new Clojure process will be spun up for each project,
but this is intended for large projects where memory use is more of a concern.

Polylith already had a pluggable test runner system, so you could use the
[Kaocha test runner](https://github.com/imrekoszo/polylith-kaocha/) in-process
instead of the built-in default test runner. This extends that functionality
to support out-of-process test runners as well.

Switching to this test runner allowed us to reduce our BitBucket Pipelines
instance back to `1x`, cutting our CI costs in half. But we weren't done!
Read on for more CI cost savings!

### Polylith to the Rescue, Part 1

As noted above, we switched from Jetty 9 to Jetty 11 this month and it all
went very smoothly, until we went to deploy a new build from staging to production.
We have an internal "admin app" that manages all of the configuration and
nearly all of the content for our dating platform. It runs in both our staging and
production tiers, with "promote to production" functionality on staging and,
mostly, moderation functionality on production. In addition to promoting
various types of translated content, we added the ability for the business
team to promote build artifacts whenever they're happy with new functionality
on staging. Artifact promotion triggers an automated deployment across the
production cluster, which we handle by uploading the JAR to S3 and creating a
flag that indicates a new deployment is needed. That S3 upload was handled by
[Cognitect's AWS API](https://github.com/cognitect-labs/aws-api/).

Unfortunately, it [isn't compatible with Jetty 11](https://github.com/cognitect-labs/aws-api/issues/181)
and I didn't notice that until we went to do our first promotion after the
upgrade (the artifact uploader tests run in a context that doesn't have
Jetty at all so they passed).

Time was fairly critical at this point, because -- unusually for us -- we were
juggling some infrastructure changes and renaming as part of the same overall
build.

"Polylith to the Rescue!" -- I copied our `web-server` component to
`web-server-9`, edited the dependencies to switch that implementation back to
the standard Jetty 9 Ring adapter, updated `projects/wsadmin/deps.edn` to use
that implementation instead of the regular `web-server` one, and triggered a
build and deploy of the "admin app" to staging.

We were able to continue our deployment with only about a 30 minute delay!

> Note: we've since switched from the Cognitect AWS API to [Michael Glasemann's awyeah-api](https://github.com/grzm/awyeah-api) which doesn't depend on the Jetty 9 HTTP client and is therefore compatible with our Jetty 11 codebase (so we deleted that `web-server-9` component).

### Polylith to the Rescue, Part 2

We have a legacy app, that predates our switch to Clojure and still requires
JDK 8 to run. We're probably going to rewrite it completely at some point so
migrating it completely to Clojure is low on our list of priorities. In fact,
we haven't even deployed a new version of it for almost ten months. But it
does rely on some shared Clojure code (called from ColdFusion -- I kid you not!)
and we've done a **lot** of refactoring on the Clojure side since then. I
decided it was time to update it so that -- if necessary -- we could build and
deploy a new version of it.

The last time I worked on it, the ColdFusion code depended on two of our
"kitchen sink" Clojure subprojects which have since been broken up and
migrated to Polylith. The app had its own legacy subproject with `deps.edn`
and had no associated "build" project, since it was deployed from a tarball
of source code (mixed CFML and Clojure). Figuring out exactly which Polylith
`components` it now depended on was... well, a bit of a nightmare to be
honest. I spent a couple of days, updating namespace references in the CFML
code and updating `deps.edn`, trying to get the app to start up and run
cleanly but it was pretty frustrating.

Overnight, I had an idea: use Polylith to figure out the dependencies!

The next day, I moved the app's legacy subproject into a Polylith base and
added a project for the "build" aspect -- and `poly check` told me exactly
which bricks were missing from the new project's `deps.edn` file!

When the app starts up, it runs `clojure -Spath -A:cfml-app` to get the
classpath of all the Clojure code and its dependencies, so I modified that
alias in our main `deps.edn` to depend on the new project (instead of the
old subproject) and the app came up on the first attempt!

A nice side-effect of this was that the legacy Clojure code that is only used
by this legacy CFML app is automatically tested by Polylith when we change
anything that might affect it, so we can be more confident that we haven't
broken that app (and haven't messed up its dependencies) for any future
deployment of it we decide to do.

### Polylith to the Rescue, Part 3

I did slightly gloss over one additional problem I ran into while getting that
legacy app up and running again on JDK 8: the fact that we'd switched from
Jetty 9 to Jetty 11 -- and that's not compatible with JDK 8.

Although the CFML code runs on a different web server (Undertow), we reuse
some of the web server adjacent functionality from our Clojure code base, such
as health checks for our monitoring services. That brought a little bit of
Jetty 11 into the mix and caused the app to fail on JDK 8.

As with the Cognitect AWS API issue above, the solution was to create an
alternative implementation of our `web-server` component that stubbed out
the part that relied on Jetty 11 so we could reuse it on JDK 8 without having
to refactor it into separate pieces and update all the code that referred to
those pieces.

### Polylith to the Rescue, Part 4

And finally, we come to the CI cost savings I hinted at earlier. Polylith
is already giving us shorter CI cycles since it only tests code that could
be affected by changes (since the last staging deploy) -- although if you
change a component that is very widely used it can cause a bit of a cascade
of testing which means some CI cycles might be longer than just testing all
the components on their own (Polylith runs tests for each project that might
be affected). In general, our CI cycles for Pull Requests are down to around
five minutes rather than the fifteen they used to be before Polylith.

Being able to switch back to a regular instance instead of the `2x` instance,
now that we've switched to the external test runner, has also cut costs.

But we were still building all the project artifacts and deploying them all
to staging which meant an extra 10-15 minutes for all merges to the main
branch.

"Polylith to the Rescue!" here as well, because you can ask Polylith to
print a list of projects that are affected by changes, so you know which
ones to rebuild and deploy. This is explained in the
[Continuous Integration](https://polylith.gitbook.io/poly/workflow/continuous-integration)
section of the Polylith documentation:

```bash
poly ws get:changes:changed-or-affected-projects since:release skip:dev
```

We want to run that programmatically and capture the output, and read it as
EDN, so we also need `color-mode:none` to produce plain text.

We invoke this in our `build.clj` file using `tools.build` to run that
as a `java` subprocess, based on the `:poly` alias in our workspace `deps.edn`
file, and capture the output.

```clojure
(defn- changed-projects
  "Produce the list of projects that need building.

  `since` should be `:before-tag` or `:after-tag`"
  [since]
  (let [basis    (b/create-basis {:aliases [:poly]})
        combined (t/combine-aliases basis [:poly])
        cmds     (b/java-command
                  {:basis     basis
                   :java-cmd  (find-java)
                   :java-opts (:jvm-opts combined)
                   :main      'clojure.main
                   :main-args (into (:main-opts combined)
                                    ["ws"
                                     "get:changes:changed-or-affected-projects"
                                     (str "since:"
                                          (case since
                                            :before-tag "release"
                                            :after-tag  "previous-release"))
                                     "skip:dev"
                                     "color-mode:none"])})
        {:keys [exit out err]}
        (b/process (assoc cmds :out :capture))]
    (when (seq err) (println err))
    (if (zero? exit)
      (edn/read-string out)
      (throw (ex-info "Unable to determine changed projects"
                      {:exit exit :out :out})))))
```

The `since` argument allows us to get changes before or after we've added the
new release tag in CI (we test first, then tag, then build -- so the tag is
only applied to passing builds and can then be incorporated into the JAR file).

Then we have a task like this to build all the artifacts that changed:

```clojure
(defn build-all-uberjars
  "Build uberjars for all changed artifacts."
  [params]
  (let [projects (-> (changed-projects (get params :since :before-tag))
                     (set)
                     (set/difference (set billing-build-artifacts)))]
    (uberjars (assoc params :projects projects))))
```

`billig-build-artifacts` is the list of projects that are not yet migrated to
Polylith.

Just in time for the holiday season, Polylith is the gift that keeps on giving!
