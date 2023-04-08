(ns labels.label-helper
  (:require [labels.labelling :as l]
            [labels.unit :as u]))

(defn is-unit [label]
  (= (:label label) ::l/unit))

(defn is-open-parens [token]
  (= (:label token) ::l/parens-open))

(defn is-close-parens [token]
  (= (:label token) ::l/parens-close))

(defn is-comma [token]
  (and (= (:label token) ::l/other)
       (= (:value token) ",")))

(defn unit [label]
  (if (is-unit label)
    (:value label)
    nil))

(defn labels [ingredients]
  (map :label ingredients))

(defn is-vol-or-weight [unit]
  (let [type (u/unit-type unit)]
    (or (= type ::u/weight)
        (= type ::u/volume))))


