{:title "Release! Release! Release!",
 :date "2019-08-07 15:30:00",
 :tags ["clojure" "honeysql" "jdbc"]}
## Lots of Releases

Over the last week or so I've released minor updates to several of the projects I maintain, so I thought it would be nice to have a summary blog post rather than a scattering of minor announcements.

### clj-time 0.15.2

Add type hints, fix a bug in `overlaps?`, improve tests and documentation.

See the [change log](https://github.com/clj-time/clj-time/blob/master/ChangeLog.md) for more details.

### jkk/honeysql 0.9.5

Support JDK11 for dev/test, support Turkish language users, enhance `format-predicate` to match `format` (`parameterizer`).

See the [change log](https://github.com/jkk/honeysql/blob/master/CHANGES.md) for more details.

### seancorfield/clj-new 0.7.7

Pin Clojure (and `test.check`) version in generated projects instead of using `"RELEASE"`.

See the [change log](https://github.com/seancorfield/clj-new/blob/master/CHANGELOG.md) for more details.

### seancorfield/depstar 0.3.1

Add `-m` / `--main` option to override `Main-Class` in generated manifest.

See the [change log](https://github.com/seancorfield/depstar#changes) for more details.

### seancorfield/next.jdbc 1.0.5

`plan`'s "mapified" `ResultSet` is now a full `IPersistentMap` implementation which adds support for `cons`, `count`, `dissoc`, `empty`, etc. Continued improvements to documentation.

See the [change log](https://github.com/seancorfield/next-jdbc/blob/master/CHANGELOG.md) for more details.
