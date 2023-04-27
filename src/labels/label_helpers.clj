(ns labels.label-helpers
  (:require
   [labels.unit :as u]))

(defn unit? [label]
  (= (:label label) :unit))

(defn qty? [label]
  (= (:label label) :qty))

(defn open-parens? [token]
  (= (:label token) :parens-open))

(defn close-parens? [token]
  (= (:label token) :parens-close))

(defn comma? [token]
  (and (= (:label token) :other)
       (= (:value token) ",")))

(defn unit [label]
  (if (unit? label)
    (:value label)
    nil))

(defn qty [label]
  (if (qty? label)
    (:value label)
    nil))

(defn labels [ingredients]
  (map :label ingredients))

(defn is-vol-or-weight [unit]
  (let [type (u/unit-type unit)]
    (or (= type ::u/weight)
        (= type ::u/volume))))


