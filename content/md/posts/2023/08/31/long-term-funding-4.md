{:title "Long-Term Funding, Update #4",
 :date "2023-08-31 10:00:00", :draft? true,
 :tags ["clojure" "clojure-doc.org" "honeysql" "clojure-clr" "jdbc" "open source" "community" "clojurists together"]}

In my [previous Long-Term Funding update](https://corfield.org/blog/2023/06/30/long-term-funding-3/)
I said I would review/overhaul the "ecosystem" and "tutorials" sections.<!--more-->

## `clojure-doc.org`

Incorporated feedback from the community on the `tools.build` cookbook.
## Polylith

Switched from `build-clj` to `tools.build`.

## `clj-new`

1.2.404

Switched from `build-clj` to `tools.build`.

## `clj-commons`

Information about `clj-commons` was spread across the
[`clj-commons`](https://clj-commons.org) website and the
[`meta`](https://github.com/clj-commons/meta) repository's `README`
and the Wiki there. Based on some recent feedback, I wanted to consolidate
that information and bring it up to date.

As I started on that, I realized that the
[`clj-commons` project list](https://clj-commons.org/projects.html)
was very outdated so I decided to regenerate it (there's a
[Clojure script](https://github.com/clj-commons/clj-commons.github.io/blob/master/src/clj_commons/projects.clj)
for this). That uncovered a number of projects that were missing either the
`ORIGINATOR` file in the root of the repo (how `clj-commons` identifies the
original author of a project) or the `.github/CODEOWNERS` file that lists
the current active maintainers.

I went through every `clj-commons` repo and added the missing files,
updated the `projects.clj` script to support `#` comments in `CODEOWNERS`,
and regenerated the `projects.html` page.

And now, back to the governance docs...

Governance docs?

## Other Projects

## What's Next?

In September/October, I'm hoping to complete a review and update of the
"cookbooks" section of clojure-doc.org, and then in the
remaining period, I'll tackle the "language" section.

## On a personal note...

I mentioned in the previous update that my mother was back in hospital and
not doing too well. She passed away on my birthday, July 7th, and much of
my energy for the rest of the month was focused on that. She would have been
90 this coming December.
