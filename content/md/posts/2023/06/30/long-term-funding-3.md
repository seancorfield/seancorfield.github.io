{:title "Long-Term Funding, Update #3",
 :date "2023-06-30 12:00:00",
 :tags ["clojure" "clojure-doc.org" "honeysql" "clojure-clr" "open source" "community" "clojurists together"]}

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
[`honeysql` v2.0.1033](https://github.com/seancorfield/honeysql/releases/tag/v2.4.1033).
`tools.nrepl` has been ported to ClojureCLR and Peter Strömberg (maintainer of Calva)
has created a [ClojureCLR starter project for VS Code/Calva](https://github.com/PEZ/clojure-clr-starter)
which I've also been helping to test on Windows and Ubuntu.

## `clojure-doc.org`

All of the content from the library directory has been incorporated into
The Clojure Toolbox at this point. Every library that was previously listed
on `clojure-doc.org` is now listed on the Toolbox and all of the one-line
descriptions have been added to the Toolbox as well. The Toolbox still has
a lot of libraries listed without descriptions so, hopefully, that's something
the community can add over time (or help James with automating, using project
descriptions from GitHub?).

The library authoring guide has been substantially rewritten to use the Clojure
CLI, `deps.edn`, and `build.clj`. The old Leiningen-based library authoring guide
has been lightly updated and is still available, linked from the new guide.

In addition to the library work mentioned above, I've been working on
the `tools.build` cookbook...

## What's Next?

In July/August, I'm hoping to complete a review and update of both the
"ecosystem" and "tutorials" section of clojure-doc.org, and then in the
two remaining periods, I'll tackle the "cookbooks" and "language" sections.
