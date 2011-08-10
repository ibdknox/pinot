(ns noir.pinot.remotes)

(def remote-regex #"/pinotremotecall")
(def remotes (atom {}))

(defn get-remote [remote]
  (get @remotes remote))

(defn add-remote [remote func]
  (swap! remotes assoc remote func))

(defmacro defremote [remote params & body]
  `(do
    (defn ~remote ~params ~@body)
    (add-remote ~(keyword (name remote)) ~remote)))

(defn call-remote [remote params]
  (if-let [func (get-remote remote)]
    (let [result (apply func params)]
      {:status 202
       :headers {"ContentType" "application/clojure"}
       :body (pr-str result)})
    {:status 404}))

(defn wrap-remotes [handler]
  (fn [{:keys [uri body] :as req}]
    (if (re-seq remote-regex uri)
      (let [{:keys [remote params]} (read-string (slurp body))
            remote (keyword remote)]
        (call-remote remote params))
      (handler req))))
