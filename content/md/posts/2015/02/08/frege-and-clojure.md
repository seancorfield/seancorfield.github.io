{:title "Frege (and Clojure)",
 :date "2015-02-08 123:32:18 -0700",
 :tags ["clojure" "frege"]}
I've often said that I try to follow [The Pragmatic Programmer's](https://pragprog.com/the-pragmatic-programmer) advice to learn a new language every year. I don't always achieve it, but I try. As I've settled into Clojure as my primary language over the last several years, I've made a fair attempt to learn Python, Ruby, Racket/Scheme, Standard ML and more recently [Elm](http://elm-lang.org). I learned that I like Python, I don't like Ruby, Racket/Scheme is "just another Lisp" (I already have Clojure) and SML is very interesting but not really widely useful these days (it's a great language for learning Functional Programming concepts tho'!). I also spent some time with Go last year (don't like it).

The Elm language is really nice - and useful for building interactive browser-based applications (and games). I've been meaning to blog about it for quite a while, and I hope to get around to that in due course. Elm is sort of inspired by Haskell, and that's really what this blog post is about. Sort of.<!-- more -->

Haskell and I have a strange relationship. I really liked Haskell when it appeared in the early 90's. I hoped it would finally be the language to help Functional Programming go mainstream (I'd been dabbling with FP for about a decade by then). It didn't. Sigh. But I continued to dabble with FP - and Haskell - on and off (mostly off) for another couple of decades. I've occasionally blogged about Haskell (yes, on my old ColdFusion-focused blog it used to crop up about once a year), and I've always wanted to be able to _use_ Haskell for something more than just playing around. For a long, long time tho', my programming life has been tied to the JVM, for better or worse, and Haskell's standalone nature has meant that I haven't been able to integrate it into my daily stack.

That tie to the JVM is why I learned Groovy, Scala, Clojure - and took them all to production - but haven't really been able to get deeply into Python (much as I like it)... or poor old Haskell, despite now decades of toying with it.

Fortunately for me, Ingo Wechsung likes Haskell enough that he created the [Frege programming language](http://www.frege-lang.org) which is "a **pure** functional programming language for the JVM in the spirit of Haskell". It's sufficiently similar to Haskell - see [Differences between Frege and Haskell](https://github.com/Frege/frege/wiki/Differences-between-Frege-and-Haskell) - that many people consider Frege to be "Haskell for the JVM". This makes me happy because I can finally start to use Haskell (sort of) as part of my daily stack and therefore _really learn it_! Finally!

So why is this post titled **Frege (and Clojure)**?

Given that Clojure is my primary language, what I really want is to be able to use Frege alongside Clojure, writing small routines in Frege that I can call from Clojure. That means I need a way to compile and load Frege code via Leiningen, Clojure's build tool. So I created `lein-fregec`, a [Frege plugin for Leiningen](https://github.com/seancorfield/lein-fregec), that allows you to compile pure Frege projects, as well as compile and run mixed language Frege / Clojure projects.

Today I released version ~~3.22.324~~ **3.22.367-i** (to match the current version of the Frege compiler), along with two example Leiningen projects (in that same repo) to show how to use `lein-fregec` for pure Frege projects as well as Clojure / Frege projects. Let's take a look at the mixed language one.

Here's the Frege code:

    module Fibonacci where
    
    -- lazy infinite sequence of Fibonacci numbers starting with a, b:
    fibs a b = a : fibs b (a + b)
    
    -- lazy infinite sequence of Fibonacci numbers (0, 1, 1, 2, 3, ...):
    fibonacci = fibs 0 1
    
    -- let Frege infer the types here (it'll be Int -> Int):
    fibn n = head $ drop n $ fibonacci
    
    -- Clojure uses Long by default so this is our public API:
    fib :: Long -> Long
    fib n = Int.long $ fibn (Long.int n)

This declares a module (class in the Java bytecode) called `Fibonacci` which contains four functions. Even tho' Frege is a (very) strongly typed language, you can often omit the types as it will infer them for you. Starting at the bottom, we have `fib` which is declared to take a `Long` and return a `Long` - and will be compiled down to a static method on the `Fibonacci` class and therefore easily callable from Clojure (or Java). It casts its argument `n` to an integer, calls `fibn` on it, and then casts the result to a `Long` to return it to Clojure. `f $ g x` is shorthand for `f (g x)` that avoids the parentheses. `fibn`, in turn starts with the (lazy infinite) sequence of fibonacci numbers and drops the first `n` of them (the sequence starts with zero, but I'm treating one as the "first" number) and then returns the first of what's left (the `head`). Again `$` means we don't have to write `head (drop n (fibonnaci))`. The `fibonacci` function takes no arguments and just calls `fibs` with the seed values of zero and one. `fibs` takes two arguments and returns a sequence with the first argument followed by the fibonacci sequence that starts with the second argument. Frege, like Haskell, is a non-strict (lazy) language so `fibs` isn't really a recursive call, even tho' it looks like it ought to blow the stack. Instead, when it is called, it returns a list whose first element is (the value of) `a` and whose remaining elements will be evaluated as needed - or rather _if needed_. Technically, it doesn't even evaluate `a` unless you actually refer to the value.

What that means is that when `fib` is called, the cast (from `Int` to `Long`) forces evaluation of the call to `fibn`, which in turn asks for the `head` of ... the sequence we get by dropping the first `n` elements of ... that lazy infinite sequence. So we construct the first `n + 1` elements of the list and then we look at the last one of those, which forces the calculation of that value (yes, the additions are deferred until the value is needed!). We write simple, obvious code that looks like it might be very inefficient but Frege ensures that only the values we actually need are ever calculated - and because of the way the sequence is constructed from previous elements, it means that once we've calculated the _nth_ number, all the previous ones are essentially calculated and cached for us. This is possible because our functions are **pure** (no side effects).

What about the Clojure code? Since Frege compiles modules to classes and certain functions (with basic Java type signatures) to static methods, we simply import the compiled class and call the function we want:

    (ns example.core
      (:import Fibonacci))
    
    ... (Fibonacci/fib 13) ...

We can even play with the code from the REPL:

    $ lein do fregec, repl
    ...
    example.core=> (Fibonacci/fib 11)
    89
    example.core=> (Fibonacci/fib 42)
    267914296
    example.core=> 
