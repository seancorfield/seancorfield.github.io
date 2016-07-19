---
layout: post
title: "Start Your Engine"
date: 2016-07-18 11:00:00
comments: true
categories: [clojure]
---
Today I'm inspired by the [latest issue of Eric Normand's Clojure Gazette](http://us4.campaign-archive1.com/?u=a33b5228d1b5bf2e0c68a83f4&id=56d35f53c5) which talks about why his "Joy of Programming" comes from learning and exploration.

I got into programming as a child because I was curious about solving puzzles and problems: given the (relatively) limited vocabulary of a programming language and its input and output features, and some interesting problem that came to mind, can I solve it in a usable (and hopefully elegant) way?

Over the years, I've written a **lot** of fun little programs to solve all sorts of interesting puzzles and problems that I've either run across or invented just to amuse myself. I learn different programming languages to learn new vocabularies for solving problems, and new ways of looking at problems.

Some of those programs become libraries that I've ended up using at work in one form or another, some become open source projects where I'm pretty much the only user, a very small number become widely used projects.<!-- more -->

Back in 2009, when my work was primarily CFML-based (and I wrote side projects in other languages for fun), I sketched out, figuratively on a [napkin, my ideas for a minimal MVC framework, based on conventions](http://framework-one.github.io/blog/2010/02/06/fw1-the-napkin-spec/). It was intended as an exercise to "scratch an itch". It went on to become one of the most popular MVC frameworks in the CFML community (and I still maintain it to this day, even tho' my work is almost entirely Clojure now).

Again, as an exercise to "scratch an itch", while I was still relatively new to Clojure, I decided to [port the core of that framework to Clojure](http://framework-one.github.io/blog/2011/11/07/fw1-comes-to-clojure/). It started as a bundle of [Ring](https://github.com/ring-clojure/ring), [Enlive](https://github.com/cgrand/enlive), and a convention-based routes-to-namespace-and-function mapping. Along the way, I replaced Enlive with [Selmer](https://github.com/yogthos/Selmer), and this "fun little program" now falls into the category of an open source project where I'm pretty much the only user (we're starting to use it at work).

About a year ago, I had another puzzle in mind: could I create a usable library that allowed for a separation of queryable data sources, pure business logic, and committable changes (inserts, updates, and deletes). I created [Engine](https://github.com/seancorfield/engine) purely to scratch that itch over a few days at the end of May 2015 and, apart from converting it to use Boot at the end of 2015, it's languished on GitHub ever since. Until a few weeks ago, when it looked like a solution to a problem at work.

We wanted a clearly delineated idiom where we could refactor our business logic out into pure functions, that depended on a set of queryable resources (some readonly JDBC and MongoDB data sources, a Component-based "system", etc), and produce a set of changes that could be applied to a database or sent over a message queue to be processed or to update a search engine etc. We wanted something that "forced" this on our code (or at least "strongly encouraged" this separation of concerns).

Using Engine in the real world has caused it to grow and evolve to make it more fluent in production code (hence the flurry of changes and new releases over the last few weeks) but it's also allowed us to take a long, hard look at what shape our code needs to be, in order to support our legacy production platform, our new production platform, and our future production platform.

Although it would be nice to get extra eyes on both Engine and FW/1 for Clojure and, yes, additional users of both projects, I'm posting this mostly as encouragement to "scratch your itch" and to write code as a way of learning, and exploring new ideas. It doesn't matter if that code is ultimately useful to anyone other than to you, as a medium for expressing your thoughts: write it down, play with it, use it to investigate a new concept or to rethink an existing one. Above all, **enjoy** programming!
