(ns labels.labelling
  (:require [labels.quantity :refer [try-quantity] :as q]
            [labels.unit :refer [try-unit] :as l]
            [labels.prep :as prep]
            [labels.tokenize :refer [tokenize]]))

(defn label-token [token]
  (let [unit (try-unit token)
        qty (try-quantity token)]
    (cond
      (some? unit)  {:label ::unit :value unit}
      (some? qty)   {:label ::qty :value qty}
      (= token "(") {:label ::parens-open}
      (= token ")") {:label ::parens-close}
      :else         {:label ::other :value token})))

(defn label-ingredient-phrase
  "Transforms an ingredient phrase into a list of labels. The string is cleaned,
   tokenized and then labelled. No heuristics applied."
  [phrase]
  (let [normalized (-> phrase
                       prep/replace-fractions
                       prep/split-qty-and-unit)
        tokens (tokenize normalized)
        labelled (mapv label-token tokens)]
    labelled))

(defn label-ingredients [phrases]
  (map label-ingredient-phrase phrases))
