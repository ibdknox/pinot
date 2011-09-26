(ns todo.core
  (:require [pinot.html :as ph]
            [pinot.html.tags :as tags]
            [pinot.dom :as dom]
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

(dom/append (dom/query "body")
              (ph/html
                [:div
                  (todo-form)
                  [:ul 
                    (map todo-item @todos)]]))

(pe/on todo-form :submit 
       (fn [me e]
         (let [text (dom/val (dom/query "#todoText"))
               neue (add-todo text)]
           (dom/append (dom/query "ul") (todo-item neue))
           (pe/prevent e))))

(pe/on todo-item :click
       (fn [me e]
         (let [id (dom/attr me :id)]
           (remove-todo id)
           (dom/unappend me)
           (pe/prevent e))))

