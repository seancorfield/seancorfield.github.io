{:title "Lessons of Open Source Maintenance",
 :date "2019-08-11 10:00:00",
 :draft? true,
 :tags ["clojure" "jdbc"]}
In [Next.JDBC to 1.0.0 and Beyond!](https://corfield.org/blog/2019/07/04/next-jdbc/) I talked about "a lot of lessons learned from eight years of maintaining `clojure.java.jdbc`" but didn't enumerate them. As Jakub Holy said in the comments there "it is possible to derive some" but it seems like a good blog post to be explicit about those as well as the others that might not be so obvious.

Looking over the history of `clojure.java.jdbc`, I think there are probably a dozen separate considerations that stand out that are worthy of further discussion. Some of these are "lessons" with a clear decision to be made and a forward direction to advance toward "better" programming practices. That said, I'm going to present most of these as trade offs rather than trying to claim one approach is empirically better than another approach, even though I have developed strong opinions on several of them<!-- more -->

## Dynamic (Global) State

This is a trade off between using global variables for convenience against the concerns of thread safety and the inconvenience / overhead of passing data as parameters down the entire call tree.

When I inherited `clojure.java.jdbc`, it relied on a global dynamic variable `*db*` as the "current connection" to the database. It was convenient: you set up a connection at the start of your program and then you could forget about it -- all calls just implicitly used the current connection.

This is an approach taken by several libraries that have some global, managed state. [CongoMongo](https://github.com/congomongo/congomongo) which I maintained for several years worked that way. Early versions of [Monger](https://github.com/michaelklishin/monger) and current versions of [Mount](https://github.com/tolitius/mount) also.

It's easy. You don't have to worry about passing values through your call chain. Adding a call into such a library doesn't require you to laboriously go back through all your callers and add a new argument everywhere. But it has some fairly significant downsides. It is much harder to develop code that relies on multiple instances of whatever is in that global state. It is much harder to test such code because of the global environment you must set up. It is much harder to reuse such code and combine libraries that assume they can "own" such global state.

I ran into all of those problems and eventually changed `clojure.java.jdbc`'s API to require the "db spec" be passed into every single function that needed it -- and remove the global `*db*` variable. It was a major breakage of the API and one I would handle very differently today (see **Deprecation and Breaking Changes** below), but it was also a lesson that has made me extremely suspicious of global state (and a big reason why I prefer [Component](https://github.com/stuartsierra/component) over Mount).

Accordingly, `next.jdbc` requires "connectables" be passed as arguments and eschews any global state.

## Keyword Arguments

This is a trade off between readability and convenience against the concerns of composability.



keyword arguments and composability

## Implementation Architecture

This is an interesting one and I think "best practices" has shifted back and forth several times here over the years.

`impl` namespaces vs private/public

## Deprecation and Breaking Changes

I'm going to cast this one as a question: if you publish a "bad" API, should you _replace_ it with a "better" one, or should you just add the "better" one alongside the "bad" one?

breaking changes / deprecation - removal of features

## Backward Compatibility

I'm also going to cast this one as a question: separate from breaking changes in the API itself, how much backward compatibility should you maintain with older versions of Clojure (or Java)?

supporting older versions of Clojure - new functions, new protocols, new abstractions

## Simple? Or Easy?

This is the classic trade off that Rich Hickey has been talking about for years and which infuses so much of Clojure's design, and that of many libraries.

minimal API - simple vs easy

## Syntactic Sugar

As a follow-on from Simple vs. Easy, if you go with a small, focused, _simple_ API, how much "easiness" should you add on, and how much are you willing to support and maintain forever?

convenience vs core

## Domain Specific Languages

A variant of syntactic sugar, DSLs are another layer on top of a basic, simple API, that can add "ease of use" but also add complexity (and, again, a support and maintenance burden).

DSLs

## Consistency of the API

This might not seem like a trade off -- you might think "of course you want consistency!" -- but there can be good reasons why different parts of the API might be better off exposing different machinery, or behaving differently under the hood.

consistency of behavior / passing options

## Extension Points

Designing an API that is arbitrarily extensible is extremely hard. Each extension point has the potential to add complexity and a support and maintenance burden while also making your library easier to use. How many extension points can you offer and still remain "simple"?

Extension points - multimethods, protocols, HOFs

## Performance

Most of the time, simple Clojure code is "fast enough", but choosing performance as a goal means making all sorts of trade offs, some of which will bleed into your API design.

performance: hash maps, structs, records, protocols

## Transducers

I don't know whether I'd really categorize this as a trade off per se, but it definitely represents a shift of idioms in the Clojure world and it dates back, for `clojure.java.jdbc`, to [JDBC-99](https://clojure.atlassian.net/browse/JDBC-99).

transducers
