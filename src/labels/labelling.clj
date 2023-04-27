(ns labels.labelling
  (:require [labels.heuristics.heuristics :refer [apply-heuristics]]
            [labels.preprocess :as prep]
            [labels.quantity :refer [try-quantity] :as q]
            [labels.tokenize :refer [tokenize]]
            [labels.unit :refer [try-unit] :as l]
            [labels.view-model :refer [to-view-model]]))

(defn label-token [token]
  (let [unit (try-unit token)
        qty (try-quantity token)]
    (cond
      (some? unit)  {:label :unit :value unit}
      (some? qty)   {:label :qty :value qty}
      (= token "(") {:label :parens-open}
      (= token ")") {:label :parens-close}
      :else         {:label :other :value token})))

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

(def labelling-pipeline
  (comp
   (map prep/replace-fractions)
   (map prep/split-qty-and-unit)
   (map tokenize)
   (map (fn [tokens] (mapv label-token tokens)))
   (map apply-heuristics)
   (map to-view-model)))

(defn label-ingredients [phrases]
  (into [] labelling-pipeline phrases))


