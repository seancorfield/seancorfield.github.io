---
layout: post
title: "Rebooting Clojure"
date: 2016-01-29 22:30:00
comments: true
categories: [clojure]
---
We switched from [Leiningen](http://leiningen.org/) to [Boot](http://boot-clj.com/). What is Boot and why did we switch?<!-- more -->

## Leiningen

Before we talk about Boot, let's first talk about Leiningen. Leiningen describes itself as being "for automating Clojure projects without setting your hair on fire" and claims to be "the easiest way to use Clojure". Rightly so. Leiningen hides all of the messy Maven-y dependency stuff, makes it drop-dead simple to package your applications and libraries as JAR files, makes it easy to run Clojure and provides a nice REPL experience, as well as integrating well with all the editors that Clojurians enjoy.

For a long time, Leiningen was the only game in town, and when we started using Clojure at World Singles, it was the fundamental basis of everything we did with parentheses. We started with a 1.x version of Leiningen, we upgraded to 2.x and we constructed a fair bit of our build / deploy / execute chain around it.

We wrote a couple of Leiningen plugins to deal with some "interesting" use cases in our environment: one to copy just dependencies to a specific target folder, one to set up a browser environment to run multiple [clj-webdriver](https://github.com/semperos/clj-webdriver) tests in a single browser session.

Over time we evolved a fairly large Ant script and a bunch of shell scripts but we really wanted to do more of that automation with Leiningen -- since it's all about "automating Clojure projects" -- but the reality of writing Leiningen plugins for general automation doesn't quite live up to the promise. Between the declarative nature of `project.clj` and overall framework in which plugins must execute, there's a lot more than just "writing Clojure" and there are evaluation restrictions around the processes. Bottom line, we just didn't get as far as we'd have liked with Leiningen.

## Early Boot

Going back to the second ever [ClojureBridge](http://www.clojurebridge.org/) workshop. I was organizing this and at teacher training we were going over the curriculum that had been built for the first workshop and it used an early version of Boot. Unfortunately, it was a very rough experience and virtually unusable on Windows. As my first exposure to Boot, it was less than ideal and it put me off exploring it any further.

Looking back, I'm sure Boot was well-designed from the start and the design choices made were all solid -- Boot was created by some very smart people -- but I was less than charitable about Boot and unnecessarily vocal. I didn't take Boot very seriously.

## Boot 2.5.0

Coming back to the present, Boot 2.5.0 was released in mid-December 2015. I owed it a serious second look. I was impressed. For starters, the installation process had become hella slick and the documentation looked really good. My early exploration was to try Boot on Windows 10, Mac OS X, and Linux for both the REPL experience and the basic project build process. One of the most striking differences from Leiningen was that "it's just Clojure": the build script is straightforward executable Clojure, and tasks are just functions, and everything is based on an abstraction of a fileset. Another striking difference is that composability is baked right into the core of Boot: tasks are like Ring middleware and can wrap each other in a pipeline that allows tasks to take control both before and after other tasks in the pipeline.

After a promising "first look", I decided to convert a couple of our core processes to run under Boot so that I could see what a real `build.boot` would look like in our environment. The only fly in the ointment for us was that Boot uses a single JVM and we were used to being able to compute our JVM options in `project.clj` (inside the first Leiningen JVM) for use by the application JVM (Leiningen's second JVM). By contrast, Boot uses environment variables to set up a single JVM environment and is a minimal intrusion itself. Boot further mitigates its own intrusion by allowing you execute code in a "pod" with its own isolated dependencies.

We opted for a wrapper shell script to deal with our JVM environment computation. Once we had that in place, having just a single JVM up and running was a win all round: every process started up faster, our task and automation code was simpler.

We decided on a two week spike to replace Leiningen with Boot across our entire project, and lift all of our executable processes up as Boot tasks. As of January 21st, we have Boot as an integral part of our production infrastructure. We're happy.

In the next blog post, I'll talk about some of the specifics in our Boot infrastructure and the tasks we're writing. As a teaser, you can look at [boot-expectations](https://github.com/seancorfield/boot-expectations) and [boot-new](https://github.com/seancorfield/boot-new).
