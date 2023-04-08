(ns labels.rules.rules
  (:require [labels.rules.no-unit-qty-in-parens :refer [rule-no-unit-qty-in-parens]]))


(defn check-rule [labelled {:keys [condition rule]}]
  (if (condition labelled)
    (rule labelled)
    labelled))

(defn apply-rules-to-ingr [labelled]
  (-> labelled
      (check-rule rule-no-unit-qty-in-parens)))

(defn apply-rules [labelled-ingredients]
  (map apply-rules-to-ingr labelled-ingredients))
