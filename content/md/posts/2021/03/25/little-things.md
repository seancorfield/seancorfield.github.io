{:title "It's the \"Little Things\"...",
 :date "2021-03-25 21:10:00",
 :tags ["clojure"]}

Our Clojure team is a big fan of reducing dependencies and, in particular,
avoiding dependencies that are known to be troublesome (such as the special
circle of hell that is all the different versions of the Jackson JSON libraries).<!--more-->

We've recently been looking at switching from libraries that have a lot
of dependencies to equivalent libraries that have fewer (or none) if they
are "fast enough".

One example is [Cheshire](https://github.com/dakrone/cheshire) because
of its Jackson dependencies -- and the fact that it is widespread and
often pulled in transitively in different versions, which in turn want
to bring in different versions of Jackson. For a long time, your high
performance choices were Cheshire or [Jsonista](https://github.com/metosin/jsonista),
which is also a Jackson-based library. [`data.json`](https://github.com/clojure/data.json)
just wasn't fast enough to be considered for a lot of use cases, even
though it is a pure Clojure project with zero dependencies.

`data.json` recently got a serious overhaul, courtesy of [Erik Assum](https://github.com/slipset),
that addressed a lot of performance issues and has made it reasonable
to use this library in all but the most time-critical situations.
At work, we decided to switch from Cheshire to `data.json` to reduce
our dependencies -- I'll talk a bit more about that later.

## clj-http and http-kit

Our latest target is [clj-http](https://github.com/dakrone/clj-http) which
has a bunch of Apache HTTP dependencies and Slingshot and Potemkin, and
then indirectly relies on Cheshire and several other "optional" libraries
for convenient features like automatic JSON, EDN, and Transit support.
For historical reasons (we leverage quite a bit of our Clojure code from
a legacy, non-Clojure web application), the Apache HTTP dependencies in
particular are a pain for us because we need to control the versions so
that they are compatible with both clj-http *and* our legacy web container.
And of course we'd just gone through the Cheshire-to-`data.json` migration
so having clj-http drag Cheshire back in was less than ideal.

Functionally, there's nothing wrong with clj-http (just as there's nothing
functionally wrong with Cheshire). But non-functional requirements can be
important too sometimes.

Migration looked fairly simple:

* add the http-kit dependencies to all subprojects that currently depend on clj-http
* edit each namespace that uses `clj-http.core` to use `org.httpkit.client` instead and add a `deref` on all of the calls (http-kit is async by default)
* edit the options passed in the calls:
  * clj-http throws exceptions by default for a lot of things and you can pass `:throw-exceptions false` to make it return HTTP status instead; http-kit doesn't throw exceptions (but can return an `:error` key for any non-HTTP exception encountered)
  * clj-http can process the body automatically `:as :json` (if you have Cheshire on your classpath!); http-kit requires explicit JSON conversion

Since clj-http only decodes the response body on a successful call,
we already had to JSON-decode response bodies for non-200 HTTP status
responses. Since we had that code in place, it wasn't much work to
make it JSON-decode response bodies for 200 status responses as well.

The migration was mostly fairly smooth but one weird hiccup took me hours
to debug -- and that's what I want to talk about here.

Back in the day, our main REST-like API was written in a non-Clojure language and
so were our initial API tests. At some point, it was considered important
that certain API endpoints trimmed whitespace on some of their arguments,
so the tests were updated to include query arguments with trailing whitespace.
All was good: the code was updated to trim arguments and the tests passed.

When we introduced Clojure, we rewrote the tests to use clj-http and
[Expectations](https://github.com/clojure-expectations/clojure-test) and the
new test suite added lots more tests -- and retained the existing ones that
build query strings with trailing spaces. Everything passed (after fixing a
few bugs that the newly-added tests detected).

Then we rewrote the legacy API in Clojure and got all the tests working
again: we were confident that our new API server could replace our legacy
API server and we were happy, and we migrated and everything continued
to work just fine. In fact, life was much better: the Clojure version used
less memory and ran faster, and was easier to deploy.

This week, I converted the tests from clj-http to http-kit and the
trailing-whitespace tests failed. Hmm. I added a few logging calls into
the API and discovered that the trailing whitespace was indeed coming
through "as expected" but the API was not handling it. How could this be?
We'd had tests for _years_ that passed trailing whitespace into the API!

I reverted to clj-http but keep the logging calls and was _shocked_ to
discover that, somehow, clj-http was _trimming the trailing whitespace from the URL_!
That seemed incredible to us so we started digging. Nothing in the clj-http
source suggested that it was actively trimming whitespace anywhere.
It _was_ doing some slightly sketchy "URL encoding" on the full URL
passed in but it definitely wasn't _trimming_ anything.

I continued to dig and test and dig and test and after several hours(!)
I had drilled down to a call to `java.net.URL`'s constructor in the
clj-http code as the culprit:

```clojure
user=> (java.net.URL. "http://localhost:3333/email?foo=bar   ")
#object[java.net.URL 0x4ba32242 "http://localhost:3333/email?foo=bar"]
```

When clj-http is analyzing the URL and its components, it leans on `java.net.URL`
to pick things apart, before going on to use `java.net.URI` for the
actual HTTP call -- and this was completely incidentally stripping
whitespace from the URL!

http-kit also uses `java.net.URI` for setting up the actual call but
does not round-trip the URL through `java.net.URL`.

This meant that our tests had been broken for years and we just hadn't known!
And, technically, our new API had also been broken since the rewrite
to Clojure -- but no one had noticed or complained so we decided to
simply remove the tests that had been broken by clj-http's whitespace trimming.

### Sketchy URL Encoding

I mentioned above that clj-http seemed to be doing some slightly
sketchy URL encoding and I uncovered that via another test that
broke when I switched to http-kit. This test was deliberately invoking
an API endpoint with a URL that included a space in the resource ID.

A client application in a browser would URL encode such a request
of course so this test was just making sure that such requests worked.

http-kit threw an exception instead. `java.net.URI` does not allow
a URI that contains a space.

But.

Wait a minute, clj-http also uses `java.net.URI` so why doesn't it
throw an exception?

Because clj-http includes a [very specific encoding](https://github.com/dakrone/clj-http/blob/3.x/src/clj_http/client.clj#L160-L168)
of the URL's path and query string that doesn't just use
`java.net.URLEncoder/encode` but instead directly replaces
spaces with `%20` and then runs `encode` on portions of the
URL segments that include characters outside a particular set.

clj-http was masking an error in our tests which http-kit exposed.
The fix this time was to update the tests to use properly
encoded URLs in the first place. At least the API code was
already doing the right thing in this case!

## Cheshire and data.json

I said I'd talk about our migration from Cheshire to `data.json`
because that also had a few speed bumps. Cheshire is very
helpful: it converts quite a few non-JSON data types to strings
for you, such as UUIDs and dates. It was so convenient that it
had never occurred to me that `data.json` did not do this.

Naturally, we relied on these extra conversions, but `data.json`
lets you provide a `:value-fn` converter so you can supply
them yourself.

The most obvious one was from `java.util.Date` or `#inst` to
a format that turned out to be `ISO_INSTANT` in Java Time.
This is Cheshire's default. We already had a similar converter for
`java.time.Instant` in our code so this was trivial to add.

More tests passed.

The next failure was because we passed a UUID through Cheshire,
now `data.json`, but that's easy to handle just by calling `str`
to get the same result as Cheshire.

More tests passed.

The final failure seemed to be date-related. We were now
getting an exception from Java Time that it couldn't format
the date. Given the converter we had already installed, this
seemed strange... until we realized the new date was `java.sql.Date`
and _not_ `java.util.Date` and the SQL version very helpfully
throws `UnsupportedOperationException` from its `toInstant()`
method -- because it has no seconds.

So we added a special case for `java.sql.Date` that round-tripped
through `java.time.LocalDate` to `java.time.LocalDateTime` and
that worked perfectly.

Then we hit another date-related failure and this time it was
because we were using a custom string pattern instead of
`ISO_INSTANT` (and, frankly, this is why Java Time makes me
pull my hair out: both `ISO_INSTANT` and our custom pattern
want year, month, day, hour, minute, second but apparently
they want some of these fields "differently" so different
Java Time types may or may not satisfy the criteria).

A bit of conditional futzing around based on whether we wanted
the default format or our custom format and we were back off
to the races!

## It's the Little Things in life...

Overall, both migrations -- from Cheshire to `data.json` and
from clj-http to http-kit -- have gone very smoothly and I'll
attribute a lot of that to the careful design that many Clojure
developers apply to their code.

But both migrations also included bizarre edge cases that
produced hard-to-debug test failures that, ultimately, came
down to peculiarities of how those libraries used the underlying
Java standard library functions. Hmm...

## What about java.net.http?

Given our desire to reduce dependencies, a very reasonable question
would be "Why not just use [Java's built-in HTTP client](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html)?"

It's a very well-designed and flexible HTTP client, providing both
synchronous and asynchronous operation, as well as ways to plug in
your own response body processing via handlers and subscribers. It
has a modern "builder-style" interface and I'm sure it is lovely
to use from Java.

Two problems for us:

1. Frankly, the builder-style APIs are nasty and it's nearly always more convenient in Clojure to use a wrapper that lets you provide the entire configuration as a plain old hash map instead of all those chained interop calls.
2. A lot of our Clojure code that does HTTP calls is shared between our modern, all-Clojure apps and our legacy, non-Clojure code... and that legacy code has to run on Java 8, unfortunately, which means no `java.net.http` as that only arrived in Java 11.

_The latter issue has been a big driver for our rewrite to Clojure!_