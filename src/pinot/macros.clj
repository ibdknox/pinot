(ns pinot.macros)

(defmacro defpartial
  [name params & body]
  `(let [group# (swap! pinot.html/group-id inc)]
     (defn ^{:pinotGroup group#} 
       ~name ~params
       (pinot.dom/attr 
         (pinot.html/html
           ~@body)
         {:pinotGroup group#}))))

(defmacro defelem
  "Defines a function that will return a tag vector. If the first argument
  passed to the resulting function is a map, it merges it with the attribute
  map of the returned tag value."
  [name & fdecl]
  `(let [func# (fn ~@fdecl)]
    (def ~name (pinot.html.tags/add-optional-attrs func#))))

(defmacro remote
  [[sym & params] & [destruct & body]]
  (let [func (if destruct
               `(fn ~destruct ~@body)
               nil)]
    `(pinot.remotes/remote-callback ~(name sym)
                                    ~(vec params)
                                    ~func)))

(defmacro letrem
  [bindings & body]
  (let [bindings (partition 2 bindings)]
    (reduce
      (fn [prev [destruct func]]
        `(remote ~func [~destruct] ~prev))
      `(do ~@body)
      (reverse bindings))))

