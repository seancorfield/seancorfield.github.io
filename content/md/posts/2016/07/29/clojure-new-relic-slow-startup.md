{:title "Clojure, New Relic, and Slow Application Startup",
 :date "2016-07-29 20:20:00",
 :tags ["clojure"]}
A couple of years ago, I blogged about [instrumenting Clojure for New Relic monitoring](http://corfield.org/blog/2013/05/01/instrumenting-clojure-for-new-relic-monitoring/) and we've generally been pretty happy with New Relic as a service overall. A while back, we had tried to update our New Relic Agent (used with our Tomcat-based web applications) from 3.21.0 to 3.25.0 and we ran into exceedingly long application start times, so we rolled back and continued on with 3.21.0. Recently, we decided to update the Agent to 3.30.1 to take advantage of advertised performance improvements and security enhancements. Once again we ran into exceedingly long application start times.

An application that took just over four minutes to start up fully with 3.21.0 was taking around forty minutes to start up with 3.30.1 -- an order of magnitude slower!<!-- more -->

Since we really wanted this update, we contacted New Relic Technical Support. Somewhat cryptically, they asked us to try version 3.24.1 -- and that did not exhibit the slow startup -- at which point they acknowledged that they'd had reports that, with some applications, some customers had experienced slow startups since the 3.25.0 release. They asked us to set the logging level to "finest", start the application up on a test machine, and then send them the log file. It was over 230MB(!) and full of Weave violations that the "original bytecode" could not be found. They very quickly traced this to how their instrumentation code tries to decide which classes to trace and which to ignore -- and noted that Clojure creates a lot of `clojure.lang.DynamicClassLoader` instances (about 176,000 warnings in the log files originated from this class!) and, since the instrumentation never finds anything useful to instrument, loaded via those classloaders, they suggested that we tell the Agent to skip them.

As far as I can tell, this is not a documented configuration item (although there is a similar `classloader_excludes` list):
```
  class_transformer:
    classloader_blacklist: clojure.lang.DynamicClassLoader
```
This stops the Agent from examining this classloader and/or the code loaded by it and it dramatically cut the application start times. After adding this to `newrelic.yml`, our applications started up slightly faster than they had with 3.21.0.

So, thank you to Jesse @ New Relic for the swift troubleshooting on this issue! I'm posting this because I couldn't find a solution to the problem via Google -- although I could find people complaining about the problem. Hopefully this will help others using New Relic with Clojure (or other languages that hit the same issue).
