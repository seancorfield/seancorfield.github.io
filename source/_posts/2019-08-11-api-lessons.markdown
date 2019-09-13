---
layout: post
title: "Lessons of Open Source Maintenance"
date: 2019-08-11 10:00:00
comments: true
published: false
categories: [clojure, jdbc]
---
In [Next.JDBC to 1.0.0 and Beyond!](https://corfield.org/blog/2019/07/04/next-jdbc/) I talked about "a lot of lessons learned from eight years of maintaining `clojure.java.jdbc`" but didn't enumerate them. As Jakub Holy said in the comments there "it is possible to derive some" but it seems like a good blog post to be explicit about those as well as the others that might not be so obvious.

Looking over the history of `clojure.java.jdbc`, I think there are probably a dozen separate considerations that stand out that are worthy of further discussion. Some of these are "lessons" with a clear decision to be made and a forward direction to advance toward "better" programming practices. That said, I'm going to present most of these as trade offs rather than trying to claim one approach is empirically better than another approach, even though I have developed strong opinions on several of them<!-- more -->

## Dynamic (Global) State

This is a trade off between using global variables for convenience against the concerns of thread safety and the inconvenience / overhead of passing data as parameters down the entire call tree.

dynamic variables vs parameters

## Keyword Arguments

This is a trade off between readability and convenience against the concerns of composability.

keyword arguments and composability

## Implementation Architecture

This is an interesting one and I think "best practices" has shifted back and forth several times here over the years.

`impl` namespaces vs private/public

## Deprecation and Breaking Changes



breaking changes / deprecation - removal of features

## Backward Compatibility

supporting older versions of Clojure - new functions, new protocols, new abstractions

## Simple? Or Easy?

minimal API - simple vs easy

## Syntactic Sugar

convenience vs core

## Domain Specific Languages

DSLs

## Consistency of the API

consistency of behavior / passing options

## Extension Points

Extension points - multimethods, protocols, HOFs

## Performance

performance: hash maps, structs, records, protocols

## Transducers

transducers
