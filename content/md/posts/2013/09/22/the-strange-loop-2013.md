{:title "The Strange Loop 2013",
 :date "2013-09-22 11:00:00 -0700",
 :tags ["conferences"]}
<p>This was my second time at <a href="http://thestrangeloop.com">The Strange Loop</a>. When I attended in 2011, I said that it was one of the best conferences I had ever attended, and I was disappointed that family plans meant I couldn't attend in 2012. That meant my expectations were high. The main hotel for the event was the beautiful DoubleTree Union Station, an historic castle-like building that was once an ornate train station. The conference itself was a short walk away at the Peabody Opera House. Alex Miller, organizer of The Strange Loop, <a href="http://clojurewest.com">Clojure/West</a>, and <a href="http://lambdajam.com">Lambda Jam</a> (new this year), likes to use interesting venues, to make the conferences extra special.</p>
<p>I'm providing a brief summary here of what sessions I attended, followed by some general commentary about the event. As I said last time, if you can only attend one conference a year, this should be the one.</p>
<ul>
<li><a href="http://twitter.com/jrfinkel">Jenny Finkel</a> - Machine Learning for Relevance and Serendipity. The conference kicked off with a keynote from one of Prismatic's engineering team talking about how they use machine learning to discover news and articles that you will want to read. She did a great job of explaining the concepts and outlining the machinery, along with some of the interesting problems they encountered and solved.</li>
<li><a href="http://twitter.com/Love2Code">Maxime Chevalier-Boisvert</a> - Fast and Dynamic. Maxime took us on a tour of dynamic programming languages through history and showed how many of the innovations from earlier languages are now staples of modern dynamic languages. One slide presented JavaScript's take on <tt>n + 1</tt> for various interesting values of <tt>n</tt>, showing the stranger side of dynamic typing - a "WAT?" moment.</li>
<li><a href="http://twitter.com/mbroecheler">Matthias Broecheler</a> - Graph Computing at Scale. Matthias opened his talk with an interesting exercise of asking the audience two fairly simple questions, as a way of illustrating the sort of problems we're good at solving (associative network based knowledge) and not so good at solving (a simple bit of math and history). He pointed out the hard question for us was a simple one for SQL, but the easy question for us would be a four-way join in SQL. Then he introduced graph databases and showed how associative network based questions can be easily answered and started to go deeper into how to achieve high performance at scale with such databases. His company produces Titan, a high scale, distributed graph database.</li>
<li>Over lunch, two students from Colombia told us about the <a href="http://railsgirls.com">Rails Girls</a> initiative, designed to encourage more young women into the field of technology. This was the first conference they had presented at and English was not their native language so it must have been very nerve-wracking to stand up in front of 1,100 people - mostly straight white males - and get their message across. I'll have a bit more to say about this topic at the end.</li>
<li><a href="http://twitter.com/sadukie">Sarah Dutkiewicz</a> - The History of Women in Technology. Sarah kicked off the afternoon with a keynote tour through some of the great innovations in technology, brought to us by women. She started with Ada Lovelace and her work with Charles Babbage on the difference engine, then looked at the team of women who worked on the ENIAC, several of whom went on to work on UNIVAC 1. Admiral Grace Hopper's work on Flow-Matic - part of the UNIVAC 1 project - and subsequent work on COBOL was highlighted next. Barbara Liskov (the L in SOLID) was also covered in depth, along with several others. These are good role models that we can use to encourage more diversity in our field - and to whom we all owe a debt of gratitude for going against the flow and marking their mark.</li>
<li><a href="http://twitter.com/czaplic">Evan Czaplicki</a> - Functional Reactive Programming in Elm. This talk's description had caught my eye a while before the conference, enough so that I downloaded <a href="http://elm-lang.org">Elm</a> and experimented with it, building it from source on both my Mac desktop and my Windows laptop, during the prerelease cycle of what became the 0.9 and 0.9.0.2 versions. Elm grew out of Evan's desire to express graphics and animation in a purely functional style and has become an interesting language for building highly interactive browser-based applications. Elm is strongly typed and heavily inspired by Haskell, with an excellent abstraction for values that change over time (such as mouse position, keyboard input, and time itself). After a very brief background to Elm, Evan live coded the physics and interaction for a Mario platform game with a lot of humor (in just 40 lines of Elm!). He also showed how code updates could be hot-swapped into the game while it was running. A great presentation and very entertaining!</li>
<li><a href="http://twitter.com/keithmadams">Keith Adams</a> - Taking PHP Seriously. Like CFML, PHP gets a lot of flak for being a hot mess of a language. Keith showed us that, whilst the criticisms are pretty much all true, PHP can make good programmers very productive and enable some of the world's most popular web software. Modern PHP has traits (borrowed from Scala), closures, generators / yield (inspired by Python and developed by Facebook). Facebook's high performance "HipHop VM" runs all of their PHP code and is open source and available to all. Facebook have also developed a gradual type checking system for PHP, called Hack, which is about to be made available as open source. It was very interesting to hear about the pros and cons of this old warhorse of a language from the people who are pushing it the furthest on the web.</li>
<li><a href="http://twitter.com/chiuki">Chiu-Ki Chan</a> - Bust the Android Fragmentation Myth. Chiu-Ki was formerly a mobile app developer at Google and now runs her own company building mobile apps. She walked us through numerous best practices for creating a write-once, run-anywhere Android application, with a focus on various declarative techniques for dealing with the many screen sizes, layouts and resolutions that are out there. It was interesting to see a Java + XML approach that reminded me very much of Apache Flex (formerly Adobe Flex). At the end, someone asked her whether similar techniques could be applied to iOS app development and she observed that until very recently, all iOS devices had the same aspect ratio and same screen density so, with auto-layout functionality in iOS 6, it really wasn't much of an issue over in Apple-land.</li>
<li><a href="http://twitter.com/alissapajer">Alissa Pajer</a> - Category Theory: An Abstraction for Everything. In 2011, the joke was that we got category theory for breakfast in the opening keynote. This year I took it on by choice in the late afternoon of the first day! Alissa's talk was very interesting, using Scala's type system as one of the illustrations of categories, functors, and morphisms to show how we can use abstractions to apply knowledge of one type of problem to other problems that we might not recognize as being similar, without category theory. Like monads, this stuff is hard to internalize, and it can take many, many presentations, papers, and a lot of reading around the subject, but the abstractions are very powerful and, ultimately, useful.</li>
<li><a href="http://twitter.com/antiheroine">Jen Myers</a> - Making Software Development Make Sense For Everyone. Closing out day one was a keynote by Jen Myers, primarily known as a designer and front end developer, who strives to make the software process more approachable and more understandable for people. Her talk was a call for us all to help remove some of the mysticism around our work and encourage more people to get involved - as well as to encourage people in the software industry to grow and mature in how we interact. As she pointed out, we don't really want our industry to be viewed through the lens of movies like "The Social Network" which makes developers look like assholes!.</li>
<li><a href="http://twitter.com/odersky">Martin Odersky</a> - The Trouble with Types. The creator of Scala started day two by walking us through some of the commonly perceived pros and cons of both static typing and dynamic typing. He talked about what constitutes good design - discovered, rather than invented - and then presented his latest work on type systems: DOT and the Dotty programming language. This collapses some of the complexities of parameterized types (from functional programming) down onto a more object-oriented type system, with types as abstract members of classes. Compared to Scala (which has both functional and object-oriented types), this provides a substantial simplification without losing any of the expressiveness, and could be folded into "Scala.Next" if they can make it compatible enough. This would help remove one of the major complaints against Scala: the complexity of its type system!</li>
<li><a href="http://twitter.com/MridsJayaraman">Mridula Jayaraman</a> - How Developers Treat Ovarian Cancer. I missed Ola Bini's talk on this topic at a previous conference so it was great to hear one of his teammates provide a case study on this fascinating project. ThoughtWorks worked with the Clearity Foundation and Annai Systems - a genomics startup - to help gather and analyze research data, and to automate the process of providing treatment recommendations for women with ovarian cancer. She went over the architecture of the system and (huge!) scale of the data, as well as many of the problems they faced with how "dirty" and unstructured the data was. They used JRuby for parsing the various input data and Clojure for their DSLs, interacting with graph databases, the recommendation engine and the back end of the web application they built.</li>
<li><a href="http://twitter.com/cristalopes">Crista Lopes</a> - Exercises in Style. Noting that art students are taught various styles of art, along with analysis of those styles, and the rules and guidelines (or constraints) of those styles, Crista observed that we have no similar framework for teaching programming styles. The Wikipedia article on programming style barely goes beyond code layout - despite referencing Kernighan's "Elements of Programming Style"! She is writing a book called "Exercises in Programming Style", due in Spring 2014 that should showcase 33 styles of programming. She then showed us a concordance program (word frequencies) in Python, written in nine different styles. The code walkthrough got a little rushed at the end but it was interesting to see the same problem solved in so many different ways. It should be a good book and it will be educational for many developers who've only been exposed to one "house" style in the company where they work.</li>
<li><a href="http://twitter.com/marthakelly">Martha Girdler</a> - The Javascript Interpreter, Interpreted. Martha walked us through the basics of variable lookups and execution contexts in JavaScript, explaining variable hoisting, scope lookup (in the absence of block scope) and the foibles of "this". It was a short and somewhat basic preso that many attendees had hoped would be much longer and more in depth. I think it was the only disappointing session I attended, and only because of the lack of more material.</li>
<li><a href="http://twitter.com/dpp">David Pollak</a> - Getting Pushy. David is the creator of the Lift web framework in Scala that takes a very thorough approach to security and network fallibility around browser/server communication. He covered that experience to set the scene for the work he is now doing in the Clojure community, developing a lightweight push-based web framework called Plugh that leverages several well-known Clojure libraries to provide a seamless, front-to-back solution in Clojure(Script), without callbacks (thanks to core.async). Key to his work is the way he has enabled serialization of core.async "channels" so that they can be sent over the wire between the client and the server. He also showed how he has enabled live evaluation of ClojureScript from the client - with a demo of a spreadsheet-like web app that you program in ClojureScript (which is round-tripped to the server to be compiled to JavaScript, which is then evaluated on the client!).</li>
<li><a href="http://twitter.com/lmeyerov">Leo Meyerovich</a> - Thinking DSLs for Massive Visualization. I had actually planned to attend <a href="http://twitter.com/samj0hn">Samantha John</a>'s presentation on Hopscotch, a visual programming system used to teach children to program, but it was completely full! Leo's talk was in the main theater so there was still room in the balcony and it was an excellent talk, covering program synthesis and parallel execution of JavaScript (through a browser plugin that offloads execution of JavaScript to a specialized VM that runs on the GPU). The data visualization engine his team has built has a declarative DSL for layout, and uses program synthesis to generate parallel JS for layout, regex for data extraction, and SQL for data analysis. The performance of the system was <em>three orders of magnitude faster</em> than a traditional approach!</li>
<li><a href="http://twitter.com/ibdknox">Chris Granger</a> - Finding a Way Out. Some of you may have been following Chris's work on <a href="http://lighttable.com">LightTable</a>, an IDE that provides live code execution "in place" to give instant feedback as you develop software. If you're doing JavaScript, Python, or Clojure(Script), it's worth checking out. This talk was more inspirational that product-related (although he did show off a proof of concept of some of the ideas, toward the end). In thinking about "How do we make programming better?" he said there are three fundamental problems with programming today: it is unobservable, indirect, and incidentally complex. As an example, consider <tt>person.walk()</tt>, a fairly typical object-oriented construct, where it's impossible to see what is going on with data behind the scenes (what side effects does it have? which classes implement <tt>walk()</tt>?). We translate from the problem domain to symbols and add abstractions and indirections. We have to deal with infrastructure and manage the passage of time and the complexities of concurrency. He challenged us that programming is primarily about transforming data and posited a programming workflow where we can see our data and interactively transform it, capturing the process from end to end so we can replay it forwards and backwards, making it directly observable and only as complex as the transformation workflow itself. It's an interesting vision, and some people are starting to work on languages and tools that help move us in that direction - including Chris with LightTable and Evan with Elm's live code editor - but we have a long way to go to get out of the "<a href="http://shaffner.us/cs/papers/tarpit.pdf">tar pit</a>".</li>
<li>Douglas Hofstadter, David Stutz, a brass quintet, actors, and aerialists - Strange Loops. The two-part finale to the conference began with the author of "G&ouml;del, Escher, and Bach" and "I am a Strange Loop" talking about the concepts in his books, challenging our idea of perception and self and consciousness. After a thought-provoking dose of philosophy, David Stutz and his troope took to the stage to act out a circus-themed musical piece inspired by Hofstadter's works. In addition to the live quintet, Stutz used Emacs and Clojure to provide visual, musical, and programmatic accompaniment. It was a truly "Strange" performance but somehow very fitting for a conference that has a history of pushing the edges of our thinking!</li>
</ul>
<p>Does anything unusual jump out at you from the above session listing? Think about the average technical conference you attend. Who are the speakers? Alex Miller and the team behind The Strange Loop made a special effort this year to reach out beyond the "straight white male" speaker community and solicit submissions from further afield. I had selected most of my schedule, based on topic descriptions, before it dawned on me just how many of the speakers were women: over half of the sessions I attended! Since I didn't recognize the vast majority of speaker names on the schedule - so many of them were from outside the specific technical community I inhabit - I wasn't really paying any attention to the names when I was reading the descriptions. The content was excellent, covering the broad spectrum I was expecting, based on my experience in 2011, with a lot of challenging and fascinating material, so the conference was a terrific success in that respect. That so many women in technology were represented on stage was an unexpected but very pleasant surprise and it should provide an inspiration to other technology conferences to reach beyond their normal pool of speakers too. I hope more conferences will follow suit and try to address the lack of diversity we seem to take for granted!</p>
<p>I already mentioned the great venues - both the hotel and the conference location - but I also want to call out the party organized at the <a href="http://citymuseum.org/">St Louis City Museum</a> for part of the overall "wonder" of the experience that was The Strange Loop 2013. The City Museum defies description. It is a work of industrial art, full of tunnels and climbing structures, with a surprise around every corner. Three local breweries provided good beer, and there was a delicious range of somewhat unusual hot snacks available (bacon-wrapped pineapple is genius - that and the mini pretzel bacon cheeseburgers were my two favorites). It was quiet enough on the upper floors to talk tech or chill out, while <a href="http://moonhooch.bandcamp.com/">Moon Hooch</a> entertained loudly downstairs, and the outdoor climbing structures provided physical entertainment for the adventurous with a head for heights (not me: my vertigo kept me on the first two stories!).</p>
<p>In summary then, the "must attend" conference of the year, as before! Kudos to Alex Miller and his team!</p>