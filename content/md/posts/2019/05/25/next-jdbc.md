{:title "Next.JDBC Beta 1",
 :date "2019-05-25 15:00:00",
 :tags ["clojure" "jdbc" "open source"]}

## seancorfield/next.jdbc 1.0.0-beta1

`next.jdbc` -- the "next generation" of `clojure.java.jdbc` -- is a modern Clojure wrapper for JDBC. Beta 1 is now available to test -- only accretive and fixative changes will be made from this point on, so the API should be considered stable enough for production usage.

https://cljdoc.org/d/seancorfield/next.jdbc/1.0.0-beta1/doc/readme

The group and artifact ID will remain `seancorfield/next.jdbc`, the namespace structure will remain `next.jdbc.*`. With auto-generated documentation hosted on cljdoc.org and Continuous Integration testing hosted on circleci.com, I have decided to continue work on `next.jdbc` outside Clojure Contrib, rather than merge it into `clojure.java.jdbc` as new namespaces there. I have updated the [Contributing page on GitHub](https://github.com/seancorfield/next-jdbc/blob/master/CONTRIBUTING.md) to reflect that Pull Requests can now be submitted.

### Changes since Alpha 13

* Set up CircleCI testing (just local DBs for now).
* Address [#21](https://github.com/seancorfield/next-jdbc/issues/21) by adding `next.jdbc.specs` and documenting basic usage.
* Fix [#19](https://github.com/seancorfield/next-jdbc/issues/19) by caching loaded database driver classes.
* Address [#16](https://github.com/seancorfield/next-jdbc/issues/16) by renaming `reducible!` to `plan` (this is a **BREAKING CHANGE!** from Alpha 13 -- the naming of this function was the blocker for moving from Alpha to Beta).
* Address [#3](https://github.com/seancorfield/next-jdbc/issues/3) by deciding to maintain this library outside Clojure Contrib.

Please take it for a test drive and let me know if you run into any problems via [GitHub issues](https://github.com/seancorfield/next-jdbc/issues) or in the [`#sql` channel on the Clojurians Slack](https://clojurians.slack.com/messages/C1Q164V29/details/) or the [`#sql` stream on the Clojurians Zulip](https://clojurians.zulipchat.com/#narrow/stream/152063-sql).
