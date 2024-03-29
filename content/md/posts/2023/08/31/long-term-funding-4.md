{:title "Long-Term Funding, Update #4",
 :date "2023-08-30 13:00:00",
 :tags ["clojure" "clojure-doc.org" "honeysql" "clj-commons" "open source" "community" "clojurists together" "polylith"]}

In my [previous Long-Term Funding update](https://corfield.org/blog/2023/06/30/long-term-funding-3/)
I said I would review/overhaul the "ecosystem" and "tutorials" sections.<!--more-->

## On a personal note...

I ended the previous update with a personal note but I'm going to start this
update with one. It's been a very difficult couple of months. My mother
passed away in early July (on my birthday!) and much of the month involved
a lot of back and forth with the funeral home in England and family around
the world. My wife & I attended the service via Zoom at the end of July and then
it took some additional time to get the service booklet and recording
distributed to family and friends.

Then in early August, both my wife and I got COVID-19 -- after three and a
half years of avoiding it, and having all our shots and boosters. My wife
tested positive soon after symptoms started and got paxlovid. I had all the
same symptoms too, but kept testing negative until I was outside the window for
paxlovid -- and then started testing positive. It took about two weeks for us
both to test negative reliably. We're both still recovering from the fatigue
and brain fog but every day is an improvement. Wear your masks, folks, and
get all your shots and boosters!

Consequently, I didn't get as much done as I'd hoped in the past two months.

## `clojure-doc.org`

I incorporated feedback from the community on the `tools.build` cookbook.
Many thanks, in particular, to [@phronmophobic](https://github.com/phronmophobic)
who provided extensive feedback and Pull Requests!

I reviewed the "ecosystem" and "tutorials" sections but only so far as to
remove outdated content. I reviewed and updated the main **Content** page
to reflect the current state of the site, reordering sections and removing
outdated content.

Feedback from the community suggested that I should
review sections in a different order to my initial plan,
so I turned my attention to the "language"
section and updated the following pages from Clojure 1.5 to Clojure 1.11:

* [Language: Functions](https://clojure-doc.org/articles/language/functions/)
* [Language: clojure.core](https://clojure-doc.org/articles/language/core_overview/)
* [Language: Collections and Sequences](https://clojure-doc.org/articles/language/collections_and_sequences/)
* [Language: Namespaces](https://clojure-doc.org/articles/language/namespaces/)
* [Language: Java Interop](https://clojure-doc.org/articles/language/interop/)
* [Language: Polymorphism](https://clojure-doc.org/articles/language/polymorphism/)

In some cases that just meant double-checking the code examples still worked
as shown, but in other cases it meant updating the examples to use newer
functions, adding new examples showing alternative approaches that are now
possible, or changing the error messages shown to match the current behavior
(since Clojure 1.10 did a lot to improve error messages).

I still need to make additional passes over several of these pages to address
the remaining "TBD" items (mostly adding more examples).

## HoneySQL

Several complicated changes were made to HoneySQL this period, leading to the
[2.4.1066 release](https://github.com/seancorfield/honeysql/releases/tag/v2.4.1066),
which included the first pass at supporting temporal queries, and reworking
how `:insert-into`, `:columns`, and `:values` work together which should
make it easier to avoid generating invalid SQL as well as making it easier
to extend HoneySQL to support additional features around `INSERT` statements.

## Polylith

The [Polylith project](https://github.com/polyfy/polylith)
(and documentation) was still using my old (archived) `build-clj`
wrapper so I worked on a Pull Request to switch everything to plain
`tools.build` usage as a better example for the community. That has been
merged in and updated documentation will be released soon (on cljdoc.org
instead of GitBook!).

## `clj-new`

This project also still used `build-clj` so I updated all the project
templates to use `tools.build` directly and released version
1.2.404 so that, going forward, newly-generated projects will be better
examples for the community. `deps-new` had previously been updated to
generate projects using `tools.build` directly.

## `clj-commons`

Information about CLJ Commons governance was spread across the
[`clj-commons`](https://clj-commons.org) website and the
[`meta`](https://github.com/clj-commons/meta) repository's `README`
and other pages.
Based on some community feedback in June, I wanted to consolidate
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

Finally, I consolidated and updated the information about how CLJ Commons accepts and maintains
projects, and updated the `README` in the `meta` repo to reflect that this
information is all on the main website now:

* [Accepting projects into clj-commons](https://clj-commons.org/accepting-projects.html)
* [Maintaining projects in clj-commons](https://clj-commons.org/maintaining-projects.html)

## What's Next?

In September/October, I'm hoping to complete a review and update of the
"tutorials" and "ecosystem" sections of clojure-doc.org, and then in the
remaining period, I'll tackle the "cookbooks" section and make another
pass of "TBD" items in the "language" section.
