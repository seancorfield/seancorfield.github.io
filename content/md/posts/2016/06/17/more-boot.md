{:title "More Boot",
 :date "2016-06-17 20:00:00",
 :tags ["boot" "clojure"]}
Back in February I talked about [boot-new](https://corfield.org/blog/2016/02/02/boot-new/) and talked about a "future 1.0.0 release". We're not there yet, but [generators](https://github.com/seancorfield/boot-new#boot-generators) got added in release 0.4.0 and, in the four minor releases since, the focus has been on refactoring to match the core [Boot](http://boot-clj.com/) task structure and improving compatibility with Leiningen templates. At World Singles, we've continued to extend our usage of Boot until we have only a couple of Ant tasks left and we expect those to be within Boot's reach soon. In this post, I want to cover some of the things we've been doing with Boot recently.<!-- more -->

I feel I should start with an apology for the "radio silence" since February -- it's a combination of work being extremely engaging (and busy!) and some aspects of my personal life going somewhat to hell in a handbasket... But things have improved lately (thankfully!) and I hope to be more regular in my blogging (I certainly have a decent queue of article ideas in my head!).

At work, we depend on a lot of libraries, both Java and Clojure, and we've tended to be a bit cavalier about conflicts in transitive dependencies. Sure, we check that the desired version of any given library ends up being selected (via `boot ... show -p`) but we've tended to only add `:exclusions` where absolutely necessary to avoid specific cases of the wrong version being selected. That had served us fairly well until we started to experiment with [`clojure.spec`](https://clojure.org/about/spec), introduced in the Clojure 1.9.0 Alpha builds. Along with the new namespace, a number of new predicates have been introduced, in `clojure.core`, across several of those Alpha builds with names that are common enough that they conflict with user-defined predicates in a number of libraries. Normally, that wouldn't matter much: you'd get:

    WARNING: ... already refers to: #'clojure.core/... in namespace: ..., being replaced by: #'...

and your program would continue to function normally. At some point, the library maintainer would add the new symbol to the `:exclude` list in their `:refer-clojure` clause, you'd update, and life would be good.

Something strange was happening for us, however. Instead of the warnings, the user-defined function would just silently vanish and the program would fail trying to call an unbound `Var`. Weird. I found a workaround for one case (where a `defn` was inside a `do` in a `.cljx` file) but I couldn't reproduce the problem as a test case (and nor could Kevin Downey, who was skeptical enough of my line of reasoning to spend time trying to help me find the real reason -- thank you sir!). I ran into a couple more similar bugs. It became clear Kevin was right (I feel like saying "of course!" here) and I needed to approach the problem differently. A few conversations happening around the same time -- including one between Rich Hickey and Micha Niskin in the [#boot channel on Slack](https://clojurians.slack.com/messages/boot/) -- led me to focus on version conflicts in our transitive dependencies... Fortunately, Boot provides an easy way to detect if there are any conflicts (even if they resolve "correctly") and Micha shared some code that I turned into the following Boot task:

    (deftask check-conflicts
      "Verify there are no dependency conflicts."
      []
      (with-pass-thru fs
        (require '[boot.pedantic :as pedant])
        (let [dep-conflicts (resolve 'pedant/dep-conflicts)]
          (if-let [conflicts (not-empty (dep-conflicts pod/env))]
            (throw (ex-info (str "Unresolved dependency conflicts. "
                                 "Use :exclusions to resolve them!")
                            conflicts))
            (println "\nVerified there are no dependency conflicts.")))))

I added this to our build pipeline and `Unresolved dependency conflicts.` became a very familiar sight until I'd added enough `:exclusions` to our dependencies to finally see `Verified there are no dependency conflicts.`. Was I glad to see _that_ at the end of the day!

One of the things I noticed was the huge number of libraries that pull in some old version of Clojure itself as a transitive dependency, so I ended up adding a global exclusion to our `build.boot` file:

    (set-env! :exclusions ['org.clojure/clojure])

A few discussions ensued on Slack about whether projects should declare `org.clojure/clojure` as a `"provided"` dependency but there was no consensus, unfortunately. FWIW, I'm in the `"provided"` camp.

The other big shift we've made at work is to adopt [Stuart Sierra's Component](https://github.com/stuartsierra/component). This might seem like a no brainer for many people but most of our Clojure code operates inside a large non-Clojure web application and started life as a small set of libraries. Component is a great fit for "whole program" code but it was [hard see how to fit it into our model](https://github.com/stuartsierra/component#disadvantages-of-the-component-model), especially since (unfortunately) we'd gradually sprouted quite a bit of global state (go on, boo all you like!). Having talked to a number of people who introduced Component into "legacy" Clojure applications, I began to suspect that as long as the `start` and `stop` lifecycle methods managed that global state, it wouldn't be too painful to introduce it piecemeal into our code base.

We still have global state (go on, boo again!) but we're slowly moving away from it now and we have some processes that have "inverted" and are now entirely managed by Component. One of the side effects of moving to Component is that you need to `start` your system after you fire up your REPL. It's great being able to `start` and `stop` your application inside the REPL (we're not quite at the Nirvana of refreshing all our namespaces due to that pesky global state, but we're on track). On the other hand, after years of just firing up a REPL and going to work, it takes some getting used to and seeing the message that our database connections have not been started became fairly familiar for the first few days. OK, weeks.

One of the nice things about Boot is that if you want functions available in the REPL, in your `boot.user` namespace, you simply define them in your `build.boot` file. This allows us to follow [Stuart's "Clojure Reloaded" workflow](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded) with minimal effort. Having the machinery in `build.boot` to work with Component also lends itself to using that lifecycle in your tasks. That's good hygiene and encourages you to think about packaging functionality into Components which fail into a natural `start` / `stop` rhythm around the Boot task pipeline architecture which also has a natural start (followed by other tasks in the pipeline) and then a natural stop. In some ways, Boot and Component are "made for each other":

    (deftask my-component-task
      "A Component-based task."
      [...]
      (let [my-task (make-task-component ...)]
        (fn [next-handler]
          (fn [fileset]
            (let [app (component/start my-task)]
              ... ; work is done with app here
              (let [fileset' (... fileset)
                    fileset' (commit! fileset')
                    result   (next-handler fileset')]
                ... ; side effects are performed with app here
                (component/stop app)
                result)))))))

The final piece of Boot-ness I want to mention in this post is how we've shifted some of our general shell commands from Ant to Boot. Here's an example of how we invoke Grunt from Boot -- a similar pattern is followed for other shell commands:

    (defn ws-root []
      (System/getProperty "user.dir"))

    (deftask grunt
      "Run Grunt (in www)."
      []
      (require '[clojure.java.shell :as sh])
      (let [sh (resolve 'sh/sh)]
        (with-pass-thru fs
          (let [{:keys [exit out err]} (sh "grunt" :dir (str (ws-root) "/www"))]
            (println out)
            (when-not (zero? exit)
              (println err)
              (throw (ex-info "Grunt failed." {:exit exit})))))))

I'll probably post about Boot again when we've finally laid Ant to rest, and talk about any interesting things we run into during that last sprint.
