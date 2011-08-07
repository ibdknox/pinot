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

