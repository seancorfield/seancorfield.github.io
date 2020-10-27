{:title "Next.JDBC",
 :date "2019-04-21 23:00:00",
 :tags ["clojure" "jdbc"]}
## seancorfield/next.jdbc 1.0.0-alpha8

I've talked about this in a few groups -- it's been a long time coming. This is the "next generation" of `clojure.java.jdbc` -- a modern wrapper for JDBC, that focuses on `reduce`/transducers, qualified-keywords, and `datafy`/`nav` support (so, yes, it requires Clojure 1.10).

https://cljdoc.org/d/seancorfield/next.jdbc/1.0.0-alpha8/doc/readme  

_The next generation of `clojure.java.jdbc`: a new low-level Clojure wrapper for JDBC-based access to databases._  It's intended to be both faster and simpler than `clojure.java.jdbc` and it's where I intend to focus my future energy, although I have not yet decided whether it will ultimately be a new set of namespaces in the Contrib lib or a separate, standalone OSS library!

At this point, I'm looking for feedback on the API and the approach (as well as bugs, performance issues, etc). Please take it for a spin and let me know what you think via [GitHub issues](https://github.com/seancorfield/next-jdbc/issues) or in the [`#sql` channel on the Clojurians Slack](https://clojurians.slack.com/messages/C1Q164V29/details/) or the [`#sql` stream on the Clojurians Zulip](https://clojurians.zulipchat.com/#narrow/stream/152063-sql).

_The group/artifact ID will change at some point:_ and the actual namespaces will too, but I will try to make that as painless as possible when I take this out of the alpha phase.
