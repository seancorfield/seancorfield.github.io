{:title "The new clojure-doc web site",
 :date "2021-11-14 16:00:00",
 :tags ["clojure" "clojure-doc.org"]}

Back when I was working on the `clojure.java.jdbc` Contrib library, I moved
its documentation to clojure-doc.org so that the community could contribute
to it, without the CLA that covers contributions to Contrib itself. Over time
I became a general contributor to `clojuredocs/guides` which was the repository
behind the clojure-doc.org web site.

Unfortunately, about three years ago, the infrastructure that runs clojure-doc.org
became inaccessible to the maintainers of the site so, although pull requests
continued to be accepted, the site itself could no longer be updated. I talked
with Michael Klishin, the original creator of the site, about moving it to GitHub
pages but we never quite got around to it. Until today.

[clojure-doc.github.io](https://clojure-doc.github.io) is the new version of the site,
hosted as GitHub pages, under a new organization (to match the domain). It has been
converted from Jekyll to [Cryogen](http://cryogenweb.org/) so it is "powered by Clojure"
now. The look'n'feel could do with some love and there may still be some broken links
in there -- [contributors welcome](https://github.com/clojure-doc/clojure-doc.github.io)! --
and there is definitely some stale content that could do with updating.

Many people have contributed to clojure-doc.org over the years and I hope they and many
more will do so for this new version of the site!
