### Pinot is still very alpha, symbol names and the general shape of the API are still likely to change

##Pinot
Pinot is a ClojureScript client-side framework designed to make it easy to write your websites completely in Clojure.

Currently Pinot provides a wrapper over several DOM interaction points in the goog.\* libraries. It also provides an implementation of [Hiccup](https://github.com/weavejester/hiccup) that translates directly into DOM objects instead of a string of HTML.

For an example look at the examples/todo.cljs

##Usage
From any leiningen project file:

```clojure
[pinot "0.1.1-SNAPSHOT"]
```
Then make sure you have your lib folder on your classpath.

For the best development experience, try out [cljs-watch](https://github.com/ibdknox/cljs-watch). If you do, you'll need to replace the goog.jar in your CLOJURESCRIPT_HOME/lib with the goog-jar.jar that comes down as a dependency with pinot.

###Dom manipulation

Pinot includes a basic set of dom interaction pieces, including an implementation of hiccup:

```clojure
(ns client.test
  (:require [pinot.html :as html]
            [pinot.dom :as dom])
  (:require-macros [pinot.macros :as pm]))

(def x (html/html [:p [:em "hey"]]))
(dom/css x {:color :blue})
(dom/attr x {:class "para"})
(dom/val (dom/query "input"))
(dom/append (dom/query "#content div.body") x)
```

Pinot also includes `(defpartial)` like in Noir, however Pinot derives even greater advantage from it:

```clojure
(pm/defpartial todo [{:keys [done? text]}]
  [:li
   [:h2 t]
   [:span {:class (when done? "done")}]])

;;You can pass the partial function to dom-find to find all the todos.
(def all-todos (dom/query todo))
```

###Events

Events can also take advantage of partials

```clojure
(ns playground.client.test
  (:require [pinot.dom :as dom]
            [pinot.events :as events]))

(events/on (dom/query "li") :click
           (fn [me e]
             (dom/css me {:background :blue})
             (events/prevent e)))

;; Partials can also be passed here, allowing you to add an event to every
;; element created through that partial.
(events/on todo :click
           (fn [me e]
             (dom/css me {:background :blue})
             (events/prevent e)))
```

###Remotes

Remotes let you make calls to a noir server without having to think about XHR. On the client-side you simply have code that looks like this:

```clojure
(ns playground.client.test
  (:require [pinot.remotes :as remotes])
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

###Visualization

Pinot also includes a set of functions for creating visualizations in the style of [D3](https://github.com/mbostock/d3).

```clojure
(ns playground.client.test
  (:require [pinot.dom :as dom]
            [pinot.draw.visualization :as vis])
  (:require-macros [pinot.macros :as pm]))

(def items (range 0 10))

;;For SVG we have to namespace our elements
(pm/defpartial canvas []
                [:svg:svg {:width 800 :height 400}])

(pm/defpartial item [x]
                [:svg:circle {:r (* 2 x)}])

(dom/append (dom/query "#wrapper")
                (canvas))

(-> (vis/visual items)
  (vis/elem item)
  (vis/attr :stroke "#333")
  (vis/attr :fill "#777")
  (vis/attr :cx #(+ 20 (rand-int 800)))
  (vis/attr :cy #(+ 80 (* 10 (mod % 4))))
  (vis/enter (partial dom/append (dom/query "svg"))))

(-> (vis/select item)
  (vis/transition 500)
  (vis/data items)
  (vis/attr :cx #(* 50 %))
  (vis/attr :cy #(+ 30 (* 20 (mod % 3))))
  (vis/start))
```

## Docs
* Coming soon...

Checkout examples/todo.cljs as an example.

## Roadmap

* more DOM manipulation
* finish events
* MVC replacement (something like the Clojure version of backbone).

## License

Copyright (C) 2011 Chris Granger

Distributed under the Eclipse Public License, the same as Clojure.

