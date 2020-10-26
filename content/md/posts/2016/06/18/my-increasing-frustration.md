{:title "My Increasing Frustration...",
 :date "2016-06-18 10:00:00",
 :draft? true,
 :tags ["clojure"]}
...with other people's frustrations! 

When folks are having a rant about their "home" language, I generally just watch to see how the discussion unfolds. I'll occasionally chip in with a comment or two. I rarely write a blog post in response. I've watched this particular tire fire for almost a week and today there was a tweet about a new comment and that just tipped me over the edge.<!-- more -->

What's the tire fire this time? [Ashton Kemerling's rant](http://ashtonkemerling.com/blog/2016/06/11/my-increasing-frustration-with-clojure/) about the way Clojure is managed. When I first read the post, I immediately wanted to jump in and tell Ashton he was wrong on so many levels. Fortunately Alex Miller [calmly responded to Ashton's points](http://ashtonkemerling.com/blog/2016/06/11/my-increasing-frustration-with-clojure/#comment-2725501771) and said much of what I wanted to say in a much more politic way. Still, I'm going to reiterate some of my thoughts on Ashton's points anyway because... well, this is my blog and I can say what I want here...

Ashton starts out with some history. I don't know how many years he's been programming but he talks about his first post-college job being Common Lisp and then, after "a few years" (of dynamic scripting languages), he discovered Clojure and has been doing that for... ? ...years. It's pretty cool to get paid to write Lisp in any form, but it's hard to get a good perspective on programming languages until you've worked in a broad range of them: both dynamically typed and statically typed, both interpreted and compiled, both natively implemented and VM-supported.

Let's walk through Ashton's concerns...

Ignorance or apathy around the behavior of `clojure.set` functions. In the ANSI/ISO C and C++ Standards, and in many other language standards, there are the concepts of implementation-defined behavior, undefined behavior and unspecified behavior. In the former case, the behavior may vary between implementations but that behavior is defined and documented. Clojure and ClojureScript (and probably ClojureCLR) have this in a few places where the behavior is documented but different. In the latter case, the behavior may vary between implementations -- or even between computers, or just multiple runs of the same program -- and is within a range of possible options. It is acceptable behavior for a well-formed program with valid data. I can't think of anything in Clojure that fits in that category but there may be some such features. Then there's undefined behavior. This case covers programs that are given data for which the language definition provides no guarantees. An implementation can do whatever it wants in these cases. It can throw an exception, produce completely crazy results, produce sensible results, or cause the sudden heat death of the universe. Let's look at the docstring for `clojure.set/union` as an example:

    Return a set that is the union of the input sets

This says, given any number of _sets_ as input, you'll get a _set_ back that is the union of those sets. If you give this function something other that _sets_ as input, the behavior is **undefined**. Programming languages take this path so they don't have to impose the overhead of input validation on all users: give me valid data and I'll give you a valid result; give me invalid data and all bets are off. You may not agree with that decision but you're not the language designer. It's been standard practice for C and C++ for decades (and other languages too) so it's a perfectly valid decision. Other languages may make other decisions.

Inconsistency between best practices and (Clojure) implementation.

Show stopping bugs remain untouched.

Strange priorities.

Transit. Transducers. Spec. `clojure.test`.

http://ashtonkemerling.com/blog/2016/06/11/my-increasing-frustration-with-clojure/#comment-2736653188

