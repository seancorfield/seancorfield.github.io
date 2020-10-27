{:title "Where Did 2015 Go?",
 :date "2016-01-03 16:00:00",
 :tags ["clojure" "frege" "conferences" "diversity"]}
I did not intend to stop blogging in 2015 but that's certainly what it looks like here!

So what kept me so busy that I didn't get around to blogging anything?<!-- more -->

Almost a year ago, I [talked about my Leiningen plugin for Frege](http://corfield.org/blog/2015/02/13/frege-and-clojure/). Back then it was version 3.22.367. That plugin is now version 3.23.450 and there's a Leiningen template to go with it, tracking prerelease builds of Frege which I've been publishing to Sonatype's OSS Snapshots repository.

    lein new frege myapp

That will generate a new, pure Frege project for you to build on. If you want a mixed Frege and Clojure project, like the original concept:

    lein new frege myapp -- :with-clojure

I haven't gotten to work with Frege as much as I'd hoped but I managed to contribute ports of two very small Haskell 2010 libraries (`System.Environment` and `System.Exit`) as well as exposing a few more pieces of Java's `Runtime` class in Frege's `java.lang.System` data type. I hope 2016 will bring a lot more Frege to my life!

In that blog post, I also talked about learning a new language every year. I mentioned [Elm](http://elm-lang.org/): I'd experimented with it quite a bit in its early days and I'd hoped to continue experimenting, but work and life distracted me from front end concerns and I lost touch with Elm's evolution. All I can say is that Elm continues to go from strength to strength, and it is gaining more press inches and more visibility at conferences, which is all good. It's truly innovative and I want to see it succeed! I did however spend quite a bit of time learning a little [Rust](https://www.rust-lang.org) and it makes me wish I did a lot more close-to-the-metal programming: it's a _really_ nice language and the "borrowing" system is very impressive (even if you find yourself fighting with it a lot at first!).

In the past, I've also blogged about almost every conference I've attended so you would be forgiven for thinking I didn't attend any events in 2015. I was able to attend The Strange Loop and Clojure/conj this past year. I would have loved to have attended Clojure/West as [my colleague Fumiko gave a talk about HoneySQL](http://clojurewest.org/speakers#fhanreich). That link will evaporate when this year's conference comes around but you can [watch Fumiko's talk about HoneySQL](https://www.youtube.com/watch?v=alkcjyhesjI) on ClojureTV. It was her first ever conference talk and she did a great job -- I'm very proud of her! Oh, she also likes Elm.

Fumiko and I worked on a project with ClojureScript, [Reagent](http://reagent-project.github.io/) -- a ClojureScript wrapper for [React.js](http://facebook.github.io/react/), and [Sente](https://github.com/ptaoussanis/sente) -- core.async over WebSockets. It was a lot of fun and very interesting. It was a proof of concept but, in the end, our company decided to go with JavaScript as being more mainstream (but still with React.js so, win!).

[The Strange Loop](http://www.thestrangeloop.com/) was its usual amazing self. Alex Miller and his team manage to excel themselves, year-on-year. The keynote talks were _phenomenal_ this year with Idalin Bob&eacute;'s inspiring talk about activism and Morgan Marquis-Boire's terrifying security and espionage talk as the highlights of the whole conference. The "theme" this year was distributed systems. I think this image sums it up well:

![It's Fine!](/img/distributed.jpg)

The other highlight for me was a social event: the [LGBTQ in Technology Slack](http://lgbtq.technology/) dinner, generously sponsored by [Code Climate](https://codeclimate.com/). The Strange Loop team work hard to ensure the conference is a diverse, welcoming, safe space for everyone and I was excited to be able to organize this dinner with the support of the conference organizers.

That social theme continued at [Clojure/conj](http://clojure-conj.org/) in Philadelphia where I was able to spend time with more folks from the LGBTQ in Technology Slack community (and had a lovely dinner at [The Twisted Tail](http://www.thetwistedtail.com/) with my friend [Danielle](https://twitter.com/quephird). If you're ever in Philly, *go to that restaurant*!!).

As usual, Clojure/conj was an incredible mix of real world Clojure, academic research, and bleeding edge exploration in industry. One talk made the entire conference worth the cost for me: Bobby Calderwood's [From REST to CQRS](https://www.youtube.com/watch?v=qDNPQo9UmJA) with Clojure, Kafka (and Samza), and Datomic. The "hallway track" that followed this talk caused me to miss the last two talks but it was oh so valuable! Fast forward two months and we're starting down the path of Kafka and distributed systems at work. Priceless, as they say. Other highlights were the father and son Engelbergs on Automata, Ram Krishnan's "Clojure for Business Teams", Stuart Halloway's keynote "Debugging with the Scientific Method" (*everyone* should watch [this talk](https://www.youtube.com/watch?v=FihU5JxmnBg)), Lee Spector's "Genetic Programming" talk, and Benjamin Pierce's keynote about formal specifications and generative testing.

And then there was work...

I love my job, I love my team, and we get to solve some fascinating problems at [World Singles](http://worldsinglesnetworks.com/). The company has a history of using [ColdFusion](http://www.adobe.com/products/coldfusion-family.html), although we switched to a Free Open Source Software alternative in 2009 and we've been running on [Lucee](http://lucee.org/) for most of 2015. We started using Clojure in production almost five years ago now (with Clojure 1.3 Alpha 7 or Alpha 8) and we declared it our "primary language" about a year ago. Since then we've systematically rewritten code in Clojure as we've needed to enhance functionality -- definitely a situation where "refactor" really does mean "rewrite". I created a bridge project that allows Cloure to be loaded and run easily inside CFML engines about five years ago and that's continued to evolve. The MVC framework I created in 2009 has continued to evolve as well, with 2015 seeing release 3.1 and 3.5 -- the latter has integrated the Clojure bridge, to enable seamless mixed language projects. We rely heavily on this. Release 4.0 is almost ready for Alpha 1, and focuses on enhancing REST API capabilities.

In addition, we created an OAuth 2 server, mostly in Clojure, to support our REST API, and we've worked hard to move all of our persistence from CFML's "query" functionality to a Clojure "[data mapper](https://github.com/seancorfield/datamapper)" based on [clojure.java.jdbc](http://clojure-doc.org/articles/ecosystem/java_jdbc/home.html). We closed out 2015 with _all_ of our persistence handled by Clojure, a big win for performance and maintainability!

Our other big change, coming at the tail end of year, was a switch from [Leiningen](http://leiningen.org/) to [Boot](http://boot-clj.com/) for our primary Clojure build tool. I'll be blogging about this more in due course, but for now I'll just mention the [Boot task for running Expectations tests](https://github.com/seancorfield/boot-expectations) and that Leiningen-style templates will be coming to Boot soon...

Happy 2016!
