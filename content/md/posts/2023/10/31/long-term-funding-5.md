{:title "Long-Term Funding, Update #5",
 :date "2023-10-31 10:00:00", :draft? true
 :tags ["clojure" "clojure-doc.org" "honeysql" "jdbc" "open source" "community" "clojurists together"]}

In my [previous Long-Term Funding update](https://corfield.org/blog/2023/08/31/long-term-funding-4/)
I said I would review/overhaul the "ecosystem" and "tutorials" sections.<!--more-->

## `clojure-doc.org`

* Language: Collections and Sequences -- new section on transducers.
* Language: Concurrency and Parallelism -- updated to Clojure 1.11.
* Language: Macros -- updated to Clojure 1.11.
* Language: Laziness -- updated to Clojure 1.11.
* Ecosystem: Generating Documentation -- added cljdoc.org, removed outdated content.
* Ecosystem: Web Development -- almost entirely rewritten, added a lot of new content, removed outdated content, incorporated a lot of community feedback (thank you!).
* Cookbooks: `tools.build` -- new section on template `pom.xml` and `:pom-data` approaches.


## `deps-new`

0.5.3 -- switch to `tools.build` 0.9.6 for `pom.xml` generation via `:pom-data`.


## Expectations

2.1.182 -- overhaul failure reporting to be more consistent and informative.


## HoneySQL

2.4.1078 -- fix bug in helpers that was preventing their use with quoted-symbol DSL; improve BigQuery support


## `next.jdbc`

1.3.894 -- provide variants of `with-transaction` and `on-connection` that will rewrap an options-wrapped connectable.


## What's Next?

In November/December, I'm hoping to complete a review and update of the
"cookbooks" section and make another pass of "TBD" items in the "language"
section.
