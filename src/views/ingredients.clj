(ns views.ingredients
  (:require [labels.label-helper :refer [is-close-parens is-comma
                                         is-open-parens is-qty is-unit qty unit]]
            [labels.labelling :as l]
            [labels.unit :as u]))

(defn unit-name [unit]
  (case unit
    ::u/cup [:space "cups"]
    ::u/deg-f ["°F"]
    ::u/gram ["g"]
    ::u/l [:space "litres"]
    ::u/lb [:space "pounds"]
    ::u/ml ["ml"]
    ::u/oz [:space "ounces"]
    ::u/qt [:space "quarts"]
    ::u/tbsp [:space "tablespoons"]
    ::u/tsp [:space "teaspoons"]
    [(name unit)]))

(defn frac [num denom]
  [:span [:sup num] "&frasl;" [:sub denom]])

(defn format-qty [qty]
  (if (= 0 (mod qty 1))
    qty
    (let [whole-part (int qty)
          fraction (case (mod qty 1)
                     0.125 (frac 1 8)
                     0.25  (frac 1 4)
                     0.33  (frac 1 3)
                     0.5 (frac 1 2)
                     0.75  (frac 3 4)
                     nil)]
      (cond
        (and (not= whole-part 0) (some? fraction)) (str whole-part " " fraction)
        (and (= whole-part 0) (some? fraction)) fraction
        :else qty))))

(defn render-single [token]
  (let [token-str (case (:label token)
                    ::l/qty [(format-qty (qty token))]
                    ::l/unit (unit-name (unit token))
                    ::l/other (:value token)
                    ::l/parens-open "("
                    ::l/parens-close ")"
                    "token")]
    (if (vector? token-str)
      token-str
      [token-str])))

(defn token-text [cur next]
  (cond
    (nil? next) (render-single cur)
    (is-open-parens cur) (render-single cur)
    (is-close-parens next) (render-single cur)
    (is-comma next) (render-single cur)
    (is-unit next) (render-single cur)
    :else (conj (render-single cur) :space)))

(defn labelled-ingredient [[cur next]]
  (if (some? cur)
    (for [txt (token-text cur next)]
      (if (= txt :space)
        " "
        [:span {:data-label (:label cur)} txt]))
    nil))

(defn quantity-element [tokens]
  [:div.ingredient__quantity
   (if-let [qty-token (first (filter is-qty tokens))]
     (format-qty (qty qty-token))
     "")])

(defn remove-first [pred coll]
  (let [[before non-pred-and-after] (split-with (complement pred) coll)]
    (concat before (rest non-pred-and-after))))

(defn ingredient-list [recipe]
  (let [ingredients (:labelled-ingredients recipe)]
    [:ul#recipe-ingredients.ingredient-list
     (for [ingredient-tokens ingredients]
       [:li.ingredient
        (quantity-element ingredient-tokens)
        [:div.ingredient__label
         (map labelled-ingredient (partition 2 1 nil (remove-first is-qty ingredient-tokens)))]])]))
