(ns labels.labelling-test
  (:require [clojure.test :refer :all]
            [labels.labelling :refer [label-ingredient-phrase] :as l]
            [labels.unit :as u]))

(defn do-label [phrase]
  (label-ingredient-phrase phrase))

(defn is-unit [label expected-unit]
  (is (= ::l/unit (:label label)) (str "Not a unit " label))
  (is (= expected-unit (:value label)) (str "Expected unit: " expected-unit ", got: " (:value label))))

(defn is-qty [label expected-qty]
  (is (= ::l/qty (:label label)) (str "Not a qty " label))
  (is (= expected-qty (:value label)) (str "Expected qty: " expected-qty ", got: " (:value label))))

(deftest units
  (let [[_ cup _ _ gram] (do-label "3 cups (360g) King Arthur Unbleached Bread Flour")]
    (is-unit cup ::u/cup)
    (is-unit gram ::u/gram))
  (let [[_ tsp _ _ gram] (do-label "1 1/2 teaspoons (8g) salt 1/3 foo")]
    (is-unit tsp ::u/tsp)
    (is-unit gram ::u/gram))
  (let [[_ tsp _ _ gram _] (do-label "1 teaspoon (3g) instant yeast")]
    (is-unit tsp ::u/tsp)
    (is-unit gram ::u/gram))
  (let [[_ cup _ _ tbsp _ _ gram _] (do-label "1 cup plus 3 tablespoons (270g) water, lukewarm (95°F)")]
    (is-unit cup ::u/cup)
    (is-unit tbsp ::u/tbsp)
    (is-unit gram ::u/gram))
  (let [[_ lb] (do-label "1 pound (454g) Yukon Gold potatoes, scrubbed")]
    (is-unit lb ::u/lb))
  (let [[_ oz _ _] (do-label "4 ounces thick-cut bacon, roughly chopped; optional")]
    (is-unit oz ::u/oz))
  (let [[_ _ _ _ ml _ _] (do-label "2 cups (475ml) extra-virgin olive oil")]
    (is-unit ml ::u/ml)))

(deftest quantity
  (let [[qty0 _ _ qty1 _] (do-label "3 cups (360g) King Arthur Unbleached Bread Flour")]
    (is-qty qty0 3)
    (is-qty qty1 360))
  (let [[qty0 _ _ qty1] (do-label "1 1/2 teaspoons (8g) salt 1/3 foo")]
    (is-qty qty0 1.5)
    (is-qty qty1 8))
  (let [[_ _ qty deg _] (do-label "lukewarm (95°F)")]
    (is-qty qty 95)
    (is-unit deg ::u/deg-f)))

