(ns todo.core
  (:require [pinot.html :as ph]
            [pinot.html.tags :as tags]
            [pinot.validation :as vali]
            [pinot.events :as pe])
  (:require-macros [pinot.macros :as pm]))

(def todos (atom {1 "Get milk"
                  2 "Pay bills"
                  3 "Do stuff"}))

(defn add-todo [text]
  (let [neue-key (inc (apply max (keys @todos)))]
    (swap! todos assoc neue-key text)
    [neue-key text]))

(defn remove-todo [id]
  (swap! todos dissoc id))

(pm/defpartial todo-item [[id item]]
            [:li (tags/link-to {:class "remove" :id id} "#" item)])

(pm/defpartial todo-form []
            (tags/form-to {:id "todoForm"} [:post "/todos"]
                     (tags/label "todoText" "Todo: ")
                     (tags/text-field "todoText")
                     (tags/submit-button {:class "submit"} "add todo")))

(ph/append-to (ph/dom-find "body")
              (ph/html
                [:div
                  (todo-form)
                  [:ul 
                    (map todo-item @todos)]]))

(pe/on (ph/dom-find "#todoForm") :submit 
       (fn [me e]
         (let [text (ph/val (ph/dom-find "#todoText"))
               neue (add-todo text)]
           (ph/append-to (ph/dom-find "ul") (todo-item neue))
           (pe/prevent e))))

(pe/on (ph/dom-find "a.remove") :click
       (fn [me e]
         (let [id (ph/attr me :id)]
           (remove-todo id)
           (ph/unappend me)
           (pe/prevent e))))
