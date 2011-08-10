(ns pinot.remotes
  (:require [goog.net.XhrIo :as xhr]
            [clojure.string :as string]
            [pinot.util.js :as pjs]
            [cljs.reader :as reader]
            [goog.events :as events]))

(def remote-uri "/pinotremotecall")

(defn xhr [uri method content callback]
  (let [req (new goog.net.XhrIo)
        content (pr-str content)
        method (string/upper-case (name method))]
    (when callback
      (events/listen req goog.net.EventType/COMPLETE #(callback req)))
    (. req (send uri method content (pjs/map->js {"Content-Type" 
                                                  "application/clojure;charset=utf-8"})))))

(defn remote-callback [remote params callback]
  (xhr remote-uri :post 
       {:remote remote
        :params params} 
       (when callback
         (fn [req]
           (let [data (. req (getResponseText))]
             (callback (reader/read-string data)))))))

