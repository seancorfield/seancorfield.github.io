{:title "Instrumenting Clojure for New Relic Monitoring",
 :date "2013-05-01 20:09:04 -0700",
 :tags ["clojure"]}
We've recently started evaluating the [New Relic monitoring service](http://newrelic.com/) at World Singles and when you use their Java agent with your web application container, you can get a lot of information about what's going on inside your application (JVM activity, database activity, external HTTP calls, web transaction traces).<!-- more --> For a CFML application tho', all you tend to get in the web transaction traces is the Servlet entry point, some JDBC SQL reports, and some of the low-level Java libraries (if you're lucky!).

However, we have a mixture of CFML and Clojure, running on the [free open source Railo server](http://www.getrailo.org) so I thought it might be possible to somehow instrument the Clojure code to enable more visibility into our application traces.

This [New Relic blog post talks about custom instrumentation](http://blog.newrelic.com/2012/11/13/setting-up-custom-instrumentation-using-the-new-relic-java-agent/) and shows how you can use method annotations in Java to make specific function calls show up in web transaction traces. (The New Relic documentation, accessible from their monitoring console, provides more detail)

In Java (approximately):

    import com.newrelic.api.agent.Trace;
    
    public class Thing {
    
        @Trace
        public void someMethod() {
            ...
        }
    
    }

So how do you do the same thing in Clojure? Annotations have been supported for quite a while in Clojure but they're very poorly documented and they require some specialized code.

Let's suppose you have this Clojure code:

    (ns my-project.stuff)
    
    (defn some-func [x y z] ...)
    
    (defn another-fn [a b] ...)

We want to annotate both of these functions so they show up in New Relic web transaction traces. You can add annotations in a `deftype` so we have to transform our code quite a bit. First off, you'll need the New Relic JAR as a dependency in `project.clj`:

      :dependencies
        [...
         [com.newrelic.agent.java/newrelic-api "3.10.0"]
         ...]

Check [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cnewrelic-api) for the latest version! _As of September 26th, 2014, it was `3.10.0`._

Then you need to import the `Trace` annotation:

    (ns my-project.stuff
      (:import com.newrelic.api.agent.Trace))

Since we need to use `deftype` we need to define an interface type for the functions we want to instrument. We also need to use Java-compatible names. Here's a first cut:

    (definterface INR
      (some_func [x y z])
      (another_fn [a b]))

Now we can define the implementation type with the annotations. Then we'll need to expose a Clojure API based on that. Let's start by renaming our existing implementations and making them private, so we can call them from the `deftype` with minimal code changes:

    (defn- some-func* [x y z] ...)
    
    (defn- another-fn* [a b] ...)

Now we can write our `deftype` with annotations:

    (deftype NR []
      INR
      ;; @Trace maps to Trace {} metadata:
      (^{Trace {}} some_func  [_ x y z] (some-func* x y z))
      (^{Trace {}} another_fn [_ a b]   (another-fn* a b)))

Here we have an implementation - `NR` - of our interface - `INR` - which provides the two methods. Note that they ignore their first argument (`this`) because the object type is just an artifact of `deftype` for us to add the annotations. Finally, we can reimplement our original API in terms of the new type. In order to do that, we need an instance of our type, and to avoid classloader issues, we'll create a new instance in each call. _In my original blog post I suggested using a private singleton but later discovered that caused problems with classloaders sometimes._

And finally here is our original API reimplemented in a traceable way:

    (defn some-func  [x y z] (.some_func (NR.) x y z))
    
    (defn another-fn [a b]   (.another_fn (NR.) a b))

Now you just need to start your web application container with the JVM option `-javaagent:/path/to/newrelic.jar` (which is your licensed JAR file downloaded from your monitoring console, not the one pulled in by Leiningen based on the dependency we added above!).

When you drill into web transaction traces, you should see `my_project.stuff.NR.some_func` and `my_project.stuff.NR.another_fn` entries in it!
