(ns labels.quantity
  (:require [clojure.string :as str]
            [clojure.edn :as edn]))

(def digit-expression #"^(\d*\.)?\d+$")

(defn try-quantity [token]
  (let [t (str/lower-case token)]
    (cond
      (re-matches digit-expression t) (edn/read-string t)
      ;; TODO: non-numeric quantities: "head" of garlic, salad, "pinch" of salt
      :else nil)))
