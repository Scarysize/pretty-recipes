(ns labels.quantity
  (:require [clojure.string :as str]
            [clojure.edn :refer [read-string]]))

(def digit-expression #"^(\d*\.)?\d+$")

(defn try-quantity [token]
  (let [t (str/lower-case token)]
    (cond
      (re-matches digit-expression t) (read-string t)
      ;; TODO: non-numeric quantities: "head" of garlic, salad, "pinch" of salt
      :else nil)))
