{:title "Next.JDBC to 1.0.0 and Beyond!",
 :date "2019-07-04 10:00:00",
 :tags ["clojure" "jdbc"]}
# next.jdbc 1.0.0 and 1.0.1

First off, [seancorfield/next.jdbc 1.0.0](https://github.com/seancorfield/next-jdbc/releases/tag/v1.0.0) was released on June 13th, 2019 (and I [announced it on ClojureVerse](https://clojureverse.org/t/next-jdbc-1-0-0-the-gold-release/4379) but did not blog about it), and yesterday I released [seancorfield/next.jdbc 1.0.1](https://github.com/seancorfield/next-jdbc/releases/tag/v1.0.1) which is mostly documentation improvements.

Someone recently commented that this blog had the [Release Candidate announcement](https://corfield.org/blog/2019/06/04/next-jdbc/) on June 4th and was surprised I didn't "make a big deal" about the "gold" release. The 1.0.0 release is a **big deal** and this blog post is about that -- what it's taken to get here and what's to come.<!-- more -->

## clojure.contrib.sql

I learned Clojure back in 2010 and started using it at work in early 2011. For us to use it in production, we needed JDBC support but `clojure.contrib.sql` was not being actively maintained at that point and the Clojure 1.3 release was in its Alpha phase. Part of the 1.3 release cycle involved [breaking up the "Monolithic Contrib" from the 1.2 release and finding new maintainers](https://clojure.org/community/contrib_history) in order for its parts to move forward. That's how I became the maintainer of [`clojure.java.jdbc`](https://github.com/clojure/java.jdbc) -- `clojure.contrib.sql`'s new name and home. `clojure.java.jdbc` started life with an 0.0.1 release.

## clojure.java.jdbc

The 0.0.x releases mostly focused on compatibility with more databases and ease of use enhancements.

0.1.0 rewrote the result set handling to use regular Clojure hash maps instead of the deprecated `struct-map` machinery, hence the bump in version numbers. The 0.1.x releases continued to expand database support, expand the API, and fix bugs.

0.2.0 merged the internal namespace into the main `clojure.java.jdbc` namespace and adjusted the visibility (and naming) of several symbols, hence the bump in version numbers. The 0.2.x releases continued the path of improving database support, improving usability, fixing bugs, and some performance improvements.

0.3.0 was a fairly massive overhaul of the API -- these were definitely breaking changes, as most of the existing API was deprecated (based on the dynamic `*db*` variable), and a new API introduced (which accepted the db spec everywhere as an argument, including the first versions of `insert!`, `query`, `update!`, `delete!`, and `execute!`). 0.3.0 also introduced `IResultSetReadColumn` and `ISQLValue` as extension points. Prerelease builds of 0.3.0 introduced a mini-DSL for building SQL but that was removed before the gold release -- the DDL support stayed, however. The 0.3.x releases mostly focused on bug releases after the big upheaval in the API, and adding support for more and more options. The modern "db-spec" with `:dbtype` and `:dbname` was introduced during this time.

0.4.0 dropped support for Clojure 1.2 and the 0.4.x releases fixed bugs, broadened database support, and added yet more options.

0.5.0 dropped support for Clojure 1.3 and 0.5.5 started the shift from "named" arguments (unrolled keyword arguments) to passing options in a single hash map, which makes it much easier to compose function calls. The 0.5.x releases continued deprecation of the unrolled keyword arguments.

0.6.0 removed all deprecated APIs and signatures. This was a pretty volatile time to be using `clojure.java.jdbc`. There was a 0.6.1 release but the upcoming 0.6.2 release became a series of 0.7.0 prerelease builds as I replumbed the functions to correctly pass options through the entire chain, and dropped support for Clojure 1.4, 1.5, and finally 1.6... This also saw the introduction of `reducible-query` (which laid the groundwork for `next.jdbc`).

By the time 0.7.0 was released, I was beginning to think more seriously about a 1.0.0 release. The 0.7.x releases continued to expand database support, to add more and more options, and also started to look at performance improvements.

## Contrib Evolving

I'd solicited feedback from the Clojure/core folks about what it would mean for a 1.0.0 release at various points, starting as far back as the 0.3x series of releases, and I'd been folding that feedback into the various changes over time. Also, over time, it became clear that Clojure/core were less worried about placing restrictions on how maintainers ran their projects. Some Contrib libraries had their 1.0.0 releases and continued on, some were still in the 0.0.x phase, others run the gamut from 0.1.x to 0.10.x.

It's good to read [Stuart Sierra's post from 2012](https://clojure.org/news/2012/02/17/clojure-governance) about how Clojure (and Contrib's) governance evolved over the years, as well as the [current state of Contrib libraries](https://clojure.org/community/contrib_libs). In addition to the recent clarifications about Contrib's purpose and governance, we've seen `clojure.tools.nrepl` fork back out of Contrib as [nrepl/nrepl](https://github.com/nrepl/nrepl) and a lot of adjustment in tooling to adapt to the new group, artifact, and namespace names.

It was in this environment that I continued to think about what a 1.0.0 release of `clojure.java.jdbc` should be. At Clojure/conj (2018), I was excited about [REBL](https://github.com/cognitect-labs/REBL-distro) and `datafy`/`nav`, and I introduced [experimental support for all that in `clojure.java.jdbc`](https://corfield.org/blog/2018/12/03/datafy-nav/). I was also thinking about `clojure.spec` and the shift to qualified keywords and the increased use of transducers and the focus on simplicity and consistency...

## The Birth of next.jdbc

It was around that point that I realized that I wouldn't really be comfortable declaring a 1.0.0 release on `clojure.java.jdbc`: there was no natural "end state" and it had become large and complex (and slow in places) -- and I couldn't realistically change that without making a whole 'nother round of breaking changes. But this time I heeded [Rich's advice about accretion and fixation](https://www.youtube.com/watch?v=oyLBGkS5ICk) and started to design the "next" version of `clojure.java.jdbc` not as a series of (breaking) changes to what already existed but as a completely new set of functions built on a completely new set of implementations, based on what I'd learned from nearly eight years of maintaining `clojure.java.jdbc`.

I talk about my [motivation for this new JDBC wrapper](https://github.com/seancorfield/next-jdbc#motivation) in the project README but I'll provide a bullet point recap here as well:

* Improve performance,
* Provide a "modern" Clojure approach/API,
* Provide a simpler library/API.

I spent a month or so sketching out what this new version of the library would look like and how it would behave using a local git repository (stored on OneDrive). I first mentioned that I was working on it in early January (in [a thread about HTTP servers on ClojureVerse](https://clojureverse.org/t/what-http-server-library-to-use/3423/29?u=seancorfield), of all places) and moved it into a private GitHub repository, once I had usable code. Over the next three months, I continued to develop it until the first public release on April 21st: 1.0.0-alpha8. Over the next month, I gathered feedback from anyone who was willing to use it, making a few small adjustments in the API, until the first stable release on May 25th: 1.0.0-beta1.

One of the key decisions I had to make was whether this would become new namespaces inside `clojure.java.jdbc` in Contrib or live standalone somewhere -- because I viewed it very much as the "next version" of that Contrib library. I was surprised that no one seemed to care if a library was published under someone's name as a group ID, nor even if the namespaces themselves contained someone's name. I'd viewed Contrib's well-maintained Continuous Integration system and automated documentation generator as pros, along with my perception that Contrib libraries were easy to find and had some stamp of "authority" by virtue of being in the `clojure` GitHub organization -- but I seemed to be very much in the minority in that perception. Once I had CI set up for [`next.jdbc` on CircleCI](https://circleci.com/gh/seancorfield/next-jdbc) and auto-generated [documentation for `next.jdbc` on cljdoc.org](https://cljdoc.org/d/seancorfield/next.jdbc), I decided that `seancorfield/next.jdbc` was going to live on, outside Contrib.

## Beta 1 and Stability

I mentioned above that I considered Beta 1 to be the first stable release. Having gone through so many breaking changes with `clojure.java.jdbc` and having listened to Rich (and others in the Clojure community) talk about the need for backward compatibility and the futility of semantic versioning, it was important to me that I could draw a line in the sand at some point and say "no more breaking changes". I decided that the move from Alpha to Beta should be that line for `next.jdbc` and the last breaking change was renaming `reducible!` to `plan` as part of the beta release -- indeed, that was the gating factor for exiting the alpha phase of development.

My goal is to never break backward compatibility across future versions of `next.jdbc` -- only adding new functionality (and fixing things that are clearly broken). I expect future versions to appear slowly and contain very few additional features, perhaps focusing mostly on improving the documentation in response to questions from users of the library.

## Wrapping Up

I couldn't have made `next.jdbc` without a lot of lessons learned from eight years of maintaining `clojure.java.jdbc` and all the feedback from the community -- both on `clojure.java.jdbc` itself and during the alpha phase of `next.jdbc`'s development. It's faster, more modern, and simpler than `clojure.java.jdbc`. It embraces the host platform by being based on JDBC types directly, but offers value beyond being "just" a wrapper for those types and their methods. It looks to the future by yielding qualified keywords by default, supporting `datafy`, `nav`, and focusing on reducing/transducing as a core part of the main API (via `plan`).

I consider `next.jdbc` to be the 1.0.0 release that `clojure.java.jdbc` was never going to achieve.
