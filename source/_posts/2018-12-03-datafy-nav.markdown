---
layout: post
title: "Clojure 1.10's datafy and nav"
date: 2018-12-03 17:20:00
comments: true
categories: [clojure, jdbc]
---
One of the more mysterious new features in Clojure 1.10 seems to be the pairing of [`datafy` and `nav`](https://github.com/clojure/clojure/blob/master/changes.md#26-datafy-and-nav) (and their underlying protocols, `Datafiable` and `Navigable`). Interest in these new functions has been piqued after Stuart Halloway showed off [REBL at Clojure/conj](https://www.youtube.com/watch?v=c52QhiXsmyI&list=PLZdCLR02grLpMkEBXT22FTaJYxB92i3V3&index=3) (video). Stu presented this functionality as "generalized laziness": `datafy` produces a "data representation" of things and `nav` lets you (lazily) navigate around that data.<!-- more -->

The [REBL](http://rebl.cognitect.com/) "is a graphical, interactive tool for browsing Clojure data". And in Clojure we're used to the concept of "it's just data" so a graphical browser might sound useful but not exactly earthshaking. But REBL is just an example of what can be built with the new functionality in 1.10 and, indeed, Stu's claim of "generalized laziness" is well made but a little hard to grok until you actually build something with the new protocols and functions.

Since I've done exactly that -- with experimental support for [lazy navigation of related records in `java.jdbc`](https://github.com/clojure/java.jdbc/blob/master/src/main/clojure/clojure/java/jdbc/datafy.clj) -- and spent some time on Slack today explaining how it all works, I figured it would be worth writing down in a more permanent place, as a blog post.

The `Datafiable` protocol (new in `clojure.core.protocols`) is defined for `nil` and `Object` to just return those values as-is, and then extended in `clojure.datafy` to cover:

* `Throwable` -- producing a simple hash map by calling `Throwable->map`
* `clojure.lang.IRef` -- producing a vector containing the dereferenced value, with the original metadata from the reference attached to that vector
* `clojure.lang.Namespace` -- producing a hash map that is the data representation of the namespace (`:name`, `:publics`, `:imports`, and `:interns`) with the metadata of the namespace attached
* `java.lang.Class` -- producing the `clojure.reflect/reflect` representation of the class, with an additional member `:name`, and the `:members` of the class grouped by name and sorted

The `Navigable` protocol (new in `clojure.core.protocols`) is defined just for `Object` and, given some sort of object or collection, some sort of "key", and a value, it will just return the value itself.

Based on that, it may not be clear how to implement the protocols or use the functions. The key thing that may not be obvious here is that `datafy` is intended to convert an arbitrary "thing" of any type into a pure Clojure data representation and, from that starting point, you can then use `nav` to "navigate" to a value derived from the original "thing", which you would then process with `datafy` to get a pure Clojure data representation again. Those data representations may contain pieces that satisfy the `Navigable` protocol so that navigation (via `nav`) provides more than just a simple data-level lookup.

Using the `clojure.java.jdbc.datafy` code as an example, you would call `query` (from that new namespace) and get back a result set. That looks like a sequence of hash maps (rows) but adds metadata to the rows that provides an implementation of `Datafiable` ([protocol extension via metadata](https://github.com/clojure/clojure/blob/master/changes.md#22-protocol-extension-by-metadata) is also new in Clojure 1.10). The row is the "arbitrary thing" that we are starting with. One or more of the columns in that row may be a foreign key into another table. When you turn the row into a pure Clojure data representation -- by calling `datafy` on it -- it still looks like a hash map but now it has metadata that provides an implementation of `Navigable`. That supports calling `(nav row column value)` and, if the column is considered to be a foreign key to another table, it will fetch the relevant row(s) from that table and return that as the next "arbitrary thing", otherwise it will just return the column's value as passed in. The cycle of converting that to data (via `datafy`) and navigating through it (by navigating the Clojure data and then calling `nav` on that) can be continued indefinitely, until you bottom out to simple values.

You can sum this up as:

* Starting with a "thing"...
* ...you convert it to data (with `datafy`)...
* ...and walk it with simple Clojure data access...
* ...and, at each stage, you can navigate to the corresponding "new thing" by calling `nav`...
* ...which may return just that value or may do something more complex...
* ...and from that "new thing" you convert it to data (with `datafy`) and continue the process.

Relating this back to REBL, it works by taking some arbitrary value produced in the REPL and converting it to data (via `datafy`) so that it can be displayed in the UI. With any part of that data highlighted you can "drill down", at which point REBL calls `nav` to perform the (potentially lazy, complex) navigation and then converts that to data (via `datafy`) and displays that as the next "level" of data. Given an associative data representation, it does `(get coll k)` first to get `v`, and then it calls `(nav coll k v)` to allow the underlying navigation to return an updated value.

You go from "thing" to "data representation" of "thing" (via `datafy`), and then you can do the simple associative lookup _in the data representation, not the original thing_, and then you use `nav` to get back to the relevant equivalent part of the "thing", and then you `datafy` that again to get pure data. For built-in Clojure data types that are already pure data, `datafy` does nothing and `nav` just returns the selected value. For hash maps, navigation is just simple key lookup. For vectors, navigation is also simple key (index) lookup. This new machinery only starts to shine in more complex situations.
