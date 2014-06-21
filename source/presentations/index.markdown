---
layout: page
title: "presentations"
date: 2014-06-20 17:53
comments: true
sharing: true
footer: true
---
This is a selection of talks that I've given over recent years, roughly in reverse date order. Most of my talks have been aimed at a CFML audience, even when discussing Clojure. I used to list about two dozen talks on my site, but a lot of those are outdated now, and even that list only went back to about 2008.

Learn You a What for Great Good
---
Subtitled "Polyglot Lessons to Improve Your CFML", this is a walk thru JavaScript, Groovy and Clojure (and a quick look at Scala) to show some interesting and powerful idioms that we can use in CFML to leverage the real power behind arrays and structs, and some closure goodness too! See [the code on Github](https://github.com/seancorfield/cfo2013/). Presented at cf.Objective() 2013. [Learn You a What for Great Good slides (85Kb PDF)](http://corfield.org/articles/polyglot.pdf).

ORM, NoSQL, and Vietnam
---
This plays on blog posts by Ted Neward and Jeff Atwood to put Object-Relational Mapping under the microscope and look at where the mapping breaks down and how it can "leak" into your code, making your life harder. After that I take a quick walk thru the general "No-SQL" space and then focus on document-based data stores as a good (better?) match for OOP and provide examples based on MongoDB and cfmongodb, with a quick look at how common SQL idioms play out in that world. See [the code on Github](https://github.com/seancorfield/cfo2013/). Presented at cf.Objective() 2013. [ORM, NoSQL, and Vietnam slides (95Kb PDF)](http://corfield.org/articles/ORM.pdf).

Humongous MongoDB
---
Essentially a follow-on to the ORM, NoSQL, and Vietnam talk above, this looks at replica sets, sharding, read preference, write concern, map/reduce and the aggregation framework, to show how MongoDB can scale out to support true "Big Data". The talk featured a live demo of setting up a replica set and using it from CFML, including coping robustly with failover, and a live demo of setting up a sharded cluster (and using it from CFML) to show how MongoDB handles extremely large data sets in a fairly simple, robust manner. See [the code on Github](https://github.com/seancorfield/cfo2013/). Presented at cf.Objective() 2013. [Humongous MongoDB slides (136Kb PDF)](http://corfield.org/articles/humongous.pdf).

Clojure and CFML Sitting in a Tree
---
Created primarily as a case study of World Singles' use of Clojure alongside CFML and presented at CFML User Groups in San Francisco, CA and Melbourne and Sydney in Australia in mid-2012. Since I can't share the code shown during the live talks, I've added slides with notes about what the code was examples of. [Clojure and CFML Sitting in a Tree slides (736Kb PDF)](http://corfield.org/articles/WorldSinglesWeb.pdf).

Doing Boring Things with an Exciting Language
---
A forerunner to the above talk, this is a 25 minute talk that I gave at Clojure/West 2012, talking about how World Singles uses Clojure and why it works so well as a general purpose scripting language that isn't just for solving hard problems at large scale. It doesn't go into any depth - it's a short talk - but I hope it gives a taste for what Clojure can do. [Doing Boring Things with an Exciting Language slides (3Mb PDF)](https://github.com/seancorfield/clojurewest2012-slides/blob/master/Corfield-Boring.pdf?raw=true).

Functional Programming: What is it and why should I care?
---
An introduction to functional programming, showing techniques that you can use to make your CFML easier to test, safer with concurrency and more reusable - using examples from Clojure as well as CFML. Created for cf.Objective() 2011. [Functional Programming slides (400Kb PDF)](http://corfield.org/articles/functional-notes.pdf).

FW/1 - The Invisible Framework
---
This talk introduces my lightweight MVC framework. It covers the principles, the conventions and refers to the examples that ship with FW/1 as demos. This version is from Scotch on the Rocks 2011. [FW/1 - The Invisible Framework slides (319Kb PDF)](http://corfield.org/articles/fw1.pdf).
