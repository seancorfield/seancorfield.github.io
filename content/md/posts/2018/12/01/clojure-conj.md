{:title "Clojure/conj Was Great!",
 :date "2018-12-01 13:40:00",
 :draft? true,
 :tags ["clojure" "conj" "jdbc"]}
[Clojure/conj](https://2018.clojure-conj.org/) is over for another year and, as always, it was a great experience. Lots of interesting and inspiring talks (which are probably all up on [ClojureTV](https://www.youtube.com/user/ClojureTV)
by now!) and of course the ever important "hallway track" which is the real value-add of attending the conference in person.

Just over a month ago, I [blogged about my expectations for Conj](http://corfield.org/blog/2018/10/25/clojure-conj/) so this is my follow-up post with
my thoughts on the sessions I actually attended.

* As I suspected, Stuart Halloway did show off some tooling that uses `prepl` but mostly it was focused on `datafy` and `nav`. [REBL](https://github.com/cognitect-labs/REBL-distro) is a REPL and visual browser for Clojure data that displays the "datafication" of results and lets you "navigate" into them based on how the results (and data representation) implement the protocols behind those two new functions. You can start the browser from your regular REPL and it uses a `prepl` socket server to communicate with your REPL process. It's hard to really appreciate it until you use it, and it is just a first cut of possible tooling built on the "generalized laziness" concept that underlies datafication and on-demand navigation. Because Stu put me on the hook during his talk, here's a rough first cut of `datafy`/`nav` support for [clojure.java.jdbc](https://github.com/clojure/java.jdbc/blob/master/src/main/clojure/clojure/java/jdbc/datafy.clj) that lets you browse query results and navigate into related records in other tables.
* Christopher Small explained how the [Pol-is](https://github.com/pol-is) AI-powered survey/feedback analysis platform helped break gridlock on a number of political issues in Taiwan by expanding the reach of democracy to the populace. Offering meaningful representation to broader segments of society is a great way to improve democratic systems around the world -- and it's all open source.
* Ghadi Shayban covered a lot of ground in his talk about what the Java ecosystem brings to the table in recent versions of Java -- including Java Time (8), a built-in HTTP client (11), new and improved garbage collection strategies --  as well as looking at some of the specifics of interop with Clojure (specifically varargs, type erasure, and reification).
* Lily Goh and Dom Kiva-Meyer talked about GraphQL (which is currently migrating to its own Foundation) and their [Serene library](https://github.com/paren-com/serene) which can generate `clojure.spec` declarations from GraphQL schema definitions, as well as providing a way to augment GraphQL's schema validation with additional predicates defined using `clojure.spec`. It looks very interesting and we'll probably be taking a look at this at work.
* Rich Hickey -- Maybe Not -- Rich is always worth the price of entry!
* Unsessions -- Looking forward to seeing the schedule for these. Past years' unsessions have included some real diamonds!
* David Chelimsky -- AWS, meet Clojure.
* Ben Kamphaus -- AI Systems: Foundations for Artificial Minds or Aaron Cummings -- Making Memories: Clojure For Hardware Engineers (And Others). I'm undecided but leaning toward the AI talk.
* Elena Machkasova -- Babel: middleware for beginner-friendly error messages -- Anything that helps with Clojure's error messages (much improved in Clojure 1.10!) is always worth learning about!
* Wilker Lucio da Silva -- Scaling Full-Stack Applications Over Hundreds of Microservices or Daniel Gregoire -- Tables Considered Helpful. Undecided again but leaning toward the table talk.
* Nikolas Göbel -- Reactive Datalog for Datomic or Tyler Hobbs -- Code goes in, Art comes out. Undecided but leaning toward the Datalog talk (because I'm not hugely interested in art/computing).
* Gary Fredericks -- What Are All These Class Files Even About? And Other Stories -- Having been repeatedly bitten by AOT, I'm looking forward to this!
* Tomomi Livingstone + Hans Livingstone -- Party REPL — A multi-player REPL built for pair-programming -- This looks very, very interesting!
* Rebecca Parsons -- Closing day two keynote.
* (Party!)
* Carin Meier -- Can you GAN? -- No idea about the topic but Carin is always an engaging speaker.
* Dave Fetterman -- Learning and Teaching Clojure on the job at Amperity -- Because coming up to speed (and bringing others up to speed) is important.
* Vikash Mansinghka -- Probabilistic programming and meta-programming in Clojure -- WAT?
* Alex Engelberg and Derek Slager -- Every Clojure Talk Ever -- Cryptically enticing...
