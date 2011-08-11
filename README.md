### Pinot is still very alpha symbol names and the general shape of the API are still likely to change

##Pinot
Pinot is a ClojureScript client-side framework designed to make it easy to write your websites completely in Clojure.

Currently Pinot provides a wrapper over several DOM interaction points in the goog.\* libraries. It also provides an implementation of [Hiccup](https://github.com/weavejester/hiccup) that translates directly into DOM objects instead of a string of HTML.

For an example look at the src/todos/core.cljs

##Usage
From any leiningen project file:

```clojure
[pinot "0.1.0-SNAPSHOT"]
```
Then make sure you have your lib folder on your classpath.

If you are in a noir project, make sure to get [noir-cljs](https://github.com/ibdknox/noir-cljs) so you have your normal "make a change, refresh" workflow.

###remotes

Remotes let you make calls to a noir server without having to think about XHR. On the client-side you simply have code that looks like this:

```clojure
(ns playground.client.test
  (:require [pinot.html :as html]
            [pinot.remotes :as remotes])
  (:require-macros [pinot.macros :as pm]))

(pm/remote (adder 2 5 6) [result]
  (js/alert result))

(pm/remote (get-user 2) [{:keys [username age]}]
  (js/alert (str "Name: " username ", Age: " age)))

;;Also just added letrem
(pm/letrem [a (adder 3 4)
            b (adder 5 6)]
    (js/alert (str "a: " a " b: " b)))
```

Note that the results we get are real Clojure datastructures and so we use them just as we would in normal Clojure code. No JSON here.

The noir side of things is just as simple. All you do is declare a remote using defremote.

```clojure
(use 'noir.pinot.remotes)

(defremote adder [& nums]
           (apply + nums))

(defremote get-user [id]
           {:username "Chris"
            :age 24})

(server/add-middleware wrap-remotes)

(server/start 8080)
```

## Docs
* Coming soon...

Checkout src/todos/core.cljs as an example.

## Roadmap

* more DOM manipulation
* finish events
* MVC replacement (something like the Clojure version of backbone).

## License

Copyright (C) 2011 Chris Granger

Distributed under the Eclipse Public License, the same as Clojure.

