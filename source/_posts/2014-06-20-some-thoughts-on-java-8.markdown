---
layout: post
title: "Some thoughts on Java 8"
date: 2014-06-20 17:58:34 -0700
comments: true
categories: java
---
_Originally posted on [Google Plus](https://plus.google.com/u/0/+SeanCorfield_A/posts/dTiYLDXV5AV) on June 14th, 2014._

**Why Java 8 might win me back...**

I first started doing Java development in 1997. I was pretty invested in this "new" technology after being initially skeptical with my strong C++ background. I even wrote an editorial in a C++ journal about "the new kid on the block", casting aspersions as to whether it had what it would take to become popular. Over time, Java became the juggernaut of corporate development and I actually found it quite refreshing after C++'s somewhat obtuse syntax and complexities: Java was a simple language by comparison, with a well-structured, modular library.

At least, it started out that way.

Java and I parted ways around Java 5. I felt the changes were making it more complex without appropriate benefits. I thought generics and metadata were a disaster. I thought autoboxing would lead to sloppy code instead of forcing developers to think carefully about crossing the primitive/object boundary. I was ambivalent about varargs. I quite liked the new for loop and type-safe enums tho'. Java 6 and Java 7 left me cold (sure, the collection stuff was a step in the right direction, but most of the rest just piled complexity on complexity).

As far as I was concerned, Java had lost its way and become a bloated, verbose language that acted as a nursemaid to mediocre enterprise developers. I vowed I'd never write Java again and took every opportunity to tell recruiters that whenever they contacted me with great opportunities for Java architect roles.

I went off to Groovy, then Scala, then Clojure. And there I've stayed for the last three or four years.

When Java 8 was first getting press, I was very skeptical: how could they bring the elegance and ease of functional programming to a language so buried in boilerplate and a verbose, simplistic type system? I liked the simplicity of Groovy (and its dynamic approach to typing), I liked the elegance of Scala (even tho' I found the quirks of its type system and slow compilation to be significant drawbacks), and I really liked Clojure's combination of elegance, simplicity, and dynamic typing.

But, at World Singles, we run our dating platform on the JVM and so an upgrade to Java 8 was inevitable to take advantage of the improvements in heap management and garbage collection. Since I was testing everything on the new JVM, I figured I might as well take a look at Java's new language features...

And I was pleasantly surprised! Streams providing lazy/efficient map, filter, and reduce. Optional providing a way out of the null-checking tar pit of typical Java code. Lambda expressions providing a huge improvement in expressiveness and conciseness - compared to the ugly anonymous classes and single-method interfaces we'd had to put up with before. All those new functional interfaces. Method references. A lot of improvements in the type system and type inference.

In many ways, Java 8 is a whole new language. It's still fairly verbose but its support for functional programming is actually not bad at all. There are still no immutable collections - maybe Java 9 will address that - but streams, lambdas, etc make a huge difference in what you can do with relative ease.

I think I can safely say that I would no longer rather fall on a sword than program in Java - as long as it's Java 8!

_p.s. No, I'm not switching from Clojure, but now I view Java as "palatable" rather than "hideous"._
