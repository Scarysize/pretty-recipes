(ns labels.heuristics.heuristics
  (:require [labels.heuristics.no-unit-qty-in-parens :refer [rule-no-unit-qty-in-parens]]))


(defn check-rule [labelled {:keys [condition rule]}]
  (if (condition labelled)
    (rule labelled)
    labelled))

(defn apply-heuristics [labelled]
  (-> labelled
      (check-rule rule-no-unit-qty-in-parens)))

