{:title "Building On Boot",
 :date "2016-01-30 20:30:00",
 :tags ["boot" "clojure"]}
In yesterday's blog post, [Rebooting Clojure](https://corfield.org/blog/2016/01/29/rebooting-clojure/), I talked about our switch from Leiningen to Boot but, as Sven Richter observed in the comments, I only gave general reasons why we preferred Boot, without a list of pros and cons.

Over the coming weeks, I'll write a series of posts about some of the specifics that worked better for us, as well as some of the obstacles we had to overcome in the transition.

In this post, I'm going to cover some of the pros at a high level as it improved our build / test process.<!-- more -->

I mentioned that we'd evolved a fair size Ant script over time that does most of the heavy lifting of our build / test / deploy process. We'd gradually been replacing parts of that process with Clojure code but it was easier to stick a `-main` function in certain namespaces to call into our code than to turn chunks of our code into Leiningen plugins. A case in point was our database and data migration tasks. We have developed a persistence layer in Clojure, built on top of [clojure.java.jdbc](https://github.com/clojure/java.jdbc), that encapsulates our connection pooling strategies and environment settings. Our migration code was built on top of that. Our Ant script invoked Leiningen to run these `-main` functions at various points. With each of these, we had repeated code to deal with command line arguments, process and environment set up, and then calls to what were, in effect, a series of "tasks".

With Boot, we were able to eliminate a lot of that boilerplate. Boot tasks have command line argument parsing built-in. There's no need to create a `-main` function -- the Boot tasks can call directly into our code. At this point we could easily compose tasks in a pipeline to satisfy any combination of database and data migration scenarios we needed, and we could simplify our Ant script to run a pipeline of Boot tasks as needed: which also meant we only fired up one JVM for that whole part of our build, instead of multiple invocations of Leiningen, each invoking a `-main` function.

We also have some CFML applications in the mix. Because history. We had several tasks accessible via HTTP requests into those CFML applications and those were also invoked by the Ant script because it was easy to do. Over time, we're committed to moving more and more of the CFML functionality down into Clojure (for what I hope are obvious reasons). Some of the tasks we were invoking via HTTP in Ant were already just thin CFML veneers over Clojure functionality, fortunately, so we were easily able to create a Boot task to call that functionality instead of going through CFML via HTTP.

Could we have done all this with Leiningen and plugins? Perhaps. Our experience with writing Leiningen plugins at this point had discouraged us from large scale plugin development. We were able to achieve more in a couple of weeks with Boot than we'd able or willing to attempt with Leiningen in a long time.

There were cons too, of course, and the biggest was that several of Leiningen's built-in tasks and popular plugins, on which we relied heavily, were either absent or extremely different with Boot. I'll cover some of those in my next post.
