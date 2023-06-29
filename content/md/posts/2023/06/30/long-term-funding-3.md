{:title "Long-Term Funding, Update #3",
 :date "2023-06-29 11:50:00",
 :tags ["clojure" "clojure-doc.org" "honeysql" "clojure-clr" "jdbc" "open source" "community" "clojurists together"]}

In my [previous Long-Term Funding update](https://corfield.org/blog/2023/04/30/long-term-funding-2/)
I said I would review/overhaul the Libraries pages (both authoring and the directory)
and write the `tools.build` cookbook.

The [library authoring guide](https://clojure-doc.org/articles/ecosystem/libraries_authoring/)
has been rewritten to use the Clojure CLI, `deps-new`, and `deps-deploy` and
was well-received by the community, who provided some useful feedback that I
have also incorporated into the guide.

The information from the library directory has been integrated into
[The Clojure Toolbox](https://www.clojure-toolbox.com/)
via a couple of Pull Requests that
[added optional tool-tip descriptions](https://github.com/weavejester/clojure-toolbox.com/pull/470)
and [libraries that were on `clojure-doc`](https://github.com/weavejester/clojure-toolbox.com/pull/472)
but missing from the Toolbox. Thanks to James Reeves for accepting those PRs!

What else did I get done?<!--more-->

## ClojureCLR, HoneySQL

There's been quite a bit of activity around [ClojureCLR](https://github.com/clojure/clojure-clr)
recently, so I've been testing .NET-related things on Windows and on Ubuntu.
David Miller submitted a patch to `tools.cli` to add CLR support which I
released as [`tools.cli` v1.0.219](https://github.com/clojure/tools.cli/releases/tag/v1.0.219)
and I updated HoneySQL to add CLR support:
[`honeysql` v2.4.1033](https://github.com/seancorfield/honeysql/releases/tag/v2.4.1033).
`tools.nrepl` has been ported to ClojureCLR and Peter Strömberg (maintainer of Calva)
has created a [ClojureCLR starter project for VS Code/Calva](https://github.com/PEZ/clojure-clr-starter)
which I've also been helping to test on Windows and Ubuntu.

HoneySQL saw another release, mostly improving documentation and docstrings,
near the end of the this period: [v2.4.1045](https://github.com/seancorfield/honeysql/releases/tag/v2.4.1045).
Both releases improved the experience with `:on-conflict` clauses.

## `next.jdbc`

[`next.jdbc` v1.3.883](https://github.com/seancorfield/next-jdbc/releases/tag/v1.3.883)
was also released in this period, also mostly improving documentation and docstrings,
and adding an `active-tx?` predicate to expose whether `next.jdbc` thinks you
are currently in a `with-transaction` context.

## `clojure-doc.org`

All of the content from the library directory has been incorporated into
The Clojure Toolbox at this point. Every library that was previously listed
on `clojure-doc.org` is now listed on the Toolbox and all of the one-line
descriptions have been added to the Toolbox as well (which now show up
as tooltips when you hover over the library name/link). The Toolbox still has
a lot of libraries listed without descriptions so, hopefully, that's something
the community can add over time (or help James with automating, using project
descriptions from GitHub, perhaps?).

The library authoring guide has been substantially rewritten to use the Clojure
CLI, `deps.edn`, and `build.clj`. The old Leiningen-based library authoring guide
has been lightly updated and is still available, linked from the new guide.

In addition to the library work mentioned above, I've been working on
the [`tools.build` cookbook](https://clojure-doc.org/articles/cookbooks/cli_build_projects/).
I shared an early draft to get community feedback and then shared the
completed version this week. The whole thing is
over 3,000 words now, with a lot of code examples. I've tried to distill
everything I've learned about `tools.build` into a single document that
covers various scenarios that go beyond what is in the
[official `tools.build` guide](https://clojure.org/guides/tools_build).

Some additional community feedback has already been incorporated and more
will be incorporated over the next few weeks, I expect.

## What's Next?

In July/August, I'm hoping to complete a review and update of both the
"ecosystem" and "tutorials" section of clojure-doc.org, and then in the
two remaining periods, I'll tackle the "cookbooks" and "language" sections.

## On a personal note...

I mentioned in the previous update that my mother was in hospital and I
want to thank everyone who reached out to me with kind words and support.
She came home and was doing well for a while but then she had another fall and
she's back in hospital as I write this, this time with severe anemia on
top of her other issues. She's had a blood transfusion and seems to be
doing better but we don't know when she'll be home. It's times like these
when I really do feel the five and a half thousand miles between us...
