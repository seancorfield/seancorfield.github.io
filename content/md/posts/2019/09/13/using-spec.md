{:title "How do you use clojure.spec",
 :date "2019-09-13 11:00:00",
 :tags ["clojure" "expectations"]}

An interesting Clojure question came up on Quora recently and I decided that [my answer to "how do you use clojure.spec"](https://www.quora.com/If-you-use-Clojure-Spec-how-do-you-use-it-Do-you-tend-to-put-all-your-specs-in-one-place-or-distribute-them-through-the-modules-of-your-program/answer/Sean-Corfield?srid=upNN) there should probably be a blog post so that folks without a Quora account can still read it. _[If you do have a Quora account, feel free to read it there instead and upvote it!]_<!-- more -->

The original question on Quora was:

> If you use Clojure Spec, how do you use it? Do you tend to put all your specs in one place or distribute them through the "modules" of your program?

Here's what I wrote, back in August 2019:

We've been using Spec very heavily at work since the first alpha builds appeared. We use Spec in a number of ways: we use it for data validation (production), we use it for test data generation for example tests and for generative testing of functions (as well as generative scenario testing -- more below), we use it at dev time as a checker for function calls (instrumentation) as well as for deriving code from the specs of data structures.

**Data validation**

We write specs for our API inputs and these specs live in their own namespace, along with the predicates needed for the specs. We `s/conform` the inputs against the specs and test the result with `s/invalid?` If the inputs conformed, we process the conformed data, else we use `s/explain-data` and then a heuristic algorithm to turn the explained failure back into an error code and message for API consumers. Note that we actually do some coercion of string inputs into basic data types via some of these specs -- this is not considered good practice by some people but we've found it very convenient for this use case.

We make sure nearly all of these API input specs will generate so that we can use them to generate test data for example-based tests (next section) or generative testing of some APIs (this isn't practical for a lot of API endpoints but it can help in a few cases).

**Test data generation**

As noted above, we use the API input specs -- and other specs -- to generate some test data for example-based tests. We use this approach where we want a certain amount of random "good" data to throw at functions in general, when we only need to control a couple of the arguments or a couple of fields in those arguments -- the randomly-generated data tends to be fairly "edge case" so it's a good test of robustness (e.g., strings that include Unicode characters, generated from a regex using Gary Fredericks' test.chuck library).

**Generative testing of functions**

For some "key" functions, we'll write specs that include `:ret` and/or `:fn` and we'll test these generatively. The specs live in the same namespace as the functions, usually immediately above the function they are for. Some of these generative checks are run as part of our main test suite (so they are run every time). Some are too slow for that and the `check` calls live in `(comment ,,,)` to be run manually when we've been working on the function in question (we use Rich Comment Forms a lot for code that is convenient for our development workflow but does not really constitute test code).

**Generative scenario testing**

This might be an unusual use of specs but I think it's potentially an important one. If you have an interactive application, you will have a number of paths through it that your users can take to get to certain known states. If you think of the paths as collections of user-generated events then you can write a spec for those (valid) paths through the application and you can then rely on spec to generate events that you can use to drive your application and then verify the end state. These specs live in the tests where they are needed.

**Development time checking**

This is a pretty basic, standard use of spec. Write specs for data structures and some functions. The latter live with the functions (`s/fdef` immediately above `defn`). The former may live with the functions if they are tightly coupled to them or in their own namespace if they are more concerned with data structures used by various namespaces. Then you instrument those functions while you are working in the REPL, doing day-to-day development.

Another use of specs in development/testing is within the tests where we want to verify that results from functions are the expected "shape" in example-based tests. In this case we write specs for "good" results and "bad" results and we use [clojure-expectations/clojure-test](https://github.com/clojure-expectations/clojure-test) as an add-on for `clojure.test` so that we can `(expect ::some-spec (a-function call here))` -- a more expressive form of `clojure.test/is`.

**Deriving code from specs**

In this case we write specs for data structures, such as rows in database tables, and then we generate named CRUD operations and supporting functionality from the specs themselves -- using macros that take specs as input with some control parameters and expand into a number of `defn` and other forms. The important aspect of this is that the spec is the "system of record" for the data structure: it can be used for validation, test data generation, and as the source for the keys and "types" that shape the functions needed to operate on them.
