(ns playground
  (:require [pinot.html :as ph]
            [pinot.events :as pe]))

(def items (ph/html [:li "test"]
                    [:li "cool"]
                    [:li "yay!"]))
(ph/append-to (ph/dom-find "body") (ph/html [:ul {:id "list"}]))
(ph/append-to (ph/dom-find "body") (ph/html [:a {:id "button"} "hey"]))
(ph/append-to (ph/dom-find "#list") items)

;; Listen to the click event for "#button" [created manually] and append a message to the body.
(def button (first (ph/dom-find "#button")))
(pe/on button :click (fn [_]
                       (ph/css button :color :blue)))
